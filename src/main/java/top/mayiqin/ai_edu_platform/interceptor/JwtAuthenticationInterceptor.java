package top.mayiqin.ai_edu_platform.interceptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import top.mayiqin.ai_edu_platform.constant.MessageConstant;
import top.mayiqin.ai_edu_platform.properties.JwtProperties;
import top.mayiqin.ai_edu_platform.utils.JwtUtil;
import top.mayiqin.ai_edu_platform.utils.UserContext;

/**
 * JWT认证拦截器
 * @author m'y'q
 */
@Component
@Slf4j
public class JwtAuthenticationInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取token（标准 Authorization 头）
        String token = request.getHeader(MessageConstant.AUTH_HEADER_NAME);
        
        // 处理Bearer前缀
        if (StringUtils.hasText(token) && token.startsWith(MessageConstant.AUTH_TOKEN_PREFIX)) {
            token = token.substring(MessageConstant.AUTH_TOKEN_PREFIX.length());
            log.info("去除Bearer前缀后的Token: {}...", token.substring(0, Math.min(20, token.length())));
        }
        
        // 如果没有token，返回401未授权
        if (!StringUtils.hasText(token)) {
            log.warn("❌ 请求未携带token，拒绝访问: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(String.format("{\"code\":401,\"msg\":\"%s\",\"data\":null}", MessageConstant.TOKEN_MISSING_OR_EXPIRED));
            return false;
        }
        
        try {
            // 使用JwtUtil验证token并解析Claims
            log.debug("🔑 当前使用的JWT密钥: {}", jwtProperties.getSecretKey());
            Claims claims = JwtUtil.parseJWT(jwtProperties.getSecretKey(), token);
            
            // 提取userId
            Object userIdObj = claims.get("userId");
            if (userIdObj == null) {
                log.warn("Token中缺少userId字段");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(String.format("{\"code\":401,\"msg\":\"%s\",\"data\":null}", "Token中缺少用户ID"));
                return false;
            }
            Long userId = ((Number) userIdObj).longValue();
            
            // 提取role（默认为user）
            String role = claims.get("role", String.class);
            if (role == null || role.isEmpty()) {
                role = "user"; // 默认角色
            }
            
            // 将userId和role存入当前线程的ThreadLocal
            UserContext.setCurrentUserId(userId);
            UserContext.setCurrentUserRole(role);
            log.info("✅ Token验证成功 - URI: {}, userId: {}, role: {}", request.getRequestURI(), userId, role);
        } catch (IllegalArgumentException e) {
            // token无效或过期，返回401状态码
            log.warn("❌ Token验证失败 - URI: {}, 错误: {}", request.getRequestURI(), e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(String.format("{\"code\":401,\"msg\":\"%s\",\"data\":null}", MessageConstant.TOKEN_INVALID));
            return false;
        }
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.remove();
        log.debug("已清除ThreadLocal中的用户信息");
    }
}

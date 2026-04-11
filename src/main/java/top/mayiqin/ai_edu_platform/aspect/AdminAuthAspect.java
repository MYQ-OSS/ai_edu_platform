package top.mayiqin.ai_edu_platform.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import top.mayiqin.ai_edu_platform.annotation.RequireAdmin;
import top.mayiqin.ai_edu_platform.constant.MessageConstant;
import top.mayiqin.ai_edu_platform.exception.BusinessException;
import top.mayiqin.ai_edu_platform.utils.UserContext;

/**
 * 管理员权限校验切面
 * 拦截带有 @RequireAdmin 注解的方法，验证当前用户是否为管理员
 * @author m'y'q
 */
@Aspect
@Component
@Slf4j
public class AdminAuthAspect {

    /**
     * 环绕通知：拦截所有带有 @RequireAdmin 注解的方法
     */
    @Around("@annotation(requireAdmin)")
    public Object checkAdminPermission(ProceedingJoinPoint joinPoint, RequireAdmin requireAdmin) throws Throwable {
        log.debug("开始执行管理员权限校验: method={}", joinPoint.getSignature().toShortString());
        
        // 1. 获取当前用户ID
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            log.warn("❌ 权限校验失败：用户未登录");
            throw new BusinessException(401, MessageConstant.USER_NOT_LOGIN);
        }
        
        // 2. 检查是否为管理员
        if (!UserContext.isAdmin()) {
            String role = UserContext.getCurrentUserRole();
            log.warn("❌ 权限校验失败：userId={}, role={}, 需要admin权限", userId, role);
            throw new BusinessException(403, MessageConstant.ADMIN_PERMISSION_DENIED);
        }
        
        log.info("✅ 权限校验通过：userId={}, method={}", userId, joinPoint.getSignature().toShortString());
        
        // 3. 权限校验通过，执行目标方法
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error("目标方法执行异常: {}", e.getMessage(), e);
            throw e;
        }
    }
}

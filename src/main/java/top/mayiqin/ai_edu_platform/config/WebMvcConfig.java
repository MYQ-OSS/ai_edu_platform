package top.mayiqin.ai_edu_platform.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.mayiqin.ai_edu_platform.interceptor.JwtAuthenticationInterceptor;

/**
 * @author m'y'q
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthenticationInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求（context-path 已配置为 /api）
                .excludePathPatterns(
                        "/user/login",      // 登录接口（实际路径：/api/user/login）
                        "/user/register",   // 注册接口（实际路径：/api/user/register）
                        "/doc.html",        // Knife4j 文档首页
                        "/webjars/**",      // Swagger 静态资源
                        "/swagger-resources/**",  // Swagger 资源配置
                        "/v3/api-docs/**",   // OpenAPI 3 文档 JSON
                        "/favicon.ico"       // 网站图标（浏览器自动请求）
                );
    }
}

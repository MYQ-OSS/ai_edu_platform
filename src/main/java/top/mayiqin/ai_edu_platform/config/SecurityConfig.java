package top.mayiqin.ai_edu_platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 安全配置类
 * @author m'y'q
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Knife4j 和 OpenAPI 相关的放行路径
     */
    private static final String[] KNIFE4J_WHITELIST = {
            "/doc.html",
            "/webjars/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/favicon.ico"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（前后端分离项目通常禁用）
                .csrf(AbstractHttpConfigurer::disable)
                // 配置请求授权规则
                .authorizeHttpRequests(auth -> auth
                        // Knife4j 文档相关路径放行
                        .requestMatchers(KNIFE4J_WHITELIST).permitAll()
                        // 其他所有请求暂时全部放行（可根据需要调整）
                        .anyRequest().permitAll()
                )
                // 允许 iframe 嵌套（Knife4j 需要）
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                );

        return http.build();
    }
}

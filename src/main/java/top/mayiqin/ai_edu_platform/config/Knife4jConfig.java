package top.mayiqin.ai_edu_platform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j (OpenAPI 3) 接口文档配置类
 * 访问地址: <a href="http://localhost:8080/api/doc.html">...</a>
 *
 * @author m'y'q
 */
@Configuration
public class Knife4jConfig {

    /**
     * 配置 OpenAPI 基本信息
     */
    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("面向程序员的 AI+ 教育创新应用——技术挑战与薪资评估一体化平台 API 文档")
                        .version("1.0.0")
                        .description("面向程序员的 AI+ 教育创新应用——技术挑战与薪资评估一体化平台后端接口文档")
                        .contact(new Contact()
                                .name("mayiqin")
                                .url("https://mayiqin.top"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                // 添加全局安全认证（Bearer Token）
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Authorization", new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .description("JWT Token 认证，格式: Bearer {token}")));
    }
}

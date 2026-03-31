package top.mayiqin.ai_edu_platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@MapperScan("top.mayiqin.ai_edu_platform.mapper")
public class AiEduPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiEduPlatformApplication.class, args);
    }

}

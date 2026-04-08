package top.mayiqin.ai_edu_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author m'y'q
 */
@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
public class AiEduPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiEduPlatformApplication.class, args);
    }

}

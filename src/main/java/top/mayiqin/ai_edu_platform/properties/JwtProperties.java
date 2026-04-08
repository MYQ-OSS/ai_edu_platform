package top.mayiqin.ai_edu_platform.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置属性类
 * 从配置文件中读取JWT相关配置
 * @author m'y'q
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    
    /**
     * JWT签名密钥
     */
    private String secretKey;
    
    /**
     * Token过期时间（毫秒）
     */
    private Long expiration;
}

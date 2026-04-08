package top.mayiqin.ai_edu_platform.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录请求DTO
 * @author m'y'q
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "用户登录请求参数")
public class UserLoginDTO {
    
    /**
     * 登录账号（必填）
     */
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "登录账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "zhangsan")
    private String username;
    
    /**
     * 密码（必填，前端明文传输）
     */
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码（明文）", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String password;
}

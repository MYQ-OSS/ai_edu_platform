package top.mayiqin.ai_edu_platform.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户注册请求DTO
 * @author m'y'q
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "用户注册请求参数")
public class UserRegisterDTO {
    
    /**
     * 登录账号（必填，1-50位）
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 1, max = 50, message = "用户名长度必须在1-50位之间")
    @Schema(description = "登录账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "zhangsan")
    private String username;
    
    /**
     * 密码（必填，前端明文传输）
     */
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码（明文）", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String password;
    
    /**
     * 用户身份（可选，如学生/初级开发者等，最多23位）
     */
    @Size(max = 23, message = "用户身份长度不能超过23位")
    @Schema(description = "用户身份", example = "学生")
    private String identity;
    
    /**
     * 期望/当前薪资（可选，整数）
     */
    @Schema(description = "期望/当前薪资", example = "15000")
    private Integer salary;
    
    /**
     * 项目/工作经历（可选，文本）
     */
    @Schema(description = "项目/工作经历", example = "有2年Java开发经验")
    private String experience;
}

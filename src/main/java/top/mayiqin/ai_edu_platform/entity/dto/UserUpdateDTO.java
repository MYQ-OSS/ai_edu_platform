package top.mayiqin.ai_edu_platform.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息更新请求DTO
 * 用于修改个人信息接口
 * @author m'y'q
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "用户信息更新请求")
public class UserUpdateDTO {
    
    /**
     * 用户ID（必填，从Token中获取，此处可选）
     */
    @Schema(description = "用户ID", example = "1", hidden = true)
    private Long id;
    
    /**
     * 密码（可选，如需修改则提供）
     */
    @Schema(description = "密码（可选，明文传输）", example = "123456")
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

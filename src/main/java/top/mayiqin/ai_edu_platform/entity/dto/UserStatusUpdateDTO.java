package top.mayiqin.ai_edu_platform.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用户状态更新DTO
 * @author m'y'q
 */
@Data
@Schema(description = "用户状态更新DTO")
public class UserStatusUpdateDTO {
    
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "^[01]$", message = "状态值只能为0或1")
    @Schema(description = "用户状态：0-正常，1-禁用", example = "1")
    private String status;
}

package top.mayiqin.ai_edu_platform.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 薪资评估请求DTO
 * 用于用户提交信息进行AI薪资评估
 * @author m'y'q
 */
@Data
@Schema(description = "薪资评估请求参数")
public class SalaryEvaluateDTO {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    /**
     * 技术方向
     */
    @NotBlank(message = "技术方向不能为空")
    @Size(min = 1, max = 100, message = "技术方向长度必须在1-100位之间")
    @Schema(description = "技术方向", requiredMode = Schema.RequiredMode.REQUIRED, example = "Java后端开发")
    private String direction;

    /**
     * 目标城市
     */
    @Size(max = 50, message = "目标城市长度不能超过50位")
    @Schema(description = "目标城市", example = "北京")
    private String city;

    /**
     * 项目/工作经历
     */
    @NotBlank(message = "项目经历不能为空")
    @Schema(description = "项目/工作经历", requiredMode = Schema.RequiredMode.REQUIRED, example = "3年Spring Boot项目经验")
    private String experience;

    /**
     * 学历
     */
    @Schema(description = "学历", example = "本科")
    private String education;

    /**
     * 身份（学生/在职）
     */
    @Size(max = 23, message = "身份长度不能超过23位")
    @Schema(description = "身份（学生/在职）", example = "在职")
    private String identity;
}

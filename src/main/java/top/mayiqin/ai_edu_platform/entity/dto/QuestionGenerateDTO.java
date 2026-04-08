package top.mayiqin.ai_edu_platform.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 题目生成请求DTO
 * 用于AI智能生成面试题目
 * @author m'y'q
 */
@Data
@Schema(description = "题目生成请求参数")
public class QuestionGenerateDTO {

    /**
     * 技术方向（如Java后端、Vue前端等）
     */
    @NotBlank(message = "技术方向不能为空")
    @Size(min = 1, max = 100, message = "技术方向长度需在1-100位之间")
    @Schema(description = "技术方向", requiredMode = Schema.RequiredMode.REQUIRED, example = "Java后端开发")
    private String direction;

    /**
     * 期望薪资（单位：元/月）
     */
    @NotNull(message = "期望薪资不能为空")
    @Positive(message = "期望薪资必须为正整数")
    @Schema(description = "期望薪资（元/月）", requiredMode = Schema.RequiredMode.REQUIRED, example = "15000")
    private Integer targetSalary;

    /**
     * 用户身份（如学生、初级开发者等）
     */
    @Size(max = 23, message = "用户身份长度不能超过23位")
    @Schema(description = "用户身份", example = "学生")
    private String identity;

    /**
     * 就业城市
     */
    @Size(max = 50, message = "就业城市长度不能超过50位")
    @Schema(description = "就业城市", example = "北京")
    private String city;

    /**
     * 答题限时（单位：秒）
     */
    @Min(value = 1, message = "答题限时必须为正整数")
    @Schema(description = "答题限时（秒）", example = "30")
    private Integer timeLimit = 30;
}

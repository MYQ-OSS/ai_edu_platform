package top.mayiqin.ai_edu_platform.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 答题提交请求DTO
 * 用于用户提交答题结果
 * @author m'y'q
 */
@Data
@Schema(description = "答题提交请求参数")
public class QuizSubmitDTO {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    /**
     * 题目ID
     */
    @NotNull(message = "题目ID不能为空")
    @Schema(description = "题目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long questionId;

    /**
     * 用户选择的选项（JSON格式）
     */
    @NotBlank(message = "用户选项不能为空")
    @Schema(description = "用户选择的选项（JSON格式）", requiredMode = Schema.RequiredMode.REQUIRED, example = "[{\"label\":\"SpringBoot\",\"value\":\"springboot\"}]")
    private String userOptions;

    /**
     * 用户文本答案
     */
    @NotBlank(message = "用户答案不能为空")
    @Schema(description = "用户文本答案", requiredMode = Schema.RequiredMode.REQUIRED, example = "我会使用SpringBoot作为主要框架...")
    private String userAnswer;
}

package top.mayiqin.ai_edu_platform.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 题目收藏请求DTO
 * 用于用户收藏或取消收藏题目
 * @author m'y'q
 */
@Data
@Schema(description = "题目收藏请求参数")
public class QuizCollectDTO {

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
     * 是否收藏（true=收藏，false=取消）
     */
    @NotNull(message = "收藏状态不能为空")
    @Schema(description = "是否收藏（true=收藏，false=取消）", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean isCollect;
}

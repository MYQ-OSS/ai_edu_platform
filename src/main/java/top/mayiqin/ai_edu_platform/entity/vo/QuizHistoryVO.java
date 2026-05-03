package top.mayiqin.ai_edu_platform.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 答题历史简要信息（用于会话上下文附加等列表展示）
 */
@Data
@Builder
@Schema(description = "答题历史简要信息")
public class QuizHistoryVO {

    @Schema(description = "答题记录ID")
    private Long id;

    @Schema(description = "题目名称")
    private String questionName;

    @Schema(description = "得分")
    private Integer score;

    @Schema(description = "评价摘要")
    private String comment;

    @Schema(description = "评分原因")
    private String reason;

    @Schema(description = "题目解析")
    private String analysis;

    @Schema(description = "提交时间", example = "2024-05-01 10:00:00")
    private String createTime;
}

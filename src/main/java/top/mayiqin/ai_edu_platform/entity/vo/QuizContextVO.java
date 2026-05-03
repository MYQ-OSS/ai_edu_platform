package top.mayiqin.ai_edu_platform.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 答题上下文VO
 *
 * @author m'y'q
 */
@Data
@Builder
@Schema(description = "答题记录详情")
public class QuizContextVO {

    @Schema(description = "记录ID")
    private Long recordId;

    @Schema(description = "题目名称")
    private String questionName;

    @Schema(description = "用户选项")
    private String userOptions;

    @Schema(description = "用户答案")
    private String userAnswer;

    @Schema(description = "得分")
    private Integer score;

    @Schema(description = "正确率")
    private String accuracy;

    @Schema(description = "评价")
    private String comment;

    @Schema(description = "建议")
    private String suggest;

    @Schema(description = "原因分析")
    private String reason;

    @Schema(description = "题目解析")
    private String analysis;

    @Schema(description = "创建时间")
    private String createTime;
}

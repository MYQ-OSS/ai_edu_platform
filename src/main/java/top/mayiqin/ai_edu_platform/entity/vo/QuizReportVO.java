package top.mayiqin.ai_edu_platform.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 答题报告响应VO
 * 返回详细的答题报告信息
 * @author m'y'q
 */
@Data
@Builder
@Schema(description = "答题报告响应数据")
public class QuizReportVO {

    /**
     * 答题记录ID
     */
    @Schema(description = "答题记录ID", example = "1")
    private Long recordId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /**
     * 题目ID
     */
    @Schema(description = "题目ID", example = "1")
    private Long questionId;

    /**
     * 用户选择的选项（JSON格式）
     */
    @Schema(description = "用户选择的选项（JSON格式）")
    private String userOptions;

    /**
     * 用户文本答案
     */
    @Schema(description = "用户文本答案")
    private String userAnswer;

    /**
     * 得分
     */
    @Schema(description = "得分", example = "85")
    private Integer score;

    /**
     * 评价内容
     */
    @Schema(description = "评价内容")
    private String comment;

    /**
     * 投递建议
     */
    @Schema(description = "投递建议")
    private String suggest;

    /**
     * 评分原因
     */
    @Schema(description = "评分原因")
    private String reason;

    /**
     * 正确选项（JSON格式）
     */
    @Schema(description = "正确选项（JSON格式）")
    private String trueOptions;

    /**
     * 题目解析
     */
    @Schema(description = "题目解析")
    private String analysis;

    /**
     * 正确率（百分比）
     */
    @Schema(description = "正确率（百分比）", example = "85.00")
    private BigDecimal accuracy;

    /**
     * 提交时间
     */
    @Schema(description = "提交时间", example = "2024-05-01 10:00:00")
    private String createTime;
}

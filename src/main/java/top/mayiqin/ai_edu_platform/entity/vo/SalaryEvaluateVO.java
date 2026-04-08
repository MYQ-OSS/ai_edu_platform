package top.mayiqin.ai_edu_platform.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 薪资评估报告响应VO
 * 返回AI生成的薪资评估结果
 * @author m'y'q
 */
@Data
@Builder
@Schema(description = "薪资评估报告响应数据")
public class SalaryEvaluateVO {

    /**
     * 报告ID
     */
    @Schema(description = "报告ID", example = "1")
    private Long reportId;

    /**
     * AI预测薪资区间
     */
    @Schema(description = "AI预测薪资区间", example = "8k-12k")
    private String salaryRange;

    /**
     * AI给出的能力提升建议
     */
    @Schema(description = "AI给出的能力提升建议")
    private String aiSuggestion;

    /**
     * 生成时间
     */
    @Schema(description = "生成时间", example = "2024-05-01 10:00:00")
    private String createTime;
}

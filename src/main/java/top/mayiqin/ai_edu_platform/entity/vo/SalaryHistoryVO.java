package top.mayiqin.ai_edu_platform.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 薪资评估历史记录简要VO
 * 用于列表展示，只包含核心字段
 * @author m'y'q
 */
@Data
@Builder
@Schema(description = "薪资评估历史记录简要信息")
public class SalaryHistoryVO {

    /**
     * 报告ID
     */
    @Schema(description = "报告ID", example = "1")
    private Long id;

    /**
     * AI预测薪资区间
     */
    @Schema(description = "AI预测薪资区间", example = "8k-12k")
    private String salaryRange;

    /**
     * 生成时间
     */
    @Schema(description = "生成时间", example = "2024-05-01 10:00:00")
    private String createTime;
}

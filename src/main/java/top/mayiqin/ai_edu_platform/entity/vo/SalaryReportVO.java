package top.mayiqin.ai_edu_platform.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 薪资评估报告详细响应VO
 * 返回完整的薪资评估报告信息
 * @author m'y'q
 */
@Data
@Builder
@Schema(description = "薪资评估报告详细响应数据")
public class SalaryReportVO {

    /**
     * 报告ID
     */
    @Schema(description = "报告ID", example = "1")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /**
     * 技术方向
     */
    @Schema(description = "技术方向", example = "Java后端开发")
    private String direction;

    /**
     * 目标城市
     */
    @Schema(description = "目标城市", example = "北京")
    private String city;

    /**
     * 项目/工作经历
     */
    @Schema(description = "项目/工作经历")
    private String experience;

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

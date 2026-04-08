package top.mayiqin.ai_edu_platform.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.mayiqin.ai_edu_platform.entity.po.QuizRecord;
import top.mayiqin.ai_edu_platform.entity.po.SalaryReport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 学习足迹响应DTO
 * @author m'y'q
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "学习足迹响应数据")
public class LearningHistoryVO {
    
    /**
     * 答题记录列表
     */
    @Schema(description = "答题记录列表")
    private List<QuizRecord> quizRecords = new ArrayList<>();
    
    /**
     * 正确率趋势（最近10次答题的正确率）
     */
    @Schema(description = "正确率趋势（最近10次答题的正确率）")
    private List<BigDecimal> accuracyTrend = new ArrayList<>();
    
    /**
     * 薪资报告列表
     */
    @Schema(description = "薪资报告列表")
    private List<SalaryReport> salaryReports = new ArrayList<>();
}

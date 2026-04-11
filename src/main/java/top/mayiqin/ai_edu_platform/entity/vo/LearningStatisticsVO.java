package top.mayiqin.ai_edu_platform.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 学习统计响应VO
 * @author m'y'q
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "学习统计响应数据")
public class LearningStatisticsVO {
    
    /**
     * 总答题次数
     */
    @Schema(description = "总答题次数")
    private Integer totalQuizCount;
    
    /**
     * 平均得分
     */
    @Schema(description = "平均得分")
    private BigDecimal averageScore;
    
    /**
     * 平均正确率
     */
    @Schema(description = "平均正确率")
    private BigDecimal averageAccuracy;
    
    /**
     * 最高得分
     */
    @Schema(description = "最高得分")
    private Integer maxScore;
    
    /**
     * 最低得分
     */
    @Schema(description = "最低得分")
    private Integer minScore;
    
    /**
     * 得分趋势数据（按时间顺序）
     */
    @Schema(description = "得分趋势数据")
    @Builder.Default
    private List<ScoreTrendItem> scoreTrend = new ArrayList<>();
    
    /**
     * 正确率趋势数据（按时间顺序）
     */
    @Schema(description = "正确率趋势数据")
    @Builder.Default
    private List<AccuracyTrendItem> accuracyTrend = new ArrayList<>();
    
    /**
     * 得分趋势项
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "得分趋势项")
    public static class ScoreTrendItem {
        /**
         * 答题时间
         */
        @Schema(description = "答题时间")
        private String quizTime;
        
        /**
         * 得分
         */
        @Schema(description = "得分")
        private Integer score;
        
        /**
         * 题目名称
         */
        @Schema(description = "题目名称")
        private String questionName;
    }
    
    /**
     * 正确率趋势项
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "正确率趋势项")
    public static class AccuracyTrendItem {
        /**
         * 答题时间
         */
        @Schema(description = "答题时间")
        private String quizTime;
        
        /**
         * 正确率
         */
        @Schema(description = "正确率")
        private BigDecimal accuracy;
        
        /**
         * 题目名称
         */
        @Schema(description = "题目名称")
        private String questionName;
    }
}

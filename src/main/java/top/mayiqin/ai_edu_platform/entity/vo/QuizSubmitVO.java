package top.mayiqin.ai_edu_platform.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 答题提交响应VO
 * 返回AI判分后的答题结果
 * @author m'y'q
 */
@Data
@Builder
@Schema(description = "答题提交响应数据")
public class QuizSubmitVO {

    /**
     * 答题记录ID
     */
    @Schema(description = "答题记录ID", example = "1")
    private Long recordId;

    /**
     * 得分
     */
    @Schema(description = "得分", example = "85")
    private Integer score;

    /**
     * 正确率（百分比）
     */
    @Schema(description = "正确率（百分比）", example = "85.00")
    private BigDecimal accuracy;
}

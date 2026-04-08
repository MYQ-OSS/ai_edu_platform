package top.mayiqin.ai_edu_platform.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户答题记录表
 * @author m'y'q
 * @TableName t_quiz_record
 */
@TableName(value ="t_quiz_record")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class QuizRecord {
    /**
     * 记录唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户唯一ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 题目唯一ID
     */
    @TableField(value = "question_id")
    private Long questionId;

    /**
     * 用户选择的选项（JSON格式）
     */
    @TableField(value = "user_options")
    private String userOptions;

    /**
     * 用户答案（文本）
     */
    @TableField(value = "user_answer")
    private String userAnswer;

    /**
     * 本次测试得分
     */
    @TableField(value = "score")
    private Integer score;

    /**
     * 评价内容
     */
    @TableField(value = "comment")
    private String comment;

    /**
     * 公司投递建议
     */
    @TableField(value = "suggest")
    private String suggest;

    /**
     * 评分原因解析
     */
    @TableField(value = "reason")
    private String reason;

    /**
     * 正确选项（JSON格式）
     */
    @TableField(value = "true_options")
    private String trueOptions;

    /**
     * AI生成的题目解析
     */
    @TableField(value = "analysis")
    private String analysis;

    /**
     * 正确率（百分比，如80.00）
     */
    @TableField(value = "accuracy")
    private BigDecimal accuracy;

    /**
     * 提交时间
     */
    @TableField(value = "create_time")
    private Date createTime;
}
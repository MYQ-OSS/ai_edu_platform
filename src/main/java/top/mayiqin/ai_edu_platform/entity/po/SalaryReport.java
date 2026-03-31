package top.mayiqin.ai_edu_platform.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 薪资评估报告表
 * @TableName t_salary_report
 */
@TableName(value ="t_salary_report")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SalaryReport {
    /**
     * 报告唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户唯一ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 技术方向
     */
    @TableField(value = "direction")
    private String direction;

    /**
     * 目标城市
     */
    @TableField(value = "city")
    private String city;

    /**
     * 项目/工作经历
     */
    @TableField(value = "experience")
    private String experience;

    /**
     * AI预测薪资区间（如10k-15k）
     */
    @TableField(value = "salary_range")
    private String salaryRange;

    /**
     * AI给出的能力提升建议
     */
    @TableField(value = "ai_suggestion")
    private String aiSuggestion;

    /**
     * 生成时间
     */
    @TableField(value = "create_time")
    private Date createTime;
}
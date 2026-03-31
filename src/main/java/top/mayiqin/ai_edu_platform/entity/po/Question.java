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
 * AI生成题库表
 * @author m'y'q
 * @TableName t_question
 */
@TableName(value ="t_question")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Question {
    /**
     * 题目唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 题目名称
     */
    @TableField(value = "question_name")
    private String questionName;

    /**
     * 题目需求描述
     */
    @TableField(value = "question_desc")
    private String questionDesc;

    /**
     * 题目技术栈选项（JSON格式）
     */
    @TableField(value = "options")
    private String options;

    /**
     * 目标薪资
     */
    @TableField(value = "target_salary")
    private Integer targetSalary;

    /**
     * 技术方向（如Java后端开发）
     */
    @TableField(value = "direction")
    private String direction;

    /**
     * AI生成的题目解析
     */
    @TableField(value = "analysis")
    private String analysis;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;
}
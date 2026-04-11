package top.mayiqin.ai_edu_platform.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 题目列表VO
 * @author m'y'q
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionListVO {

    /**
     * 题目ID
     */
    private Long id;

    /**
     * 题目名称
     */
    private String questionName;

    /**
     * 题目描述
     */
    private String questionDesc;

    /**
     * 选项（JSON格式）
     */
    private String options;

    /**
     * 目标薪资
     */
    private Integer targetSalary;

    /**
     * 技术方向
     */
    private String direction;

    /**
     * 题目解析
     */
    private String analysis;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}

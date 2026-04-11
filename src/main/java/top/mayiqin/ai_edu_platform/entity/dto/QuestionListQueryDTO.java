package top.mayiqin.ai_edu_platform.entity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 题目列表查询DTO
 * @author m'y'q
 */
@Data
public class QuestionListQueryDTO {

    /**
     * 页码
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    @Min(value = 1, message = "每页条数必须大于0")
    private Integer pageSize = 10;

    /**
     * 题目名称（模糊查询）
     */
    @Size(max = 200, message = "题目名称长度不能超过200位")
    private String questionName;

    /**
     * 技术方向
     */
    @Size(max = 100, message = "技术方向长度不能超过100位")
    private String direction;

    /**
     * 目标薪资
     */
    private Integer targetSalary;
}

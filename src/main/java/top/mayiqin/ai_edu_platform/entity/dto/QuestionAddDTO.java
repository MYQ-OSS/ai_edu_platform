package top.mayiqin.ai_edu_platform.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 管理员新增题目DTO
 * @author m'y'q
 */
@Data
public class QuestionAddDTO {

    /**
     * 题目名称
     */
    @NotBlank(message = "题目名称不能为空")
    @Length(min = 1, max = 200, message = "题目名称长度必须在1-200位之间")
    private String questionName;

    /**
     * 题目需求描述
     */
    @NotBlank(message = "题目描述不能为空")
    private String questionDesc;

    /**
     * 题目技术栈选项（JSON格式）
     */
    @NotBlank(message = "题目选项不能为空")
    private String options;

    /**
     * 目标薪资（单位：元）
     */
    @NotNull(message = "目标薪资不能为空")
    private Integer targetSalary;

    /**
     * 技术方向
     */
    @NotBlank(message = "技术方向不能为空")
    @Length(min = 1, max = 100, message = "技术方向长度必须在1-100位之间")
    private String direction;

    /**
     * 题目解析（可选）
     */
    private String analysis;
}

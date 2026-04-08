package top.mayiqin.ai_edu_platform.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 题目响应VO
 * 返回AI生成的题目信息
 * @author m'y'q
 */
@Data
@Builder
@Schema(description = "题目信息响应")
public class QuestionVO {

    /**
     * 题目ID
     */
    @Schema(description = "题目ID", example = "1")
    private Long questionId;

    /**
     * 题目名称
     */
    @Schema(description = "题目名称", example = "Spring Bean生命周期")
    private String questionName;

    /**
     * 题目描述
     */
    @Schema(description = "题目描述", example = "请简述Spring Bean的生命周期")
    private String questionDesc;

    /**
     * 选项（JSON格式）
     */
    @Schema(description = "选项（JSON格式）", example = "{\"A\":\"选项A\",\"B\":\"选项B\"}")
    private String options;

    /**
     * 目标薪资
     */
    @Schema(description = "目标薪资（元/月）", example = "15000")
    private Integer targetSalary;

    /**
     * 技术方向
     */
    @Schema(description = "技术方向", example = "Java后端开发")
    private String direction;
}

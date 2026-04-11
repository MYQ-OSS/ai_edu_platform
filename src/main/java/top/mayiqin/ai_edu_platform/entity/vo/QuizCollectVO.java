package top.mayiqin.ai_edu_platform.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户收藏题目响应VO
 * 返回用户收藏的题目列表信息
 * @author m'y'q
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "用户收藏题目响应数据")
public class QuizCollectVO {

    /**
     * 收藏记录ID
     */
    @Schema(description = "收藏记录ID", example = "1")
    private Long recordId;

    /**
     * 题目ID
     */
    @Schema(description = "题目ID", example = "1")
    private Long questionId;

    /**
     * 题目名称
     */
    @Schema(description = "题目名称", example = "Java后端开发挑战题")
    private String questionName;

    /**
     * 题目描述
     */
    @Schema(description = "题目描述")
    private String questionDesc;

    /**
     * 技术方向
     */
    @Schema(description = "技术方向", example = "Java后端开发")
    private String direction;

    /**
     * 目标薪资
     */
    @Schema(description = "目标薪资", example = "15000")
    private Integer targetSalary;

    /**
     * 题目选项（JSON格式）
     */
    @Schema(description = "题目选项（JSON格式）")
    private String options;

    /**
     * 收藏时间
     */
    @Schema(description = "收藏时间", example = "2024-05-01 10:00:00")
    private String collectTime;
}

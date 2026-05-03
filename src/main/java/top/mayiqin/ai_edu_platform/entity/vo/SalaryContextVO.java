package top.mayiqin.ai_edu_platform.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 薪资上下文VO
 *
 * @author m'y'q
 */
@Data
@Builder
@Schema(description = "薪资报告详情")
public class SalaryContextVO {

    @Schema(description = "报告ID")
    private Long reportId;

    @Schema(description = "技术方向")
    private String direction;

    @Schema(description = "目标城市")
    private String city;

    @Schema(description = "工作经历")
    private String experience;

    @Schema(description = "学历")
    private String education;

    @Schema(description = "身份")
    private String identity;

    @Schema(description = "薪资范围")
    private String salaryRange;

    @Schema(description = "AI建议")
    private String aiSuggestion;

    @Schema(description = "创建时间")
    private String createTime;
}

package top.mayiqin.ai_edu_platform.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 聊天请求DTO
 *
 * @author m'y'q
 */
@Data
@Schema(description = "聊天请求参数")
public class ChatRequestDTO {

    @NotBlank(message = "会话ID不能为空")
    @Schema(description = "会话ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sessionId;

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 2000, message = "消息长度不能超过2000字符")
    @Schema(description = "消息内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    @Size(max = 5, message = "最多附加5条答题记录")
    @Schema(description = "答题记录ID列表")
    private List<Long> quizRecordIds;

    @Size(max = 5, message = "最多附加5条薪资报告")
    @Schema(description = "薪资报告ID列表")
    private List<Long> salaryReportIds;
}

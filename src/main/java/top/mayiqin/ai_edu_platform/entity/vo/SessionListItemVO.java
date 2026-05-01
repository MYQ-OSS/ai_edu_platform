package top.mayiqin.ai_edu_platform.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 会话列表项VO
 *
 * @author m'y'q
 */
@Data
@Builder
@Schema(description = "会话列表项")
public class SessionListItemVO {

    @Schema(description = "会话ID")
    private String sessionId;

    @Schema(description = "最后一条消息摘要")
    private String lastMessageSummary;

    @Schema(description = "最后活跃时间")
    private String lastActiveTime;

    @Schema(description = "消息总数")
    private Integer messageCount;
}

package top.mayiqin.ai_edu_platform.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 会话消息VO
 *
 * @author m'y'q
 */
@Data
@Builder
@Schema(description = "会话消息")
public class SessionMessageVO {

    @Schema(description = "消息ID")
    private String messageId;

    @Schema(description = "角色")
    private String role;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "时间戳")
    private String timestamp;

    @Schema(description = "消息类型")
    private String type;
}

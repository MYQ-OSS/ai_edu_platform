package top.mayiqin.ai_edu_platform.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 会话消息列表VO
 *
 * @author m'y'q
 */
@Data
@Builder
@Schema(description = "会话消息列表")
public class SessionMessagesVO {

    @Schema(description = "消息列表")
    private List<SessionMessageVO> messages;

    @Schema(description = "是否还有更多消息")
    private Boolean hasMore;

    @Schema(description = "消息总数")
    private Integer total;
}

package top.mayiqin.ai_edu_platform.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 会话信息VO
 *
 * @author m'y'q
 */
@Data
@Builder
@Schema(description = "会话信息")
public class SessionInfoVO {

    @Schema(description = "会话ID")
    private String sessionId;

    @Schema(description = "创建时间")
    private String createTime;
}

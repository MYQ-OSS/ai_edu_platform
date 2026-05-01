package top.mayiqin.ai_edu_platform.entity;

import lombok.Builder;
import lombok.Data;
import top.mayiqin.ai_edu_platform.enums.MessageTypeEnum;

import java.time.LocalDateTime;

/**
 * 聊天消息对象
 *
 * @author m'y'q
 */
@Data
@Builder
public class ChatMessage {

    private String messageId;

    private String role;

    private String content;

    private LocalDateTime timestamp;

    private MessageTypeEnum type;
}

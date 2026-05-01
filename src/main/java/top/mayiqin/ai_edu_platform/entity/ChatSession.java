package top.mayiqin.ai_edu_platform.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天会话对象
 *
 * @author m'y'q
 */
@Data
@Builder
public class ChatSession {

    private String sessionId;

    private Long userId;

    private List<ChatMessage> messages;

    private LocalDateTime createTime;

    private LocalDateTime lastActiveTime;

    private volatile boolean locked;
}

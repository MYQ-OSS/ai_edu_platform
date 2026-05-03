package top.mayiqin.ai_edu_platform.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.mayiqin.ai_edu_platform.entity.ChatMessage;
import top.mayiqin.ai_edu_platform.entity.ChatSession;
import top.mayiqin.ai_edu_platform.exception.BusinessException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 会话管理器
 * 负责会话的创建、查询、删除及超时清理
 *
 * @author m'y'q
 */
@Slf4j
@Component
public class SessionManager {

    private static final int MAX_SESSIONS_PER_USER = 10;
    private static final int MAX_MESSAGES_PER_SESSION = 100;
    private static final long SESSION_TIMEOUT_MS = 30L * 60 * 1000;

    private final ConcurrentHashMap<String, ChatSession> sessionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, List<String>> userSessionsMap = new ConcurrentHashMap<>();

    /**
     * 创建会话
     */
    public ChatSession createSession(Long userId) {
        List<String> sessionIds = userSessionsMap.getOrDefault(userId, new CopyOnWriteArrayList<>());
        if (sessionIds.size() >= MAX_SESSIONS_PER_USER) {
            throw new BusinessException(400, "会话数量已达上限，请先删除旧会话");
        }

        String sessionId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        ChatSession session = ChatSession.builder()
                .sessionId(sessionId)
                .userId(userId)
                .messages(new CopyOnWriteArrayList<>())
                .createTime(now)
                .lastActiveTime(now)
                .locked(false)
                .build();

        sessionMap.put(sessionId, session);
        userSessionsMap.computeIfAbsent(userId, key -> new CopyOnWriteArrayList<>()).add(sessionId);
        return session;
    }

    /**
     * 获取会话
     */
    public ChatSession getSession(String sessionId) {
        return sessionMap.get(sessionId);
    }

    /**
     * 获取用户会话列表
     */
    public List<ChatSession> listUserSessions(Long userId) {
        List<String> sessionIds = userSessionsMap.getOrDefault(userId, new ArrayList<>());
        List<ChatSession> sessions = new ArrayList<>();
        for (String sessionId : sessionIds) {
            ChatSession session = sessionMap.get(sessionId);
            if (session != null) {
                sessions.add(session);
            }
        }
        sessions.sort(Comparator.comparing(ChatSession::getLastActiveTime).reversed());
        return sessions;
    }

    /**
     * 删除会话
     */
    public void deleteSession(String sessionId) {
        ChatSession session = sessionMap.remove(sessionId);
        if (session == null) {
            return;
        }

        List<String> sessionIds = userSessionsMap.get(session.getUserId());
        if (sessionIds != null) {
            sessionIds.remove(sessionId);
        }
    }

    /**
     * 添加消息
     */
    public void addMessage(String sessionId, ChatMessage message) {
        ChatSession session = sessionMap.get(sessionId);
        if (session == null) {
            return;
        }

        List<ChatMessage> messages = session.getMessages();
        messages.add(message);
        while (messages.size() > MAX_MESSAGES_PER_SESSION) {
            messages.removeFirst();
        }
        session.setLastActiveTime(LocalDateTime.now());
    }

    /**
     * 清理超时会话
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void cleanupExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        sessionMap.entrySet().removeIf(entry -> {
            ChatSession session = entry.getValue();
            Duration duration = Duration.between(session.getLastActiveTime(), now);
            boolean expired = duration.toMillis() > SESSION_TIMEOUT_MS;
            if (expired) {
                deleteSession(entry.getKey());
                log.info("清理超时会话: sessionId={}, userId={}", session.getSessionId(), session.getUserId());
            }
            return expired;
        });
    }
}

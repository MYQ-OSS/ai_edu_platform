package top.mayiqin.ai_edu_platform.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import top.mayiqin.ai_edu_platform.entity.ChatMessage;
import top.mayiqin.ai_edu_platform.entity.ChatSession;
import top.mayiqin.ai_edu_platform.entity.dto.ChatRequestDTO;
import top.mayiqin.ai_edu_platform.entity.po.Question;
import top.mayiqin.ai_edu_platform.entity.po.QuizRecord;
import top.mayiqin.ai_edu_platform.entity.po.SalaryReport;
import top.mayiqin.ai_edu_platform.entity.vo.QuizContextVO;
import top.mayiqin.ai_edu_platform.entity.vo.SalaryContextVO;
import top.mayiqin.ai_edu_platform.entity.vo.SessionInfoVO;
import top.mayiqin.ai_edu_platform.entity.vo.SessionListItemVO;
import top.mayiqin.ai_edu_platform.entity.vo.SessionMessageVO;
import top.mayiqin.ai_edu_platform.entity.vo.SessionMessagesVO;
import top.mayiqin.ai_edu_platform.enums.MessageTypeEnum;
import top.mayiqin.ai_edu_platform.exception.BusinessException;
import top.mayiqin.ai_edu_platform.manager.SessionManager;
import top.mayiqin.ai_edu_platform.mapper.QuestionMapper;
import top.mayiqin.ai_edu_platform.mapper.QuizRecordMapper;
import top.mayiqin.ai_edu_platform.mapper.SalaryReportMapper;
import top.mayiqin.ai_edu_platform.service.ChatAssistantService;
import top.mayiqin.ai_edu_platform.utils.SensitiveWordFilter;
import top.mayiqin.ai_edu_platform.utils.UserContext;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * AI对话助手服务实现
 *
 * @author m'y'q
 */
@Slf4j
@Service
public class ChatAssistantServiceImpl implements ChatAssistantService {

    private static final int MAX_PROMPT_LENGTH = 8000;
    private static final int MAX_CONTEXT_ITEMS = 5;
    private static final int SESSION_MESSAGE_PREVIEW_LENGTH = 20;

    private final SessionManager sessionManager;
    private final ChatClient chatClient;
    private final QuizRecordMapper quizRecordMapper;
    private final SalaryReportMapper salaryReportMapper;
    private final QuestionMapper questionMapper;
    private final SensitiveWordFilter sensitiveWordFilter;

    public ChatAssistantServiceImpl(SessionManager sessionManager,
                                    ChatClient chatClient,
                                    QuizRecordMapper quizRecordMapper,
                                    SalaryReportMapper salaryReportMapper,
                                    QuestionMapper questionMapper,
                                    SensitiveWordFilter sensitiveWordFilter) {
        this.sessionManager = sessionManager;
        this.chatClient = chatClient;
        this.quizRecordMapper = quizRecordMapper;
        this.salaryReportMapper = salaryReportMapper;
        this.questionMapper = questionMapper;
        this.sensitiveWordFilter = sensitiveWordFilter;
    }

    @Override
    public SessionInfoVO createSession(Long userId) {
        validateUserId(userId);
        ChatSession session = sessionManager.createSession(userId);
        return SessionInfoVO.builder()
                .sessionId(session.getSessionId())
                .createTime(formatLocalDateTime(session.getCreateTime()))
                .build();
    }

    @Override
    public List<SessionListItemVO> listSession() {
        Long userId = getCurrentUserId();
        List<ChatSession> sessions = sessionManager.listUserSessions(userId);
        List<SessionListItemVO> result = new ArrayList<>();
        for (ChatSession session : sessions) {
            String preview = "";
            if (!session.getMessages().isEmpty()) {
                // 获取最后一条非系统消息的摘要
                String lastMessage = session.getMessages().stream()
                        .filter(msg -> !"SYSTEM".equals(msg.getType().getCode()))
                        .reduce((first, second) -> second)  // 获取最后一条
                        .map(ChatMessage::getContent)
                        .orElse("");

                // 如果没有非系统消息，则使用最后一条消息
                if (lastMessage.isEmpty()) {
                    lastMessage = session.getMessages().getLast().getContent();
                }

                preview = StrUtil.maxLength(lastMessage, SESSION_MESSAGE_PREVIEW_LENGTH);
            }
            result.add(SessionListItemVO.builder()
                    .sessionId(session.getSessionId())
                    .lastMessageSummary(preview)
                    .lastActiveTime(formatLocalDateTime(session.getLastActiveTime()))
                    .messageCount(session.getMessages().size())
                    .build());
        }
        return result;
    }

    @Override
    public SessionMessagesVO getSessionMessages(String sessionId, Integer offset, Integer limit) {
        Long userId = getCurrentUserId();
        ChatSession session = getAndValidateSession(sessionId, userId);

        int safeOffset = offset == null || offset < 0 ? 0 : offset;
        int safeLimit = limit == null || limit <= 0 ? 50 : limit;
        int total = session.getMessages().size();
        if (safeOffset >= total) {
            return SessionMessagesVO.builder()
                    .messages(new ArrayList<>())
                    .hasMore(false)
                    .total(total)
                    .build();
        }

        int end = Math.min(safeOffset + safeLimit, total);
        List<SessionMessageVO> messages = session.getMessages().subList(safeOffset, end).stream()
                .map(message -> SessionMessageVO.builder()
                        .messageId(message.getMessageId())
                        .role(message.getRole())
                        .content(message.getContent())
                        .timestamp(formatLocalDateTime(message.getTimestamp()))
                        .type(message.getType().getCode())
                        .build())
                .toList();

        return SessionMessagesVO.builder()
                .messages(messages)
                .hasMore(end < total)
                .total(total)
                .build();
    }

    @Override
    public reactor.core.publisher.Flux<String> streamSendMessage(ChatRequestDTO request) {
        validateUserId(request.getUserId());
        ChatSession session = getAndValidateSession(request.getSessionId(), request.getUserId());
        validateContextCount(request);

        synchronized (session) {
            if (session.isLocked()) {
                throw new BusinessException(429, "请稍候，当前会话正在处理其他请求");
            }
            session.setLocked(true);
        }

        String filteredMessage = filterInput(request.getMessage());
        // 构建包含历史对话的Prompt
        String prompt = buildPromptWithHistory(filteredMessage, request, session);
        StringBuilder assistantReply = new StringBuilder();

        saveMessage(request.getSessionId(), "user", filteredMessage, MessageTypeEnum.TEXT);

        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content()
                .doOnNext(assistantReply::append)
                .doOnComplete(() -> saveMessage(request.getSessionId(), "assistant",
                        assistantReply.toString(), MessageTypeEnum.TEXT))
                .doOnError(error -> {
                    log.error("AI流式调用失败: sessionId={}, error={}", request.getSessionId(), error.getMessage(), error);
                    saveMessage(request.getSessionId(), "assistant", "服务暂时不可用，请稍后重试", MessageTypeEnum.ERROR);
                })
                .doFinally(signal -> session.setLocked(false));
    }

    @Override
    public void deleteSession(String sessionId) {
        Long userId = getCurrentUserId();
        getAndValidateSession(sessionId, userId);
        sessionManager.deleteSession(sessionId);
    }

    @Override
    public int deleteAllUserSessions() {
        Long userId = getCurrentUserId();
        int count = sessionManager.deleteAllUserSessions(userId);
        log.info("清空用户所有会话: userId={}, 删除会话数={}", userId, count);
        return count;
    }

    @Override
    public QuizContextVO getQuizContextDetail(Long recordId) {
        Long userId = getCurrentUserId();
        QuizRecord record = quizRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException(404, "答题记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权访问该答题记录");
        }
        return buildQuizContext(record);
    }

    @Override
    public SalaryContextVO getSalaryContextDetail(Long reportId) {
        Long userId = getCurrentUserId();
        SalaryReport report = salaryReportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException(404, "薪资报告不存在");
        }
        if (!report.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权访问该薪资报告");
        }
        return buildSalaryContext(report);
    }

    private void validateContextCount(ChatRequestDTO request) {
        int quizSize = request.getQuizRecordIds() == null ? 0 : request.getQuizRecordIds().size();
        int salarySize = request.getSalaryReportIds() == null ? 0 : request.getSalaryReportIds().size();
        if (quizSize + salarySize > MAX_CONTEXT_ITEMS) {
            throw new BusinessException(400, "附加内容总数不能超过5条");
        }
    }

    private String filterInput(String input) {
        if (input.length() > 2000) {
            throw new BusinessException(400, "消息长度不能超过2000字符");
        }
        if (sensitiveWordFilter.containsSensitiveWord(input)) {
            throw new BusinessException(400, "包含不当内容，请修改后重试");
        }
        return HtmlUtil.escape(input);
    }

    private String buildPrompt(String userMessage, ChatRequestDTO request) {
        return buildPromptWithHistory(userMessage, request, null);
    }

    private String buildPromptWithHistory(String userMessage, ChatRequestDTO request, ChatSession session) {
        StringBuilder contextSection = new StringBuilder();

        // 添加历史对话上下文（最近10轮对话）
        if (session != null && !session.getMessages().isEmpty()) {
            contextSection.append("【历史对话】\n");
            List<ChatMessage> messages = session.getMessages();
            int startIndex = Math.max(0, messages.size() - 20); // 最多保留20条消息（10轮对话）
            for (int i = startIndex; i < messages.size(); i++) {
                ChatMessage msg = messages.get(i);
                String role = "user".equals(msg.getRole()) ? "用户" : "AI助手";
                contextSection.append(role).append(": ").append(msg.getContent()).append("\n");
            }
            contextSection.append("\n");
        }

        if (request.getQuizRecordIds() != null && !request.getQuizRecordIds().isEmpty()) {
            contextSection.append("【答题记录分析】\n");
            List<QuizRecord> records = quizRecordMapper.selectBatchIds(request.getQuizRecordIds());
            for (QuizRecord record : records) {
                if (record == null || !record.getUserId().equals(request.getUserId())) {
                    continue;
                }
                QuizContextVO quizContext = buildQuizContext(record);
                contextSection.append("- 题目: ").append(quizContext.getQuestionName()).append('\n')
                        .append("- 得分: ").append(quizContext.getScore()).append('\n')
                        .append("- 正确率: ").append(quizContext.getAccuracy()).append('\n')
                        .append("- 评价: ").append(StrUtil.maxLength(quizContext.getComment(), 120)).append('\n')
                        .append("- 建议: ").append(StrUtil.maxLength(quizContext.getSuggest(), 120)).append("\n\n");
            }
        }

        if (request.getSalaryReportIds() != null && !request.getSalaryReportIds().isEmpty()) {
            contextSection.append("【薪资报告分析】\n");
            List<SalaryReport> reports = salaryReportMapper.selectBatchIds(request.getSalaryReportIds());
            for (SalaryReport report : reports) {
                if (report == null || !report.getUserId().equals(request.getUserId())) {
                    continue;
                }
                SalaryContextVO salaryContext = buildSalaryContext(report);
                contextSection.append("- 方向: ").append(salaryContext.getDirection()).append('\n')
                        .append("- 城市: ").append(salaryContext.getCity()).append('\n')
                        .append("- 薪资范围: ").append(salaryContext.getSalaryRange()).append('\n')
                        .append("- 建议: ").append(StrUtil.maxLength(salaryContext.getAiSuggestion(), 150)).append("\n\n");
            }
        }

        String fullPrompt = """
                你是一位专业的AI教育助手，擅长分析用户的答题情况和职业规划。
                请结合上下文给出结构化、可执行的建议。
                
                **重要：请按照以下格式回复：**
                1. 首先在 ``` 代码块中展示你的思考过程和分析步骤
                2. 然后在代码块外给出最终的清晰回答和建议
                
                示例格式：
                ```
                思考过程：
                - 分析用户的问题...
                - 考虑相关因素...
                - 得出结论...
                ```
                
                这里是给用户的最终回答和建议...

                【上下文信息】
                %s

                【用户问题】
                %s
                """.formatted(contextSection, userMessage);

        return StrUtil.maxLength(fullPrompt, MAX_PROMPT_LENGTH);
    }

    private QuizContextVO buildQuizContext(QuizRecord record) {
        Question question = questionMapper.selectById(record.getQuestionId());
        String accuracy = formatAccuracy(record.getAccuracy());
        return QuizContextVO.builder()
                .recordId(record.getId())
                .questionName(question == null ? "未知题目" : question.getQuestionName())
                .userOptions(record.getUserOptions())
                .userAnswer(record.getUserAnswer())
                .score(record.getScore())
                .accuracy(accuracy)
                .comment(record.getComment())
                .suggest(record.getSuggest())
                .reason(record.getReason())
                .analysis(record.getAnalysis())
                .createTime(formatDate(record.getCreateTime()))
                .build();
    }

    private SalaryContextVO buildSalaryContext(SalaryReport report) {
        return SalaryContextVO.builder()
                .reportId(report.getId())
                .direction(report.getDirection())
                .city(report.getCity())
                .experience(report.getExperience())
                .education("")
                .identity("")
                .salaryRange(report.getSalaryRange())
                .aiSuggestion(report.getAiSuggestion())
                .createTime(formatDate(report.getCreateTime()))
                .build();
    }

    private void saveMessage(String sessionId, String role, String content, MessageTypeEnum type) {
        ChatMessage message = ChatMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .role(role)
                .content(content)
                .timestamp(LocalDateTime.now())
                .type(type)
                .build();
        sessionManager.addMessage(sessionId, message);
    }

    private ChatSession getAndValidateSession(String sessionId, Long userId) {
        ChatSession session = sessionManager.getSession(sessionId);
        if (session == null) {
            throw new BusinessException(404, "会话不存在");
        }
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权访问该会话");
        }
        return session;
    }

    private void validateUserId(Long userId) {
        Long currentUserId = getCurrentUserId();
        if (!userId.equals(currentUserId)) {
            throw new BusinessException(403, "无权访问该用户数据");
        }
    }

    private Long getCurrentUserId() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "用户未登录");
        }
        return userId;
    }

    private String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toString().replace("T", " ");
    }

    private String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    private String formatAccuracy(BigDecimal accuracy) {
        if (accuracy == null) {
            return null;
        }
        return accuracy.stripTrailingZeros().toPlainString();
    }
}

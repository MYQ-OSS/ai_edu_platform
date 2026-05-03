package top.mayiqin.ai_edu_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import top.mayiqin.ai_edu_platform.constant.MessageConstant;
import top.mayiqin.ai_edu_platform.entity.dto.ChatRequestDTO;
import top.mayiqin.ai_edu_platform.entity.dto.CreateSessionDTO;
import top.mayiqin.ai_edu_platform.entity.vo.QuizContextVO;
import top.mayiqin.ai_edu_platform.entity.vo.SalaryContextVO;
import top.mayiqin.ai_edu_platform.entity.vo.SessionInfoVO;
import top.mayiqin.ai_edu_platform.entity.vo.SessionListItemVO;
import top.mayiqin.ai_edu_platform.entity.vo.SessionMessagesVO;
import top.mayiqin.ai_edu_platform.exception.Result;
import top.mayiqin.ai_edu_platform.service.ChatAssistantService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * AI对话助手控制器
 * 提供会话管理、上下文详情及流式聊天接口
 *
 * @author m'y'q
 */
@Slf4j
@RestController
@RequestMapping("/chat")
@Tag(name = "AI对话助手", description = "提供AI智能对话服务")
public class ChatAssistantController {

    private final ChatAssistantService chatAssistantService;

    public ChatAssistantController(ChatAssistantService chatAssistantService) {
        this.chatAssistantService = chatAssistantService;
    }

    @Operation(summary = "创建新会话", description = "创建新会话并返回sessionId")
    @PostMapping("/session/create")
    public Result<SessionInfoVO> createSession(@Valid @RequestBody CreateSessionDTO request) {
        SessionInfoVO data = chatAssistantService.createSession(request.getUserId());
        return Result.success(MessageConstant.OPERATION_SUCCESS, data);
    }

    @Operation(summary = "获取会话列表", description = "获取用户活跃会话列表")
    @GetMapping("/session/list")
    public Result<List<SessionListItemVO>> listSession() {
        List<SessionListItemVO> data = chatAssistantService.listSession();
        return Result.success(MessageConstant.OPERATION_SUCCESS, data);
    }

    @Operation(summary = "获取会话消息", description = "获取指定会话的历史消息")
    @GetMapping("/session/{sessionId}/messages")
    public Result<SessionMessagesVO> getSessionMessages(@PathVariable String sessionId,
                                                        @RequestParam(required = false) Integer offset,
                                                        @RequestParam(required = false) Integer limit) {
        SessionMessagesVO data = chatAssistantService.getSessionMessages(sessionId, offset, limit);
        return Result.success(MessageConstant.OPERATION_SUCCESS, data);
    }

    @Operation(summary = "发送消息（流式）", description = "发送消息并通过SSE接收流式响应")
    @PostMapping(value = "/message/send", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamSendMessage(@Valid @RequestBody ChatRequestDTO request) {
        SseEmitter emitter = new SseEmitter(0L);

        chatAssistantService.streamSendMessage(request).subscribe(
                chunk -> sendEvent(emitter, "message", Map.of("chunk", chunk)),
                error -> {
                    sendEvent(emitter, "error", Map.of("msg", error.getMessage()));
                    emitter.completeWithError(error);
                },
                () -> {
                    sendEvent(emitter, "done", Map.of("complete", true));
                    emitter.complete();
                }
        );
        return emitter;
    }

    @Operation(summary = "删除会话", description = "删除指定会话")
    @DeleteMapping("/session/{sessionId}")
    public Result<Void> deleteSession(@PathVariable String sessionId) {
        chatAssistantService.deleteSession(sessionId);
        return Result.success("会话已删除", null);
    }

    @Operation(summary = "获取答题记录详情", description = "获取答题记录详细信息")
    @GetMapping("/context/quiz/{recordId}")
    public Result<QuizContextVO> getQuizContext(@PathVariable Long recordId) {
        QuizContextVO data = chatAssistantService.getQuizContextDetail(recordId);
        return Result.success(MessageConstant.OPERATION_SUCCESS, data);
    }

    @Operation(summary = "获取薪资报告详情", description = "获取薪资报告详细信息")
    @GetMapping("/context/salary/{reportId}")
    public Result<SalaryContextVO> getSalaryContext(@PathVariable Long reportId) {
        SalaryContextVO data = chatAssistantService.getSalaryContextDetail(reportId);
        return Result.success(MessageConstant.OPERATION_SUCCESS, data);
    }

    private void sendEvent(SseEmitter emitter, String event, Object data) {
        try {
            emitter.send(SseEmitter.event().name(event).data(data));
        } catch (IOException e) {
            log.warn("SSE发送失败: event={}, error={}", event, e.getMessage());
            emitter.completeWithError(e);
        }
    }
}

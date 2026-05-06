package top.mayiqin.ai_edu_platform.service;

import reactor.core.publisher.Flux;
import top.mayiqin.ai_edu_platform.entity.dto.ChatRequestDTO;
import top.mayiqin.ai_edu_platform.entity.vo.QuizContextVO;
import top.mayiqin.ai_edu_platform.entity.vo.SalaryContextVO;
import top.mayiqin.ai_edu_platform.entity.vo.SessionInfoVO;
import top.mayiqin.ai_edu_platform.entity.vo.SessionListItemVO;
import top.mayiqin.ai_edu_platform.entity.vo.SessionMessagesVO;

import java.util.List;

/**
 * AI对话助手服务接口
 *
 * @author m'y'q
 */
public interface ChatAssistantService {

    SessionInfoVO createSession(Long userId);

    List<SessionListItemVO> listSession();

    SessionMessagesVO getSessionMessages(String sessionId, Integer offset, Integer limit);

    Flux<String> streamSendMessage(ChatRequestDTO request);

    void deleteSession(String sessionId);

    int deleteAllUserSessions();

    QuizContextVO getQuizContextDetail(Long recordId);

    SalaryContextVO getSalaryContextDetail(Long reportId);
}

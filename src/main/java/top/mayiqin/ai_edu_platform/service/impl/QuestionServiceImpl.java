package top.mayiqin.ai_edu_platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import top.mayiqin.ai_edu_platform.ai.tool.QuestionGenerateTool;
import top.mayiqin.ai_edu_platform.entity.dto.QuestionGenerateDTO;
import top.mayiqin.ai_edu_platform.entity.po.Question;
import top.mayiqin.ai_edu_platform.constant.MessageConstant;
import top.mayiqin.ai_edu_platform.exception.BusinessException;
import top.mayiqin.ai_edu_platform.mapper.QuestionMapper;
import top.mayiqin.ai_edu_platform.service.QuestionService;
import top.mayiqin.ai_edu_platform.entity.vo.QuestionVO;

import java.util.Date;

/**
 * 题目服务实现类
 * 使用 Spring AI 调用大语言模型生成面试题目
 * @author m'y'q
 * @description 针对表【t_question(AI生成题库表)】的数据库操作Service实现
 * @createDate 2026-03-31 20:55:08
 */
@Service
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private final ChatClient chatClient;
    private final QuestionGenerateTool questionGenerateTool;

    public QuestionServiceImpl(ChatClient chatClient, QuestionGenerateTool questionGenerateTool) {
        this.chatClient = chatClient;
        this.questionGenerateTool = questionGenerateTool;
    }

    @Override
    public QuestionVO generateQuestion(QuestionGenerateDTO request) {
        // 参数校验
        if (request == null) {
            throw new BusinessException(400, MessageConstant.REQUEST_PARAM_EMPTY);
        }
        
        try {
            // 调用 AI 生成题目
            String aiJson = callAiGenerateQuestion(request);
            
            // 使用 Tool 验证并解析 AI 返回的 JSON
            JsonNode root = questionGenerateTool.validateAndParseJson(aiJson);
            String questionName = root.get("questionName").asText();
            String questionDesc = root.get("questionDesc").asText();
            JsonNode optionsNode = root.get("options");
            String options = questionGenerateTool.getObjectMapper().writeValueAsString(optionsNode);

            // 构建题目实体并保存到数据库
            Question question = Question.builder()
                    .questionName(questionName)
                    .questionDesc(questionDesc)
                    .options(options)
                    .targetSalary(request.getTargetSalary())
                    .direction(request.getDirection())
                    .createTime(new Date())
                    .build();

            boolean saved = this.save(question);
            if (!saved || question.getId() == null) {
                log.error("题目保存失败: {}", question);
                throw new BusinessException(500, MessageConstant.SAVE_QUESTION_FAILED);
            }

            log.info("题目生成并保存成功: questionId={}, questionName={}", question.getId(), question.getQuestionName());

            // 返回 VO
            return QuestionVO.builder()
                    .questionId(question.getId())
                    .questionName(question.getQuestionName())
                    .questionDesc(question.getQuestionDesc())
                    .options(question.getOptions())
                    .targetSalary(question.getTargetSalary())
                    .direction(question.getDirection())
                    .build();
        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            log.error("题目生成失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "题目生成失败: " + e.getMessage());
        }
    }

    /**
     * 调用 Spring AI ChatClient 生成题目
     *
     * @param request 题目生成请求参数
     * @return AI 返回的 JSON 字符串
     */
    private String callAiGenerateQuestion(QuestionGenerateDTO request) {
        // 使用 Tool 构建 Prompt
        String prompt = questionGenerateTool.generateQuestion(
                request.getDirection(),
                request.getTargetSalary(),
                request.getIdentity(),
                request.getCity(),
                request.getTimeLimit()
        );
        
        log.trace("调用AI生成题目，prompt: {}", prompt);

        try {
            // 使用 Spring AI ChatClient 调用大模型
            log.debug("开始调用 AI 接口...");
            String result = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            if (result == null || result.isBlank()) {
                log.error("AI返回内容为空");
                throw new BusinessException(500, MessageConstant.AI_RESPONSE_EMPTY);
            }

            log.debug("AI返回内容长度: {}", result.length());

            return result;
        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (org.springframework.ai.retry.NonTransientAiException e) {
            // AI 服务非临时性异常（如 404、401、403 等）
            log.error("AI 服务调用失败: {}", e.getMessage());
            log.error("请检查：1) API Key 是否有效 2) 网络连接是否正常 3) 模型名称是否正确");
            log.debug("详细异常信息", e);
            throw new BusinessException(500, MessageConstant.AI_SERVICE_CALL_FAILED);
        } catch (Exception e) {
            log.error("调用AI接口失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "调用AI接口失败: " + e.getMessage());
        }
    }


}

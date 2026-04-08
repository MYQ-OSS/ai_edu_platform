package top.mayiqin.ai_edu_platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.mayiqin.ai_edu_platform.dto.question.QuestionGenerateDTO;
import top.mayiqin.ai_edu_platform.entity.po.Question;
import top.mayiqin.ai_edu_platform.exception.BusinessException;
import top.mayiqin.ai_edu_platform.mapper.QuestionMapper;
import top.mayiqin.ai_edu_platform.service.QuestionService;
import top.mayiqin.ai_edu_platform.vo.QuestionVO;

import java.util.Date;
import java.util.Objects;

/**
* @author m'y'q
* @description 针对表【t_question(AI生成题库表)】的数据库操作Service实现
* @createDate 2026-03-31 20:55:08
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Value("${ai.dashscope.api-key}")
    private String apiKey;

    @Value("${ai.dashscope.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String baseUrl;

    @Value("${ai.dashscope.model-name:qwen-max}")
    private String modelName;

    @Value("${ai.dashscope.temperature:0.3}")
    private Double temperature;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public QuestionVO generateQuestion(QuestionGenerateDTO request) {
        try {
            String aiJson = callAiGenerateQuestion(request);
            JsonNode root = objectMapper.readTree(aiJson);
            String questionName = requireText(root, "questionName");
            String questionDesc = requireText(root, "questionDesc");
            JsonNode optionsNode = root.get("options");
            if (optionsNode == null || !optionsNode.isArray() || optionsNode.isEmpty()) {
                throw new BusinessException(500, "AI接口调用失败");
            }
            String options = objectMapper.writeValueAsString(optionsNode);

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
                throw new BusinessException(500, "AI接口调用失败");
            }

            return QuestionVO.builder()
                    .questionId(question.getId())
                    .questionName(question.getQuestionName())
                    .questionDesc(question.getQuestionDesc())
                    .options(question.getOptions())
                    .targetSalary(question.getTargetSalary())
                    .direction(question.getDirection())
                    .build();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(500, "AI接口调用失败");
        }
    }

    private String callAiGenerateQuestion(QuestionGenerateDTO request) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessException(500, "AI接口调用失败：api-key未配置");
        }

        String prompt = buildPrompt(request);
        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(prompt)
                .model(modelName)
                .temperature(temperature)
                .build();

        ChatCompletion completion = client.chat().completions().create(params);
        if (completion.choices() == null || completion.choices().isEmpty()) {
            throw new BusinessException(500, "AI接口调用失败");
        }

        String content = completion.choices().getFirst().message().content().orElse(null);
        if (content == null || content.isBlank()) {
            throw new BusinessException(500, "AI接口调用失败");
        }
        return stripCodeFence(content);
    }

    private String buildPrompt(QuestionGenerateDTO request) {
        String identity = Objects.requireNonNullElse(request.getIdentity(), "求职者");
        String city = Objects.requireNonNullElse(request.getCity(), "目标城市不限");
        Integer timeLimit = Objects.requireNonNullElse(request.getTimeLimit(), 30);
        return """
                你是资深技术面试官。请根据以下信息生成一道个性化技术挑战题。
                direction: %s
                targetSalary: %d
                identity: %s
                city: %s
                timeLimit: %d

                请仅返回JSON，不要包含任何额外文字，格式必须严格如下：
                {
                  "questionName": "题目名称",
                  "questionDesc": "题目描述",
                  "options": [
                    {"label": "技术栈1", "value": "stack1"},
                    {"label": "技术栈2", "value": "stack2"},
                    {"label": "技术栈3", "value": "stack3"}
                  ]
                }
                """.formatted(request.getDirection(), request.getTargetSalary(), identity, city, timeLimit);
    }

    private String stripCodeFence(String raw) {
        String trimmed = raw.trim();
        if (trimmed.startsWith("```")) {
            int start = trimmed.indexOf('\n');
            int end = trimmed.lastIndexOf("```");
            if (start >= 0 && end > start) {
                return trimmed.substring(start + 1, end).trim();
            }
        }
        return trimmed;
    }

    private String requireText(JsonNode root, String fieldName) {
        JsonNode node = root.get(fieldName);
        if (node == null || node.asText().isBlank()) {
            throw new BusinessException(500, "AI接口调用失败");
        }
        return node.asText();
    }
}

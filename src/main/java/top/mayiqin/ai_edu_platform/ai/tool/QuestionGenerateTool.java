package top.mayiqin.ai_edu_platform.ai.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import top.mayiqin.ai_edu_platform.constant.MessageConstant;
import top.mayiqin.ai_edu_platform.exception.BusinessException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 题目生成 AI Tool
 * 提供基于 Spring AI Function Calling 的题目生成功能
 * 可被 ChatClient 自动调用以生成个性化技术面试题目
 * 
 * @author m'y'q
 */
@Slf4j
@Component
public class QuestionGenerateTool {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String PROMPT_TEMPLATE_PATH = "prompt/question-generate-prompt.txt";
    private String promptTemplate;

    /**
     * 初始化时加载 Prompt 模板
     */
    public QuestionGenerateTool() {
        this.promptTemplate = loadPromptTemplate();
    }

    /**
     * 获取 ObjectMapper 实例
     *
     * @return ObjectMapper
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * 从 resources 目录加载 Prompt 模板文件
     *
     * @return Prompt 模板内容
     */
    private String loadPromptTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource(PROMPT_TEMPLATE_PATH);
            byte[] bytes = resource.getContentAsByteArray();
            String template = new String(bytes, StandardCharsets.UTF_8);
            log.info("成功加载 Prompt 模板: {}", PROMPT_TEMPLATE_PATH);
            return template;
        } catch (IOException e) {
            log.error("加载 Prompt 模板失败: {}", PROMPT_TEMPLATE_PATH, e);
            throw new BusinessException(500, MessageConstant.AI_SERVICE_INIT_FAILED);
        }
    }

    /**
     * 生成技术面试题目
     * 此方法可以被 Spring AI 的 Function Calling 机制自动调用
     *
     * @param direction    技术方向（如：Java后端、前端开发等）
     * @param targetSalary 目标薪资（单位：K）
     * @param identity     用户身份（如：求职者、学生等）
     * @param city         目标城市
     * @param timeLimit    答题时限（分钟）
     * @return JSON 格式的题目信息，包含 questionName、questionDesc 和 options
     */
    public String generateQuestion(String direction, int targetSalary, String identity, String city, int timeLimit) {
        try {
            log.info("AI Tool 被调用：生成题目 - direction={}, targetSalary={}, identity={}, city={}, timeLimit={}",
                    direction, targetSalary, identity, city, timeLimit);

            // 构建 Prompt
            String prompt = buildPrompt(direction, targetSalary, identity, city, timeLimit);
            
            log.debug("构建的 Prompt: {}", prompt);

            // 注意：这里不直接调用 ChatClient，而是由上层服务调用
            // 这个 Tool 主要作为 Function Definition 注册到 Spring AI
            // 实际调用逻辑在 QuestionServiceImpl 中
            
            return prompt;
        } catch (Exception e) {
            log.error("题目生成 Tool 执行失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "题目生成失败: " + e.getMessage());
        }
    }

    /**
     * 根据模板构建 AI Prompt
     *
     * @param direction    技术方向
     * @param targetSalary 目标薪资
     * @param identity     用户身份
     * @param city         目标城市
     * @param timeLimit    答题时限
     * @return 完整的 Prompt 文本
     */
    private String buildPrompt(String direction, int targetSalary, String identity, String city, int timeLimit) {
        String safeIdentity = Objects.requireNonNullElse(identity, "求职者");
        String safeCity = Objects.requireNonNullElse(city, "目标城市不限");
        
        // 使用模板替换占位符
        return promptTemplate
                .replace("{direction}", direction)
                .replace("{targetSalary}", String.valueOf(targetSalary))
                .replace("{identity}", safeIdentity)
                .replace("{city}", safeCity)
                .replace("{timeLimit}", String.valueOf(timeLimit));
    }

    /**
     * 验证并解析 AI 返回的 JSON
     *
     * @param aiJson AI 返回的 JSON 字符串
     * @return 解析后的 JsonNode
     */
    public JsonNode validateAndParseJson(String aiJson) {
        try {
            if (aiJson == null || aiJson.isBlank()) {
                log.error("AI返回内容为空");
                throw new BusinessException(500, MessageConstant.AI_RESPONSE_EMPTY);
            }

            // 去除可能的 Markdown 代码块标记
            String cleanedJson = stripCodeFence(aiJson);
            log.debug("清理后的JSON: {}", cleanedJson);

            JsonNode root = objectMapper.readTree(cleanedJson);
            
            // 验证必填字段
            validateRequiredFields(root);
            
            return root;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("JSON解析失败: {}", e.getMessage(), e);
            throw new BusinessException(500, MessageConstant.AI_RESPONSE_FORMAT_ERROR);
        }
    }

    /**
     * 验证必填字段
     *
     * @param root JSON 根节点
     */
    private void validateRequiredFields(JsonNode root) {
        String[] requiredFields = {"questionName", "questionDesc", "options"};
        
        for (String fieldName : requiredFields) {
            JsonNode node = root.get(fieldName);
            if (node == null) {
                log.error("AI返回的JSON缺少必填字段: {}", fieldName);
                throw new BusinessException(500, "AI生成的题目格式错误：缺少" + fieldName);
            }
        }
        
        // 验证 questionName 和 questionDesc 不为空
        if (root.get("questionName").asText().isBlank()) {
            throw new BusinessException(500, MessageConstant.AI_GENERATED_CONTENT_INVALID + "：questionName为空");
        }
        if (root.get("questionDesc").asText().isBlank()) {
            throw new BusinessException(500, MessageConstant.AI_GENERATED_CONTENT_INVALID + "：questionDesc为空");
        }
        
        // 验证 options 是数组且不为空
        JsonNode optionsNode = root.get("options");
        if (!optionsNode.isArray() || optionsNode.isEmpty()) {
            throw new BusinessException(500, MessageConstant.AI_GENERATED_CONTENT_INVALID + "：options必须是非空数组");
        }
    }

    /**
     * 去除 Markdown 代码块标记
     *
     * @param raw 原始文本
     * @return 清理后的文本
     */
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
}

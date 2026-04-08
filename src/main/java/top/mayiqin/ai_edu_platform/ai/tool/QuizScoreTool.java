package top.mayiqin.ai_edu_platform.ai.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import top.mayiqin.ai_edu_platform.exception.BusinessException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 答题判分 AI Tool
 * 提供基于 Spring AI Function Calling 的答题判分功能
 * 可被 ChatClient 自动调用以对用户答题进行AI评分和解析
 * 
 * @author m'y'q
 */
@Slf4j
@Component
public class QuizScoreTool {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String PROMPT_TEMPLATE_PATH = "prompt/quiz-score-prompt.txt";
    private String promptTemplate;

    /**
     * 初始化时加载 Prompt 模板
     */
    public QuizScoreTool() {
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
            log.info("成功加载判分 Prompt 模板: {}", PROMPT_TEMPLATE_PATH);
            return template;
        } catch (IOException e) {
            log.error("加载判分 Prompt 模板失败: {}", PROMPT_TEMPLATE_PATH, e);
            throw new BusinessException(500, "系统初始化失败：无法加载判分 Prompt 模板");
        }
    }

    /**
     * 对用户答题进行AI判分
     * 此方法可以被 Spring AI 的 Function Calling 机制自动调用
     *
     * @param questionName 题目名称
     * @param questionDesc 题目描述
     * @param options      题目选项（JSON格式）
     * @param trueOptions  正确选项（JSON格式，可为空由AI判断）
     * @param analysis     题目标准解析（可为空）
     * @param userOptions  用户选择的选项（JSON格式）
     * @param userAnswer   用户的文本回答
     * @return JSON 格式的判分结果，包含 score、comment、suggest、reason、trueOptions、analysis、accuracy
     */
    public String scoreQuiz(String questionName, String questionDesc, String options,
                           String trueOptions, String analysis,
                           String userOptions, String userAnswer) {
        try {
            log.info("AI Tool 被调用：答题判分 - questionName={}", questionName);

            // 构建 Prompt
            String prompt = buildPrompt(questionName, questionDesc, options, 
                                       trueOptions, analysis, userOptions, userAnswer);
            
            log.debug("构建的判分 Prompt: {}", prompt);

            // 注意：这里不直接调用 ChatClient，而是由上层服务调用
            // 这个 Tool 主要作为 Function Definition 注册到 Spring AI
            // 实际调用逻辑在 QuizRecordServiceImpl 中
            
            return prompt;
        } catch (Exception e) {
            log.error("答题判分 Tool 执行失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "AI判分失败: " + e.getMessage());
        }
    }

    /**
     * 根据模板构建 AI 判分 Prompt
     *
     * @param questionName 题目名称
     * @param questionDesc 题目描述
     * @param options      题目选项
     * @param trueOptions  正确选项
     * @param analysis     题目解析
     * @param userOptions  用户选项
     * @param userAnswer   用户答案
     * @return 完整的 Prompt 文本
     */
    private String buildPrompt(String questionName, String questionDesc, String options,
                              String trueOptions, String analysis,
                              String userOptions, String userAnswer) {
        String safeTrueOptions = Objects.requireNonNullElse(trueOptions, "");
        String safeAnalysis = Objects.requireNonNullElse(analysis, "");
        
        // 使用模板替换占位符
        return promptTemplate
                .replace("{questionName}", questionName)
                .replace("{questionDesc}", questionDesc)
                .replace("{options}", options)
                .replace("{trueOptions}", safeTrueOptions)
                .replace("{analysis}", safeAnalysis)
                .replace("{userOptions}", userOptions)
                .replace("{userAnswer}", userAnswer);
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
                throw new BusinessException(500, "AI返回内容为空");
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
            throw new BusinessException(500, "AI判分返回数据格式错误");
        }
    }

    /**
     * 验证必填字段
     *
     * @param root JSON 根节点
     */
    private void validateRequiredFields(JsonNode root) {
        String[] requiredFields = {"score", "comment", "suggest", "reason", "trueOptions", "analysis", "accuracy"};
        
        for (String fieldName : requiredFields) {
            JsonNode node = root.get(fieldName);
            if (node == null) {
                log.error("AI返回的JSON缺少必填字段: {}", fieldName);
                throw new BusinessException(500, "AI判分结果格式错误：缺少" + fieldName);
            }
        }
        
        // 验证 score 是数字
        if (!root.get("score").isNumber()) {
            throw new BusinessException(500, "AI判分结果格式错误：score必须是数字");
        }
        
        // 验证 accuracy 是数字
        if (!root.get("accuracy").isNumber()) {
            throw new BusinessException(500, "AI判分结果格式错误：accuracy必须是数字");
        }
        
        // 验证 trueOptions 是数组
        JsonNode trueOptionsNode = root.get("trueOptions");
        if (!trueOptionsNode.isArray()) {
            throw new BusinessException(500, "AI判分结果格式错误：trueOptions必须是数组");
        }
        
        // 验证文本字段不为空
        if (root.get("comment").asText().isBlank()) {
            throw new BusinessException(500, "AI判分结果格式错误：comment为空");
        }
        if (root.get("analysis").asText().isBlank()) {
            throw new BusinessException(500, "AI判分结果格式错误：analysis为空");
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

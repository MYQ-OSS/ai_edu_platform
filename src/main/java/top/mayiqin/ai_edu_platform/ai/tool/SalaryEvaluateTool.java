package top.mayiqin.ai_edu_platform.ai.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import top.mayiqin.ai_edu_platform.exception.BusinessException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * AI薪资评估工具类
 * 负责调用AI进行薪资分析和能力建议生成
 * @author m'y'q
 */
@Component
@Slf4j
public class SalaryEvaluateTool {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SalaryEvaluateTool(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 调用AI进行薪资评估
     * 
     * @param direction 技术方向
     * @param city 目标城市
     * @param experience 项目/工作经历
     * @param education 学历
     * @param identity 身份
     * @return AI返回的JSON字符串（包含salaryRange和aiSuggestion）
     */
    public String evaluateSalary(String direction, String city, String experience, String education, String identity) {
        log.info("开始构建AI薪资评估Prompt...");
        
        try {
            // a) 加载Prompt模板
            ClassPathResource resource = new ClassPathResource("prompt/salary-evaluate-prompt.txt");
            String promptTemplate = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            
            // b) 替换占位符
            String prompt = promptTemplate
                    .replace("{direction}", direction != null ? direction : "未提供")
                    .replace("{city}", city != null ? city : "未提供")
                    .replace("{experience}", experience != null ? experience : "未提供")
                    .replace("{education}", education != null ? education : "未提供")
                    .replace("{identity}", identity != null ? identity : "未提供");
            
            log.debug("构建的薪资评估Prompt: {}", prompt);
            
            // d) 调用ChatClient获取AI响应
            String aiResponse = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            
            // e) 验证AI返回结果不为空
            if (aiResponse == null || aiResponse.isBlank()) {
                log.error("AI返回内容为空");
                throw new BusinessException(500, "AI薪资评估失败：返回内容为空");
            }
            
            log.info("AI薪资评估完成，返回内容长度: {}", aiResponse.length());
            
            return aiResponse;
            
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            log.error("加载Prompt模板失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "加载Prompt模板失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("AI薪资评估异常: {}", e.getMessage(), e);
            throw new BusinessException(500, "AI薪资评估异常: " + e.getMessage());
        }
    }

    /**
     * 验证并解析AI返回的JSON
     * 
     * @param aiJson AI返回的JSON字符串
     * @return JsonNode 解析后的JSON节点
     * @throws BusinessException 当JSON格式错误或缺少必填字段时抛出异常
     */
    public JsonNode validateAndParseJson(String aiJson) {
        try {
            // a) 尝试解析JSON
            JsonNode jsonNode = objectMapper.readTree(aiJson);
            
            // b) 验证必填字段
            if (!jsonNode.has("salaryRange") || !jsonNode.has("aiSuggestion")) {
                log.error("AI返回JSON缺少必填字段: {}", aiJson);
                throw new BusinessException(500, "AI返回数据格式错误：缺少salaryRange或aiSuggestion字段");
            }
            
            if (jsonNode.get("salaryRange").asText().isEmpty() || jsonNode.get("aiSuggestion").asText().isEmpty()) {
                log.error("AI返回JSON字段值为空: {}", aiJson);
                throw new BusinessException(500, "AI返回数据格式错误：salaryRange或aiSuggestion为空");
            }
            
            log.debug("AI返回JSON验证通过: salaryRange={}", jsonNode.get("salaryRange").asText());
            
            return jsonNode;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("解析AI返回JSON失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "AI返回数据格式错误: " + e.getMessage());
        }
    }
}

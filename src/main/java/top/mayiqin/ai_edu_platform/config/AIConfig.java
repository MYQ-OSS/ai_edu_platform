package top.mayiqin.ai_edu_platform.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 配置类
 * 配置 Spring AI ChatClient Bean，用于调用大语言模型
 * <p>
 * 注意：Spring AI OpenAI Starter 会自动读取 application.yml 中的以下配置：
 * - spring.ai.openai.api-key: API 密钥
 * - spring.ai.openai.base-url: API 基础 URL
 * - spring.ai.openai.chat.options.model: 模型名称
 * - spring.ai.openai.chat.options.temperature: 温度参数
 * <p>
 * 本配置类用于自定义 ChatClient 的行为，如添加默认选项、拦截器等
 * 
 * @author m'y'q
 */
@Configuration
public class AIConfig {

    /**
     * 创建自定义 ChatClient Bean
     * <p>
     * Spring AI 会自动根据配置文件中的 spring.ai.openai.* 配置初始化 OpenAI 客户端，
     * 并注入 ChatClient.Builder。我们可以在这里添加额外的配置，如：
     * - 默认的聊天选项
     * - 请求/响应拦截器
     * - 日志记录
     * - 重试机制等
     * 
     * @param builder ChatClient.Builder，由 Spring AI 自动注入，已包含配置文件中的设置
     * @return 配置好的 ChatClient 实例
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        // 使用 Builder 构建 ChatClient
        // Builder 已经包含了 application.yml 中的配置（api-key, base-url, model, temperature 等）
        return builder.build();
    }
}

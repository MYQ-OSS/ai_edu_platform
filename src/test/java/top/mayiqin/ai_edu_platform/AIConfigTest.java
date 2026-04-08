package top.mayiqin.ai_edu_platform;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * AI 配置测试类
 * 用于验证 Spring AI 配置是否正确
 */
@SpringBootTest
public class AIConfigTest {

    @Autowired
    private ChatClient chatClient;

    @Test
    public void testChatClient() {
        try {
            String result = chatClient.prompt()
                    .user("你好")
                    .call()
                    .content();
            System.out.println("AI 响应: " + result);
        } catch (Exception e) {
            System.err.println("AI 调用失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

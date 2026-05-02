<template>
  <div class="chat-assistant">
    <!-- 头部 -->
    <div class="chat-assistant__header">
      <div class="chat-assistant__title">
        <span class="chat-assistant__icon">🤖</span>
        AI对话助手
      </div>
      <div class="chat-assistant__actions">
        <el-popconfirm
          title="退出后对话记录将永久丢失且无法恢复，确定要退出吗？"
          confirm-button-text="确定退出"
          cancel-button-text="取消"
          @confirm="handleExit"
        >
          <template #reference>
            <el-button type="danger" plain size="small">
              <el-icon><SwitchButton /></el-icon>
              退出聊天
            </el-button>
          </template>
        </el-popconfirm>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="chat-assistant__body">
      <!-- 左侧会话列表 -->
      <div class="chat-assistant__sidebar">
        <SessionList @new-session="handleNewSession" />
      </div>

      <!-- 右侧聊天区域 -->
      <div class="chat-assistant__main">
        <!-- 无会话提示 -->
        <div v-if="!chatStore.currentSessionId" class="chat-assistant__no-session">
          <div class="no-session-card">
            <div class="no-session-icon">💬</div>
            <h3>开始新对话</h3>
            <p>点击左侧「新对话」按钮开始与AI助手的对话</p>
            <el-button type="primary" @click="handleNewSession">
              <el-icon><Plus /></el-icon>
              新对话
            </el-button>
          </div>
        </div>

        <!-- 聊天内容 -->
        <div v-else class="chat-assistant__content">
          <MessageList
            ref="messageListRef"
            :messages="chatStore.currentMessages"
            :loading="sending"
          />

          <ChatInput
            ref="chatInputRef"
            :session-id="chatStore.currentSessionId"
            :sending="sending"
            :max-length="2000"
            @send="handleSendMessage"
            @add-quiz="showQuizSelector = true"
            @add-salary="showSalarySelector = true"
          />
        </div>
      </div>
    </div>

    <!-- 答题记录选择器 -->
    <ContextSelector
      v-model:visible="showQuizSelector"
      type="quiz"
      :user-id="userStore.userInfo?.id"
      :max-select="5"
      :existing-ids="chatStore.context.quizRecordIds"
      @confirm="handleContextConfirm"
    />

    <!-- 薪资报告选择器 -->
    <ContextSelector
      v-model:visible="showSalarySelector"
      type="salary"
      :user-id="userStore.userInfo?.id"
      :max-select="5"
      :existing-ids="chatStore.context.salaryReportIds"
      @confirm="handleContextConfirm"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import { SwitchButton, Plus } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useChatStore } from "../../store/chatStore";
import { useUserStore } from "../../store/userStore";
import SessionList from "../../components/business/SessionList.vue";
import MessageList from "../../components/business/MessageList.vue";
import ChatInput from "../../components/business/ChatInput.vue";
import ContextSelector from "../../components/business/ContextSelector.vue";

const router = useRouter();
const chatStore = useChatStore();
const userStore = useUserStore();

// 组件引用
const messageListRef = ref(null);
const chatInputRef = ref(null);

// 状态
const sending = ref(false);
const showQuizSelector = ref(false);
const showSalarySelector = ref(false);

// 重试次数
let retryCount = 0;
const maxRetries = 3;

// 初始化
onMounted(async () => {
  // 检查用户登录状态
  if (!userStore.userInfo?.id) {
    ElMessage.error("请先登录");
    router.push("/login");
    return;
  }

  // 加载会话列表
  try {
    await chatStore.fetchSessionList(userStore.userInfo.id);
  } catch (error) {
    console.error("加载会话列表失败:", error);
  }

  // 监听页面关闭事件
  window.addEventListener("beforeunload", handleBeforeUnload);
});

// 清理
onBeforeUnmount(() => {
  window.removeEventListener("beforeunload", handleBeforeUnload);
});

// 页面关闭前处理
const handleBeforeUnload = (e) => {
  if (chatStore.currentSessionId && chatStore.currentMessages.length > 0) {
    e.preventDefault();
    e.returnValue = "";
  }
};

// 创建新会话
const handleNewSession = async () => {
  try {
    await chatStore.createSession(userStore.userInfo.id);
    // 添加欢迎消息
    const welcomeMessage = {
      messageId: "system-welcome",
      role: "assistant",
      content:
        "你好！我是AI教育助手，可以帮助你：\n1. 📊 分析答题记录和薄弱环节\n2. 💰 解读薪资评估报告\n3. 📚 提供学习建议和职业规划\n\n你可以直接提问，或者点击下方按钮附加答题记录或薪资报告进行分析。",
      timestamp: new Date().toLocaleString(),
      type: "SYSTEM",
    };
    chatStore.addMessage(chatStore.currentSessionId, welcomeMessage);
  } catch (error) {
    ElMessage.error(error.message || "创建会话失败");
  }
};

// 发送消息
const handleSendMessage = async (messageText) => {
  if (!chatStore.currentSessionId) {
    ElMessage.warning("请先创建新对话");
    return;
  }

  if (!messageText.trim()) {
    ElMessage.warning("请输入消息内容");
    return;
  }

  sending.value = true;
  retryCount = 0;

  // 清空输入框
  if (chatInputRef.value) {
    chatInputRef.value.clearInput();
  }

  try {
    await streamSendMessage(messageText);
  } catch (error) {
    console.error("发送消息失败:", error);
    ElMessage.error(error.message || "发送消息失败，请稍后重试");
  } finally {
    sending.value = false;
  }
};

// 流式发送消息
const streamSendMessage = async (messageText) => {
  const sessionId = chatStore.currentSessionId;
  const userId = userStore.userInfo.id;

  // 添加用户消息
  const userMessage = {
    messageId: "user-" + Date.now(),
    role: "user",
    content: messageText,
    timestamp: new Date().toLocaleString(),
    type: "TEXT",
  };
  chatStore.addMessage(sessionId, userMessage);

  // 添加AI消息占位
  const aiMessage = {
    messageId: "ai-" + Date.now(),
    role: "assistant",
    content: "",
    timestamp: new Date().toLocaleString(),
    type: "TEXT",
  };
  chatStore.addMessage(sessionId, aiMessage);

  // 清空上下文（发送后）
  const currentContext = { ...chatStore.context };
  chatStore.clearContext();

  try {
    // 使用 fetch 进行 SSE 请求
    const token = localStorage.getItem("token");
    const response = await fetch("/api/chat/message/send", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: token ? `Bearer ${token}` : "",
      },
      body: JSON.stringify({
        sessionId: sessionId,
        userId: userId,
        message: messageText,
        quizRecordIds: currentContext.quizRecordIds,
        salaryReportIds: currentContext.salaryReportIds,
      }),
    });

    if (!response.ok) {
      throw new Error(`请求失败: ${response.status}`);
    }

    // 处理SSE流
    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let fullContent = "";

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;

      const chunk = decoder.decode(value, { stream: true });
      const lines = chunk.split("\n");

      for (const line of lines) {
        if (line.startsWith("data: ")) {
          try {
            const data = JSON.parse(line.substring(6));

            if (data.chunk) {
              fullContent += data.chunk;
              // 更新AI消息内容
              chatStore.updateLastAIMessage(sessionId, fullContent);
            }

            if (data.complete) {
              // 完成
              break;
            }
          } catch (e) {
            // 忽略解析错误
          }
        }
      }
    }

    // 更新最终消息
    if (fullContent) {
      const msgs = chatStore.messages[sessionId];
      if (msgs && msgs.length > 0) {
        const lastMsg = msgs[msgs.length - 1];
        if (lastMsg.role === "assistant") {
          lastMsg.content = fullContent;
        }
      }
    }
  } catch (error) {
    // 处理错误
    const msgs = chatStore.messages[sessionId];
    if (msgs && msgs.length > 0) {
      const lastMsg = msgs[msgs.length - 1];
      if (lastMsg.role === "assistant") {
        lastMsg.type = "ERROR";
        lastMsg.content = "抱歉，AI服务暂时不可用，请稍后重试。";
      }
    }

    // 自动重试
    if (retryCount < maxRetries) {
      retryCount++;
      ElMessage.warning(`连接中断，正在重试 (${retryCount}/${maxRetries})...`);
      await new Promise((resolve) => setTimeout(resolve, 2000));
      return streamSendMessage(messageText);
    }

    throw error;
  }
};

// 上下文确认
const handleContextConfirm = ({ type, ids }) => {
  if (type === "quiz") {
    ids.forEach((id) => {
      if (!chatStore.context.quizRecordIds.includes(id)) {
        chatStore.addQuizContext(id);
      }
    });
    ElMessage.success(`已添加 ${ids.length} 条答题记录`);
  } else {
    ids.forEach((id) => {
      if (!chatStore.context.salaryReportIds.includes(id)) {
        chatStore.addSalaryContext(id);
      }
    });
    ElMessage.success(`已添加 ${ids.length} 条薪资报告`);
  }
};

// 退出聊天
const handleExit = async () => {
  try {
    // 如果有当前会话，删除它
    if (chatStore.currentSessionId) {
      try {
        await chatStore.deleteSession(chatStore.currentSessionId);
      } catch (e) {
        console.warn("删除会话失败:", e);
      }
    }

    // 清空所有本地会话数据
    chatStore.clearAllSessions();

    // 跳转回主页
    router.push("/home");
  } catch (error) {
    console.error("退出聊天失败:", error);
    ElMessage.error("退出失败，请稍后重试");
  }
};
</script>

<style scoped>
.chat-assistant {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: linear-gradient(180deg, #0a0a1a 0%, #1a1a3a 100%);
}

.chat-assistant__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: rgba(20, 20, 40, 0.9);
  border-bottom: 1px solid rgba(0, 255, 255, 0.2);
}

.chat-assistant__title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 700;
  color: #00ffff;
  text-shadow: 0 0 20px rgba(0, 255, 255, 0.5);
}

.chat-assistant__icon {
  font-size: 28px;
}

.chat-assistant__actions .el-button {
  background: rgba(255, 0, 128, 0.1);
  border: 1px solid rgba(255, 0, 128, 0.3);
  color: #ff0080;
}

.chat-assistant__actions .el-button:hover {
  background: rgba(255, 0, 128, 0.2);
  border-color: rgba(255, 0, 128, 0.5);
}

.chat-assistant__body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.chat-assistant__sidebar {
  width: 280px;
  flex-shrink: 0;
  border-right: 1px solid rgba(0, 255, 255, 0.2);
}

.chat-assistant__main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-assistant__no-session {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.no-session-card {
  text-align: center;
  padding: 48px;
  border-radius: 16px;
  background: rgba(30, 30, 60, 0.5);
  border: 1px solid rgba(0, 255, 255, 0.2);
  max-width: 400px;
}

.no-session-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.no-session-card h3 {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 12px 0;
}

.no-session-card p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0 0 24px 0;
  line-height: 1.6;
}

.no-session-card .el-button {
  background: linear-gradient(135deg, #00ffff 0%, #b44aff 100%);
  border: none;
  font-weight: 600;
  padding: 12px 32px;
}

.chat-assistant__content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 统一按钮样式 - 使用渐变色 */
.chat-assistant .el-button--default,
.chat-assistant .el-button {
  background: linear-gradient(135deg, #00ffff 0%, #b44aff 100%) !important;
  border: none !important;
  color: #fff !important;
  font-weight: 600 !important;
}

.chat-assistant .el-button--default:hover,
.chat-assistant .el-button:hover:not(:disabled) {
  box-shadow: 0 0 20px rgba(0, 255, 255, 0.4) !important;
  transform: translateY(-2px) !important;
}

.chat-assistant .el-button--primary {
  background: linear-gradient(135deg, #00ffff 0%, #b44aff 100%) !important;
  border: none !important;
  color: #fff !important;
  font-weight: 600 !important;
}

.chat-assistant .el-button--primary:hover:not(:disabled) {
  box-shadow: 0 0 20px rgba(0, 255, 255, 0.4) !important;
  transform: translateY(-2px) !important;
}

/* 响应式 */
@media (max-width: 768px) {
  .chat-assistant__header {
    padding: 12px 16px;
  }

  .chat-assistant__title {
    font-size: 18px;
  }

  .chat-assistant__sidebar {
    width: 240px;
  }

  .no-session-card {
    padding: 32px 24px;
    margin: 0 16px;
  }

  .no-session-icon {
    font-size: 48px;
  }

  .no-session-card h3 {
    font-size: 20px;
  }
}
</style>

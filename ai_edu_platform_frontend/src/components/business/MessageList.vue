<template>
  <div class="message-list" ref="messageListRef">
    <div v-if="messages.length === 0" class="message-list__empty">
      <div class="welcome-card">
        <div class="welcome-icon">🤖</div>
        <h2 class="welcome-title">你好！我是AI教育助手</h2>
        <div class="welcome-desc">
          可以帮助你：<br />
          1. 📊 分析答题记录和薄弱环节<br />
          2. 💰 解读薪资评估报告<br />
          3. 📚 提供学习建议和职业规划<br /><br />
          你可以直接提问，或者点击下方按钮附加答题记录或薪资报告进行分析。
        </div>
      </div>
    </div>

    <div v-else class="message-list__content">
      <div
        v-for="(message, index) in messages"
        :key="message.messageId || index"
        class="message-item"
        :class="{
          'message-item--user': message.role === 'user',
          'message-item--ai': message.role === 'assistant',
          'message-item--system': message.type === 'SYSTEM',
        }"
      >
        <div class="message-item__avatar">
          <span v-if="message.role === 'user'">👤</span>
          <span v-else-if="message.role === 'assistant'">🤖</span>
          <span v-else>📢</span>
        </div>

        <div class="message-item__content">
          <div class="message-item__header">
            <span class="message-item__role">
              {{ message.role === "user" ? "你" : message.role === "assistant" ? "AI助手" : "系统" }}
            </span>
            <span class="message-item__time">{{ message.timestamp }}</span>
          </div>

          <div class="message-item__bubble">
            <div v-if="message.type === 'ERROR'" class="message-item__error">
              {{ message.content }}
            </div>
            <div v-else class="message-item__text">
              <!-- AI助手的消息支持Markdown渲染和折叠 -->
              <template v-if="message.role === 'assistant'">
                <div v-if="hasThinkingContent(message)" class="thinking-content">
                  <div class="thinking-header" @click="toggleCollapse(message.messageId)">
                    <span class="thinking-title">💭 思考过程</span>
                    <el-icon class="collapse-icon" :class="{ 'is-collapsed': isCollapsed(message.messageId) }">
                      <ArrowDown />
                    </el-icon>
                  </div>
                  <div 
                    v-show="!isCollapsed(message.messageId)"
                    class="thinking-body"
                    v-html="renderMarkdown(splitContent(message.content).thinking)"
                  ></div>
                </div>
                <!-- 回答内容单独显示 -->
                <div 
                  v-if="splitContent(message.content).answer" 
                  class="answer-content"
                  v-html="renderMarkdown(splitContent(message.content).answer)"
                ></div>
              </template>
              <!-- 用户消息纯文本显示 -->
              <template v-else>
                {{ message.content }}
              </template>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载中动画 -->
      <div v-if="loading" class="message-item message-item--ai">
        <div class="message-item__avatar">🤖</div>
        <div class="message-item__content">
          <div class="message-item__bubble">
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 回到底部按钮 -->
    <transition name="fade">
      <div
        v-if="showScrollBtn"
        class="scroll-to-bottom"
        @click="scrollToBottom"
      >
        <el-icon><ArrowDown /></el-icon>
        回到底部
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, watch, nextTick, onMounted, computed } from "vue";
import { ArrowDown } from "@element-plus/icons-vue";
import { marked } from "marked";
import DOMPurify from "dompurify";

const props = defineProps({
  messages: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
});

const messageListRef = ref(null);
const showScrollBtn = ref(false);
const userScrolled = ref(false);
const collapsedMessages = ref(new Set()); // 存储已折叠的消息ID

// Markdown渲染函数
const renderMarkdown = (content) => {
  if (!content) return "";
  const rawHtml = marked.parse(content);
  return DOMPurify.sanitize(rawHtml);
};

// 判断消息是否包含思考内容（以```或特定标记开头）
const hasThinkingContent = (message) => {
  if (message.role !== "assistant") return false;
  const content = message.content || "";
  // 检查是否包含代码块标记或思考过程标记
  return content.includes("```") || content.includes("思考过程") || content.includes("分析过程") || content.includes("###");
};

// 分离思考内容和回答内容
const splitContent = (content) => {
  if (!content) return { thinking: "", answer: content };
  
  // 尝试按代码块分割
  const codeBlockMatch = content.match(/```[\s\S]*?```/);
  if (codeBlockMatch) {
    const thinking = codeBlockMatch[0];
    const answer = content.replace(codeBlockMatch[0], "").trim();
    return { thinking, answer };
  }
  
  // 尝试按标题分割（如 ### 思考过程）
  const titleMatch = content.match(/(#{1,3}\s*(?:思考|分析|推理)[^\n]*\n[\s\S]*?)(?=\n#{1,3}|$)/);
  if (titleMatch) {
    const thinking = titleMatch[1].trim();
    const answer = content.replace(titleMatch[1], "").trim();
    return { thinking, answer };
  }
  
  // 如果没有找到明确的分隔，返回原内容
  return { thinking: "", answer: content };
};

// 切换折叠状态
const toggleCollapse = (messageId) => {
  if (collapsedMessages.value.has(messageId)) {
    collapsedMessages.value.delete(messageId);
  } else {
    collapsedMessages.value.add(messageId);
  }
  // 触发响应式更新
  collapsedMessages.value = new Set(collapsedMessages.value);
};

// 检查消息是否已折叠
const isCollapsed = (messageId) => {
  return collapsedMessages.value.has(messageId);
};

// 滚动到底部
const scrollToBottom = (smooth = true) => {
  if (messageListRef.value) {
    messageListRef.value.scrollTo({
      top: messageListRef.value.scrollHeight,
      behavior: smooth ? "smooth" : "auto",
    });
    userScrolled.value = false;
    showScrollBtn.value = false;
  }
};

// 检测是否需要显示回到底部按钮
const checkScroll = () => {
  if (messageListRef.value) {
    const { scrollTop, scrollHeight, clientHeight } = messageListRef.value;
    const distanceFromBottom = scrollHeight - scrollTop - clientHeight;
    showScrollBtn.value = distanceFromBottom > 100;
    userScrolled.value = distanceFromBottom > 100;
  }
};

// 监听消息变化，自动滚动
watch(
  () => props.messages.length,
  async () => {
    await nextTick();
    if (!userScrolled.value) {
      scrollToBottom();
    }
  }
);

// 监听加载状态变化
watch(
  () => props.loading,
  async (isLoading) => {
    if (!isLoading) {
      await nextTick();
      scrollToBottom();
    }
  }
);

onMounted(() => {
  if (messageListRef.value) {
    messageListRef.value.addEventListener("scroll", checkScroll);
  }
  scrollToBottom(false);
});

defineExpose({
  scrollToBottom,
});
</script>

<style scoped>
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  position: relative;
  background: linear-gradient(180deg, rgba(20, 20, 40, 0.9) 0%, rgba(30, 30, 60, 0.8) 100%);
}

.message-list__empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 20px;
}

.welcome-card {
  text-align: center;
  padding: 40px;
  border-radius: 16px;
  background: rgba(0, 255, 255, 0.05);
  border: 1px solid rgba(0, 255, 255, 0.2);
  max-width: 500px;
}

.welcome-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.welcome-title {
  font-size: 24px;
  font-weight: 700;
  color: #00ffff;
  margin: 0 0 16px 0;
  text-shadow: 0 0 20px rgba(0, 255, 255, 0.5);
}

.welcome-desc {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.8;
  text-align: left;
}

.message-list__content {
  max-width: 800px;
  margin: 0 auto;
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-item--user {
  flex-direction: row-reverse;
}

.message-item__avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
  background: rgba(0, 255, 255, 0.2);
  border: 1px solid rgba(0, 255, 255, 0.3);
}

.message-item--user .message-item__avatar {
  background: rgba(180, 74, 255, 0.2);
  border-color: rgba(180, 74, 255, 0.3);
}

.message-item__content {
  max-width: 60%;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.message-item--user .message-item__content {
  align-items: flex-end;
}

.message-item__header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.message-item--user .message-item__header {
  flex-direction: row-reverse;
}

.message-item__role {
  font-weight: 600;
  color: rgba(255, 255, 255, 0.8);
}

.message-item--user .message-item__role {
  color: #b44aff;
}

.message-item--ai .message-item__role {
  color: #00ffff;
}

.message-item__time {
  color: rgba(255, 255, 255, 0.4);
  font-size: 11px;
}

.message-item__bubble {
  padding: 8px 12px;
  border-radius: 16px;
  font-size: 15px;
  line-height: 1.6;
  word-break: break-word;
}

.message-item--ai .message-item__bubble {
  background: linear-gradient(135deg, rgba(0, 255, 255, 0.1) 0%, rgba(180, 74, 255, 0.1) 100%);
  border: 1px solid rgba(0, 255, 255, 0.3);
  border-radius: 4px 16px 16px 16px;
  color: #fff;
}

.message-item--user .message-item__bubble {
  background: linear-gradient(135deg, rgba(180, 74, 255, 0.3) 0%, rgba(180, 74, 255, 0.5) 100%);
  border: 1px solid rgba(180, 74, 255, 0.5);
  border-radius: 16px 4px 16px 16px;
  color: #fff;
}

.message-item--system .message-item__bubble {
  background: rgba(255, 200, 0, 0.1);
  border: 1px solid rgba(255, 200, 0, 0.3);
  color: #ffc800;
  font-size: 13px;
}

.message-item__error {
  color: #ff6b6b;
}

.message-item__text {
  white-space: pre-wrap;
}

/* Markdown内容样式 - 保持原始格式 */
.message-item__text :deep(h1),
.message-item__text :deep(h2),
.message-item__text :deep(h3),
.message-item__text :deep(h4),
.message-item__text :deep(h5),
.message-item__text :deep(h6) {
  margin: 0.6em 0 0.3em 0;
  font-weight: 600;
  line-height: 1.3;
}

.message-item__text :deep(p) {
  margin: 0.3em 0;
  line-height: 1.5;
}

.message-item__text :deep(ul),
.message-item__text :deep(ol) {
  margin: 0.3em 0;
  padding-left: 1.5em;
}

.message-item__text :deep(li) {
  margin: 0.15em 0;
}

.message-item__text :deep(code) {
  background: rgba(0, 255, 255, 0.1);
  padding: 0.15em 0.3em;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
  font-size: 0.9em;
}

.message-item__text :deep(pre) {
  background: rgba(0, 0, 0, 0.3);
  padding: 0.8em;
  border-radius: 6px;
  overflow-x: auto;
  margin: 0.6em 0;
  border: 1px solid rgba(0, 255, 255, 0.2);
}

.message-item__text :deep(pre code) {
  background: transparent;
  padding: 0;
  color: inherit;
}

.message-item__text :deep(blockquote) {
  border-left: 4px solid rgba(0, 255, 255, 0.5);
  padding-left: 0.8em;
  margin: 0.6em 0;
  color: rgba(255, 255, 255, 0.7);
  font-style: italic;
}

.message-item__text :deep(table) {
  border-collapse: collapse;
  width: 100%;
  margin: 0.6em 0;
}

.message-item__text :deep(th),
.message-item__text :deep(td) {
  border: 1px solid rgba(0, 255, 255, 0.3);
  padding: 0.4em 0.6em;
  text-align: left;
}

.message-item__text :deep(th) {
  background: rgba(0, 255, 255, 0.1);
  font-weight: 600;
}

/* 思考内容折叠样式 */
.thinking-content {
  margin-top: 6px;
}

.thinking-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 10px;
  background: rgba(180, 74, 255, 0.15);
  border: 1px solid rgba(180, 74, 255, 0.3);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  user-select: none;
}

.thinking-header:hover {
  background: rgba(180, 74, 255, 0.25);
  border-color: rgba(180, 74, 255, 0.5);
}

.thinking-title {
  font-size: 13px;
  font-weight: 600;
  color: #b44aff;
}

.collapse-icon {
  transition: transform 0.3s ease;
  color: #b44aff;
}

.collapse-icon.is-collapsed {
  transform: rotate(-90deg);
}

.thinking-body {
  margin-top: 6px;
  padding: 10px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 8px;
  border: 1px solid rgba(180, 74, 255, 0.2);
}

/* 回答内容样式 */
.answer-content {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid rgba(0, 255, 255, 0.2);
}

/* 打字机动画 */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 4px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #00ffff;
  animation: typing 1.4s infinite ease-in-out both;
}

.typing-indicator span:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes typing {
  0%,
  80%,
  100% {
    transform: scale(0);
    opacity: 0.4;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 回到底部按钮 */
.scroll-to-bottom {
  position: absolute;
  bottom: 20px;
  right: 30px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #b44aff 0%, #00ffff 100%);
  border-radius: 20px;
  color: #fff;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(180, 74, 255, 0.4);
  transition: all 0.3s ease;
}

.scroll-to-bottom:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(180, 74, 255, 0.5);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 自定义滚动条 */
.message-list::-webkit-scrollbar {
  width: 6px;
}

.message-list::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
}

.message-list::-webkit-scrollbar-thumb {
  background: rgba(0, 255, 255, 0.3);
  border-radius: 3px;
}

.message-list::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 255, 255, 0.5);
}

/* 响应式 */
@media (max-width: 768px) {
  .message-item__content {
    max-width: 85%;
  }

  .welcome-card {
    padding: 24px;
    margin: 0 16px;
  }

  .welcome-title {
    font-size: 20px;
  }
}
</style>

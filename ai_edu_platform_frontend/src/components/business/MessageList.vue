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
              {{ message.content }}
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
import { ref, watch, nextTick, onMounted } from "vue";
import { ArrowDown } from "@element-plus/icons-vue";

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
  max-width: 70%;
  display: flex;
  flex-direction: column;
  gap: 4px;
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
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 14px;
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
  left: 50%;
  transform: translateX(-50%);
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
  transform: translateX(-50%) translateY(-2px);
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

<template>
  <div class="session-list">
    <div class="session-list__header">
      <el-button type="primary" class="new-session-btn" @click="handleNewSession">
        <el-icon><Plus /></el-icon>
        新对话
      </el-button>
    </div>

    <div class="session-list__content" v-loading="chatStore.loading">
      <div v-if="chatStore.sessions.length === 0" class="session-list__empty">
        <el-empty description="暂无会话记录" :image-size="60" />
      </div>

      <div
        v-for="session in chatStore.sessions"
        :key="session.sessionId"
        class="session-item"
        :class="{ 'session-item--active': session.sessionId === chatStore.currentSessionId }"
        @click="handleSelectSession(session)"
      >
        <div class="session-item__content">
          <div class="session-item__title">
            <span class="session-item__icon">💬</span>
            会话 {{ session.sessionId?.substring(0, 8) || "新会话" }}
          </div>
          <div class="session-item__summary">
            {{ session.lastMessageSummary || "暂无消息" }}
          </div>
          <div class="session-item__time">
            {{ formatTime(session.lastActiveTime) }}
          </div>
        </div>
        <div class="session-item__actions">
          <el-popconfirm
            title="确定要删除此会话吗？"
            confirm-button-text="确定"
            cancel-button-text="取消"
            @confirm="handleDeleteSession(session)"
          >
            <template #reference>
              <el-button type="danger" size="small" circle>
                <el-icon><Delete /></el-icon>
              </el-button>
            </template>
          </el-popconfirm>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { Plus, Delete } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { useChatStore } from "../../store/chatStore";
import { useUserStore } from "../../store/userStore";

const chatStore = useChatStore();
const userStore = useUserStore();

const emit = defineEmits(["new-session"]);

// 创建新会话
const handleNewSession = async () => {
  try {
    const userId = userStore.userInfo?.id;
    if (!userId) {
      ElMessage.error("用户未登录");
      return;
    }

    // 创建会话并自动添加欢迎消息
    await chatStore.createSession(userId, true);
    emit("new-session");
  } catch (error) {
    ElMessage.error(error.message || "创建会话失败");
  }
};

// 选择会话
const handleSelectSession = async (session) => {
  chatStore.setCurrentSession(session.sessionId);
  // 如果消息未加载，则加载消息
  const msgs = chatStore.messages[session.sessionId];
  if (!msgs || msgs.length === 0) {
    try {
      await chatStore.fetchSessionMessages(session.sessionId);
    } catch (error) {
      console.error("加载消息失败:", error);
    }
  }
};

// 删除会话
const handleDeleteSession = async (session) => {
  try {
    await chatStore.deleteSession(session.sessionId);
    ElMessage.success("会话已删除");
  } catch (error) {
    ElMessage.error(error.message || "删除会话失败");
  }
};

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return "";

  const time = new Date(timeStr);
  const now = new Date();
  const diff = now - time;
  const minutes = Math.floor(diff / (1000 * 60));
  const hours = Math.floor(diff / (1000 * 60 * 60));
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));

  if (minutes < 1) return "刚刚";
  if (minutes < 60) return `${minutes}分钟前`;
  if (hours < 24) return `${hours}小时前`;
  if (days < 7) return `${days}天前`;

  return time.toLocaleDateString();
};
</script>

<style scoped>
.session-list {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: rgba(20, 20, 40, 0.8);
  border-right: 1px solid rgba(0, 255, 255, 0.2);
}

.session-list__header {
  padding: 16px;
  border-bottom: 1px solid rgba(0, 255, 255, 0.1);
}

.new-session-btn {
  width: 100%;
  background: linear-gradient(135deg, #00ffff 0%, #b44aff 100%);
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
  color: #fff !important;
}

.new-session-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 255, 255, 0.4);
}

/* 删除按钮保持danger样式 */
.session-item__actions .el-button--danger {
  background: rgba(255, 0, 128, 0.1) !important;
  border: 1px solid rgba(255, 0, 128, 0.3) !important;
  color: #ff0080 !important;
}

.session-item__actions .el-button--danger:hover {
  background: rgba(255, 0, 128, 0.3) !important;
  border-color: rgba(255, 0, 128, 0.5) !important;
  box-shadow: 0 0 10px rgba(255, 0, 128, 0.3) !important;
}

.session-list__content {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.session-list__empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
}

.session-item {
  display: flex;
  align-items: center;
  padding: 12px;
  margin-bottom: 8px;
  border-radius: 8px;
  background: rgba(30, 30, 60, 0.5);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.session-item:hover {
  background: rgba(0, 255, 255, 0.1);
  border-color: rgba(0, 255, 255, 0.3);
}

.session-item--active {
  background: rgba(180, 74, 255, 0.2);
  border-color: #b44aff;
}

.session-item__content {
  flex: 1;
  overflow: hidden;
}

.session-item__title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 4px;
}

.session-item__icon {
  font-size: 16px;
}

.session-item__summary {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}

.session-item__time {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.4);
}

.session-item__actions {
  opacity: 0;
  transition: opacity 0.3s ease;
}

.session-item:hover .session-item__actions {
  opacity: 1;
}

/* 自定义滚动条 */
.session-list__content::-webkit-scrollbar {
  width: 4px;
}

.session-list__content::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
}

.session-list__content::-webkit-scrollbar-thumb {
  background: rgba(0, 255, 255, 0.3);
  border-radius: 2px;
}

.session-list__content::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 255, 255, 0.5);
}
</style>

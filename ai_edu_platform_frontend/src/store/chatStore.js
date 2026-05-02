import { defineStore } from "pinia";
import { ref, computed } from "vue";
import * as chatApi from "../api/chatApi";

export const useChatStore = defineStore("chat", () => {
  // State
  const currentSessionId = ref(null);
  const sessions = ref([]);
  const messages = ref({}); // { sessionId: [messages] }
  const loading = ref(false);
  const sending = ref(false);
  const error = ref(null);

  // Getters
  const currentMessages = computed(() => {
    return currentSessionId.value
      ? messages.value[currentSessionId.value] || []
      : [];
  });

  const currentSession = computed(() => {
    return sessions.value.find((s) => s.sessionId === currentSessionId.value);
  });

  const hasContext = computed(() => {
    const ctx = context.value;
    return ctx.quizRecordIds?.length > 0 || ctx.salaryReportIds?.length > 0;
  });

  // 上下文附加内容
  const context = ref({
    quizRecordIds: [],
    salaryReportIds: [],
  });

  // Actions

  /**
   * 创建新会话
   * @param {number} userId - 用户ID
   */
  async function createSession(userId) {
    loading.value = true;
    error.value = null;
    try {
      const response = await chatApi.createSession({ userId });
      if (response.code === 200) {
        const session = {
          sessionId: response.data.sessionId,
          createTime: response.data.createTime,
          lastMessageSummary: "",
          lastActiveTime: response.data.createTime,
          messageCount: 0,
        };
        sessions.value.unshift(session);
        messages.value[session.sessionId] = [];
        setCurrentSession(session.sessionId);
        return session;
      } else {
        throw new Error(response.msg || "创建会话失败");
      }
    } catch (e) {
      error.value = e.message;
      throw e;
    } finally {
      loading.value = false;
    }
  }

  /**
   * 获取会话列表
   * @param {number} userId - 用户ID
   */
  async function fetchSessionList(userId) {
    loading.value = true;
    error.value = null;
    try {
      const response = await chatApi.getSessionList(userId);
      if (response.code === 200) {
        sessions.value = response.data || [];
        // 初始化消息缓存
        sessions.value.forEach((session) => {
          if (!messages.value[session.sessionId]) {
            messages.value[session.sessionId] = [];
          }
        });
      } else {
        throw new Error(response.msg || "获取会话列表失败");
      }
    } catch (e) {
      error.value = e.message;
      console.error("获取会话列表失败:", e);
    } finally {
      loading.value = false;
    }
  }

  /**
   * 切换当前会话
   * @param {string} sessionId - 会话ID
   */
  function setCurrentSession(sessionId) {
    currentSessionId.value = sessionId;
    // 确保当前会话的消息已初始化
    if (!messages.value[sessionId]) {
      messages.value[sessionId] = [];
    }
  }

  /**
   * 获取会话消息
   * @param {string} sessionId - 会话ID
   * @param {number} offset - 偏移量
   * @param {number} limit - 限制数量
   */
  async function fetchSessionMessages(sessionId, offset = 0, limit = 50) {
    try {
      const response = await chatApi.getSessionMessages(sessionId, {
        offset,
        limit,
      });
      if (response.code === 200) {
        const loadedMessages = response.data.messages || [];
        if (offset === 0) {
          messages.value[sessionId] = loadedMessages;
        } else {
          // 向头部插入历史消息
          messages.value[sessionId] = [
            ...loadedMessages,
            ...(messages.value[sessionId] || []),
          ];
        }
        return {
          messages: loadedMessages,
          hasMore: response.data.hasMore,
          total: response.data.total,
        };
      } else {
        throw new Error(response.msg || "获取消息失败");
      }
    } catch (e) {
      console.error("获取消息失败:", e);
      throw e;
    }
  }

  /**
   * 添加消息到会话
   * @param {string} sessionId - 会话ID
   * @param {Object} message - 消息对象
   */
  function addMessage(sessionId, message) {
    if (!messages.value[sessionId]) {
      messages.value[sessionId] = [];
    }
    messages.value[sessionId].push(message);

    // 更新会话的最后消息摘要
    const session = sessions.value.find((s) => s.sessionId === sessionId);
    if (session) {
      session.lastMessageSummary =
        message.content?.substring(0, 20) ||
        message.chunk?.substring(0, 20) ||
        "";
      session.messageCount = messages.value[sessionId].length;
    }
  }

  /**
   * 更新最后一条AI消息（用于流式更新）
   * @param {string} sessionId - 会话ID
   * @param {string} content - 更新的内容
   */
  function updateLastAIMessage(sessionId, content) {
    const msgs = messages.value[sessionId];
    if (msgs && msgs.length > 0) {
      const lastMsg = msgs[msgs.length - 1];
      if (lastMsg.role === "assistant") {
        lastMsg.content = content;
      }
    }
  }

  /**
   * 删除会话
   * @param {string} sessionId - 会话ID
   */
  async function deleteSession(sessionId) {
    loading.value = true;
    error.value = null;
    try {
      const response = await chatApi.deleteSession(sessionId);
      if (response.code === 200) {
        clearSession(sessionId);
        return true;
      } else {
        throw new Error(response.msg || "删除会话失败");
      }
    } catch (e) {
      error.value = e.message;
      throw e;
    } finally {
      loading.value = false;
    }
  }

  /**
   * 清空本地会话数据
   * @param {string} sessionId - 会话ID
   */
  function clearSession(sessionId) {
    delete messages.value[sessionId];
    sessions.value = sessions.value.filter((s) => s.sessionId !== sessionId);
    if (currentSessionId.value === sessionId) {
      currentSessionId.value =
        sessions.value.length > 0 ? sessions.value[0].sessionId : null;
      if (!messages.value[currentSessionId.value]) {
        messages.value[currentSessionId.value] = [];
      }
    }
  }

  /**
   * 清空所有会话数据
   */
  function clearAllSessions() {
    currentSessionId.value = null;
    sessions.value = [];
    messages.value = {};
    clearContext();
  }

  /**
   * 添加答题记录到上下文
   * @param {number} recordId - 答题记录ID
   */
  function addQuizContext(recordId) {
    if (!context.value.quizRecordIds.includes(recordId)) {
      context.value.quizRecordIds.push(recordId);
    }
  }

  /**
   * 移除答题记录从上下文
   * @param {number} recordId - 答题记录ID
   */
  function removeQuizContext(recordId) {
    context.value.quizRecordIds = context.value.quizRecordIds.filter(
      (id) => id !== recordId,
    );
  }

  /**
   * 添加薪资报告到上下文
   * @param {number} reportId - 薪资报告ID
   */
  function addSalaryContext(reportId) {
    if (!context.value.salaryReportIds.includes(reportId)) {
      context.value.salaryReportIds.push(reportId);
    }
  }

  /**
   * 移除薪资报告从上下文
   * @param {number} reportId - 薪资报告ID
   */
  function removeSalaryContext(reportId) {
    context.value.salaryReportIds = context.value.salaryReportIds.filter(
      (id) => id !== reportId,
    );
  }

  /**
   * 清空上下文
   */
  function clearContext() {
    context.value = {
      quizRecordIds: [],
      salaryReportIds: [],
    };
  }

  /**
   * 发送消息（流式）
   * @param {Object} params - 发送参数
   * @param {string} params.sessionId - 会话ID
   * @param {number} params.userId - 用户ID
   * @param {string} params.message - 消息内容
   * @returns {Promise} 返回流式响应
   */
  async function sendMessage(params) {
    sending.value = true;
    error.value = null;

    try {
      // 添加用户消息
      const userMessage = {
        messageId: "user-" + Date.now(),
        role: "user",
        content: params.message,
        timestamp: new Date().toLocaleString(),
        type: "TEXT",
      };
      addMessage(params.sessionId, userMessage);

      // 调用API发送消息
      const response = await chatApi.sendStreamMessage({
        sessionId: params.sessionId,
        userId: params.userId,
        message: params.message,
        quizRecordIds: context.value.quizRecordIds,
        salaryReportIds: context.value.salaryReportIds,
      });

      return response;
    } catch (e) {
      error.value = e.message;
      throw e;
    } finally {
      sending.value = false;
    }
  }

  return {
    // State
    currentSessionId,
    sessions,
    messages,
    loading,
    sending,
    error,
    context,
    // Getters
    currentMessages,
    currentSession,
    hasContext,
    // Actions
    createSession,
    fetchSessionList,
    setCurrentSession,
    fetchSessionMessages,
    addMessage,
    updateLastAIMessage,
    deleteSession,
    clearSession,
    clearAllSessions,
    addQuizContext,
    removeQuizContext,
    addSalaryContext,
    removeSalaryContext,
    clearContext,
    sendMessage,
  };
});

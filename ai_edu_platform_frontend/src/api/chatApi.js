import request from "./request";

/**
 * 创建新会话
 * @param {Object} data - 请求数据
 * @param {number} data.userId - 用户ID
 * @returns {Promise} 返回会话信息
 */
export const createSession = (data) => {
  return request({
    url: "/chat/session/create",
    method: "POST",
    data,
  });
};

/**
 * 获取会话列表
 * @param {number} userId - 用户ID
 * @returns {Promise} 返回会话列表
 */
export const getSessionList = (userId) => {
  return request({
    url: "/chat/session/list",
    method: "GET",
    params: { userId },
  });
};

/**
 * 获取会话消息
 * @param {string} sessionId - 会话ID
 * @param {Object} params - 查询参数
 * @param {number} params.offset - 偏移量
 * @param {number} params.limit - 限制数量
 * @returns {Promise} 返回消息列表
 */
export const getSessionMessages = (sessionId, params = {}) => {
  return request({
    url: `/chat/session/${sessionId}/messages`,
    method: "GET",
    params,
  });
};

/**
 * 发送消息（流式）
 * @param {Object} data - 请求数据
 * @param {string} data.sessionId - 会话ID
 * @param {number} data.userId - 用户ID
 * @param {string} data.message - 消息内容
 * @param {number[]} data.quizRecordIds - 答题记录ID列表
 * @param {number[]} data.salaryReportIds - 薪资报告ID列表
 * @returns {Promise} 返回SSE流
 */
export const sendStreamMessage = (data) => {
  return request({
    url: "/chat/message/send",
    method: "POST",
    data,
    responseType: "stream",
  });
};

/**
 * 删除会话
 * @param {string} sessionId - 会话ID
 * @returns {Promise} 返回删除结果
 */
export const deleteSession = (sessionId) => {
  return request({
    url: `/chat/session/${sessionId}`,
    method: "DELETE",
  });
};

/**
 * 获取答题记录详情
 * @param {number} recordId - 答题记录ID
 * @returns {Promise} 返回答题记录详情
 */
export const getQuizContext = (recordId) => {
  return request({
    url: `/chat/context/quiz/${recordId}`,
    method: "GET",
  });
};

/**
 * 获取薪资报告详情
 * @param {number} reportId - 薪资报告ID
 * @returns {Promise} 返回薪资报告详情
 */
export const getSalaryContext = (reportId) => {
  return request({
    url: `/chat/context/salary/${reportId}`,
    method: "GET",
  });
};

/**
 * 获取用户的答题历史列表（用于上下文附加选择）
 * @param {number} userId - 用户ID
 * @returns {Promise} 返回答题历史列表
 */
export const getUserQuizHistory = (userId) => {
  return request({
    url: "/question/history",
    method: "GET",
    params: { userId },
  });
};

/**
 * 获取用户的薪资报告历史列表（用于上下文附加选择）
 * @param {number} userId - 用户ID
 * @returns {Promise} 返回薪资报告历史列表
 */
export const getUserSalaryHistory = (userId) => {
  return request({
    url: "/salary/history",
    method: "GET",
    params: { userId },
  });
};

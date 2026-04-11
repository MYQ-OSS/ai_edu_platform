// 后台管理接口
import request from './request'

/**
 * 查询用户列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码，默认1
 * @param {number} params.pageSize - 每页条数，默认10
 * @param {string} params.username - 用户名模糊查询（可选）
 * @param {string} params.status - 用户状态筛选（可选，0或1）
 * @param {string} params.role - 用户角色筛选（可选，user或admin）
 * @returns {Promise} 返回分页用户列表
 */
export function getUserList(params) {
  return request({
    url: '/admin/user/list',
    method: 'get',
    params
  })
}

/**
 * 禁用/启用用户
 * @param {number} userId - 用户ID
 * @param {string} status - 目标状态（0-正常 1-禁用）
 * @returns {Promise}
 */
export function updateUserStatus(userId, status) {
  return request({
    url: `/admin/user/status/${userId}`,
    method: 'put',
    data: { status }
  })
}

/**
 * 重置用户密码
 * @param {number} userId - 用户ID
 * @returns {Promise}
 */
export function resetUserPassword(userId) {
  return request({
    url: `/admin/user/password/reset/${userId}`,
    method: 'put'
  })
}

/**
 * 新增基础题目
 * @param {Object} data - 题目数据
 * @param {string} data.questionName - 题目名称
 * @param {string} data.questionDesc - 题目描述
 * @param {string} data.options - 选项JSON字符串
 * @param {number} data.targetSalary - 目标薪资
 * @param {string} data.direction - 技术方向
 * @param {string} [data.analysis] - 题目解析（可选）
 * @returns {Promise}
 */
export function addQuestion(data) {
  return request({
    url: '/admin/question/add',
    method: 'post',
    data
  })
}

/**
 * 查询题目列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码，默认1
 * @param {number} params.pageSize - 每页条数，默认10
 * @param {string} params.questionName - 题目名称模糊查询（可选）
 * @param {string} params.direction - 技术方向筛选（可选）
 * @param {number} params.targetSalary - 目标薪资筛选（可选）
 * @returns {Promise} 返回分页题目列表
 */
export function getQuestionList(params) {
  return request({
    url: '/admin/question/list',
    method: 'get',
    params
  })
}

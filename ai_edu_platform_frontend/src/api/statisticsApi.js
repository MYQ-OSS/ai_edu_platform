import request from './request'

/**
 * 获取学习统计信息
 */
export const getLearningStatistics = () => {
  return request({
    url: '/user/learning-statistics',
    method: 'GET'
  })
}

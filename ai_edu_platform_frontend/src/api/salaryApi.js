// 薪资评估模块接口
import request from "./request";

// 0. 获取技术方向字典数据
export const getTechDirections = () => {
  return request({
    url: "/dict/tech-directions",
    method: "GET",
  });
};

// 1. 提交薪资评估数据接口
export const evaluateSalary = (data) => {
  return request({
    url: "/salary/evaluate",
    method: "POST",
    data,
  });
};

// 2. 获取薪资评估报告接口
export const getSalaryReport = (reportId) => {
  return request({
    url: `/salary/report/${reportId}`,
    method: "GET",
  });
};

// 3. 获取薪资评估历史接口（后端要求 query: userId）
export const getSalaryHistory = (userId) => {
  return request({
    url: "/salary/history",
    method: "GET",
    params: { userId },
  });
};

/** ContextSelector 等处使用的别名 */
export const getHistory = getSalaryHistory;

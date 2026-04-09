// 技术挑战模块接口
import request from "./request";

// 0. 获取技术方向字典数据
export const getTechDirections = () => {
  return request({
    url: "/dict/tech-directions",
    method: "GET",
  });
};

// 1. 生成AI题目接口
export const generateQuiz = (data) => {
  return request({
    url: "/quiz/generate",
    method: "POST",
    data,
  });
};

// 2. 提交答题结果接口
export const submitQuiz = (data) => {
  return request({
    url: "/quiz/submit",
    method: "POST",
    data,
  });
};

// 3. 获取答题报告接口
export const getQuizReport = (recordId) => {
  return request({
    url: `/quiz/report/${recordId}`,
    method: "GET",
  });
};

// 4. 收藏题目接口
export const collectQuiz = (data) => {
  return request({
    url: "/quiz/collect",
    method: "POST",
    data,
  });
};
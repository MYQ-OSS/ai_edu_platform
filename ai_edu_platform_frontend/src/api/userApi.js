import request from "./request";

// 用户注册
export const register = (data) => {
  return request({
    url: "/user/register",
    method: "POST",
    data,
  });
};

// 用户登录
export const login = (data) => {
  return request({
    url: "/user/login",
    method: "POST",
    data,
  });
};

// 获取个人信息
export const getUserInfo = () => {
  return request({
    url: "/user/info",
    method: "GET",
  });
};

// 编辑个人信息
export const editUserInfo = (data) => {
  return request({
    url: "/user/info/edit",
    method: "PUT",
    data,
  });
};

// 获取学习足迹
export const getLearningHistory = () => {
  return request({
    url: "/user/learning-history",
    method: "GET",
  });
};

// 获取用户答题统计信息
export const getQuizStatistics = () => {
  return request({
    url: "/user/quiz-statistics",
    method: "GET",
  });
};

const routes = [
  {
    path: "/",
    redirect: "/home",
  },
  {
    path: "/home",
    name: "Home",
    component: () => import("../views/home/Home.vue"),
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("../views/login/Login.vue"),
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("../views/login/Register.vue"),
  },
  {
    path: "/question/input",
    name: "QuestionInput",
    component: () => import("../views/question/QuestionInput.vue"),
  },
  {
    path: "/question/answer",
    name: "QuestionAnswer",
    component: () => import("../views/question/QuestionAnswer.vue"),
  },
  {
    path: "/question/report",
    name: "QuestionReport",
    component: () => import("../views/question/QuestionReport.vue"),
  },
  {
    path: "/salary/input",
    name: "SalaryInput",
    component: () => import("../views/salary/SalaryInput.vue"),
  },
  {
    path: "/salary/report",
    name: "SalaryReport",
    component: () => import("../views/salary/SalaryReport.vue"),
  },
  {
    path: "/personal/info",
    name: "PersonalInfo",
    component: () => import("../views/personal/PersonalInfo.vue"),
  },
  {
    path: "/personal/edit-info",
    name: "EditInfo",
    component: () => import("../views/personal/EditInfo.vue"),
  },
  {
    path: "/personal/answer-history",
    name: "AnswerHistory",
    component: () => import("../views/personal/AnswerHistory.vue"),
  },
  {
    path: "/personal/salary-history",
    name: "SalaryHistory",
    component: () => import("../views/personal/SalaryHistory.vue"),
  },
  {
    path: "/personal/answer-detail/:id",
    name: "AnswerDetail",
    component: () => import("../views/personal/AnswerDetail.vue"),
  },
  {
    path: "/personal/salary-detail/:id",
    name: "SalaryDetail",
    component: () => import("../views/personal/SalaryDetail.vue"),
  },
  {
    path: "/personal/learning-statistics",
    name: "LearningStatistics",
    component: () => import("../views/personal/LearningStatistics.vue"),
  },
  {
    path: "/admin/user-manage",
    name: "UserManage",
    component: () => import("../views/admin/UserManage.vue"),
  },
  {
    path: "/admin/question-manage",
    name: "QuestionManage",
    component: () => import("../views/admin/QuestionManage.vue"),
  },
  {
    path: "/admin/data-dict",
    name: "DataDict",
    component: () => import("../views/admin/DataDict.vue"),
  },
];

export default routes;

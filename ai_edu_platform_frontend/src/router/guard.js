import { useUserStore } from "../store/userStore";

// 路由守卫配置
export const setupRouterGuard = (router) => {
  router.beforeEach((to, from, next) => {
    const userStore = useUserStore();
    const isLoggedIn = userStore.isLoggedIn;

    // 需要登录的页面（除了首页和登录注册页）
    const requiresAuth = [
      "/question/input",
      "/question/answer",
      "/question/report",
      "/salary/input",
      "/salary/report",
      "/personal/info",
      "/personal/edit-info",
      "/personal/answer-history",
      "/personal/salary-history",
      "/personal/answer-detail",
      "/personal/salary-detail",
      "/personal/learning-statistics",
      "/admin/user-manage",
      "/admin/question-manage",
      "/admin/data-dict",
      "/chat",
    ];

    // 登录/注册页面
    const authPages = ["/login", "/register"];

    // 未登录用户访问需要登录的页面，跳转到登录页
    if (requiresAuth.includes(to.path) && !isLoggedIn) {
      next("/login");
    }
    // 已登录用户访问登录/注册页面，跳转到首页
    else if (authPages.includes(to.path) && isLoggedIn) {
      next("/home");
    }
    // 其他情况正常跳转
    else {
      next();
    }
  });

  // 路由跳转后滚动到顶部 - 使用 nextTick 避免阻塞渲染
  router.afterEach(() => {
    requestAnimationFrame(() => {
      window.scrollTo(0, 0);
    });
  });
};

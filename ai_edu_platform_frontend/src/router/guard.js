import { useUserStore } from '../store/userStore'

// 路由守卫配置
export const setupRouterGuard = (router) => {
  router.beforeEach((to, from, next) => {
    const userStore = useUserStore()
    const isLoggedIn = userStore.isLoggedIn
    
    // 需要登录的页面
    const requiresAuth = [
      '/personal/info',
      '/personal/answer-history',
      '/personal/salary-history',
      '/admin/user-manage',
      '/admin/question-manage',
      '/admin/data-dict'
    ]
    
    // 登录/注册页面
    const authPages = ['/login', '/register']
    
    // 未登录用户访问需要登录的页面，跳转到登录页
    if (requiresAuth.includes(to.path) && !isLoggedIn) {
      next('/login')
    }
    // 已登录用户访问登录/注册页面，跳转到首页
    else if (authPages.includes(to.path) && isLoggedIn) {
      next('/home')
    }
    // 其他情况正常跳转
    else {
      next()
    }
  })
}
package top.mayiqin.ai_edu_platform.utils;

/**
 * @author m'y'q
 */
public class UserContext {
    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();

    // 存储当前用户ID（拦截器中调用）
    public static void setCurrentUserId(Long userId) {
        USER_ID_HOLDER.set(userId);
    }
    // 获取当前用户ID（Service/Controller中调用）
    public static Long getCurrentUserId() {
        return USER_ID_HOLDER.get();
    }
    // 清除数据（请求结束后调用，防止内存泄漏）
    public static void remove() {
        USER_ID_HOLDER.remove();
    }
}

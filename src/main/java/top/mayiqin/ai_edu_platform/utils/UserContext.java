package top.mayiqin.ai_edu_platform.utils;

/**
 * 用户上下文工具类
 * 使用 ThreadLocal 存储当前请求的用户信息
 * @author m'y'q
 */
public class UserContext {
    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_ROLE_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前用户ID
     */
    public static void setCurrentUserId(Long userId) {
        USER_ID_HOLDER.set(userId);
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        return USER_ID_HOLDER.get();
    }

    /**
     * 设置当前用户角色
     */
    public static void setCurrentUserRole(String role) {
        USER_ROLE_HOLDER.set(role);
    }

    /**
     * 获取当前用户角色
     */
    public static String getCurrentUserRole() {
        return USER_ROLE_HOLDER.get();
    }

    /**
     * 判断当前用户是否为管理员
     */
    public static boolean isAdmin() {
        String role = USER_ROLE_HOLDER.get();
        return "admin".equals(role);
    }

    /**
     * 清除数据（请求结束后调用，防止内存泄漏）
     */
    public static void remove() {
        USER_ID_HOLDER.remove();
        USER_ROLE_HOLDER.remove();
    }
}

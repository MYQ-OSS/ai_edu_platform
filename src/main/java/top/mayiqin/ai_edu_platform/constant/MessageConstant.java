package top.mayiqin.ai_edu_platform.constant;

/**
 * 信息提示常量类
 * @author m'y'q
 */
public class MessageConstant {

    // --- 账号相关 ---
    public static final String ACCOUNT_ALREADY_EXISTS = "账号已存在";
    public static final String PASSWORD_ERROR = "密码错误"; // 密码错误
    public static final String ACCOUNT_NOT_FOUND = "账号不存在"; // 账号不存在
    public static final String ACCOUNT_LOCKED = "账号被锁定"; // 账号被锁定
    public static final String ALREADY_EXISTS = "已存在"; // 已存在
    public static final String UNKNOWN_ERROR = "未知错误"; // 未知错误
    public static final String USER_NOT_LOGIN = "用户未登录"; // 用户未登录

    // --- 通用业务提示 ---
    public static final String OPERATION_SUCCESS = "操作成功";
    public static final String OPERATION_FAILED = "操作失败";
    public static final String DATA_NOT_FOUND = "数据不存在";
    public static final String PARAMETER_ERROR = "参数错误";
    public static final String PERMISSION_DENIED = "权限不足";
    public static final String ADMIN_PERMISSION_DENIED = "权限不足，仅管理员可操作";
    public static final String GET_TECH_DIRECTIONS_SUCCESS = "获取技术方向成功";
    public static final String SAVE_FAILED = "保存失败";
    public static final String DICT_ADD_SUCCESS = "字典数据添加成功";

    // --- 用户模块 ---
    public static final String LOGIN_SUCCESS = "登录成功";
    public static final String REGISTER_SUCCESS = "注册成功";
    public static final String UPDATE_SUCCESS = "修改成功";
    public static final String USERNAME_OR_PASSWORD_ERROR = "用户名或密码错误";
    public static final String USERNAME_ALREADY_EXISTS = "用户名已存在";
    public static final String GET_LEARNING_HISTORY_SUCCESS = "获取学习足迹成功";
    public static final String USER_NOT_FOUND = "用户不存在";
    public static final String PASSWORD_RESET_SUCCESS = "密码重置成功";
    public static final String USER_STATUS_UPDATE_SUCCESS = "用户状态更新成功";

    // --- 题目与答题模块 ---
    public static final String QUESTION_GENERATE_SUCCESS = "题目生成成功";
    public static final String QUIZ_SUBMIT_SUCCESS = "答题结果提交成功";
    public static final String GET_QUIZ_REPORT_SUCCESS = "获取答题报告成功";
    public static final String GET_COLLECT_LIST_SUCCESS = "获取收藏列表成功";
    public static final String QUESTION_NOT_EXIST = "题目不存在";
    public static final String REQUEST_PARAM_EMPTY = "请求参数不能为空";
    public static final String SAVE_QUESTION_FAILED = "题目保存失败";
    public static final String UPDATE_COLLECT_STATUS_FAILED = "更新收藏状态失败";
    public static final String ADD_COLLECT_RECORD_FAILED = "新增收藏记录失败";
    public static final String QUESTION_ADD_SUCCESS = "题目添加成功";
    public static final String QUESTION_UPDATE_SUCCESS = "题目更新成功";
    public static final String QUESTION_DELETE_SUCCESS = "题目删除成功";

    // --- 薪资评估模块 ---
    public static final String SALARY_EVALUATE_SUCCESS = "薪资评估报告生成成功";
    public static final String GET_SALARY_REPORT_SUCCESS = "获取薪资评估报告成功";
    public static final String GET_SALARY_HISTORY_SUCCESS = "获取薪资评估历史成功";
    public static final String REPORT_NOT_EXIST = "报告不存在";
    public static final String SAVE_SALARY_REPORT_FAILED = "薪资报告保存失败";

    // --- AI 服务相关 ---
    public static final String AI_SERVICE_INIT_FAILED = "系统初始化失败：无法加载 Prompt 模板";
    public static final String AI_RESPONSE_EMPTY = "AI返回内容为空";
    public static final String AI_RESPONSE_FORMAT_ERROR = "AI返回数据格式错误";
    public static final String AI_SERVICE_CALL_FAILED = "AI 服务调用失败，请检查 API Key 和网络连接";
    public static final String AI_GENERATED_CONTENT_INVALID = "AI生成的内容格式错误";
    public static final String AI_GENERATE_FALLBACK_SUCCESS = "AI生成题目暂时失败，已自动从题库中找到符合要求的题目";
    public static final String NO_SUITABLE_QUESTION_FOUND = "AI生成题目失败且找不到符合要求的题目";

    // --- 拦截器与认证相关 ---
    public static final String AUTH_HEADER_NAME = "Authorization";
    public static final String AUTH_TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_MISSING_OR_EXPIRED = "未登录或Token已过期，请先登录";
    public static final String TOKEN_INVALID = "Token无效或已过期";
}

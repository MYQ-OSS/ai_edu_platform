package top.mayiqin.ai_edu_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.mayiqin.ai_edu_platform.constant.MessageConstant;
import top.mayiqin.ai_edu_platform.entity.dto.UserLoginDTO;
import top.mayiqin.ai_edu_platform.entity.dto.UserRegisterDTO;
import top.mayiqin.ai_edu_platform.entity.dto.UserUpdateDTO;
import top.mayiqin.ai_edu_platform.entity.vo.LearningHistoryVO;
import top.mayiqin.ai_edu_platform.entity.vo.LearningStatisticsVO;
import top.mayiqin.ai_edu_platform.entity.vo.UserInfoVO;
import top.mayiqin.ai_edu_platform.exception.Result;
import top.mayiqin.ai_edu_platform.service.UserService;

import java.util.Map;

/**
 * 用户管理
 */
@RestController
@RequestMapping("/user")
@Slf4j
@Tag(name = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     *
     * @param userLoginDTO 用户登录信息
     * @return 响应结果
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录请求: username={}", userLoginDTO.getUsername());

        // 调用服务层登录，验证账号密码并生成 JWT
        Map<String, Object> responseData = userService.login(userLoginDTO);

        log.info("用户登录成功: username={}", userLoginDTO.getUsername());
        return Result.success(MessageConstant.LOGIN_SUCCESS, responseData);
    }

    /**
     * 用户注册
     *
     * @param userRegisterDTO 用户注册信息
     * @return 响应结果
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Long> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        log.info("用户注册请求: username={}", userRegisterDTO.getUsername());

        // 调用服务层注册，返回用户ID
        Long userId = userService.register(userRegisterDTO);

        log.info("用户注册成功: username={}", userRegisterDTO.getUsername());
        return Result.success(MessageConstant.REGISTER_SUCCESS, userId);
    }

    /**
     * 查询个人信息
     *
     * @return 用户信息DTO（不包含密码等敏感字段）
     */
    @Operation(summary = "查询个人信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo() {
        log.info("查询用户信息请求");

        // 获取当前登录用户信息
        UserInfoVO userInfo = userService.getUserInfo();

        log.info("查询用户信息成功: username={}", userInfo.getUsername());
        return Result.success(userInfo);
    }

    /**
     * 编辑个人信息
     *
     * @param userUpdateDTO 用户更新信息
     * @return 响应结果
     */
    @Operation(summary = "编辑个人信息")
    @PutMapping("/info/edit")
    public Result<Void> updateUserInfo(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        log.info("编辑用户信息请求");

        // 调用服务层更新用户信息
        userService.updateUserInfo(userUpdateDTO);

        return Result.success(MessageConstant.UPDATE_SUCCESS, null);
    }

    /**
     * 获取学习足迹
     *
     * @return 响应结果
     */
    @Operation(summary = "获取学习足迹")
    @GetMapping("/learning-history")
    public Result<LearningHistoryVO> getLearningHistory() {
        log.info("获取学习足迹请求");

        // 调用服务层获取学习足迹
        LearningHistoryVO learningHistory = userService.getLearningHistory();

        return Result.success(MessageConstant.GET_LEARNING_HISTORY_SUCCESS, learningHistory);
    }

    /**
     * 获取学习统计信息
     *
     * @return 响应结果
     */
    @Operation(summary = "获取学习统计信息")
    @GetMapping("/learning-statistics")
    public Result<LearningStatisticsVO> getLearningStatistics() {
        log.info("获取学习统计信息请求");

        // 调用服务层获取学习统计信息
        LearningStatisticsVO statistics = userService.getLearningStatistics();

        return Result.success("获取成功", statistics);
    }

    /**
     * 获取用户答题统计信息
     * 返回答题数量和平均分数
     *
     * @return 响应结果
     */
    @Operation(summary = "获取用户答题统计信息")
    @GetMapping("/quiz-statistics")
    public Result<Map<String, Object>> getQuizStatistics() {
        log.info("获取用户答题统计信息请求");

        // 调用服务层获取答题统计信息
        Map<String, Object> statistics = userService.getQuizStatistics();

        return Result.success("获取成功", statistics);
    }

    /**
     * 刷新Token
     * 当Access Token过期时，使用Refresh Token获取新的Token
     *
     * @param request 包含refreshToken的请求体
     * @return 新的token和refreshToken
     */
    @Operation(summary = "刷新Token")
    @PostMapping("/refresh-token")
    public Result<Map<String, Object>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        log.info("刷新Token请求");

        // 调用服务层刷新Token
        Map<String, Object> tokens = userService.refreshToken(refreshToken);

        return Result.success("Token刷新成功", tokens);
    }

}

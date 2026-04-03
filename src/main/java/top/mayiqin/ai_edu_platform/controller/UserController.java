package top.mayiqin.ai_edu_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.mayiqin.ai_edu_platform.entity.po.User;
import top.mayiqin.ai_edu_platform.exception.Result;
import top.mayiqin.ai_edu_platform.service.UserService;
import top.mayiqin.ai_edu_platform.utils.JwtUtil;


import javax.security.auth.login.AccountNotFoundException;
import java.util.HashMap;
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
     * @param user
     * @return
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<User> login(@RequestBody User user) throws AccountNotFoundException {
        log.info("用户登录：{}", user);

        User loginUser = userService.login(user);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", loginUser.getId());

        String token = JwtUtil.createJWT(
                "DaiMaDouDui",          // 自定义密钥
                3600 * 1000,           // 过期时间1小时
                claims);


        User userLoginVO = User.builder()
                .id(loginUser.getId())
                .username(loginUser.getUsername())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @Operation(summary = "退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增用户
     * @param user
     * @return
     */
    @Operation(summary = "新增用户")
    @PostMapping("/add")
    public Result save(@RequestBody User user) {
        log.info("新增用户:{}", user);
        System.out.println("当前线程的id " + Thread.currentThread().getId());
        userService.save(user);
        return Result.success();
    }

    /**
     * 查询用户信息
     * @return
     */
    @Operation(summary = "查询个人信息")
    @GetMapping("/profile")
    public Result<User> getProfile() {
        User user = userService.getCurrentUserProfile();
        // 隐藏密码，禁止返回给前端
        user.setPassword("******");
        return Result.success(user);
    }

    @Operation(summary = "编辑个人基础信息")
    @PutMapping("/profile") // PUT 请求表示更新
    public Result updateProfile(@RequestBody User user) {
        userService.updateProfile(user);
        return Result.success("修改成功");
    }

}

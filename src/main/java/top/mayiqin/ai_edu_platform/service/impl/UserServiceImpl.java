package top.mayiqin.ai_edu_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import top.mayiqin.ai_edu_platform.constant.MessageConstant;
import top.mayiqin.ai_edu_platform.entity.po.QuizRecord;
import top.mayiqin.ai_edu_platform.entity.vo.LearningHistoryVO;
import top.mayiqin.ai_edu_platform.entity.po.SalaryReport;
import top.mayiqin.ai_edu_platform.entity.po.User;
import top.mayiqin.ai_edu_platform.entity.vo.UserInfoVO;
import top.mayiqin.ai_edu_platform.exception.AccountAlreadyExistsException;
import top.mayiqin.ai_edu_platform.exception.BusinessException;
import top.mayiqin.ai_edu_platform.exception.LoginFailedException;
import top.mayiqin.ai_edu_platform.properties.JwtProperties;
import top.mayiqin.ai_edu_platform.service.QuizRecordService;
import top.mayiqin.ai_edu_platform.service.SalaryReportService;
import top.mayiqin.ai_edu_platform.service.UserService;
import top.mayiqin.ai_edu_platform.mapper.UserMapper;
import org.springframework.stereotype.Service;
import top.mayiqin.ai_edu_platform.entity.dto.UserLoginDTO;
import top.mayiqin.ai_edu_platform.entity.dto.UserRegisterDTO;
import top.mayiqin.ai_edu_platform.entity.dto.UserUpdateDTO;
import top.mayiqin.ai_edu_platform.utils.JwtUtil;
import top.mayiqin.ai_edu_platform.utils.UserContext;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author m'y'q
* @description 针对表【t_user(用户基础信息表)】的数据库操作Service实现
* @createDate 2026-03-31 20:55:09
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    // BCrypt密码编码器（单例，线程安全）
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private QuizRecordService quizRecordService;
    
    @Autowired
    private SalaryReportService salaryReportService;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public Long register(UserRegisterDTO userRegisterDTO) {
        log.info("用户注册请求: username={}", userRegisterDTO.getUsername());
        
        // 1. 参数校验（由@Valid注解自动完成，此处只需业务逻辑校验）
        String username = userRegisterDTO.getUsername();
        String password = userRegisterDTO.getPassword();
        
        // 2. 检查账号是否已存在
        User existUser = userMapper.getByUsername(username);
        if (existUser != null) {
            throw new AccountAlreadyExistsException(MessageConstant.ACCOUNT_ALREADY_EXISTS);
        }
        
        // 3. 密码加密（使用BCrypt）
        String encodedPassword = PASSWORD_ENCODER.encode(password);
        
        // 4. 构建User实体并保存
        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .identity(userRegisterDTO.getIdentity())
                .salary(userRegisterDTO.getSalary())
                .experience(userRegisterDTO.getExperience())
                .build();
        
        userMapper.insert(user);
        
        log.info("用户注册成功: userId={}, username={}", user.getId(), username);
        return user.getId();
    }

    @Override
    public Map<String, Object> login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();
                
        // 1. 参数校验（由@Valid注解自动完成，此处只需业务逻辑校验）
        
        // 2. 根据用户名查询数据库中的数据
        User userCheck = userMapper.getByUsername(username);
        
        // 3. 处理各种异常情况（用户名不存在、密码不对）
        if (userCheck == null) {
            // 账号不存在，统一返回“账号或密码错误”，避免泄露账号信息
            throw new LoginFailedException("账号或密码错误");
        }
        
        // 4. 密码比对（使用BCrypt）
        if (!PASSWORD_ENCODER.matches(password, userCheck.getPassword())) {
            // 密码错误
            throw new LoginFailedException("账号或密码错误");
        }
        
        // 5. 生成 JWT 令牌（userId已嵌入Token中）
        // 注意：必须使用可变的HashMap，因为JJWT库需要修改claims Map
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userCheck.getId());
        
        String token = JwtUtil.createJWT(
            jwtProperties.getSecretKey(),
            jwtProperties.getExpiration(),
            claims
        );
        log.debug("用户登录成功，已生成 JWT: userId={}", userCheck.getId());
        
        // 6. 构造登录结果并返回（只返回token）
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        return result;
    }

    @Override
    public UserInfoVO getUserInfo() {
        Long userId = UserContext.getCurrentUserId();
        log.info("查询用户信息请求: userId={}", userId);
        
        if (userId == null) {
            throw new BusinessException(401, MessageConstant.USER_NOT_LOGIN);
        }
        
        // 根据ID查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, MessageConstant.ACCOUNT_NOT_FOUND);
        }
        
        // 转换为VO，自动排除密码等敏感字段
        return UserInfoVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .identity(user.getIdentity())
                .salary(user.getSalary())
                .experience(user.getExperience())
                .answerTimes(user.getAnswerTimes())
                .averageScore(user.getAverageScore())
                .createTime(user.getCreateTime())
                .build();
    }

    @Override
    public void updateUserInfo(UserUpdateDTO userUpdateDTO) {
        log.info("更新个人信息请求");
        
        // 1. 获取当前登录用户ID
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, MessageConstant.USER_NOT_LOGIN);
        }
        
        // 2. 检查用户是否存在
        User existUser = userMapper.selectById(userId);
        if (existUser == null) {
            throw new BusinessException(404, MessageConstant.ACCOUNT_NOT_FOUND);
        }
        
        // 3. 构建更新对象（只允许更新特定字段）
        User updateUser = new User();
        updateUser.setId(userId);
        
        // 只允许更新以下字段
        if (StrUtil.isNotBlank(userUpdateDTO.getIdentity())) {
            // 身份字段长度校验
            if (userUpdateDTO.getIdentity().length() > 23) {
                throw new BusinessException(400, "用户身份长度不能超过23位");
            }
            updateUser.setIdentity(userUpdateDTO.getIdentity());
        }
        
        if (userUpdateDTO.getSalary() != null) {
            // 薪资格式校验
            if (userUpdateDTO.getSalary() < 0) {
                throw new BusinessException(400, "薪资不能为负数");
            }
            updateUser.setSalary(userUpdateDTO.getSalary());
        }
        
        if (StrUtil.isNotBlank(userUpdateDTO.getExperience())) {
            updateUser.setExperience(userUpdateDTO.getExperience());
        }
        
        // 如果提供了密码，则进行加密后更新
        if (StrUtil.isNotBlank(userUpdateDTO.getPassword())) {
            String encodedPassword = PASSWORD_ENCODER.encode(userUpdateDTO.getPassword());
            updateUser.setPassword(encodedPassword);
        }
        
        // 4. 执行更新
        userMapper.updateById(updateUser);
        
        log.info("个人信息更新成功: userId={}", userId);
    }

    @Override
    public LearningHistoryVO getLearningHistory() {

        // 1. 获取当前登录用户ID
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, MessageConstant.USER_NOT_LOGIN);
        }
        
        // 2. 查询答题记录（按创建时间降序）
        LambdaQueryWrapper<QuizRecord> quizWrapper = new LambdaQueryWrapper<>();
        quizWrapper.eq(QuizRecord::getUserId, userId)
                   .orderByDesc(QuizRecord::getCreateTime);
        List<QuizRecord> quizRecords = quizRecordService.list(quizWrapper);
        
        // 3. 计算正确率趋势（最近10次答题的正确率）
        List<BigDecimal> accuracyTrend = quizRecords.stream()
                .limit(10)
                .map(QuizRecord::getAccuracy)
                .collect(Collectors.toList());
        Collections.reverse(accuracyTrend); // 反转，使时间早的在前
        
        // 4. 查询薪资报告（按创建时间降序）
        LambdaQueryWrapper<SalaryReport> salaryWrapper = new LambdaQueryWrapper<>();
        salaryWrapper.eq(SalaryReport::getUserId, userId)
                     .orderByDesc(SalaryReport::getCreateTime);
        List<SalaryReport> salaryReports = salaryReportService.list(salaryWrapper);
        
        log.info("学习足迹获取成功: userId={}, 答题记录数={}, 薪资报告数={}", 
                userId, quizRecords.size(), salaryReports.size());
        
        // 5. 构造返回数据
        return LearningHistoryVO.builder()
                .quizRecords(quizRecords)
                .accuracyTrend(accuracyTrend)
                .salaryReports(salaryReports)
                .build();
    }


}





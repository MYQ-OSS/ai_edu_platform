package top.mayiqin.ai_edu_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import top.mayiqin.ai_edu_platform.constant.MessageConstant;
import top.mayiqin.ai_edu_platform.entity.po.QuizRecord;
import top.mayiqin.ai_edu_platform.entity.vo.LearningHistoryVO;
import top.mayiqin.ai_edu_platform.entity.vo.LearningStatisticsVO;
import top.mayiqin.ai_edu_platform.entity.vo.QuizReportVO;
import top.mayiqin.ai_edu_platform.entity.po.SalaryReport;
import top.mayiqin.ai_edu_platform.entity.po.User;
import top.mayiqin.ai_edu_platform.entity.vo.UserInfoVO;
import top.mayiqin.ai_edu_platform.entity.vo.UserListVO;
import top.mayiqin.ai_edu_platform.exception.AccountAlreadyExistsException;
import top.mayiqin.ai_edu_platform.exception.BusinessException;
import top.mayiqin.ai_edu_platform.exception.LoginFailedException;
import top.mayiqin.ai_edu_platform.properties.JwtProperties;
import top.mayiqin.ai_edu_platform.service.QuizRecordService;
import top.mayiqin.ai_edu_platform.service.SalaryReportService;
import top.mayiqin.ai_edu_platform.service.UserService;
import top.mayiqin.ai_edu_platform.mapper.QuestionMapper;
import top.mayiqin.ai_edu_platform.mapper.UserMapper;
import org.springframework.stereotype.Service;
import top.mayiqin.ai_edu_platform.entity.dto.UserLoginDTO;
import top.mayiqin.ai_edu_platform.entity.dto.UserRegisterDTO;
import top.mayiqin.ai_edu_platform.entity.dto.UserUpdateDTO;
import top.mayiqin.ai_edu_platform.entity.dto.UserListQueryDTO;
import top.mayiqin.ai_edu_platform.utils.JwtUtil;
import top.mayiqin.ai_edu_platform.utils.UserContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
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
    private QuestionMapper questionMapper;

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
        
        // 5. 检查用户状态（是否被禁用）
        if ("1".equals(userCheck.getStatus())) {
            throw new LoginFailedException("该账号已被禁用，请联系管理员");
        }
        
        // 6. 生成 JWT 令牌（userId和role已嵌入Token中）
        // 注意：必须使用可变的HashMap，因为JJWT库需要修改claims Map
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userCheck.getId());
        // 添加角色信息，默认为user
        String role = userCheck.getRole() != null ? userCheck.getRole() : "user";
        claims.put("role", role);
        
        String token = JwtUtil.createJWT(
            jwtProperties.getSecretKey(),
            jwtProperties.getExpiration(),
            claims
        );
        
        // 生成Refresh Token（有效期更长）
        String refreshToken = JwtUtil.createRefreshJWT(
            jwtProperties.getSecretKey(),
            jwtProperties.getRefreshExpiration(),
            claims
        );
        
        log.debug("用户登录成功，已生成 JWT: userId={}, role={}", userCheck.getId(), role);
        
        // 7. 构造登录结果并返回（返回token和refreshToken）
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("refreshToken", refreshToken);
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
                .status(user.getStatus())
                .role(user.getRole())
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
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null) {
            throw new BusinessException(404, MessageConstant.ACCOUNT_NOT_FOUND);
        }
        
        // 3. 构建更新对象（只允许更新特定字段）
        User updateUser = new User();
        updateUser.setId(userId);
        
        // 只允许更新以下字段
        if (StrUtil.isNotBlank(userUpdateDTO.getUsername())) {
            // 检查用户名是否已被其他用户使用
            User existUser = userMapper.getByUsername(userUpdateDTO.getUsername());
            if (existUser != null && !existUser.getId().equals(userId)) {
                throw new BusinessException(400, "该用户名已被使用");
            }
            updateUser.setUsername(userUpdateDTO.getUsername());
        }
        
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
        
        // 3. 转换为QuizReportVO，包含题目信息和AI判分结果
        List<QuizReportVO> quizReportList = quizRecords.stream()
                .map(record -> {
                    // 构建QuizReportVO
                    return QuizReportVO.builder()
                            .recordId(record.getId())
                            .userId(record.getUserId())
                            .questionId(record.getQuestionId())
                            .userOptions(record.getUserOptions())
                            .userAnswer(record.getUserAnswer())
                            .score(record.getScore())
                            .comment(record.getComment())
                            .suggest(record.getSuggest())
                            .reason(record.getReason())
                            .trueOptions(record.getTrueOptions())
                            .analysis(record.getAnalysis())
                            .accuracy(record.getAccuracy())
                            .createTime(record.getCreateTime() != null ? 
                                    record.getCreateTime().toString() : "")
                            .build();
                })
                .collect(Collectors.toList());
        
        // 4. 计算正确率趋势（最近10次答题的正确率）
        List<BigDecimal> accuracyTrend = quizRecords.stream()
                .limit(10)
                .map(QuizRecord::getAccuracy)
                .collect(Collectors.toList());
        Collections.reverse(accuracyTrend); // 反转，使时间早的在前
        
        // 5. 查询薪资报告（按创建时间降序）
        LambdaQueryWrapper<SalaryReport> salaryWrapper = new LambdaQueryWrapper<>();
        salaryWrapper.eq(SalaryReport::getUserId, userId)
                     .orderByDesc(SalaryReport::getCreateTime);
        List<SalaryReport> salaryReports = salaryReportService.list(salaryWrapper);
        
        log.info("学习足迹获取成功: userId={}, 答题记录数={}, 薪资报告数={}", 
                userId, quizReportList.size(), salaryReports.size());
        
        // 6. 构造返回数据
        return LearningHistoryVO.builder()
                .quizRecords(quizReportList)
                .accuracyTrend(accuracyTrend)
                .salaryReports(salaryReports)
                .build();
    }

    @Override
    public LearningStatisticsVO getLearningStatistics() {
        // 1. 获取当前登录用户ID
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, MessageConstant.USER_NOT_LOGIN);
        }
        
        // 2. 查询答题记录（按时间升序）
        LambdaQueryWrapper<QuizRecord> quizWrapper = new LambdaQueryWrapper<>();
        quizWrapper.eq(QuizRecord::getUserId, userId)
                   .orderByAsc(QuizRecord::getCreateTime);
        List<QuizRecord> quizRecords = quizRecordService.list(quizWrapper);
        
        // 3. 计算统计数据
        int totalQuizCount = quizRecords.size();
        BigDecimal averageScore = BigDecimal.ZERO;
        BigDecimal averageAccuracy = BigDecimal.ZERO;
        int maxScore = 0;
        int minScore = 100;
        
        if (totalQuizCount > 0) {
            // 计算平均得分
            int totalScore = quizRecords.stream()
                    .mapToInt(QuizRecord::getScore)
                    .sum();
            averageScore = new BigDecimal(totalScore)
                    .divide(new BigDecimal(totalQuizCount), 2, RoundingMode.HALF_UP);
            
            // 计算平均正确率
            averageAccuracy = quizRecords.stream()
                    .map(QuizRecord::getAccuracy)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(new BigDecimal(totalQuizCount), 2, RoundingMode.HALF_UP);
            
            // 计算最高和最低得分
            maxScore = quizRecords.stream()
                    .mapToInt(QuizRecord::getScore)
                    .max()
                    .orElse(0);
            minScore = quizRecords.stream()
                    .mapToInt(QuizRecord::getScore)
                    .min()
                    .orElse(0);
        }
        
        // 4. 构建趋势数据
        List<LearningStatisticsVO.ScoreTrendItem> scoreTrend = new ArrayList<>();
        List<LearningStatisticsVO.AccuracyTrendItem> accuracyTrend = new ArrayList<>();
        
        for (QuizRecord record : quizRecords) {
            // 获取题目名称
            String questionName = questionMapper.selectQuestionNameById(record.getQuestionId());
            if (questionName == null) {
                questionName = "未知题目";
            }
            
            // 格式化时间
            String quizTime = record.getCreateTime() != null ? 
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(record.getCreateTime()) : "";
            
            // 添加得分趋势项
            scoreTrend.add(LearningStatisticsVO.ScoreTrendItem.builder()
                    .quizTime(quizTime)
                    .score(record.getScore())
                    .questionName(questionName)
                    .build());
            
            // 添加正确率趋势项
            if (record.getAccuracy() != null) {
                accuracyTrend.add(LearningStatisticsVO.AccuracyTrendItem.builder()
                        .quizTime(quizTime)
                        .accuracy(record.getAccuracy())
                        .questionName(questionName)
                        .build());
            }
        }
        
        log.info("学习统计信息获取成功: userId={}, totalQuizCount={}, averageScore={}, averageAccuracy={}",
                userId, totalQuizCount, averageScore, averageAccuracy);
        
        // 5. 构造返回数据
        return LearningStatisticsVO.builder()
                .totalQuizCount(totalQuizCount)
                .averageScore(averageScore)
                .averageAccuracy(averageAccuracy)
                .maxScore(maxScore)
                .minScore(minScore)
                .scoreTrend(scoreTrend)
                .accuracyTrend(accuracyTrend)
                .build();
    }

    @Override
    public Map<String, Object> getQuizStatistics() {
        // 1. 获取当前登录用户ID
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, MessageConstant.USER_NOT_LOGIN);
        }
        
        // 2. 查询用户的答题记录
        LambdaQueryWrapper<QuizRecord> quizWrapper = new LambdaQueryWrapper<>();
        quizWrapper.eq(QuizRecord::getUserId, userId);
        List<QuizRecord> quizRecords = quizRecordService.list(quizWrapper);
        
        // 3. 计算答题数量和平均分数
        int answerTimes = quizRecords.size();
        double averageScore = 0.0;
        
        if (answerTimes > 0) {
            // 计算总分
            int totalScore = quizRecords.stream()
                    .mapToInt(QuizRecord::getScore)
                    .sum();
            // 计算平均分（保留一位小数）
            averageScore = Math.round((double) totalScore / answerTimes * 10.0) / 10.0;
        }
        
        // 4. 更新用户表中的统计信息
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setAnswerTimes(answerTimes);
        updateUser.setAverageScore((int) averageScore);
        userMapper.updateById(updateUser);
        
        log.info("用户答题统计信息获取成功: userId={}, answerTimes={}, averageScore={}", 
                userId, answerTimes, averageScore);
        
        // 5. 构造返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("answerTimes", answerTimes);
        result.put("averageScore", averageScore);
        
        return result;
    }

    @Override
    public Page<UserListVO> getUserList(UserListQueryDTO queryDTO) {
        log.info("后台查询用户列表请求: pageNum={}, pageSize={}, username={}, status={}, role={}",
                queryDTO.getPageNum(), queryDTO.getPageSize(), queryDTO.getUsername(), 
                queryDTO.getStatus(), queryDTO.getRole());
        
        try {
            // 1. 参数校验和默认值处理
            int pageNum = queryDTO.getPageNum() != null ? queryDTO.getPageNum() : 1;
            int pageSize = queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 10;
            
            // 限制 pageSize 范围
            if (pageSize < 1) {
                pageSize = 1;
            } else if (pageSize > 500) {
                pageSize = 500;
            }
            
            // 2. 构建查询条件
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            
            // 用户名模糊查询
            if (StrUtil.isNotBlank(queryDTO.getUsername())) {
                wrapper.like(User::getUsername, queryDTO.getUsername());
            }
            
            // 状态精确匹配
            if (StrUtil.isNotBlank(queryDTO.getStatus())) {
                wrapper.eq(User::getStatus, queryDTO.getStatus());
            }
            
            // 角色精确匹配
            if (StrUtil.isNotBlank(queryDTO.getRole())) {
                wrapper.eq(User::getRole, queryDTO.getRole());
            }
            
            // 排序处理（支持前端传入排序参数）
            String orderByColumn = queryDTO.getOrderByColumn();
            String isAsc = queryDTO.getIsAsc();
            
            if (StrUtil.isNotBlank(orderByColumn)) {
                // 根据字段名动态排序
                if ("id".equals(orderByColumn)) {
                    if ("desc".equalsIgnoreCase(isAsc)) {
                        wrapper.orderByDesc(User::getId);
                    } else {
                        wrapper.orderByAsc(User::getId);
                    }
                } else if ("createTime".equals(orderByColumn)) {
                    if ("desc".equalsIgnoreCase(isAsc)) {
                        wrapper.orderByDesc(User::getCreateTime);
                    } else {
                        wrapper.orderByAsc(User::getCreateTime);
                    }
                } else {
                    // 默认按ID升序
                    wrapper.orderByAsc(User::getId);
                }
            } else {
                // 默认按ID升序
                wrapper.orderByAsc(User::getId);
            }
            
            // 3. 执行分页查询
            Page<User> page = new Page<>(pageNum, pageSize);
            Page<User> userPage = userMapper.selectPage(page, wrapper);
            
            // 4. 数据转换：User -> UserListVO
            List<UserListVO> voList = userPage.getRecords().stream()
                    .map(user -> UserListVO.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .identity(user.getIdentity())
                            .salary(user.getSalary())
                            .answerTimes(user.getAnswerTimes())
                            .averageScore(user.getAverageScore())
                            .status(user.getStatus())
                            .role(user.getRole())
                            .createTime(user.getCreateTime())
                            .updateTime(user.getUpdateTime())
                            .build())
                    .collect(Collectors.toList());
            
            // 5. 构造返回结果
            Page<UserListVO> resultPage = new Page<>();
            resultPage.setCurrent(userPage.getCurrent());
            resultPage.setSize(userPage.getSize());
            resultPage.setTotal(userPage.getTotal());
            resultPage.setRecords(voList);
            
            log.info("后台查询用户列表成功: total={}, pageNum={}, pageSize={}",
                    resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize());
            
            return resultPage;
        } catch (Exception e) {
            log.error("后台查询用户列表失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "查询用户列表失败: " + e.getMessage());
        }
    }

    @Override
    public void updateUserStatus(Long userId, String status) {
        log.info("后台修改用户状态请求: userId={}, status={}", userId, status);
        
        // 1. 获取当前管理员ID
        Long adminId = UserContext.getCurrentUserId();
        if (adminId == null) {
            throw new BusinessException(401, MessageConstant.USER_NOT_LOGIN);
        }
        
        // 2. 不能操作自己的账号
        if (adminId.equals(userId)) {
            throw new BusinessException(403, "不能操作自己的账号");
        }
        
        // 3. 检查用户是否存在
        User targetUser = userMapper.selectById(userId);
        if (targetUser == null) {
            throw new BusinessException(404, MessageConstant.ACCOUNT_NOT_FOUND);
        }
        
        // 4. 检查状态是否相同
        if (targetUser.getStatus().equals(status)) {
            String statusText = "0".equals(status) ? "启用" : "禁用";
            throw new BusinessException(400, "用户已处于" + statusText + "状态");
        }
        
        // 5. 更新状态
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setStatus(status);
        
        int rows = userMapper.updateById(updateUser);
        if (rows <= 0) {
            throw new BusinessException(500, "更新用户状态失败");
        }
        
        log.info("后台修改用户状态成功: userId={}, oldStatus={}, newStatus={}", 
                userId, targetUser.getStatus(), status);
    }

    @Override
    public void resetUserPassword(Long userId) {
        log.info("后台重置用户密码请求: userId={}", userId);
        
        // 1. 获取当前管理员ID
        Long adminId = UserContext.getCurrentUserId();
        if (adminId == null) {
            throw new BusinessException(401, MessageConstant.USER_NOT_LOGIN);
        }
        
        // 2. 不能重置自己的密码
        if (adminId.equals(userId)) {
            throw new BusinessException(403, "不能重置自己的密码");
        }
        
        // 3. 检查用户是否存在
        User targetUser = userMapper.selectById(userId);
        if (targetUser == null) {
            throw new BusinessException(404, MessageConstant.ACCOUNT_NOT_FOUND);
        }
        
        // 4. 重置为默认密码 123456（BCrypt加密）
        String defaultPassword = "123456";
        String encodedPassword = PASSWORD_ENCODER.encode(defaultPassword);
        
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(encodedPassword);
        
        int rows = userMapper.updateById(updateUser);
        if (rows <= 0) {
            throw new BusinessException(500, "重置密码失败");
        }
        
        log.info("后台重置用户密码成功: userId={}, username={}", userId, targetUser.getUsername());
    }

    @Override
    public Map<String, Object> refreshToken(String refreshToken) {
        log.info("刷新Token请求");
        
        // 1. 验证Refresh Token是否为空
        if (StrUtil.isBlank(refreshToken)) {
            throw new BusinessException(400, "Refresh Token不能为空");
        }
        
        // 2. 解析并验证Refresh Token
        Map<String, Object> claims;
        try {
            claims = JwtUtil.parseJWT(jwtProperties.getSecretKey(), refreshToken);
        } catch (Exception e) {
            log.warn("Refresh Token无效或已过期: {}", e.getMessage());
            throw new BusinessException(401, "Refresh Token无效或已过期，请重新登录");
        }
        
        // 3. 从claims中获取userId和role
        Object userIdObj = claims.get("userId");
        Object roleObj = claims.get("role");
        
        if (userIdObj == null || roleObj == null) {
            throw new BusinessException(401, "Refresh Token格式错误");
        }
        
        Long userId = Long.valueOf(userIdObj.toString());
        String role = roleObj.toString();
        
        // 4. 检查用户是否存在且状态正常
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        if ("1".equals(user.getStatus())) {
            throw new BusinessException(403, "账号已被禁用，请联系管理员");
        }
        
        // 5. 生成新的Access Token（短期有效）
        Map<String, Object> newClaims = new HashMap<>();
        newClaims.put("userId", userId);
        newClaims.put("role", role);
        
        String newToken = JwtUtil.createJWT(
            jwtProperties.getSecretKey(),
            jwtProperties.getExpiration(),
            newClaims
        );
        
        // 6. 生成新的Refresh Token（长期有效，支持连续刷新）
        String newRefreshToken = JwtUtil.createRefreshJWT(
            jwtProperties.getSecretKey(),
            jwtProperties.getRefreshExpiration(),
            newClaims
        );
        
        log.info("Token刷新成功: userId={}, role={}", userId, role);
        
        // 7. 返回新的token和refreshToken
        Map<String, Object> result = new HashMap<>();
        result.put("token", newToken);
        result.put("refreshToken", newRefreshToken);
        return result;
    }


}





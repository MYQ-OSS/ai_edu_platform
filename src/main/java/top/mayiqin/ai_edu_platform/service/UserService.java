package top.mayiqin.ai_edu_platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.mayiqin.ai_edu_platform.entity.dto.UserListQueryDTO;
import top.mayiqin.ai_edu_platform.entity.vo.LearningHistoryVO;
import top.mayiqin.ai_edu_platform.entity.dto.UserLoginDTO;
import top.mayiqin.ai_edu_platform.entity.dto.UserRegisterDTO;
import top.mayiqin.ai_edu_platform.entity.dto.UserUpdateDTO;
import top.mayiqin.ai_edu_platform.entity.po.User;
import com.baomidou.mybatisplus.extension.service.IService;
import top.mayiqin.ai_edu_platform.entity.vo.UserInfoVO;
import top.mayiqin.ai_edu_platform.entity.vo.UserListVO;

import java.util.Map;

/**
* @author m'y'q
* @description 针对表【t_user(用户基础信息表)】的数据库操作Service
* @createDate 2026-03-31 20:55:09
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterDTO 用户注册信息
     * @return 用户ID
     */
    Long register(UserRegisterDTO userRegisterDTO);

    /**
     * 用户登录
     *
     * @param userLoginDTO 用户登录信息
     * @return 包含 token 以及 userId 的登录结果
     */
    Map<String, Object> login(UserLoginDTO userLoginDTO);

    /**
     * 查询用户个人信息
     * @return 用户信息VO（不包含密码等敏感字段）
     */
    UserInfoVO getUserInfo();

    /**
     * 更新用户个人信息
     * @param userUpdateDTO 用户更新信息
     */
    void updateUserInfo(UserUpdateDTO userUpdateDTO);

    /**
     * 获取学习足迹
     *
     * @return 学习足迹数据
     */
    LearningHistoryVO getLearningHistory();

    /**
     * 获取用户答题统计信息
     * 返回答题数量和平均分数
     *
     * @return 包含答题数量和平均分数的Map
     */
    Map<String, Object> getQuizStatistics();

    /**
     * 后台查询用户列表（分页）
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<UserListVO> getUserList(UserListQueryDTO queryDTO);

    /**
     * 更新用户状态（禁用/启用）
     * @param userId 用户ID
     * @param status 目标状态（0-正常，1-禁用）
     */
    void updateUserStatus(Long userId, String status);

    /**
     * 重置用户密码
     * @param userId 用户ID
     */
    void resetUserPassword(Long userId);

    /**
     * 刷新Token
     * @param refreshToken 刷新令牌
     * @return 新的token和refreshToken
     */
    Map<String, Object> refreshToken(String refreshToken);
}




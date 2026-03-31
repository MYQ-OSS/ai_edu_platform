package top.mayiqin.ai_edu_platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.mayiqin.ai_edu_platform.entity.po.User;
import top.mayiqin.ai_edu_platform.service.UserService;
import top.mayiqin.ai_edu_platform.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author m'y'q
* @description 针对表【t_user(用户基础信息表)】的数据库操作Service实现
* @createDate 2026-03-31 20:55:09
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}





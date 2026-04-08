package top.mayiqin.ai_edu_platform.exception;

import lombok.EqualsAndHashCode;

/**
 * 账号或密码错误异常
 * 用于用户登录时认证失败的场景
 * @author m'y'q
 */
@EqualsAndHashCode(callSuper = true)
public class LoginFailedException extends BusinessException {

    /**
     * 构造登录失败异常
     *
     * @param message 错误消息
     */
    public LoginFailedException(String message) {
        super(401, message);
    }
}

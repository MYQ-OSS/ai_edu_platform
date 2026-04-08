package top.mayiqin.ai_edu_platform.exception;

import lombok.EqualsAndHashCode;

/**
 * 账号已存在异常
 * 用于用户注册时账号重复的场景
 * @author m'y'q
 */
@EqualsAndHashCode(callSuper = true)
public class AccountAlreadyExistsException extends BusinessException {

    /**
     * 构造账号已存在异常
     *
     * @param message 错误消息
     */
    public AccountAlreadyExistsException(String message) {
        super(409, message);
    }
}

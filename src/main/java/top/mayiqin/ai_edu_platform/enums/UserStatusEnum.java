package top.mayiqin.ai_edu_platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 * @author m'y'q
 */
@Getter
@AllArgsConstructor
public enum UserStatusEnum {
    
    /**
     * 正常状态
     */
    NORMAL("0", "正常"),
    
    /**
     * 禁用状态
     */
    DISABLED("1", "禁用");
    
    /**
     * 状态代码
     */
    private final String code;
    
    /**
     * 状态描述
     */
    private final String desc;
    
    /**
     * 根据代码获取枚举
     */
    public static UserStatusEnum fromCode(String code) {
        if (code == null) {
            return NORMAL; // 默认返回正常状态
        }
        for (UserStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return NORMAL; // 未知状态默认为正常
    }
}

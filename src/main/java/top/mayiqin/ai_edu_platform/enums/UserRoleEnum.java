package top.mayiqin.ai_edu_platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色枚举
 * @author m'y'q
 */
@Getter
@AllArgsConstructor
public enum UserRoleEnum {
    
    /**
     * 普通用户
     */
    USER("user", "普通用户"),
    
    /**
     * 管理员
     */
    ADMIN("admin", "管理员");
    
    /**
     * 角色代码
     */
    private final String code;
    
    /**
     * 角色描述
     */
    private final String desc;
    
    /**
     * 根据代码获取枚举
     */
    public static UserRoleEnum fromCode(String code) {
        if (code == null) {
            return USER; // 默认返回普通用户
        }
        for (UserRoleEnum role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return USER; // 未知角色默认为普通用户
    }
}

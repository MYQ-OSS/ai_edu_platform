package top.mayiqin.ai_edu_platform.annotation;

import java.lang.annotation.*;

/**
 * 管理员权限注解
 * 用于标记需要管理员权限才能访问的接口
 * @author m'y'q
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireAdmin {
    /**
     * 权限描述
     */
    String value() default "需要管理员权限";
}

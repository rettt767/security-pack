package ltd.jellyfish.security;

import java.lang.annotation.*;


/**
 * 使用此注解来实现Security的快速配置
 * - @value: role 当前url需要的角色权限
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Secure {
    public String[] value() default "USER";
}

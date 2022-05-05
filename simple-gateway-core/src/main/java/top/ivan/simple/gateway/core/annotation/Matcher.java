package top.ivan.simple.gateway.core.annotation;

import java.lang.annotation.*;

/**
 * @author Ivan
 * @description
 * @date 2020/7/23
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Matchers.class)
public @interface Matcher {

    String[] urls() default {};

    String[] methods() default {};

    String[] headers() default {};
}

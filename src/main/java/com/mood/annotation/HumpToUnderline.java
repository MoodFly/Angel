package com.mood.annotation;

import java.lang.annotation.*;

/**
 * @Auther: Mood
 * @Date: 2019-05-22 11:45:56
 * @Description: 请求类注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HumpToUnderline {
    String value() default "";
}

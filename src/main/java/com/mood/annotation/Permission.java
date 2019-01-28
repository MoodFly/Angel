package com.mood.annotation;

import java.lang.annotation.*;
/**
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 权限码注解
 * @version: 1.0
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Permission {
    String Param() default "";
}

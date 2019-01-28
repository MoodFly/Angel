package com.mood.annotation;

import java.lang.annotation.*;
/**
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 导出列注解
 * @version: 1.0
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Column {
    String Param() default "";
}

package com.mood.annotation;

import java.lang.annotation.*;
/**
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 导出文件注解
 * @version: 1.0
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SheetName {
    String Param() default "";
}

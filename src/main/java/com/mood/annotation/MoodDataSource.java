package com.mood.annotation;

import com.mood.angelenum.DataSourceType;

import java.lang.annotation.*;
/**
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 数据源注解
 * @version: 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MoodDataSource {
    DataSourceType value() default DataSourceType.WRITE;
}

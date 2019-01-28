package com.mood.aspect;

import com.mood.annotation.MoodDataSource;
import com.mood.constact.ApplicationCode;
import com.mood.angelenum.DataSourceType;
import com.mood.exception.AngelException;
import com.mood.utils.DatabaseContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author: by Mood
 * @date: 2018-8-30 11:11:11
 * @Description: 动态数据源切换切面
 * @version: 1.0
 */
@Aspect
@Component
public class MultiDataSourceAspect {
    @Around("@annotation(moodDataSource)" )
    public Object invokeMethod(ProceedingJoinPoint joinPoint, MoodDataSource moodDataSource) throws Throwable {
        try{
            if (moodDataSource.value().getValue().equals(DataSourceType.getValue("write"))){
                DatabaseContextHolder.setDatabaseType(DataSourceType.WRITE);
            }else if (moodDataSource.value().getValue().equals(DataSourceType.getValue("read"))){
                DatabaseContextHolder.setDatabaseType(DataSourceType.READ);
            }else {
                throw new AngelException(ApplicationCode.unSwitchDataSource);
            }
            return joinPoint.proceed();//调用目标方法
        }catch (Exception e){
            throw new AngelException(ApplicationCode.unSwitchDataSource);
        }finally {
            DatabaseContextHolder.clear();
        }
    }

}

package com.mood.aspect;

import com.mood.annotation.MoodDataSource;
import com.mood.constact.ApplicationCode;
import com.mood.angelenum.DataSourceType;
import com.mood.exception.AngelException;
import com.mood.utils.DatabaseContextHolder;
import com.mood.utils.DingDingNotifyUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    DingDingNotifyUtil dingDingNotifyUtil;
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiDataSourceAspect.class) ;
    @Around("@annotation(moodDataSource)" )
    public Object invokeMethod(ProceedingJoinPoint joinPoint, MoodDataSource moodDataSource) throws Throwable {
        try{
            Class clazz = joinPoint.getTarget().getClass();
            if (moodDataSource.value().getValue().equals(DataSourceType.getValue("write"))){
                DatabaseContextHolder.setDatabaseType(DataSourceType.WRITE);
            }else if (moodDataSource.value().getValue().equals(DataSourceType.getValue("read"))){
                DatabaseContextHolder.setDatabaseType(DataSourceType.READ);
            }else if (clazz.isAnnotationPresent(MoodDataSource.class)){
                MoodDataSource dataSourceAnnotation=(MoodDataSource)clazz.getAnnotation(MoodDataSource.class);
                DatabaseContextHolder.setDatabaseType(dataSourceAnnotation.value());
            }else {
                DatabaseContextHolder.setDatabaseType(DataSourceType.READ);
            }
            LOGGER.info("Choose DataSource:{} ",DatabaseContextHolder.getDatabaseType());
            return joinPoint.proceed();
        }catch (Exception e){
            dingDingNotifyUtil.sendDingDingMessage(e);
            throw new AngelException(ApplicationCode.unSwitchDataSource);
        }finally {
            DatabaseContextHolder.clear();
        }
    }

}

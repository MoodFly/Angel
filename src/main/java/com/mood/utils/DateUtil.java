package com.mood.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 *
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 时间工具包
 * @version: 1.0
 */
public class DateUtil {

    public static String defaultDateTimeFormatter="yyyy-MM-dd HH:mm:ss";

    public static String TIME_FORMAT_LONG="yyyyMMddHHmmss";

    /**
     *
     * @param dateTimeFormatter
     * @author: by Mood
     * @date: 2011-01-14 11:11:11
     * @Description: 获取当天开始时间
     * @version: 1.0
     */
    public static String getDayTime(String dateTimeFormatter){
        if (dateTimeFormatter==null||dateTimeFormatter.equals("")){
            dateTimeFormatter=DateUtil.defaultDateTimeFormatter;
        }
        return (LocalDateTime.of(LocalDate.now(), LocalTime.MIN)).format(DateTimeFormatter.ofPattern(dateTimeFormatter));
    }
    /**
     *
     * @param dateTimeFormatter
     * @author: by Mood
     * @date: 2011-01-14 11:11:11
     * @Description: 获取当天结束时间
     * @version: 1.0
     */
    public static String getDayEndTime(String dateTimeFormatter){
        if (dateTimeFormatter==null||dateTimeFormatter.equals("")){
            dateTimeFormatter=DateUtil.defaultDateTimeFormatter;
        }
        return (LocalDateTime.of(LocalDate.now(), LocalTime.MAX)).format(DateTimeFormatter.ofPattern(dateTimeFormatter));
    }
    /**
     *
     * @param dateTimeFormatter
     * @author: by Mood
     * @date: 2011-01-14 11:11:11
     * @Description: 获取本月开始时间
     * @version: 1.0
     */
    public static String getMonthStartTime(String dateTimeFormatter){
        if (dateTimeFormatter==null||dateTimeFormatter.equals("")){
            dateTimeFormatter=DateUtil.defaultDateTimeFormatter;
        }
        return  (LocalDateTime.of(LocalDateTime.now().getYear(),LocalDateTime.now().getMonth(),1,0,0)).format(DateTimeFormatter.ofPattern(dateTimeFormatter));
    }
    /**
     *
     * @param dateTimeFormatter
     * @author: by Mood
     * @date: 2011-01-14 11:11:11
     * @Description: 获取本月结束时间
     * @version: 1.0
     */
    public static String getMonthEndTime(String dateTimeFormatter){
        if (dateTimeFormatter==null||dateTimeFormatter.equals("")){
            dateTimeFormatter=DateUtil.defaultDateTimeFormatter;
        }
        return (LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth())).format(DateTimeFormatter.ofPattern(dateTimeFormatter));
    }
    /**
     *
     * @param dateTimeFormatter
     * @author: by Mood
     * @date: 2011-01-14 11:11:11
     * @Description: 获取当天时间
     * @version: 1.0
     */
    public static String getDay(String dateTimeFormatter){
        if (dateTimeFormatter==null||dateTimeFormatter.equals("")){
            dateTimeFormatter=DateUtil.defaultDateTimeFormatter;
        }
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormatter));
    }
    /**
     *
     * @param dateTimeFormatter
     * @author: by Mood
     * @date: 2011-01-14 11:11:11
     * @Description: 线程安全转换时间
     * @version: 1.0
     */
    public static String dateFormat(Date source,String dateTimeFormatter){
        if (dateTimeFormatter==null||dateTimeFormatter.equals("")){
            dateTimeFormatter=DateUtil.defaultDateTimeFormatter;
        }
        return  LocalDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(dateTimeFormatter));
    }
    /**
     *
     * @param dateTimeFormatter
     * @author: by Mood
     * @date: 2011-01-14 11:11:11
     * @Description: 线程安全转换时间
     * @version: 1.0
     */
    public static Date convertDate(String source,String dateTimeFormatter){
        if (dateTimeFormatter==null||dateTimeFormatter.equals("")){
            dateTimeFormatter=DateUtil.defaultDateTimeFormatter;
        }
        return  Date.from(LocalDateTime.parse(source,DateTimeFormatter.ofPattern(dateTimeFormatter)).atZone(ZoneId.systemDefault()).toInstant());
    }
    /**
     *
     * @param dateTimeFormatter
     * @author: by Mood
     * @date: 2011-01-14 11:11:11
     * @Description: 获取未来几天时间。默认往后一天
     * @version: 1.0
     */
    public static String getFutureTime(int pulsNo,String dateTimeFormatter){
        if (dateTimeFormatter==null||dateTimeFormatter.equals("")){
            dateTimeFormatter=DateUtil.defaultDateTimeFormatter;
        }
        return  LocalDateTime.now().plusDays(pulsNo<0?1:pulsNo).format(DateTimeFormatter.ofPattern(dateTimeFormatter));
    }
}

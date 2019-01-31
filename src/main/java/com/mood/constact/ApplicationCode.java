package com.mood.constact;

import com.mood.applicationcode.ErrorCode;
/**
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 系统使用的常量
 * @version: 1.0
 */

public interface ApplicationCode {
    ErrorCode unPermission=new ErrorCode(4401,"当前用户无权限操作");
    ErrorCode unSwitchDataSource=new ErrorCode(5501,"切换数据源错误");
    ErrorCode unCreateRedisPool=new ErrorCode(5502,"无法创建RedisPool");;
    ErrorCode unConnnectRedis=new ErrorCode(5503,"无法连接Redis");
    ErrorCode unParamException=new ErrorCode(5504,"参数校验失败");
    ErrorCode errorRequestUrl=new ErrorCode(5505,"请求地址校验失败");
    ErrorCode errorRequestResult=new ErrorCode(5506,"返回结果错误");
}

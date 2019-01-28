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
}

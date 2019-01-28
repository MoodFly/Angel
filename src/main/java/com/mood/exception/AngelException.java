package com.mood.exception;

import com.mood.applicationcode.ErrorCode;
import lombok.Getter;
import lombok.Setter;
/**
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 系统自定义异常
 * @version: 1.0
 */

@Setter
@Getter
public class AngelException extends RuntimeException {
    private int status;
    private String description = "";
    public AngelException(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.description = errorCode.getCodeMessage();
    }
}

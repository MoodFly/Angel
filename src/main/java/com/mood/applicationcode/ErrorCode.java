package com.mood.applicationcode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 系统使用错误码
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorCode {
    private int status;
    private String codeMessage;
}

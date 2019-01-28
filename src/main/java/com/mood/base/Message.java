package com.mood.base;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 消息推送基类 用于所有操作。可自行拓展
 * @version: 1.0
 */
@Data
public class Message<T> implements Serializable {
    private String messageId;
    private T messageValue;
}

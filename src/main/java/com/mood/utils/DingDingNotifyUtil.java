package com.mood.utils;

import com.google.common.collect.Maps;
import com.mood.notify.DingDingNotifyMessageText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: by Mood
 * @date: 2019-02-01 01:48:23
 * @Description: 钉钉报警工具包
 * @version: 1.0
 */
@Component
public class DingDingNotifyUtil {
    private final static Logger logger = LoggerFactory.getLogger(DingDingNotifyUtil.class);
    @Value("${dingding.webhookUrl.monitor_dingding_broadcast}")
    private String DEFAULT_PATH;
    @Value("${dingding.webhookUrl.phones}")
    private String phone;
    private final static HttpClientUtil httpClientUtil=HttpClientUtil.getInstance();

    /**
     *
     * @param path 钉钉webhook路径
     * @param json 发送的消息
     */
    public void sendDingDingMessage(String path,String json){
        httpClientUtil.httpPostMethod(path, json,"application/json" , Maps.newHashMap(), Maps.newHashMap(),"UTF-8");
    }

    /**
     *
     * @param json 发送的消息
     */
    public  void sendDingDingMessage(String json){
       sendDingDingMessage(DEFAULT_PATH,json);
    }
    /**
     * @param throwable 异常信息
     */
    public  void sendDingDingMessage(Throwable throwable){
        sendDingDingMessage(DEFAULT_PATH, DingDingNotifyMessageText.createTextMessage(throwable.getMessage(),phone));
    }
    /**
     * @param exception 异常信息
     */
    public  void sendDingDingMessage(Exception exception){
        sendDingDingMessage(DEFAULT_PATH, DingDingNotifyMessageText.createTextMessage(exception.getMessage(),phone));
    }
}

package com.mood.notify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.Arrays;

@Data
public class DingDingNotifyMessageText extends DingDingNotifyMessage {
    public static final String type="text";
    private String text;
    /**
     * @Description: 创建text message消息
     * @param wordString  消息正文
     * @param phone 多个手机号 增加@用户功能
     * @return MarkDown message消息
     */
    public static String createTextMessage(String wordString,String ...phone)  {
        DingDingNotifyMessageText dingDingNotifyMessageText=new DingDingNotifyMessageText();
        dingDingNotifyMessageText.setMsgtype(DingDingNotifyMessageText.type);
        dingDingNotifyMessageText.setText(wordString);
        ObjectMapper mapper = new ObjectMapper();
        DingDingNotifyAT at=new DingDingNotifyAT();
        at.setAtMobiles(Arrays.asList(phone));at.setIsAtAll("true");
        dingDingNotifyMessageText.setAt(at);
        try {
            return mapper.writeValueAsString(dingDingNotifyMessageText);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }
}

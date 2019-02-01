package com.mood.notify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.Arrays;

@Data
public class DingDingNotifyMessageMarkdown extends DingDingNotifyMessage {
    public static final String type="markdown";
    private DingDingNotifyMarkdown markdown;
    public String createTitle(String title,int level){
        StringBuffer stringBuffer=new StringBuffer();
        switch(level){
            case 1:stringBuffer.append("# ").append(title);break;
            case 2:stringBuffer.append("## ").append(title);break;
            case 3:stringBuffer.append("### ").append(title);break;
            case 4:stringBuffer.append("#### ").append(title);break;
            case 5:stringBuffer.append("##### ").append(title);break;
            case 6:stringBuffer.append("##### ").append(title);break;
        }
        return stringBuffer.append("\n ").toString();
    }
    /**
     * @param reference 引用
     * @return
     */
    public String createReference(String reference){
        return new StringBuffer().append("> ").append(reference).append("\n ").toString();
    }
    /**
     * @param source 黑体字
     * @return
     */
    public String createBlackbody(String source){
        return new StringBuffer().append("**").append(source).append("**").toString();
    }
    /**
     * @param source 黑体字
     * @return
     */
    public String createItalics(String source){
        return new StringBuffer().append("*").append(source).append("*").toString();
    }

    /**
     * @param title 图片OR链接的title
     * @param path 图片OR链接的路径
     * @param imageOrLink 是否是图片(图片为true，链接为false)
     * @return
     */
    public String createImageOrLink(String title,String path,boolean imageOrLink){
        StringBuffer stringBuffer=new StringBuffer();
        if (imageOrLink){
            return stringBuffer.append("\n ").append("![").append(title).append("](").append(path).append(")").append("\n ").toString();
        }else {
            return stringBuffer.append("\n ").append("[").append(title).append("](").append(path).append(")").append("\n ").toString();
        }
    }

    /**
     * @Description: 创建MarkDown message消息
     * @param title 文章标题
     * @param level 标题级别
     * @param word  文章正文
     * @param imageTitle 图片标题
     * @param imagePath  图片url
     * @param phone 多个手机号 增加@用户功能
     * @return MarkDown message消息
     * @throws JsonProcessingException
     */
    public static String createMarkDownMessage(String title,int level,String word,String imageTitle,String imagePath,String ...phone) throws JsonProcessingException {
        DingDingNotifyMessageMarkdown dingDingNotifyMessageMarkdown=new DingDingNotifyMessageMarkdown();
        dingDingNotifyMessageMarkdown.setMsgtype(DingDingNotifyMessageMarkdown.type);
        StringBuffer sb=new StringBuffer()
        .append(dingDingNotifyMessageMarkdown.createTitle(title,level))
        .append(dingDingNotifyMessageMarkdown.createReference(dingDingNotifyMessageMarkdown.createItalics(word)))
//        .append(dingDingNotifyMessageMarkdown.createImageOrLink("链接","https://open-doc.dingtalk.com/docs/doc.htm?spm=a219a.7629140.0.0.21364a97up3QcZ&docType=1&articleId=105735&treeId=257&platformId=34",false))
        .append(dingDingNotifyMessageMarkdown.createImageOrLink(imageTitle,imagePath,true));
        DingDingNotifyMarkdown dingDingNotifyMarkdown=new DingDingNotifyMarkdown();
        dingDingNotifyMarkdown.setTitle(dingDingNotifyMessageMarkdown.createTitle(title,level));
        dingDingNotifyMarkdown.setText(sb.toString());
        dingDingNotifyMessageMarkdown.setMarkdown(dingDingNotifyMarkdown);
        ObjectMapper mapper = new ObjectMapper();
        DingDingNotifyAT at=new DingDingNotifyAT();
        at.setAtMobiles(Arrays.asList(phone));at.setIsAtAll("true");
        dingDingNotifyMessageMarkdown.setAt(at);
        return mapper.writeValueAsString(dingDingNotifyMessageMarkdown);
    }
}

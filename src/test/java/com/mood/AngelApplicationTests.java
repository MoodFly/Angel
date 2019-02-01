package com.mood;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mood.notify.DingDingNotifyAT;
import com.mood.notify.DingDingNotifyMarkdown;
import com.mood.notify.DingDingNotifyMessageMarkdown;
import com.mood.utils.HttpClientUtil;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.collections.Maps;

import java.util.Arrays;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AngelApplicationTests {

	@Value("${dingding.webhookUrl.monitor_dingding_broadcast}")
	private String path;
	@Test
	public void testHttpPost() throws JsonProcessingException {

		HttpClientUtil httpClientUtil=HttpClientUtil.getInstance();
        DingDingNotifyMessageMarkdown dingDingNotifyMessageMarkdown=new DingDingNotifyMessageMarkdown();
        dingDingNotifyMessageMarkdown.setMsgtype(DingDingNotifyMessageMarkdown.type);
        StringBuffer sb=new StringBuffer();
		sb.append(dingDingNotifyMessageMarkdown.createTitle("杭州天气",4))
                .append(dingDingNotifyMessageMarkdown.createReference(dingDingNotifyMessageMarkdown.createItalics("西北风1级，空气良89，相对温度73%")))
                .append(dingDingNotifyMessageMarkdown.createImageOrLink("链接","https://open-doc.dingtalk.com/docs/doc.htm?spm=a219a.7629140.0.0.21364a97up3QcZ&docType=1&articleId=105735&treeId=257&platformId=34",false))
                .append(dingDingNotifyMessageMarkdown.createImageOrLink("图片","http://i01.lw.aliimg.com/media/lALPBbCc1ZhJGIvNAkzNBLA_1200_588.png",true));
		DingDingNotifyMarkdown dingDingNotifyMarkdown=new DingDingNotifyMarkdown();
		dingDingNotifyMarkdown.setTitle(dingDingNotifyMessageMarkdown.createTitle("杭州天气",4));
		dingDingNotifyMarkdown.setText(sb.toString());
        dingDingNotifyMessageMarkdown.setMarkdown(dingDingNotifyMarkdown);
		ObjectMapper mapper = new ObjectMapper();
		DingDingNotifyAT at=new DingDingNotifyAT();
		at.setAtMobiles(Arrays.asList("17600738048"));at.setIsAtAll("true");
		dingDingNotifyMessageMarkdown.setAt(at);
		String json = mapper.writeValueAsString(dingDingNotifyMessageMarkdown);
		httpClientUtil.httpPostMethod(path, json,"application/json" ,Maps.newHashMap(), Maps.newHashMap(),"UTF-8");
	}
	@Test
	public void testGttpGet() {
		HttpClientUtil httpClientUtil=HttpClientUtil.getInstance();
		httpClientUtil.httpGetMethod(path, Maps.newHashMap(), Maps.newHashMap(),"UTF-8");
	}

}


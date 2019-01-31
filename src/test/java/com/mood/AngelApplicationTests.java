package com.mood;

import com.mood.utils.HttpClientUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.collections.Maps;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AngelApplicationTests {

	@Value("${dingding.webhookUrl.monitor_dingding_broadcast}")
	private String path;
	@Test
	public void testHttpPost() {

		HttpClientUtil httpClientUtil=HttpClientUtil.getInstance();
		httpClientUtil.httpPostMethod(path,"{\n" +
				"     \"msgtype\": \"text\",\n" +
				"     \"text\": {\n" +
				"         \"content\": \"黄佳星,  李茂 都是是不一样的烟火\"\n" +
				"     },\n" +
				"     \"at\": {\n" +
				"         \"atMobiles\": [\n" +
				"             \"17600738048\"\n" +
				"         ], \n" +
				"         \"isAtAll\": false\n" +
				"     }\n" +
				" }","application/json" ,Maps.newHashMap(), Maps.newHashMap(),"UTF-8");
	}
	@Test
	public void testGttpGet() {
		HttpClientUtil httpClientUtil=HttpClientUtil.getInstance();
		httpClientUtil.httpGetMethod(path, Maps.newHashMap(), Maps.newHashMap(),"UTF-8");
	}

}


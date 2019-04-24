package com.mood;

import com.mood.base.AngelInstance;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;

@SpringBootApplication
@EnableSwagger2
@EnableFeignClients
@MapperScan(basePackages = "com.mood.example.dao")
public class AngelApplication {
	private final static Logger logger = LoggerFactory.getLogger(AngelApplication.class);
	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(AngelApplication.class);
		springApplication.addListeners(new ApplicationListener<ApplicationEvent>() {
			@Override
			public void onApplicationEvent(ApplicationEvent applicationEvent) {
				try {
					/**
					 * 当业务数据增加的时候，需要实时掌握服务端的运行状况，比如JVM信息，或者程序运行过程中的接口级别的访问量，
					 * 错误数据，调用异常等。参照canal引入的普罗米修斯，在angel中也增加进来。在应用程序容器启动之前，程序运行时实例预先启动，
					 * todo AngelInstance配合Controller切面，权限切面，数据源切面，幂等切面可以收集信息，在之后增加。
					 */
					AngelInstance.getInstance();
				} catch (IOException e) {
					logger.error("启动Angel实例错误");
				}
			}
		});
		springApplication.run(args);
	}

}


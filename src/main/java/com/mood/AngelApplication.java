package com.mood;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sql.DataSource;

@SpringBootApplication
@EnableSwagger2
@EnableFeignClients
@MapperScan(basePackages = "com.mood.example.dao")
public class AngelApplication {

	public static void main(String[] args) {
		SpringApplication.run(AngelApplication.class, args);
	}

}


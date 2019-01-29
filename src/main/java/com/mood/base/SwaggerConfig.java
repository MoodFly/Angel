package com.mood.base;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spring.web.plugins.Docket;

import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@Configuration()
@Profile(value = {"testing", "development"})
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.mood"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Angel Tool")
                .description("Create Angel Tools By Mood")
                .termsOfServiceUrl("")
                .version("1.0")
                .build();
    }
}

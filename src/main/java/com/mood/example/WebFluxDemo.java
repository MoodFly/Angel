package com.mood.example;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
@RestController
@Api(value = "测试接口")
public class WebFluxDemo {
        @GetMapping("/hello")
        @ApiOperation(value = "获取当前登陆人信息")
        public Mono<String> hello() {   // 【改】返回类型为Mono<String>
            return Mono.just("Welcome to reactive world ~");     // 【改】使用Mono.just生成响应式数据
        }

}

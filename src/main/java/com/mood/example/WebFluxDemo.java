package com.mood.example;

import com.mood.base.User;
import com.mood.example.model.ExampleModel;
import com.mood.example.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
@RestController
@Api(value = "测试接口")
public class WebFluxDemo {
    @Autowired
    UserService userService;
        @GetMapping("/hello/{id}")
        @ApiOperation(value = "查询信息")
        public Mono<ExampleModel> query(@PathVariable("id") long id) {   // 【改】返回类型为Mono<String>
            return Mono.just(userService.queryOne(id));     // 【改】使用Mono.just生成响应式数据
        }

}

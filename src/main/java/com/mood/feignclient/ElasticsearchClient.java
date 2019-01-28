package com.mood.feignclient;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@FeignClient(url = "${elasticsearch.host}",name = "elasticsearchClient")
public interface ElasticsearchClient {
    @RequestMapping(value = "/", method = GET)
    String hello();
}

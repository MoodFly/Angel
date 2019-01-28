package com.mood.utils;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.Callable;
/**
 *
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 异步工具包
 * @version: 1.0
 */
public class ReactorUtil<T> {
    /**
     *
     * @param supplier
     * @author: by Mood
     * @date: 2011-01-14 11:11:11
     * @Description: 提交异步任务
     * @version: 1.0
     */
     public void asyncRunJob(Callable<? extends T> supplier){
         Mono.fromCallable(supplier)
             .subscribeOn(Schedulers.elastic())
             .subscribe(System.out::println);
     }
}

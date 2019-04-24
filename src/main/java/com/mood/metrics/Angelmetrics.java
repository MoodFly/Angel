package com.mood.metrics;


import com.mood.base.AngelInstance;

/**
 * @author: by Mood
 * @date: 2019-04-24 14:33:17
 * @Description: 指标收集接口
 * @version: 1.0
 */
public interface Angelmetrics {
    /**
     * 初始化指标服务
     */
    void initialize();
    /**
     * 清除指标收集
     */
    void terminate();

    void register(AngelInstance instance);

    void unregister(AngelInstance instance);

    void setServerPort(int port);
}

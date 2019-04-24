package com.mood.metrics;

import com.mood.base.AngelInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO 待完善 需要测试。目前没时间了。先罗列主要方法。后续改进，摸鱼要被发现了
 * @author: by Mood
 * @date: 2019-04-24 14:33:17
 * @Description: 指标收集链接到普罗米修斯
 * @version: 1.0
 */
public class PrometheusService implements Angelmetrics {
    private static class SingletonHolder {
        private static final PrometheusService SINGLETON = new PrometheusService();
    }
    public static PrometheusService getInstance() {
        return SingletonHolder.SINGLETON;
    }
    private final static Logger logger = LoggerFactory.getLogger(PrometheusService.class);
    /**
     * 初始化指标服务
     */
    @Override
    public void initialize() {

    }
    /**
     * 清除指标收集
     */
    @Override
    public void terminate() {

    }
    @Override
    public void register(AngelInstance instance) {

    }
    @Override
    public void unregister(AngelInstance instance) {

    }
    @Override
    public void setServerPort(int port) {

    }
}

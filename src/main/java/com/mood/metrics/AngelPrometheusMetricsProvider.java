package com.mood.metrics;
/**
 * @author: by Mood
 * @date: 2019-04-24 14:33:17
 * @Description: 加载普罗米修斯指标收集
 * @version: 1.0
 */
public class AngelPrometheusMetricsProvider implements  AngelMetricsProvider{
    @Override
    public Angelmetrics getService() {
        return PrometheusService.getInstance();
    }
}

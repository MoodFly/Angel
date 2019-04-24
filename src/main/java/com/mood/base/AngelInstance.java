package com.mood.base;

import com.mood.applicationcode.AngelConstants;
import com.mood.metrics.AngelMetricsProvider;
import com.mood.metrics.Angelmetrics;
import com.mood.utils.JDBCUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;

/**
 * @author: by Mood
 * @date: 2019-04-24 14:37:06
 * @Description: 系统运行 收集的全部指标
 * @version: 1.0
 */
@Slf4j
public class AngelInstance {
    private static volatile AngelInstance angelInstance=null;
    private static final String CLASSPATH_URL_PREFIX = "classpath:";
    private static Angelmetrics angelmetrics=null;

    /**
     * 创建AngelInstance实例，加载配置文件读取普罗米修斯的配置信息。然后尝试spi加载普罗米修斯收集服务
     * @return
     * @throws IOException
     */
    public static AngelInstance getInstance() throws IOException {
        if (angelInstance!=null){
            synchronized (AngelInstance.class){
                if (angelInstance!=null){
                    log.info("## load canal configurations");
                    String conf = System.getProperty("angel.conf", "classpath:angel.properties");
                    Properties properties = new Properties();
                    if (conf.startsWith(CLASSPATH_URL_PREFIX)) {
                        conf = StringUtils.substringAfter(conf, CLASSPATH_URL_PREFIX);
                        properties.load(JDBCUtils.class.getClassLoader().getResourceAsStream(conf));
                    } else {
                        properties.load(new FileInputStream(conf));
                    }
                    angelInstance=new AngelInstance();
                    angelInstance.createMetricCollector(properties);
                    return angelInstance;
                }
            }
        }
        return angelInstance;
    }

    /**
     * 通过spi加载普鲁米修斯收集服务，加载成功就去初始化
     * @param properties
     */
    private void createMetricCollector(Properties properties) {
        if (angelmetrics==null){
            ServiceLoader<AngelMetricsProvider> providers = ServiceLoader.load(AngelMetricsProvider.class);
            List<AngelMetricsProvider> list = new ArrayList<>();
            for (AngelMetricsProvider provider : providers) {
                list.add(provider);
            }
            if (!list.isEmpty()) {
                if (list.size() > 1) {
                    //spi加载发现多个实现，需要暴露出来，打印在错误日志
                    for (AngelMetricsProvider p : list) {
                        log.error("Found CanalMetricsProvider: {}.", p.getClass().getName());
                    }
                }
                //目前仅仅接入单个收集服务，所以取第一个，
                AngelMetricsProvider provider = list.get(0);
                angelmetrics = provider.getService();
                angelmetrics.setServerPort(Integer.parseInt(getProperty(properties, AngelConstants.ANGEL_METRIC_PORT)));
                angelmetrics.initialize();
            }
        }
    }
    /**
     * 读取配置项
     * @param properties
     * @param key
     * @return
     */
    public static String getProperty(Properties properties, String key) {
        key = StringUtils.trim(key);
        String value = System.getProperty(key);
        if (value == null) {
            value = System.getenv(key);
        }
        if (value == null) {
            value = properties.getProperty(key);
        }
        return StringUtils.trim(value);
    }
}

package com.mood.base;

import com.mood.constact.ApplicationCode;
import com.mood.exception.AngelException;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import javax.annotation.PreDestroy;

/**
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: redis 配置
 * @version: 1.0
 */
@Configuration
public class RedisConfig {
    private static JedisPool jedisPool;
    private final static Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Getter
    @Setter
    @Configuration
    class Config{
        private String host;
        private Integer port;
        private String password;
        private Integer database;
    }
    @Bean(name="redisConfigProperties")
    @ConfigurationProperties(prefix = "mood.redis")
    public Config redisConfig(){
        return new Config();
    }

    @Bean(name="jedis")
    public Jedis createJedis(@Qualifier("redisConfigProperties") Config prop){
        connect(prop);
        if (null == jedisPool) {
            throw new AngelException(ApplicationCode.unCreateRedisPool);
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
        } catch (JedisConnectionException e) {
            logger.error("redis connection error", e);
            throw new AngelException(ApplicationCode.unConnnectRedis);
        }
        return jedis;
    }
    private synchronized static void connect(Config prop) {
        if (jedisPool != null && !jedisPool.isClosed()) return;        // 防止重复连接
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(1000);
        jedisPoolConfig.setMaxIdle(100);
        jedisPool = new JedisPool(new JedisPoolConfig(), prop.getHost(), prop.getPort(), 10000, prop.getPassword(), prop.getDatabase());
    }

    @PreDestroy
    public void close() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
        }
    }
}

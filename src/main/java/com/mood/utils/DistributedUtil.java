package com.mood.utils;

import com.alibaba.druid.util.StringUtils;
import com.mood.constact.ApplicationCode;
import com.mood.exception.AngelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author: by Mood
 * @date: 2019-05-22 14:19:27
 * @Description: 分布式锁操作工具类 支持 redis + Zookeeper
 * @version: 1.0
 */
public class DistributedUtil {

    private final static Logger logger = LoggerFactory.getLogger(DistributedUtil.class);
    private final static String OK="OK";
    private volatile static DistributedUtil distributedUtil=null;
    private DistributedUtil(){}
    private Predicate<Object> predicate=s-> s != null && !StringUtils.isEmpty(s.toString()) && OK.equals(s.toString());

    /**
     * 获取分布式锁工具类调用实例
     * @return
     */
    public DistributedUtil getInstance(){
        if (distributedUtil==null){
            distributedUtil=new DistributedUtil();
        }
        return distributedUtil;
    }
    /**
     *
     * @author: by Mood
     * @date: 2019-01-29 17:48:23
     * @Description: 获取锁
     * @version: 1.0
     */
    private boolean distributedRedisLock(Jedis jedis, String key, String value, Integer expire){
        if (jedis == null) {
            logger.error("get resource failed");
            return false;
        }
        Object s = operation(jedis,setnx(key, value, expire));
        return predicate.test(s);
    }
    /**
     *
     * @author: by Mood
     * @date: 2019-01-29 17:48:23
     * @Description: 释放锁
     * @version: 1.0
     */
    private boolean distributedRedisUnLock(Jedis jedis, String key){
        Object s = operation(jedis,(x) -> x.del(key));
        return predicate.test(s);
    }
    private  Object operation(Jedis jedis, Function<Jedis, Object> f) {
        if (f == null) {
            return null;
        }
        try {
            return f.apply(jedis);
        } catch (Exception e) {
            logger.error("redis operation failed", e);
            return null;
        } finally {
            jedis.close();
        }
    }
    private  Function<Jedis, Object> setnx(String key, String value, Integer expire) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value) || expire == null){
            throw new AngelException(ApplicationCode.unParamException);
        }
        return (Jedis jedis) -> jedis.set(key, value, "nx", "ex", expire);
    }
}


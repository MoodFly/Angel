package com.mood.utils;

import com.alibaba.druid.util.StringUtils;
import com.google.common.base.Function;
import com.google.common.collect.MigrateMap;
import lombok.Setter;
import redis.clients.jedis.Jedis;

import java.util.Map;

/**
 * @author: by Mood
 * @date: 2019-04-19 10:46:06
 * @Description: 使用redis监控幂等KEY工具类
 * @version: 1.0
 */
@Setter
public class RedisUtil {
    private static volatile Map<String, String> managerRequestKey; //管理KEY
    private static volatile Map<String, Boolean> monitorRequestKey; //监控KEY
    public static int expire=5;
    private static RedisUtil INSTANCE;
    private RedisUtil(Jedis jedis){
        monitorRequestKey= MigrateMap.makeComputingMap(new Function<String, Boolean>() {
            public Boolean apply(String requestKey) {
                return false;
            }
        });
        managerRequestKey= MigrateMap.makeComputingMap(new Function<String, String>() {
            public String apply(String requestKey) {
                String result=jedis.set(requestKey, requestKey, "nx", "ex", expire);
                if(result == null || StringUtils.isEmpty(result.toString())){
                    monitorRequestKey.put("requestKey",false);
                }else if("OK".equals(result)){
                    monitorRequestKey.put("requestKey",true);
                }
                return result;
            }
        });
    }
    @SuppressWarnings("deprecation")
    public static RedisUtil getInstance(Jedis jedis){
        if (INSTANCE!=null){
            synchronized (RedisUtil.class){
                if(INSTANCE==null){
                    INSTANCE=new RedisUtil(jedis);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 获取KEY，如果不存在 则设置进行，原子性。
     * @param requestKey
     */
    public boolean getManagerRequestKey(String requestKey){
        managerRequestKey.get(requestKey);
        return monitorRequestKey.get(requestKey);
    }

    /**
     * 删除Key
     * @param jedis
     * @param requestKey
     */
    public void deleteRequestKey(Jedis jedis,String requestKey){
        jedis.decr(requestKey);
    }
    /**
     * 获取监控key
     * @param requestKey
     */
    public boolean getMonitorRequestKey(String requestKey){
       return monitorRequestKey.get(requestKey);
    }
}

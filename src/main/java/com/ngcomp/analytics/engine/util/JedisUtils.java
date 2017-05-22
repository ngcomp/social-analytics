package com.ngcomp.analytics.engine.util;

import com.ngcomp.analytics.engine.conn.JedisConnectionPool;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Ram Parashar
 * Date: 8/26/13
 * Time: 9:28 PM
 */
public class JedisUtils {

    public static void setCount(String key, Integer counter){
        Jedis jedis = JedisConnectionPool.getResource();
        jedis.set(key, String.valueOf(counter));
        JedisConnectionPool.returnResource(jedis);
    }

    public static synchronized void decrement(String key){
        Jedis jedis = JedisConnectionPool.getResource();
        jedis.decr(key);
        JedisConnectionPool.returnResource(jedis);
    }


    /**
     *
     * @param marketId
     * @param brandId
     */
    public static Map<String, Float> getConstants(String marketId, String brandId){
        Jedis jedis = new Jedis("localhost");
        Map<String, String> keys = jedis.hgetAll(marketId + "_" + brandId);
        jedis.disconnect();

        Map<String, Float> map = new HashMap<String, Float>();

        map.put("Z_SCORE"          , Float.valueOf(keys.get("Z_SCORE")));
        map.put("AMPLIFIER_CHANNEL", Float.valueOf(keys.get("AMPLIFIER_CHANNEL")));
        map.put("DECAY"            , Float.valueOf(keys.get("DECAY")));
        map.put("AVG_STORY_DECAY"  , Float.valueOf(keys.get("AVG_STORY_DECAY")));
        map.put("W1"               , Float.valueOf(keys.get("W1")));
        map.put("W2"               , Float.valueOf(keys.get("W2")));
        map.put("W3"               , Float.valueOf(keys.get("W3")));

        return map;
    }


    /**
     *
     * @param id
     * @return
     */
    public static Map<String, Float> getConstants(String id){

        Jedis jedis = new Jedis("localhost");
        Map<String, String> keys = jedis.hgetAll(id + "_SCORES");
        jedis.disconnect();

        Map<String, Float> map = new HashMap<String, Float>();

        map.put("Z_SCORE"          , Float.valueOf(keys.get("Z_SCORE")));
        map.put("AMPLIFIER_CHANNEL", Float.valueOf(keys.get("AMPLIFIER_CHANNEL")));
        map.put("DECAY"            , Float.valueOf(keys.get("DECAY")));
        map.put("AVG_STORY_DECAY"  , Float.valueOf(keys.get("AVG_STORY_DECAY")));
        map.put("W1"               , Float.valueOf(keys.get("W1")));
        map.put("W2"               , Float.valueOf(keys.get("W2")));
        map.put("W3"               , Float.valueOf(keys.get("W3")));

        return map;
    }


    public static Map<String, String> getMap(String id){

        Jedis jedis = new Jedis("localhost");
        Map<String, String> map = jedis.hgetAll(id);
        jedis.disconnect();

        return map;
    }

}

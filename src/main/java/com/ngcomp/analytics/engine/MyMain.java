package com.ngcomp.analytics.engine;

/**
 * Created by Ram Parashar on 5/21/17.
 */
import redis.clients.jedis.Jedis;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Ram Parashar
 * Date: 8/25/13
 * Time: 11:20 AM
 */
public class MyMain {

    public static final String Z_SCORE           = "Z_SCORE";
    public static final String AMPLIFIER_CHANNEL = "AMPLIFIER_CHANNEL";
    public static final String DECAY             = "DECAY";
    public static final String AVG_STORY_DECAY   = "AVG_STORY_DECAY";
    public static final String W1 = "W1";
    public static final String W2 = "W2";
    public static final String W3 = "W3";



    public static void setConstantsInRedis(){


        Map<String, String> map = new HashMap<String, String>();

        map.put(Z_SCORE          , "10");
        map.put(AMPLIFIER_CHANNEL, "10");
        map.put(DECAY            , "0.02");
        map.put(AVG_STORY_DECAY  , "0.02");
        map.put(W1               , "1");
        map.put(W2               , "1");
        map.put(W3               , "1");
        Jedis jedis = new Jedis("192.168.1.10");
        jedis.hmset("1_SCORES", map);
        jedis.quit();
    }

    public static void main(String...strings) throws DatatypeConfigurationException {

        setConstantsInRedis();

    }
}

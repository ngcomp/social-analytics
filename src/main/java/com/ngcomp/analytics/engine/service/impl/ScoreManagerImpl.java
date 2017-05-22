package com.ngcomp.analytics.engine.service.impl;

import com.google.common.base.Strings;
import com.ngcomp.analytics.engine.service.ScoreManager;
import com.restfb.json.JsonObject;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Ram Parashar
 * Date: 9/4/13
 * Time: 11:04 PM
 */
@Service("scoreManager")
public class ScoreManagerImpl implements ScoreManager {

    /**
     *
     * @param id
     * @param zScore
     * @param amplifierChannel
     * @param decay
     * @param avgStoryDecay
     * @param w1
     * @param w2
     * @param w3
     */
    @Override
    public void updateScores(String id, String zScore, String amplifierChannel, String decay, String avgStoryDecay, String w1, String w2, String w3) {
        if(!Strings.isNullOrEmpty(id)){
            Map<String, String> props = new HashMap<String, String>();

            if(!Strings.isNullOrEmpty(amplifierChannel)){
                props.put("AMPLIFIER_CHANNEL", amplifierChannel);
            }

            if(!Strings.isNullOrEmpty(decay)){
                props.put("DECAY", decay);
            }

            if(!Strings.isNullOrEmpty(zScore)){
                props.put("Z_SCORE", zScore);
            }

            if(!Strings.isNullOrEmpty(w3)){
                props.put("W3", w3);
            }

            if(!Strings.isNullOrEmpty(w1)){
                props.put("W1", w1);
            }

            if(!Strings.isNullOrEmpty(w2)){
                props.put("W2", w2);
            }

            if(!Strings.isNullOrEmpty(avgStoryDecay)){
                props.put("AVG_STORY_DECAY", avgStoryDecay);
            }

            Jedis jedis = new Jedis("localhost");
            jedis.hmset(id + "_SCORES", props);
            jedis.disconnect();

        }
    }

    @Override
    public JsonObject getScores(String id) {
        JsonObject json = new JsonObject();
        Jedis jedis = new Jedis("localhost");
        Map<String, String> scores = jedis.hgetAll(id + "_SCORES");
        jedis.quit();

        if(scores.isEmpty()){
           json = resetScores(id);
        }else{
            for(String key : scores.keySet()){
                json.put(key, scores.get(key));
            }
        }
        return json;
    }

    @Override
    public JsonObject resetScores(String id) {

        Map<String, String> scores = new HashMap<String, String>();

        scores.put("Z_SCORE"          , "10");
        scores.put("AMPLIFIER_CHANNEL", "10");
        scores.put("DECAY"            , "0.02");
        scores.put("AVG_STORY_DECAY"  , "0.02");
        scores.put("W1"               , "1");
        scores.put("W2"               , "1");
        scores.put("W3"               , "1");
        Jedis jedis = new Jedis("localhost");
        jedis.hmset(id + "_SCORES", scores);
        jedis.disconnect();

        JsonObject json = new JsonObject();
        for(String key : scores.keySet()){
            json.put(key, scores.get(key));
        }
        return json;
    }


}

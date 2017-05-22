package com.ngcomp.analytics.engine.service;

import com.restfb.json.JsonObject;

/**
 * User: Ram Parashar
 * Date: 9/4/13
 * Time: 10:59 PM
 */
public interface ScoreManager {

    void updateScores(String id, String zScore, String amplifierChannel, String decay, String avgStoryDecay, String w1, String w2, String w3);

    JsonObject getScores(String id);

    JsonObject resetScores(String id);

}

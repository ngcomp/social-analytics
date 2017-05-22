package com.ngcomp.analytics.engine.util;

import org.apache.log4j.Logger;

/**
 * TumblerUser: Ram Parashar
 * Date: 7/20/13
 * Time: 11:35 AM
 */
public class Constants {
    static Logger logger = Logger.getLogger(Constants.class);

    /////////////////////////////////////////////
    //Redis Configuration.
    /////////////////////////////////////////////
    public static final String DEFAULT_REDIS_HOST = "localhost";
    public static final String DEFAULT_REDIS_DB   = "2";
    public static final String DEFAULT_REDIS_PORT = "6379";

    public static final String REDIS_HOST = "redis_server";
    public static final String REDIS_DB   = "redis_db";
    public static final String REDIS_PORT = "redis_port";

    public static final String LOCAL_HOST = "localhost";

    public static final String ALL_KEYS = "ALL_KEYS";




    public static final String FB_POSTS_TABLE = "fb.posts.table";
    public static final String FB_COL_FAMILY  = "fb";

    public static final String CF     = "cf";

    public static final String JSON  = "application/json;charset=utf-8";


    public static final String Z_SCORE           = "Z_SCORE";
    public static final String AMPLIFIER_CHANNEL = "AMPLIFIER_CHANNEL";
    public static final String DECAY             = "DECAY";
    public static final String AVG_STORY_DECAY   = "AVG_STORY_DECAY";
    public static final String W1 = "W1";
    public static final String W2 = "W2";
    public static final String W3 = "W3";
}

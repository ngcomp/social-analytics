package com.ngcomp.analytics.engine.conn;

import com.ngcomp.analytics.engine.util.Constants;
import com.ngcomp.analytics.engine.util.PropUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * User: Ram Parashar
 * Date: 8/28/13
 * Time: 9:06 AM
 */
public class JedisConnectionPool
{

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(JedisConnectionPool.class);

    private static final JedisPool pool = new JedisPool( new JedisPoolConfig(), getServerIp(), 6379, 2000, null, 0);


    public static Jedis getResource()
    {
        return pool.getResource();
    }


    public static final Jedis getJedisConnection(String db)
    {
        return new Jedis(db);
    }


    public static void returnResource(Jedis jedis)
    {
        pool.returnResource(jedis);
    }


    private static String getServerIp()
    {
        return (String) PropUtils.getVal(Constants.REDIS_HOST, Constants.LOCAL_HOST);
    }
}
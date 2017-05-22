package com.ngcomp.analytics.engine.thread;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.ngcomp.analytics.engine.conn.HBaseProxy;
import com.ngcomp.analytics.engine.conn.JedisConnectionPool;
import com.ngcomp.analytics.engine.connector.bing.model.BingData;
import com.ngcomp.analytics.engine.connector.rss.model.FeedMessage;
import com.ngcomp.analytics.engine.domain.Info;
import com.ngcomp.analytics.engine.model.Trend;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.xmlrpc.XmlRpcException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by rparashar on 9/23/13.
 */
public class BigMapReduceThread implements Runnable {

    private final String TOP_SCORES = "TOP_SCORES";

    @Override
    public void run(){



        HBaseProxy hBaseProxy = null;
        HTable hTable = null;

        Set<Info> weightedPosts = new TreeSet<Info>();
        Gson gson = new Gson();

        try{
            hBaseProxy = HBaseProxy.getInstance();
            String[] tables = {"1_FB_TRENDS", "2_BING"};

            hTable = HBaseProxy.getInstance().getHTable(1 + "_FB_TRENDS");
            Scan scan = new Scan();
            for (Result result : hTable.getScanner(scan)) {
                Trend trend = new Trend();
                trend.init(result);
                Info info = new Info(trend.getSourceID(), trend.getMarketID(), trend.getBrandID(), trend.getRelevanceScore(), String.valueOf(trend.getHBKey()), trend.getOriginalTokens());
                System.out.println(trend.getTS());
                weightedPosts.add(info);
                trend = null;
            }
            hTable.close();



            hTable = HBaseProxy.getInstance().getHTable(2 + "_BING");
            scan = new Scan();
            for (Result result : hTable.getScanner(scan)) {
                BingData bd = new BingData();
                bd.init(result);
                Info info = new Info(bd.getSourceID(), bd.getMarketID(), bd.getBrandID(), bd.getRelevanceScore(), String.valueOf(bd.getStoryId()), bd.getOriginalTokens());
                weightedPosts.add(info);
                bd = null;
            }
            hTable.close();


            hTable = HBaseProxy.getInstance().getHTable("596_RSS");
            scan = new Scan();
            for (Result result : hTable.getScanner(scan)) {
                FeedMessage fm = new FeedMessage();
                fm.init(result);
                Info info = new Info(fm.getSourceId(), fm.getMarketId(), fm.getBrandId(), fm.getRelevanceScore(), fm.getRowKey(), fm.getOriginalTokens());
                System.out.println(fm.getRowKey());
                weightedPosts.add(info);
                fm = null;
            }
            hTable.close();

//
            Jedis jedis = JedisConnectionPool.getResource();
            Pipeline pipe =  jedis.pipelined();

            pipe.del (TOP_SCORES);
            for(Info info : weightedPosts){
                if(!Strings.isNullOrEmpty(info.getStoryId()) && !info.getStoryId().equals("null")){
                    System.out.println("story--->" + info.getStoryId());
                    pipe.rpush(TOP_SCORES, gson.toJson(info));
                }
            }

            pipe.sync();
            JedisConnectionPool.returnResource(jedis);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (BoilerpipeProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (XmlRpcException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public static void main(String...strings){
        BigMapReduceThread b = new BigMapReduceThread();
        Thread t = new Thread(b);
        t.start();

    }

}

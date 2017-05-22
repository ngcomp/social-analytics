package com.ngcomp.analytics.engine.main;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.ngcomp.analytics.engine.conn.HBaseProxy;
import com.ngcomp.analytics.engine.model.Trend;
import com.ngcomp.analytics.engine.util.Constants;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import redis.clients.jedis.Jedis;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * User: rparashar
 * Date: 9/4/13
 * Time: 8:44 PM
 */
public class Main {

    public static void main(String...strings) throws IOException, XmlRpcException, DatatypeConfigurationException {
        Main main = new Main();
        main.fixLocalHBaseStories();
    }

    private Map<String, String> getConstants(String key){
        //Jedis jedis = new Jedis("localhost");
        Jedis jedis = new Jedis("192.168.1.10");
        Map<String, String> constants = jedis.hgetAll(key);
        jedis.quit();
        return constants;
    }
    //constants

    /**
     *
     * @throws IOException
     * @throws XmlRpcException
     */
    public void fixLocalHBaseStories() throws IOException, XmlRpcException, DatatypeConfigurationException {
        String constantKey = "1_SCORES";
        String key         = "1";

        Map<String, Float> constants = convertStringMapToFloatMap(getConstants(constantKey));
        Jedis jedis = new Jedis("192.168.1.10");
        HBaseProxy hBaseProxy = HBaseProxy.getInstance();
        Scan scan = new Scan();
        Gson gson = new Gson();
//        ResultScanner scanner = hBaseProxy.getHTable().getScanner(scan);
//        for (Result result : scanner) {
//            Trend trend = getTrend(result);
//            trend.setKeyWords(getKeywords(trend.getStory()));
//            trend.setRelevanceScore(constants);
//            System.out.println(trend.getRelevanceScore() + "   "  + trend.getKeyWords() + "   " + trend.getLongTime());
//            jedis.hset(key, String.valueOf(trend.getLongTime()), gson.toJson(trend));
//        }
//        jedis.disconnect();
    }


    /**
     *
     * @param text
     * @return
     * @throws IOException
     * @throws XmlRpcException
     */
    public List<String> getKeywords(String text) throws IOException, XmlRpcException {

        if(!Strings.isNullOrEmpty(text)){
            //Clean Story using Boilerpipe
            XmlRpcClient server = new XmlRpcClient("http://82.196.8.192:8081");
            Vector params = new Vector();
            params.addElement(text);
            Object result = server.execute("bp.extractText", params);
            server = null;

            //Get Keywords using KEA algorithm
            server = new XmlRpcClient("http://82.196.8.192:8000");
            result = server.execute("kea.extractKeyphrasesFromString", params);
            params.addElement(result);


            return (List<String>)result;
        }else{
            return null;
        }

    }


        private Trend getTrend(Result result){

            Trend trend = new Trend();
            trend.setUrl        (Bytes.toString(result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY),Bytes.toBytes("url"        ))));
            trend.setStory      (Bytes.toString(result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY),Bytes.toBytes("story"      ))));
            trend.setMedia      (Bytes.toString(result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY),Bytes.toBytes("media"      ))));
            trend.setMedia_type (Bytes.toString(result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY),Bytes.toBytes("media_type" ))));
            trend.setMedia_hash (Bytes.toString(result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY),Bytes.toBytes("media_hash" ))));
            trend.setSourceID   (Bytes.toString(result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY),Bytes.toBytes("sourceID"   ))));
            trend.setMarketID   (Bytes.toString(result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY),Bytes.toBytes("marketID"   ))));
            trend.setBrandID    (Bytes.toString(result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY),Bytes.toBytes("brandID"    ))));
            trend.setAttribution(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY), Bytes.toBytes("attribution"))));
            trend.setPlatformID (Bytes.toString(result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY), Bytes.toBytes("platformID"))));
            //trend.setScore    (Bytes.toLong  (result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY), Bytes.toBytes("score"     ))));
            trend.setTS         (Bytes.toString(result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY),Bytes.toBytes("TS"         ))));
            trend.setPrivacy    (Bytes.toString(result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY), Bytes.toBytes("privacy"  ))));
            trend.setActive     (Bytes.toString(result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY), Bytes.toBytes("active"))));
            trend.setOwned      (Bytes.toString(result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY), Bytes.toBytes("owned"))));
            trend.setLikes      (Bytes.toString(result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY), Bytes.toBytes("likes"))));
            trend.setComments   (Bytes.toString(result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY), Bytes.toBytes("comments"))));
            trend.setShares     (Bytes.toString(result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY), Bytes.toBytes("shares"))));
            trend.setTopic      (Bytes.toString(result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY), Bytes.toBytes("topic"))));
            trend.setArticle    (Bytes.toString(result.getValue(Bytes.toBytes(Constants.FB_COL_FAMILY), Bytes.toBytes("article"))));

        return trend;
    }


    private Map<String, Float> convertStringMapToFloatMap(Map<String, String> map){
        Map<String, Float> floatMap = new HashMap<String, Float>();

        for(String key : map.keySet()){
            System.out.println(key + map.get(key));
            floatMap.put(key, Float.valueOf(map.get(key)));
        }
        return floatMap;
    }
}

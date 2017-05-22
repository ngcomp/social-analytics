package com.ngcomp.analytics.engine.thread.fb;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.ngcomp.analytics.engine.config.RbtMQConnectionFactory;
import com.ngcomp.analytics.engine.conn.HBaseProxy;
import com.ngcomp.analytics.engine.connector.fb.FBURLConnector;
import com.ngcomp.analytics.engine.model.FBFeed;
import com.ngcomp.analytics.engine.model.Source;
import com.ngcomp.analytics.engine.model.Trend;
import com.ngcomp.analytics.engine.thread.fb.helper.FBFeedParser;
import com.ngcomp.analytics.engine.util.PortalUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.restfb.DefaultJsonMapper;
import com.restfb.JsonMapper;
import com.restfb.exception.FacebookJsonMappingException;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonException;
import com.restfb.json.JsonObject;
import net.sf.json.JSONObject;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * User: Ram Parashar
 * Date: 9/10/13
 * Time: 9:44 PM
 */
public class FBFeedSuckerThread implements Runnable {

    private static final Logger logger = Logger.getLogger(FBFeedSuckerThread.class);

    private String accessCode;
    private String url;
    private String identifier;
    private String sourceId;

    public FBFeedSuckerThread(){

    }

    public FBFeedSuckerThread(String url, String accessCode, String identifier, String sourceId) throws IOException {
        this.url = url;
        this.accessCode = accessCode;
        this.identifier = identifier;
        this.sourceId = sourceId;
    }

    private static long getStartTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        return calendar.getTimeInMillis() / 1000;
    }


    private Date getDate(String urlS){
        urlS = urlS.substring(urlS.indexOf("until"), urlS.indexOf("until") + 16);
        java.util.Date until = new java.util.Date(Long.valueOf((urlS.split("="))[1])*1000);
        return until;
    }

    @Override
    public void run() {

        Connection connection = null;
        Channel       channel = null;
        HTable         hTable = null;
        HTable        historyTable = null;
        QueueingConsumer consumer = null;
        HBaseProxy hBaseProxy = null;
        long startTime        = 0L;
        long endTime          = 0L;
        String msg = PortalUtils.SUCCESS_MSG;
        while(true){
            try {

                Date oneWeekAgo = new Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L * 7L);

                long end   = getStartTime();
                long start = end - 24 * 60 * 60;

                hBaseProxy = HBaseProxy.getInstance();
                connection = RbtMQConnectionFactory.getConnection();
                channel = connection.createChannel();
                channel.basicQos(1);
                consumer = new QueueingConsumer(channel);
                channel.basicConsume("FB_FEED_QUEUE", false, consumer);

                Gson gson = new Gson();
                while (true) {
                    startTime  = System.currentTimeMillis();
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    JSONObject message = JSONObject.fromObject(new String(delivery.getBody()));

                    String marketId   = message.getString("marketId");
                    String brandId = message.getString("brandId");
                    String credential = message.getString("credentials");
                    String topic = message.getString("topic");
                    String owned = message.getString("owned");
                    String identifier = message.getString("identifier");
                    String platform = message.getString("platform");
                           sourceId   = message.getString("sourceId");

                    hTable     = PortalUtils.fixHBaseFBTables(sourceId);
                    historyTable = PortalUtils.fixHistoryTable();

                                 //String id, String marketId, String brandId, String platform, String identifier, String credential, String owned, String priority, String topic, String ts, String attribution
                    Source source = new Source(sourceId, marketId, brandId, platform, identifier, credential, owned, null, topic, null, null);

                    FBURLConnector fb = new FBURLConnector(source);
                    StringBuilder stbr = new StringBuilder();
                    if(this.url == null){
                        stbr.append("https://graph.facebook.com/"  + source.getIdentifier()+ "/feed");
                        stbr.append("?fields=id,message,type,comments,likes,shares,created_time,from,application,privacy,is_hidden,source,object_id,link&limit=5000");
                        stbr.append("&access_token=").append(source.getCredential());
                        stbr.append("&until=").append(end);
                        stbr.append("&since=").append(start);
                    }else{
                        stbr.append(this.url);
                    }

                    logger.debug(stbr.toString());
                    Date until = getDate(stbr.toString());
                    JsonObject json = this.getFeeds(hBaseProxy, historyTable, hTable, source, stbr.toString(), topic);
                    while(json!= null && json.has("next") && until.getTime() > oneWeekAgo.getTime()){
                        logger.info("until->" + until);
                        String nextURL = (String) json.get("next");
                        until = this.getDate(nextURL);
                        json = this.getFeeds(hBaseProxy, historyTable, hTable, source, (String) json.get("next"), topic);
                    }
                    source = null;
                    try {
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    endTime = System.currentTimeMillis();
                    PortalUtils.api_logs(sourceId, msg, startTime, endTime);
                }

            }catch (Exception e) {
                e.printStackTrace();
            } finally{
                endTime = System.currentTimeMillis();
                PortalUtils.api_logs(sourceId, msg, startTime, endTime);
                try {
                    if(channel!= null && channel.isOpen()){
                        channel.close();
                    }
                } catch (IOException e) {
                    logger.info(PortalUtils.exceptionAsJson(e));
                }

                try {
                    if(connection!=null && connection.isOpen()){
                        connection.close();
                    }
                } catch (IOException e) {
                    logger.info(PortalUtils.exceptionAsJson(e));
                }
            }
        }
    }

    public JsonObject getFeeds(HBaseProxy hBaseProxy, HTable historyTable, HTable hTable, Source source, String urlS, String topic) {

        URL url;
        Gson gson = new Gson();
        try {

            url = new URL(urlS);


            System.out.println("Url------------->" + urlS);

            StringBuffer data = new StringBuffer() ;
            URLConnection yc = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                data.append(inputLine + "\n") ;
            }

            in.close();

            String json = data.toString() ;
            JsonObject jsonObject = null;

            System.out.println(json);

            try {
                jsonObject = new JsonObject(json);
            } catch (JsonException e) {
                throw new FacebookJsonMappingException("The connection JSON you provided was invalid: " + json, e);
            }

            // Pull out data
            JsonArray jsonData = jsonObject.getJsonArray("data");
            JsonMapper mapper = new DefaultJsonMapper() ;

            System.out.println("Total Feeds." + jsonData.length());

            for (int i = 0; i < jsonData.length(); i++) {

                FBFeed feed = mapper.toJavaObject(jsonData.get(i).toString(), FBFeed.class);

                feed.setType("facebookfeed");
                feed.setSourceId(source.getId());
                feed.setTopic(source.getTopic());
                feed.setAttribution(source.getPlatform() + "_" + feed.getFrom().getName());
                feed.setPlatformId(feed.getId());

                String keyInBKS = feed.getType()                + "__" + feed.getId();
                String valInBKS = hBaseProxy.key(feed.getType() + "__" + feed.getId());

                FBFeedParser fbFeedParser =  new FBFeedParser(source, feed);

                Trend trend = fbFeedParser.parsePost();
                trend.setRelevanceScore();
                trend.setOriginalTokens(trend.getStory());


                if(valInBKS == null){
                    feed.setSourceO(source);
                    feed.setUrl   (feed.getUrl()  );
                    feed.setMedia (feed.getUrl()  );
                    feed.setTitle(feed.getMessage());feed.setMessage(null);
                    feed.setOwned(feed.getSourceO().getOwned());
                    feed.setMediaType(feed.getType());
                    feed.setOriginalTokens(trend.getOriginalTokens());
                    feed.getOriginalTokens().add(topic);
                }else{

                }

                feed.setRelevanceScore(trend.getRelevanceScore());

                if(Strings.isNullOrEmpty(feed.getCreatedAt())){
                    feed.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                }

                String newKey  = PortalUtils.getKeyNew(null, trend.getRelevanceScore(), trend.getLongTime(), null);

                Map<String, String[]> map = PortalUtils.getHBaseKeyValMap(feed);

                //hBaseProxy.put(hTable, key, map.get("quals"), map.get("vals"));
                hBaseProxy.put(hTable, keyInBKS, valInBKS, newKey, map.get("quals"), map.get("vals"), true);

                hBaseProxy.put(historyTable, String.valueOf(Long.MAX_VALUE - System.currentTimeMillis()), sourceId,  map.get("quals"), map.get("vals"));

//                String key = PortalUtils.getKey(null, trend.getRelevanceScore(), trend.getLongTime());
//                Map<String, String[]> map = PortalUtils.getHBaseKeyValMap(feed);
//                hBaseProxy.put(hTable, key, map.get("quals"), map.get("vals"));
//
//                hBaseProxy.put(historyTable, String.valueOf(Long.MAX_VALUE - System.currentTimeMillis()), sourceId,  map.get("quals"), map.get("vals"));

            }

            if(jsonObject.has("paging")){
                return jsonObject.getJsonObject("paging");
            }
        } catch (Exception e) {
            logger.info(PortalUtils.exceptionAsJson(e));
        }

        return null;
    }

    public static void main(String...args) throws IOException {

        Connection connection = RbtMQConnectionFactory.getConnection();
        Channel channel = connection.createChannel();
        JSONObject json = new JSONObject();

        json.put("sourceId", "613"); //612
        json.put("marketId", "8");
        json.put("brandId" , "1");
        json.put("platform" , "facebookfeed");
        json.put("credentials", "");
        json.put("identifier" , "9401782383");
        json.put("topic"      , "Carlsberg");
        json.put("owned"      , true);

        channel.basicPublish("FB_FEED_EXCHANGE", "", null, json.toString().getBytes());

        channel.close();
        connection.close();

        FBFeedSuckerThread fbSucker = new FBFeedSuckerThread();
        Thread thread = new Thread(fbSucker);
        thread.start();

    }
}

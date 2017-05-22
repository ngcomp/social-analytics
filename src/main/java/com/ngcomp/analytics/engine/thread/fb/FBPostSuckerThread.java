package com.ngcomp.analytics.engine.thread.fb;

import com.google.gson.Gson;
import com.ngcomp.analytics.engine.config.RbtMQConnectionFactory;
import com.ngcomp.analytics.engine.conn.HBaseProxy;
import com.ngcomp.analytics.engine.domain.FBPost;
import com.ngcomp.analytics.engine.util.PortalUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Post;
import net.sf.json.JSONObject;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: Ram Parashar
 * Date: 9/10/13
 * Time: 10:44 PM
 */
public class FBPostSuckerThread implements Runnable {

    Logger logger = Logger.getLogger(FBPostSuckerThread.class);

    @Override
    public void run() {
    	
    	String sourceId    = null;
        long startTime = System.currentTimeMillis();
        long endTime = 0L;
        String msg = PortalUtils.SUCCESS_MSG;
        
        HBaseProxy hBaseProxy = null;
        Connection connection = null;
        Channel       channel = null;
        HTable hTable         = null;
        HTable   historyTable = null;
        QueueingConsumer consumer = null;

        while(true){

            try{
                logger.info("started ------FBPostSuckerThread------");

                Date oneWeekAgo = new Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L * 7L);

                hBaseProxy = HBaseProxy.getInstance();
                connection = RbtMQConnectionFactory.getConnection();
                channel = connection.createChannel();
                channel.basicQos(1);
                consumer = new QueueingConsumer(channel);
                channel.basicConsume("FB_POST_QUEUE", false, consumer);


                Gson gson = new Gson();


                while(true){
                    startTime  = System.currentTimeMillis();
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    JSONObject message = JSONObject.fromObject(new String(delivery.getBody()));
                    String owned = message.getString("owned");
                    sourceId   = message.getString("sourceId");

                    FacebookClient facebookClient = new DefaultFacebookClient(message.getString("credentials"));
                    String topic = message.getString("topic") + "/feed";
                    Iterator<List<Post>> feeds = facebookClient.fetchConnection(topic, Post.class, Parameter.with("until", "now"), Parameter.with("limit", 5)).iterator();
                    boolean keepRunning = true;
                    int counter = 0;

                    hTable       = PortalUtils.fixHBaseFBTables(message.getString("sourceId"));
                    historyTable = PortalUtils.fixHistoryTable();
                    while(feeds.hasNext() && keepRunning){

                        List<Post> feed = feeds.next();

                        for (Post post : feed){

                            FBPost fbPost = new FBPost();
                            fbPost.setType("facebookpost");
                            fbPost.setPlatformId(post.getId());
                            fbPost.setSourceId(message.getString("sourceId"));
                            fbPost.setBrandId (message.getString("brandId"));
                            fbPost.setMarketId(message.getString("marketId"));
                            fbPost.setCredentials   (message.getString ("credentials"));
                            fbPost.setTopic         (message.getString ("topic"      ));

                            String keyInBKS = fbPost.getType()                + "__" + post.getId();
                            String valInBKS = hBaseProxy.key(keyInBKS);

                            //Result result = hBaseProxy.getRow(hTable, String.valueOf(post.getCreatedTime().getTime()));
                            if(valInBKS == null){

                                fbPost.setOriginalTokens(post.getMessage());
                                fbPost.setCreatedAt(post.getCreatedTime().getTime());

                            }else{
                                Result result  = hBaseProxy.getRow(hTable, valInBKS);
                                fbPost.init(result);
                            }

                            fbPost.setLikeCount   (String.valueOf(post.getLikesCount()));
                            fbPost.setCommentCount(String.valueOf(post.getLikesCount()));
                            fbPost.setShareCount  (String.valueOf(post.getSharesCount()));

                            fbPost.setRelevanceScore();

                            fbPost.setPost(post);

                            if(post!=null && post.getPicture()!=null){
                                fbPost.setMedia    (post.getPicture());post.getPicture();
                                fbPost.setMediaType("image");
                                fbPost.setTitle    (post.getMessage());
                                fbPost.setUrl      (post.getLink());
                            }

                            fbPost.setOwned(Boolean.valueOf(owned));

                            //String key = PortalUtils.getKey(null, fbPost.getRelevanceScore(), fbPost.getCreatedAt());
                            String newKey  = PortalUtils.getKeyNew(keyInBKS, fbPost.getRelevanceScore(), null, null);

                            Map<String, String[]> map = PortalUtils.getHBaseKeyValMap(fbPost);

                            //hBaseProxy.put(hTable, key, map.get("quals"), map.get("vals"));
                            hBaseProxy.put(hTable, keyInBKS, valInBKS, newKey, map.get("quals"), map.get("vals"), true);

                            hBaseProxy.put(historyTable, String.valueOf(Long.MAX_VALUE - System.currentTimeMillis()), sourceId,  map.get("quals"), map.get("vals"));

                            if(post.getCreatedTime().getTime() < oneWeekAgo.getTime()){
                                keepRunning = false;
                                break;
                            }else{
                                logger.debug(counter++ + "     "  + post.getCreatedTime());
                            }
                        }
                    }
                    try {
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    } catch (IOException e) {
                        logger.info(PortalUtils.exceptionAsJson(e));
                    }

                    endTime = System.currentTimeMillis();
                    PortalUtils.api_logs(sourceId, msg, startTime, endTime);

                }

            } catch (Exception e) {
                msg = "Exception occurred " + e.getMessage();
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


    public static void main(String...strings) throws IOException {

        Connection connection = RbtMQConnectionFactory.getConnection();
        Channel channel = connection.createChannel();
        JSONObject json = new JSONObject();


        json.put("sourceId", "615"); //614 and 615
        json.put("marketId", "8");
        json.put("brandId" , "1");
        json.put("credentials", "");
        json.put("topic"      , "Carlsberg");
        json.put("owned"      , true);
        channel.basicPublish("FB_POST_EXCHANGE", "", null, json.toString().getBytes());

        channel.close();
        connection.close();

        FBPostSuckerThread rssT = new FBPostSuckerThread();
        Thread thread = new Thread(rssT);
        thread.start();

    }

}

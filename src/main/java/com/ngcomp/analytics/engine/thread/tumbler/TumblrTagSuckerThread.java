package com.ngcomp.analytics.engine.thread.tumbler;

import com.ngcomp.analytics.engine.config.RbtMQConnectionFactory;
import com.ngcomp.analytics.engine.conn.HBaseProxy;
import com.ngcomp.analytics.engine.connector.common.CommonCredentials;
import com.ngcomp.analytics.engine.connector.common.model.FBStat;
import com.ngcomp.analytics.engine.connector.tumbler.SearchTags;
import com.ngcomp.analytics.engine.connector.tumbler.model.TumblrPost;
import com.ngcomp.analytics.engine.util.PortalUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import net.sf.json.JSONObject;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TumblrTagSuckerThread implements Runnable {

    private static final Logger logger = Logger.getLogger(TumblrTagSuckerThread.class);

	@Override
	public void run() {
		
		String sourceId    = null;
        long startTime = System.currentTimeMillis();
        long endTime = 0L;
        String msg = PortalUtils.SUCCESS_MSG;
        
		HBaseProxy hBaseProxy = null;
        Connection connection = null;
        Channel channel = null;
        QueueingConsumer consumer = null;
        QueueingConsumer.Delivery delivery =  null;
        HTable hTable = null;
        HTable historyTable = null;
        while(true){
            try {

                hBaseProxy = HBaseProxy.getInstance();
                connection = RbtMQConnectionFactory.getConnection();
                channel    = connection.createChannel();
                channel.basicQos(1);
                consumer   = new QueueingConsumer(channel);
                channel.basicConsume("TUMBLR_TAG_QUEUE", false, consumer);

                while (true) {
                    startTime  = System.currentTimeMillis();

                    delivery   = consumer.nextDelivery();
                    JSONObject message = JSONObject.fromObject(new String(delivery.getBody()));

                    String topic             = message.getString("topic");
                    String consumerKey       = message.getString("consumerKey");
                    String consumerSecret    = message.getString("consumerSecret");
                    String accessToken       = message.getString("accessToken");
                    String accessTokenSecret = message.getString("accessTokenSecret");
                    String bitlyToken        = message.getString("bitlytoken");
                    boolean owned    = message.getBoolean("owned");
                    sourceId         = message.getString("sourceId");
                    String marketId = message.getString ("marketId");
                    String platform = message.getString("platform");

                    SearchTags tumblrClient = new SearchTags(consumerKey, consumerSecret,accessToken, accessTokenSecret);

                    List<TumblrPost> posts = tumblrClient.getTagInfo(false, topic,false, bitlyToken);

                    hTable   = PortalUtils.fixHBaseFBTables(sourceId);
                    historyTable = PortalUtils.fixHistoryTable();
                    for (TumblrPost data : posts) {

                        if(data == null)continue;
                        data.setSourceId(sourceId);
                        data.setType("tumblrsearch");
                        data.setOwned(owned);
                        data.setMarketId(marketId);
                        data.setPlatformId(String.valueOf(data.getId()));


                        System.out.println("Key-------------> " + data.getPlatformId());

                        String keyInBKS = data.getType()                + "__" + data.getPlatformId();
                        String valInBKS = hBaseProxy.key(data.getType() + "__" + data.getPlatformId());

                        //Result result = hBaseProxy.getRow(hTable, String.valueOf(data.getCreatedAt().getTime()));

                        if(valInBKS == null){
                            data.setSourceId(sourceId);
                            data.setBrandId (message.getString("brandId"));
                            data.setMarketId(message.getString("marketId"));

                            data.setTopic(message.getString ("topic"));
                            if(message.containsKey("owned")){
                                data.setOwned(Boolean.valueOf(message.getString("owned")));
                            }


                            FBStat stat = data.getFbStat();
                            if(stat != null){
                                data.setLikeCount   (String.valueOf(stat.getLike_count()));
                                data.setCommentCount(String.valueOf(stat.getComment_count()));
                                data.setShareCount  (String.valueOf(stat.getShare_count()));
                                data.setRelevanceScore();
                            }

                            data.setOriginalTokens(data.getStory() != null ? data.getStory().getActualStory() : topic);
                            if(data.getTags()!=null){
                                data.getOriginalTokens().addAll(data.getTags());
                            }
                        }else{
                            Result result  = hBaseProxy.getRow(hTable, valInBKS);
                            data.init(result);
                        }

                        data.setTitle(data.getText());data.setText(null);

                        if(data.getFormat()!= null && data.getFormat().equalsIgnoreCase("html")){
                            data.setMedia    (PortalUtils.getMainImage(data.getPost_url()));  data.setPost_url(null);
                            data.setMediaType("image");    data.setFormat(null);
                        }else{
                            data.setMedia    (data.getPost_url());  data.setPost_url(null);
                            data.setMediaType(data.getFormat());    data.setFormat(null);
                        }

                        data.setUrl(data.getMedia());

                        data.setAttribution(platform +"_"+ tumblrClient.ownerScreenName() );

                        String newKey  = PortalUtils.getKeyNew(data.getPlatformId(), data.getRelevanceScore(), null, null);

                        Map<String, String[]> map = PortalUtils.getHBaseKeyValMap(data);

                        hBaseProxy.put(hTable, keyInBKS, valInBKS, newKey, map.get("quals"), map.get("vals"), true);

                        hBaseProxy.put(historyTable, String.valueOf(Long.MAX_VALUE - System.currentTimeMillis()), sourceId,  map.get("quals"), map.get("vals"));
                    }

                    try {
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    } catch (IOException e) {
                        logger.info(PortalUtils.exceptionAsJson(e));
                    }
                    endTime = System.currentTimeMillis();
                    PortalUtils.api_logs(sourceId, msg, startTime, endTime);

                }
            }catch (Exception e) {
                try {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                } catch (IOException ex) {
                    logger.info(PortalUtils.exceptionAsJson(e));
                }

                msg = "Exception occurred " + PortalUtils.exceptionAsJson(e);
                logger.error(PortalUtils.exceptionAsJson(e));
                PortalUtils.api_logs(sourceId, msg, startTime, endTime);
                try {
                    Thread.sleep(60000); //Intentionally Sleep for One Minute
                } catch (InterruptedException ex) {
                    e.printStackTrace();
                }
            }finally{
                endTime = System.currentTimeMillis();
//                PortalUtils.api_logs(sourceId, msg, startTime, endTime);
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
            try {
                Thread.sleep(60000); //Intentionally Sleep for One Minute
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

	}


    public static void main(String...strings) throws IOException {

        Connection connection = RbtMQConnectionFactory.getConnection();
        Channel channel = connection.createChannel();
        JSONObject json = new JSONObject();


        json.put("sourceId", "603");
        json.put("marketId", "169");
        json.put("brandId" , "5");

        json.put("consumerKey"      , "");
        json.put("consumerSecret"   , "");
        json.put("accessToken"      , "");
        json.put("accessTokenSecret", "");

        json.put("topic"            , "Penguins");
        json.put("owned"            , false);
        json.put("bitlytoken"       , CommonCredentials.BITLY_TOKEN);
        json.put("platform"         , "tumblr");

        channel.basicPublish("TUMBLR_TAG_EXCHANGE", "", null, json.toString().getBytes());//TUMBLER_SOURCE_QUEUE

        channel.close();
        connection.close();


        TumblrTagSuckerThread bst = new TumblrTagSuckerThread();
        Thread thread = new Thread(bst);
        thread.start();
    }
	
}
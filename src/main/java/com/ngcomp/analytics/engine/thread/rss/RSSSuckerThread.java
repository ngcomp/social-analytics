package com.ngcomp.analytics.engine.thread.rss;

import com.google.common.base.Strings;
import com.ngcomp.analytics.engine.config.RbtMQConnectionFactory;
import com.ngcomp.analytics.engine.conn.HBaseProxy;
import com.ngcomp.analytics.engine.connector.common.CommonCredentials;
import com.ngcomp.analytics.engine.connector.common.model.FBStat;
import com.ngcomp.analytics.engine.connector.rss.RSSFeedParser;
import com.ngcomp.analytics.engine.connector.rss.model.Feed;
import com.ngcomp.analytics.engine.connector.rss.model.FeedMessage;
import com.ngcomp.analytics.engine.util.PortalUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import net.sf.json.JSONObject;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;

public class RSSSuckerThread implements Runnable {

    private static final Logger logger = Logger.getLogger(RSSSuckerThread.class);

	@Override
	public void run() {

		String sourceId    = null;
		long startTime = System.currentTimeMillis();
        long endTime = 0L;
        String msg = PortalUtils.SUCCESS_MSG;
		
		HBaseProxy hBaseProxy = null;
        HTable        hTable = null;
        HTable        historyTable = null;
        Connection connection = null;
        Channel       channel = null;
        QueueingConsumer          consumer = null;
        QueueingConsumer.Delivery delivery = null;

        while(true){
            try {
                hBaseProxy = HBaseProxy.getInstance();
                connection = RbtMQConnectionFactory.getConnection();
                channel    = connection.createChannel();
                channel.basicQos(1);
                consumer   = new QueueingConsumer(channel);
                channel.basicConsume("RSS_SOURCE_QUEUE", false, consumer);

                while (true) {
                    startTime  = System.currentTimeMillis();
                    delivery   = consumer.nextDelivery();

                    JSONObject message = JSONObject.fromObject(new String(delivery .getBody()));

                    String identifier = message.getString("identifier");
                           sourceId   = message.getString("sourceId");
                    String marketId = message.getString ("marketId");

                    String bitlyToken    = message.getString("bitlytoken");
                    boolean owned        = message.getBoolean("owned");
                    String platform      = message.getString("platform");

                    RSSFeedParser parser = new RSSFeedParser(identifier);
                    Feed feed = parser.readFeed(false, false, bitlyToken);


                    hTable       = PortalUtils.fixHBaseFBTables(sourceId);
                    historyTable = PortalUtils.fixHistoryTable();
                    for(FeedMessage data : feed.getMessages()){

                        data.setSourceId(sourceId);
                        data.setMarketId(marketId);
                        data.setType("rss");
                        data.setOwned(owned);
                        data.setMedia(PortalUtils.getMainImage(data.getLink()));
                        data.setMediaType("image");
                        data.setUrl  (data.getLink()       );data.setLink       (null);
                        data.setTitle(data.getDescription());data.setDescription(null);
                        data.setTopic(message.getString("topic"));
                        data.setAttribution(platform  +  "_" + data.getAuthor());
                        data.setPlatformId(data.getGuid());


                        //BKS = Big Key Store.
                        String keyInBKS = data.getGuid();
                        String valInBKS = hBaseProxy.key(keyInBKS);

                        if(valInBKS == null){
                            data.setSourceId(sourceId);
                            data.setBrandId (message.getString("brandId" ));
                            data.setMarketId(message.getString("marketId"));
                            data.setOriginalTokens(data.getTitle());
                        }else{
                            Result result  = hBaseProxy.getRow(hTable, valInBKS);
                            data.init(result);
                        }

                        if(Strings.isNullOrEmpty(data.getCreatedAt())){
                            data.setCreatedAt(String.valueOf(new java.util.Date()));
                        }

                        FBStat stat = data.getFbStat();
                        if(stat != null){
                            data.setLikeCount   (String.valueOf(stat.getLike_count(   )));
                            data.setCommentCount(String.valueOf(stat.getComment_count()));
                            data.setShareCount  (String.valueOf(stat.getShare_count(  )));
                            data.setRelevanceScore();
                        }

                        String newKey  = PortalUtils.getKeyNew(keyInBKS, data.getRelevanceScore(), null, null);

                        Map<String, String[]> map = PortalUtils.getHBaseKeyValMap(data);

                        hBaseProxy.put(hTable, keyInBKS, valInBKS, newKey, map.get("quals"), map.get("vals"), true);

                        //PUT Items in History Table.
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

            } catch (Exception e) {
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
            } finally{
                endTime = System.currentTimeMillis();

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

        JSONObject json = new JSONObject();
        Connection connection = RbtMQConnectionFactory.getConnection();
        Channel channel = connection.createChannel();

        json.put("sourceId", "596");
        json.put("marketId", "169");
        json.put("brandId" , "5");
        json.put("topic"   , "Retronaut");
        json.put("identifier", "http://www.retronaut.com/feed/");
        json.put("owned"   , false);
        json.put("bitlytoken" , CommonCredentials.BITLY_TOKEN);
        json.put("platform"   , "rss");

        channel.basicPublish("RSS_SOURCE_EXCHANGE"    , "", null, json.toString().getBytes());

        channel.close();
        connection.close();

        RSSSuckerThread rssT = new RSSSuckerThread();
        Thread thread = new Thread(rssT);
        thread.start();

    }

}

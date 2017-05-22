package com.ngcomp.analytics.engine.thread.bing;

import com.ngcomp.analytics.engine.config.RbtMQConnectionFactory;
import com.ngcomp.analytics.engine.conn.HBaseProxy;
import com.ngcomp.analytics.engine.connector.bing.BingSearch;
import com.ngcomp.analytics.engine.connector.bing.model.BingData;
import com.ngcomp.analytics.engine.connector.common.CommonCredentials;
import com.ngcomp.analytics.engine.connector.common.model.FBStat;
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

public class BingSuckerThread implements Runnable {
    private static final Logger logger = Logger.getLogger(BingSuckerThread.class);
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
        HTable        historyTable = null;
        while(true){
            try {

                hBaseProxy = HBaseProxy.getInstance();
                connection = RbtMQConnectionFactory.getConnection();
                channel = connection.createChannel();
                channel.basicQos(1);
                consumer = new QueueingConsumer(channel);
                channel.basicConsume("BING_QUEUE", false, consumer);

                while (true) {
                    startTime  = System.currentTimeMillis();

                    delivery = consumer.nextDelivery();
                    JSONObject message = JSONObject.fromObject(new String(delivery.getBody()));

                    sourceId           = message.getString ("sourceId"   );
                    String credentials = message.getString ("credentials");
                    String topic       = message.getString ("topic"      );
                    String brandId     = message.getString ("brandId"    );
                    String marketId    = message.getString ("marketId"   );
                    boolean owned      = message.getBoolean("owned"      );

                    BingSearch bingClient = new BingSearch(credentials);
                    List<BingData> searchData = bingClient.bingSearch(false, topic, false, null);

                    hTable       = PortalUtils.fixHBaseFBTables(sourceId);
                    historyTable = PortalUtils.fixHistoryTable();
                    for(BingData data : searchData){

                        //Result result = hBaseProxy.getRow(hTable, String.valueOf(data.getCreatedAt().getTime()));
                        data.setSourceId(sourceId);
                        FBStat stat = data.getFbStat();
                        if(stat != null){
                            data.setLikeCount   (String.valueOf(stat.getLike_count()   ));
                            data.setCommentCount(String.valueOf(stat.getComment_count()));
                            data.setShareCount  (String.valueOf(stat.getShare_count()  ));
                            data.setRelevanceScore();
                        }
                        data.setMarketId(marketId);
                        data.setOwned(owned);
                        data.setPlatformId(data.getId());
                        data.setType     ("bing" );

                        String keyInBKS = data.getType()                + "__" + data.getPlatformId();
                        String valInBKS = hBaseProxy.key(keyInBKS);

                        if(valInBKS == null){
                            data.setSourceId(sourceId);
                            data.setBrandId (brandId );

                            data.setTopic   (topic   );
                            data.setOriginalTokens(data.getDescription());
                        }else{
                            Result result  = hBaseProxy.getRow(hTable, valInBKS);
                            if(result.isEmpty()){
                                data.setSourceId(sourceId);
                                data.setBrandId (brandId );
                                data.setTopic   (topic   );
                                data.setOriginalTokens(data.getDescription());
                            }else{
                                data.init(result);
                            }
                        }

                        data.setMedia(PortalUtils.getMainImage(data.getUrl()));
                        data.setMediaType("image");


                        Map<String, String[]> map = PortalUtils.getHBaseKeyValMap(data);

                        //String key = PortalUtils.getKey(null, data.getRelevanceScore(), data.getLongTime());
                        String newKey  = PortalUtils.getKeyNew(keyInBKS, data.getRelevanceScore(), null, null);

                        //hBaseProxy.put(hTable, key, map.get("quals"), map.get("vals"));
                        hBaseProxy.put  (hTable, keyInBKS, valInBKS, newKey, map.get("quals"), map.get("vals"), true);

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
            }catch (Exception e) {

                try {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                } catch (IOException ex) {
                    logger.info(PortalUtils.exceptionAsJson(e));
                }
                msg = "Exception occurred " + PortalUtils.exceptionAsJson(e);
                logger.error(PortalUtils.exceptionAsJson(e));
                PortalUtils.api_logs(sourceId, msg, startTime, endTime);

            }finally{
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

//                try {
//                    if(hTable != null){
//                        hTable.close();
//                    }
//                } catch (IOException e) {
//                    logger.info(PortalUtils.exceptionAsJson(e));
//                }
//
//                try {
//                    if(historyTable != null){
//                        historyTable.close();
//                    }
//                } catch (IOException e) {
//                    logger.info(PortalUtils.exceptionAsJson(e));
//                }
            }
            try {
                Thread.sleep(60000); //Intentionally Sleep for One Minute
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String...strings) throws IOException, InterruptedException {

        Connection connection = RbtMQConnectionFactory.getConnection();
        Channel channel = connection.createChannel();
        JSONObject json = new JSONObject();


        json.put("sourceId", "6");//2
        json.put("marketId", "8");
        json.put("brandId" , "1");
        json.put("credentials", CommonCredentials.ACCOUNT_KEY_BING);

        json.put("topic"  , "Electronic Music");
        json.put("owned"  , false);
        channel.basicPublish("BING_EXCHANGE"   , "", null, json.toString().getBytes()); //BING_SOURCE_QUEUE

        channel.close();
        connection.close();
        //AddItemToFBSourceQueue.addMessage("bing");

        BingSuckerThread bst = new BingSuckerThread();
        Thread thread = new Thread(bst);
        thread.start();
        thread.join();
    }
}




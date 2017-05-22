package com.ngcomp.analytics.engine.thread.instagram;

import com.google.common.base.Strings;
import com.ngcomp.analytics.engine.config.RbtMQConnectionFactory;
import com.ngcomp.analytics.engine.conn.HBaseProxy;
import com.ngcomp.analytics.engine.connector.common.model.FBStat;
import com.ngcomp.analytics.engine.connector.instagram.SearchInstagram;
import com.ngcomp.analytics.engine.connector.instagram.dto.InstagramDTO;
import com.ngcomp.analytics.engine.util.PortalUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import net.sf.json.JSONObject;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class    InstagramSuckerThread implements Runnable {

    private static final Logger logger = Logger.getLogger(InstagramSuckerThread.class);

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
		QueueingConsumer.Delivery delivery = null;
		HTable hTable = null;
        HTable historyTable = null;
        while(true){
            try {
                hBaseProxy = HBaseProxy.getInstance();
                connection = RbtMQConnectionFactory.getConnection();
                channel    = connection.createChannel();
                channel.basicQos(1);
                consumer   = new QueueingConsumer(channel);
                channel.basicConsume("INSTAGRAM_QUEUE", false, consumer);


                while (true) {
                    startTime = System.currentTimeMillis();
                    delivery   = consumer.nextDelivery();


                    JSONObject message = JSONObject.fromObject(new String(delivery.getBody()));

                    System.out.println("message----------->" + message);

                    String clientKey   = message.getString ("clientKey"  );
                    String accessToken = message.getString ("accessToken");
                    String identifier  = message.getString ("identifier" );
                    boolean   owned    = message.getBoolean("owned"      );
                    String platform = message.getString ("platform"   );
                            sourceId   = message.getString ("sourceId"   );
                    String topic = message.getString ("topic"      );
                    String marketId = message.getString ("marketId");
                    //SearchInstagram instagram = new SearchInstagram(accessToken, clientKey);
                    SearchInstagram instagram = new SearchInstagram(clientKey, accessToken);
                    InstagramDTO[] inst = instagram.getUserMedia(identifier, false);

                    hTable = PortalUtils.fixHBaseFBTables(message.getString("sourceId"));
                    historyTable = PortalUtils.fixHistoryTable();

                    for (InstagramDTO in : inst) {
                        in.setTopic(topic);
                        in.setType("instagram");
                        in.setOwned(owned);
                        in.setMarketId(marketId);
                        in.setSourceId(sourceId);
                        in.setPlatformId(in.getId());
                        String keyInBKS = in.getType()                + "__" + in.getPlatformId();
                        String valInBKS = hBaseProxy.key(keyInBKS);

                        //Result result = hBaseProxy.getRow(hTable, String.valueOf(in.getId()));
                        if(valInBKS == null){

                            in.setSourceId(message.getString("sourceId"));
                            in.setBrandId (message.getString("brandId" ));
                            in.setMarketId(message.getString("marketId"));
                            in.setOriginalTokens(Arrays.asList(in.getTags()));
                            in.getOriginalTokens().add(in.getTopic());
                            in.setCreatedAt(String.valueOf(new Date()));
                        } else {
                            Result result  = hBaseProxy.getRow(hTable, valInBKS);
                            in.init(result);
                        }

                        if(Strings.isNullOrEmpty(in.getCreatedAt())){
                            in.setCreatedAt(String.valueOf(new Date()));
                        }


                        FBStat stat = in.getFbStat();
                        if(stat != null){
                            in.setLikeCount   (String.valueOf(stat.getLike_count()));
                            in.setCommentCount(String.valueOf(stat.getComment_count()));
                            in.setShareCount  (String.valueOf(stat.getShare_count()));
                            in.setRelevanceScore();
                        }

                        if(in.getImageUrl()!=null){
                            in.setMedia(in.getImageUrl());
                            in.setMediaType("image");in.setImageUrl(null);
                        }

                        if(in.getVideoUrl() !=null){
                            in.setMedia(in.getVideoUrl());
                            in.setMediaType("video");in.setVideoUrl(null);
                        }

                        in.setAttribution(platform + "_" + in.getUserName());

                        //String key = PortalUtils.getKey(in.getRowKey(), in.getRelevanceScore(), null);
                        String newKey  = PortalUtils.getKeyNew(keyInBKS, in.getRelevanceScore(), null, null);

                        Map<String, String[]> map = PortalUtils.getHBaseKeyValMap(in);
                        //hBaseProxy.put(hTable, key, map.get("quals"), map.get("vals"));
                        hBaseProxy.put(hTable, keyInBKS, valInBKS, newKey, map.get("quals"), map.get("vals") ,true);

                        hBaseProxy.put(historyTable, String.valueOf(Long.MAX_VALUE - System.currentTimeMillis()), sourceId,  map.get("quals"), map.get("vals"));

                    }

                    try {

                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    } catch (IOException e) {
                        e.printStackTrace();
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
                //PortalUtils.api_logs(sourceId, msg, startTime, endTime);
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
        //AddItemToFBSourceQueue.addMessage("instagram");

        JSONObject json = new JSONObject();

        json.put("sourceId", "601");
        json.put("marketId", "169");
        json.put("brandId" , "5");
//        json.put("clientKey"    , "426785c9f9c04efa94aaff87bc58a82d");
//        json.put("accessToken"  , "5935.5b9e1e6.ca4df9512f1a40ffb731aa2a82abeddf");

        //426785c9f9c04efa94aaff87bc58a82d##75935.426785c.fa73135ec3c34d249675b1d0415deae9

        json.put("clientKey"    , "");
        json.put("accessToken"  , "");

        json.put("identifier"   , "tuulavintage");
        json.put("topic"        , "Architecture");
        json.put("platform"     , "instagram");

        json.put("owned"        , false);

        Connection connection = RbtMQConnectionFactory.getConnection();
        Channel channel = connection.createChannel();

        channel.basicPublish("INSTAGRAM_EXCHANGE"    , "", null, json.toString().getBytes());//PINTEREST_SOURCE_QUEUE
        channel.close();
        connection.close();


        InstagramSuckerThread bst = new InstagramSuckerThread();
        Thread thread = new Thread(bst);
        thread.start();

    }

}

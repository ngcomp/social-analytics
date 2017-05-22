package com.ngcomp.analytics.engine.thread.pinterest;

import com.google.common.base.Strings;
import com.ngcomp.analytics.engine.config.RbtMQConnectionFactory;
import com.ngcomp.analytics.engine.conn.HBaseProxy;
import com.ngcomp.analytics.engine.connector.common.model.FBStat;
import com.ngcomp.analytics.engine.connector.pinterest.SearchPinterest;
import com.ngcomp.analytics.engine.connector.pinterest.model.PinObject;
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

public class PinterestSuckerThread implements Runnable {

    private static final Logger logger = Logger.getLogger(PinterestSuckerThread.class);

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
        HTable        historyTable = null;

        while(true){
            try {
                hBaseProxy = HBaseProxy.getInstance();

                connection = RbtMQConnectionFactory.getConnection();
                channel = connection.createChannel();
                channel.basicQos(1);
                consumer = new QueueingConsumer(channel);
                channel.basicConsume("PINTEREST_QUEUE", false, consumer);
                int counter =0 ;

                while (true) {
                    startTime  = System.currentTimeMillis();

                    delivery = consumer.nextDelivery();
                    JSONObject message = JSONObject.fromObject(new String(delivery.getBody()));

                    //String user = message.getString("username");
                    String identifier = message.getString ("identifier");
                    boolean owned     = message.getBoolean("owned"     );
                             sourceId = message.getString ("sourceId"  );
                    String platform = message.getString ("platform"  );
                    String marketId = message.getString ("marketId");
                    String topic = message.getString ("topic");

                    SearchPinterest pinterest = new SearchPinterest();
                    PinObject[] pins = pinterest.getPinsForURL(false, false, identifier);

                    hTable       = PortalUtils.fixHBaseFBTables(sourceId);
                    historyTable = PortalUtils.fixHistoryTable();

                    for(PinObject pin : pins ){
                        pin.setSourceId(sourceId);
                        pin.setType    (platform);
                        pin.setOwned   (owned   );
                        pin.setMarketId(marketId);
                        pin.setTopic   (topic);
                        pin.setAttribution(platform + "_" + identifier.split("/")[3]);
                        pin.setPlatformId(pin.getId());
                        pin.setOwned(Boolean.valueOf(message.getString("owned")));
                        pin.setBrandId (message.getString("brandId" ));
                        pin.setMarketId(message.getString("marketId"));


                        String keyInBKS = pin.getType() + "__" + pin.getPlatformId();
                        String valInBKS = hBaseProxy.key(keyInBKS);

                        //Result result = hBaseProxy.getRow(hTable, String.valueOf(pin.getPinUrl()));

                        if(valInBKS == null){

                        }else{
                            Result result  = hBaseProxy.getRow(hTable, valInBKS);
                            pin.init(result);
                        }

                        if(Strings.isNullOrEmpty(pin.getCreatedAt())){
                            pin.setCreatedAt(String.valueOf(new java.util.Date()));
                        }

                        FBStat stat = pin.getFbStat();
                        if(stat != null){
                            pin.setLikeCount   (String.valueOf(stat.getLike_count()));
                            pin.setCommentCount(String.valueOf(stat.getComment_count()));
                            pin.setShareCount  (String.valueOf(stat.getShare_count()));
                            pin.setRelevanceScore();
                        }

                        pin.setOriginalTokens(pin.getDesc());
                        pin.setMedia         (pin.getHref())  ;pin.setHref  (null);
                        pin.setMediaType     ("image");
                        pin.setTitle         (pin.getDesc())  ;pin.setDesc  (null);
                        pin.setUrl           (pin.getPinUrl());pin.setPinUrl(null);

                        //String key = PortalUtils.getKey(pin.getPinUrl(), pin.getRelevanceScore(), null);
                        String newKey  = PortalUtils.getKeyNew(keyInBKS, pin.getRelevanceScore(), null, null);

                        System.out.println("Key ===========> " + newKey);

                        Map<String, String[]> map = PortalUtils.getHBaseKeyValMap(pin);

                        hBaseProxy.put(hTable, keyInBKS, valInBKS, newKey, map.get("quals"), map.get("vals"), true);

                        hBaseProxy.put(historyTable, String.valueOf(Long.MAX_VALUE - System.currentTimeMillis()), sourceId,  map.get("quals"), map.get("vals"));

                    }
                    try {
                        if(channel!= null && channel.isOpen()){
                            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        }
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

        json.put("sourceId", "598");
        json.put("marketId", "169");
        json.put("brandId" , "5"  );
        json.put("platform", "pinterest");
        json.put("identifier", "http://www.pinterest.com/nicolekeegan94/penguins/");
        json.put("topic"     , "Penguins");
        json.put("owned"     , false);
        channel.basicPublish("PINTEREST_EXCHANGE"    , "", null, json.toString().getBytes());//PINTEREST_SOURCE_QUEUE

        channel.close();
        connection.close();

        PinterestSuckerThread bst = new PinterestSuckerThread();
        Thread thread = new Thread(bst);
        thread.start();
    }

}

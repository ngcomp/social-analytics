package com.ngcomp.analytics.engine.thread.twitter;

import com.google.common.base.Strings;
import com.ngcomp.analytics.engine.config.RbtMQConnectionFactory;
import com.ngcomp.analytics.engine.conn.HBaseProxy;
import com.ngcomp.analytics.engine.connector.common.CommonCredentials;
import com.ngcomp.analytics.engine.connector.twitter.SearchTweets;
import com.ngcomp.analytics.engine.connector.twitter.model.Tweet;
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

public class TwitterTweetSuckerThread implements Runnable {

    private static final Logger logger = Logger.getLogger(TwitterTweetSuckerThread.class);


    @Override
    public void run() {

    	String sourceId    = null;
        long startTime = System.currentTimeMillis();
        long endTime = 0L;
        String msg = PortalUtils.SUCCESS_MSG;
        
        HBaseProxy hBaseProxy = null;
        Connection connection = null;
        Channel       channel = null;
        QueueingConsumer consumer = null;
        QueueingConsumer.Delivery delivery = null;
        HTable       hTable = null;
        HTable historyTable = null;
        while(true){
            try {
                hBaseProxy = HBaseProxy.getInstance();

                connection = RbtMQConnectionFactory.getConnection();
                channel = connection.createChannel();
                channel.basicQos(1);
                consumer = new QueueingConsumer(channel);
                channel.basicConsume("TWITTER_TWEETS_QUEUE", true, consumer);

                while (true) {

                    startTime  = System.currentTimeMillis();
                    delivery = consumer.nextDelivery();

                    JSONObject message = JSONObject.fromObject(new String(delivery.getBody()));


                    String topic = message.getString("topic"            );
                    String accessTokenSecret = message.getString("accessTokenSecret");
                    String consumerKey       = message.getString("consumerKey"      );
                    String consumerSecret    = message.getString("consumerSecret"   );
                    String accessToken       = message.getString("accessToken"      );
                    String platform = message.getString("platform"         );
                                    sourceId = message.getString("sourceId"         );
                    String marketId = message.getString ("marketId");

                    SearchTweets twitter = new SearchTweets(consumerKey, consumerSecret, accessToken, accessTokenSecret);
                    List<Tweet> tweets = twitter.searchTweets(false, topic, 50, false );

                    hTable = PortalUtils.fixHBaseFBTables(message.getString("sourceId"));
                    historyTable = PortalUtils.fixHistoryTable();

                    for (Tweet tweet : tweets) {

                        System.out.println("--------------> " + tweet.getId());
                        tweet.setType("twitter");
                        tweet.setMarketId(marketId);
                        tweet.setSourceId(sourceId);
                        tweet.setPlatformId(String.valueOf(tweet.getId()));

                        String keyInBKS = tweet.getType()                + "__" + tweet.getId();
                        String valInBKS = hBaseProxy.key(tweet.getType() + "__" + tweet.getId());

                        //Result result = hBaseProxy.getRow(hTable,String.valueOf(tweet.getCreatedAt().getTime()));
                        if(valInBKS == null){


                            tweet.setBrandId (message.getString ("brandId"));
                            tweet.setMarketId(message.getString ("marketId"));

                            tweet.setOwned   (message.getBoolean("owned"));

                            tweet.setOriginalTokens(tweet.getText() != null ? tweet.getText() : topic);
                            tweet.setCreatedAt     (tweet.getCreatedAt());
                        } else {
                            Result result  = hBaseProxy.getRow(hTable, valInBKS);
                            tweet.init(result);
                        }

                        if(Strings.isNullOrEmpty(tweet.getTopic())){
                            tweet.setTopic   (message.getString ("topic"));
                        }

                        tweet.setLikeCount     (String.valueOf(tweet.getFavoriteCount()));
                        tweet.setCommentCount  (String.valueOf(tweet.getRetweetCount()));
                        tweet.setShareCount    (String.valueOf(tweet.getRetweetCount()));
                        tweet.setRelevanceScore();


                        if(tweet.getText().contains("http")){
                            String imageUrl = PortalUtils.getMainImage(PortalUtils.getUrl(tweet.getText()));
                            if(imageUrl != null && !Strings.isNullOrEmpty(imageUrl)){
                                tweet.setMedia(PortalUtils.getMainImage(PortalUtils.getUrl(tweet.getText())));
                                tweet.setUrl(PortalUtils.getUrl(tweet.getText()));
                                tweet.setMediaType("image");
                                tweet.setUrl(PortalUtils.getUrl(tweet.getText()));
                            }
                        }
                        tweet.setTitle(tweet.getText());
                        tweet.setText(null);
                        tweet.setAttribution(platform + "_" + twitter.ownerScreenName() );

                        String newKey  = PortalUtils.getKeyNew(String.valueOf(tweet.getId()), tweet.getRelevanceScore(), null, null);

                        Map<String, String[]> map = PortalUtils.getHBaseKeyValMap(tweet);
                        hBaseProxy.put(hTable, keyInBKS, valInBKS, newKey, map.get("quals"), map.get("vals"), true);
                        //hBaseProxy.put(hTable, String.valueOf(tweet.getRowKey()), map.get("quals"), map.get("vals"));
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

                msg = "Exception occurred " + PortalUtils.exceptionAsJson(e);
                logger.error(PortalUtils.exceptionAsJson(e));
                PortalUtils.api_logs(sourceId, msg, startTime, endTime);
                try {
                    Thread.sleep(60000); //Intentionally Sleep for One Minute
                } catch (InterruptedException ex) {
                    e.printStackTrace();
                }
            }  finally{
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

        //AddItemToFBSourceQueue.addMessage("twittersearch");

        Connection connection = RbtMQConnectionFactory.getConnection();
        Channel channel = connection.createChannel();
        JSONObject json = new JSONObject();

        json.put("sourceId", "616");//
        json.put("marketId", "169");
        json.put("brandId" , "5");


        json.put("consumerKey"      , CommonCredentials.CONSUMER_KEY_TWITTER);
        json.put("consumerSecret"   , CommonCredentials.CONSUMER_SECRET_TWITTER);
        json.put("accessToken"      , CommonCredentials.ACCESS_TOKEN_TWITTER);
        json.put("accessTokenSecret", CommonCredentials.ACCESS_TOKEN_SECRET_TWITTER);

        json.put("topic"  , "Penguin Juice");
        json.put("owned"  , true);
        json.put("platform"  , "twitter");
        channel.basicPublish("TWITTER_TWEETS_EXCHANGE", "", null, json.toString().getBytes());//TWITTER_SOURCE_QUEUE
        channel.close();
        connection.close();


        TwitterTweetSuckerThread bst = new TwitterTweetSuckerThread();
        Thread thread = new Thread(bst);
        thread.start();

    }
}

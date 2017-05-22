package com.ngcomp.analytics.engine.test;

import com.ngcomp.analytics.engine.connector.common.CommonCredentials;
import com.ngcomp.analytics.engine.domain.Sources;
import com.ngcomp.analytics.engine.util.PortalUtils;
import com.ngcomp.analytics.engine.util.PropUtils;
import com.ngcomp.analytics.engine.web.hb.HibernateConfig;
import com.ngcomp.analytics.engine.web.hb.JndiConfig;
import com.ngcomp.analytics.engine.web.hb.LogManagerDAO;
import com.ngcomp.analytics.engine.web.hb.SourcesDAO;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import net.sf.json.JSONObject;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.List;

/**
 * User: Ram Parashar Date: 9/10/13 Time: 9:56 PM
 */
public class AddItemToFBSourceQueue {


    public static void addMessage(List<Sources> sources, SourcesDAO dao) throws IOException {
        try {

            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
            context.register(JndiConfig.class, HibernateConfig.class);
            context.refresh();
            LogManagerDAO logManager = context.getBean(LogManagerDAO.class);

            for (Sources source : sources) {



                if (source.getOrderByCol() != null && source.getOrderByCol().equals("trend")) {
                    continue;
                }

                if(source.getIgnoreIndexing() != null && source.getIgnoreIndexing()){
                    continue;
                }

                if(source.getPlatform().equals("stock")){
                    continue;
                }

                System.out.println(source.getPlatform() + " =>" + source.getId() );

                ConnectionFactory connectionFactory = new ConnectionFactory();
                connectionFactory.setUsername(PropUtils.getVal("rbtmq.user.id"));
                connectionFactory.setPassword(PropUtils.getVal("rbtmq.password"));
                connectionFactory.setHost    (PropUtils.getVal("rbtmq.host"));
                connectionFactory.setVirtualHost("/");
                connectionFactory.setPort(5672);

                Connection connection = connectionFactory.newConnection();
                Channel channel = connection.createChannel();

                JSONObject json = new JSONObject();

                String credentials = source.getCredentials();
                String[] split = credentials.split("##");
                String platform = source.getPlatform();
                Long sourceId = source.getId();
                Long brandID  = source.getBrandID();
                Long marketID = source.getMarketID();
                boolean owned = source.getOwned() == 0 ? false : true;
                String topic  = source.getTopic();
                String identifier = source.getIdentifier();
               	Long dataTS = source.getDataTS() != null ? source.getDataTS()  : 0L ;
               	Long statTS = source.getStatTS() != null ? source.getStatTS()  : 0L;
               	int dataInterval = source.getDataInterval();
               	int statInterval = source.getStatInterval();
                boolean pullData = pull(dataInterval, dataTS);
                boolean pullStat = pull(statInterval, statTS);
                
                if(pullData){
                	source.setDataTS(System.currentTimeMillis());
                	source.setStatTS(System.currentTimeMillis());
                }
                
                if(pullStat){
                	source.setStatTS(System.currentTimeMillis());
                }
                
                try{
                	dao.updateSource(source);
                }catch(Exception e){
                	
                }
                

                json.put("sourceId", sourceId);
                json.put("marketId", marketID);
                json.put("brandId" , brandID);
                json.put("owned"   , owned);
                json.put("topic"   , topic);
                json.put("identifier", identifier);
                json.put("platform", platform);
                json.put("pullData", pullData);
                json.put("pullStat",pullStat);

                //TODO the source table in the DB has to be polled and source id and various information to be fetched in memory.
                //TODO credentials will help if they are put in as a comma separated list. Many of the networks need two or more keys (consumer key, access token etc).
                switch (platform) {
                    case "facebookfeed":
                        try {
                            json.put("credentials", split[0]);
                        } catch (Exception e) {
                            PortalUtils.logMessage(logManager, source.getPlatform(), "ERROR", PortalUtils.exceptionAsJson(e).toString());
                        }
                        channel.basicPublish("FB_FEED_EXCHANGE", "", null, json.toString().getBytes());
                        break;
                    case "facebookpost":
                        try {
                            json.put("credentials", split[0]);
                        } catch (Exception e) {
                            PortalUtils.logMessage(logManager, source.getPlatform(), "ERROR", PortalUtils.exceptionAsJson(e).toString());
                        }
                        channel.basicPublish("FB_POST_EXCHANGE", "", null, json.toString().getBytes());
                        break;
                    case "bing":
                        try {
                            json.put("credentials", split[0]);
                            json.put("bitlytoken" , CommonCredentials.BITLY_TOKEN);
                        } catch (Exception e) {
                            PortalUtils.logMessage(logManager, source.getPlatform(), "ERROR", PortalUtils.exceptionAsJson(e).toString());
                        }
                        channel.basicPublish("BING_EXCHANGE", "", null, json.toString().getBytes()); //BING_SOURCE_QUEUE
                        break;
                    case "twitter":
                        try {
                            json.put("consumerKey"   , split[0]);
                            json.put("consumerSecret", split[1]);
                            json.put("accessToken"   , split[2]);
                            json.put("accessTokenSecret", split[3]);
                        } catch (Exception e) {
                            PortalUtils.logMessage(logManager, source.getPlatform(), "ERROR", PortalUtils.exceptionAsJson(e).toString());
                        }
                        channel.basicPublish("TWITTER_TWEETS_EXCHANGE", "", null, json.toString().getBytes());//TWITTER_SOURCE_QUEUE
                        break;
                    case "twittersearch":
                        try {
                            json.put("consumerKey"      , split[0]);
                            json.put("consumerSecret"   , split[1]);
                            json.put("accessToken"      , split[2]);
                            json.put("accessTokenSecret", split[3]);
                        } catch (Exception e) {
                            PortalUtils.logMessage(logManager, source.getPlatform(), "ERROR", PortalUtils.exceptionAsJson(e).toString());
                        }
                        channel.basicPublish("TWITTER_SEARCH_EXCHANGE", "", null, json.toString().getBytes());//TWITTER_SOURCE_QUEUE
                        break;
                    case "tumblrsearch":
                        try {
                            json.put("consumerKey"      , split[0]);
                            json.put("consumerSecret"   , split[1]);
                            json.put("accessToken"      , split[2]);
                            json.put("accessTokenSecret", split[3]);
                            json.put("bitlytoken"       , CommonCredentials.BITLY_TOKEN);
                        } catch (Exception e) {
                            PortalUtils.logMessage(logManager, source.getPlatform(), "ERROR", PortalUtils.exceptionAsJson(e).toString());
                        }
                        channel.basicPublish("TUMBLR_TAG_EXCHANGE", "", null, json.toString().getBytes());//TUMBLER_SOURCE_QUEUE
                        break;
                    case "tumblr":
                        try {
                            json.put("consumerKey"      , split[0]);
                            json.put("consumerSecret"   , split[1]);
                            json.put("accessToken"      , split[2]);
                            json.put("accessTokenSecret", split[3]);
                            json.put("bitlytoken"       , CommonCredentials.BITLY_TOKEN);

                        } catch (Exception e) {
                            PortalUtils.logMessage(logManager, source.getPlatform(), "ERROR", PortalUtils.exceptionAsJson(e).toString());
                        }
                        channel.basicPublish("TUMBLR_SOURCE_EXCHANGE", "", null, json.toString().getBytes());//TUMBLER_SOURCE_QUEUE
                        break;
                    case "rss":
                        try {
                            json.put("bitlytoken", split[0]);
                        } catch (Exception e) {
                            PortalUtils.logMessage(logManager, source.getPlatform(), "ERROR", PortalUtils.exceptionAsJson(e).toString());
                        }
                        channel.basicPublish("RSS_SOURCE_EXCHANGE", "", null, json.toString().getBytes());//RSS_SOURCE_QUEUE
                        break;
                    case "pinterest":
                        channel.basicPublish("PINTEREST_EXCHANGE", "", null, json.toString().getBytes());//PINTEREST_SOURCE_QUEUE
                        break;
                    case "pinterestsearch":
                        channel.basicPublish("PINTEREST_SEARCH_EXCHANGE", "", null, json.toString().getBytes());//PINTEREST_SOURCE_QUEUE
                        break;
                    case "instagram":
                        try {
                            json.put("clientKey"  , split[0]);
                            json.put("accessToken", split[1]);
                        } catch (Exception e) {
                            PortalUtils.logMessage(logManager, source.getPlatform(), "ERROR", PortalUtils.exceptionAsJson(e).toString());
                        }
                        channel.basicPublish("INSTAGRAM_EXCHANGE", "", null, json.toString().getBytes());
                        break;
                    case "instagramsearch":
                        try {
                            json.put("clientKey"  , split[0]);
                            json.put("accessToken", split[1]);
                        } catch (Exception e) {
                            PortalUtils.logMessage(logManager, source.getPlatform(), "ERROR", PortalUtils.exceptionAsJson(e).toString());
                        }
                        channel.basicPublish("INSTAGRAM_SEARCH_EXCHANGE", "", null, json.toString().getBytes());//PINTEREST_SOURCE_QUEUE
                    case "stock":
                        break;
                }

                if (channel != null && channel.isOpen())
                    channel.close();

                if (connection != null && connection.isOpen())
                    connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // ignore of log ... want the for loop to run even if there is an exception in the previous iteration.
        }


    }
    
    private static boolean pull(int interval, long lastTS){
    	if(interval <= 0 || lastTS <= 0)
    		return true;
    	
    	int approxTimePassedInHour = approxTimePassedInHour(lastTS);
    	return approxTimePassedInHour >= interval ;
    }
    
    private static int approxTimePassedInHour(long lastTS)
    {
    	long currentTS = System.currentTimeMillis();
    	int timeElapsed = (int) (currentTS - lastTS);
    	int timeElapsedInHours = timeElapsed / (1000 * 60 * 60);
    	return timeElapsedInHours;
    }
}

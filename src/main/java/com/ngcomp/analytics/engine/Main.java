package com.ngcomp.analytics.engine;

import com.ngcomp.analytics.engine.filter.RequestFilter;
import com.ngcomp.analytics.engine.thread.bing.BingSuckerThread;
import com.ngcomp.analytics.engine.thread.fb.FBFeedSuckerThread;
import com.ngcomp.analytics.engine.thread.fb.FBPostSuckerThread;
import com.ngcomp.analytics.engine.thread.instagram.InstagramSearchSuckerThread;
import com.ngcomp.analytics.engine.thread.instagram.InstagramSuckerThread;
import com.ngcomp.analytics.engine.thread.pinterest.PinterestSearchSuckerThread;
import com.ngcomp.analytics.engine.thread.pinterest.PinterestSuckerThread;
import com.ngcomp.analytics.engine.thread.rss.RSSSuckerThread;
import com.ngcomp.analytics.engine.thread.tumbler.TumblrBlogSuckerThread;
import com.ngcomp.analytics.engine.thread.tumbler.TumblrTagSuckerThread;
import com.ngcomp.analytics.engine.thread.twitter.TwitterSearchSuckerThread;
import com.ngcomp.analytics.engine.thread.twitter.TwitterTweetSuckerThread;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * TumblerUser: Ram Parashar
 * Date: 7/19/13
 * Time: 9:18 PM
 */
public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);
    public static AnnotationConfigWebApplicationContext applicationContext;

    //		// property names for thread counts
    public static final String THREAD_COUNT_FB_POST   = "thread.count.fb.post";
    public static final String THREAD_COUNT_FBFEED    = "thread.count.fbfeed";
    public static final String THREAD_COUNT_INSTAGRAM = "thread.count.instagram";
    public static final String THREAD_COUNT_INSTAGRAMSEARCH = "thread.count.instagramsearch";
    public static final String THREAD_COUNT_TUMBLRBLOG = "thread.count.tumblrblog";
    public static final String THREAD_COUNT_TUMBLRTAG  = "thread.count.tumblrtag";
    public static final String THREAD_COUNT_PINTEREST  = "thread.count.pinterest";

    public static final String THREAD_COUNT_PINTERESTSEARCH = "thread.count.pinterestsearch";

    public static final String THREAD_COUNT_TWITTER      = "thread.count.twitter";
    public static final String THREAD_COUNT_TWITTERTWEET = "thread.count.twittertweet";

    public static final String THREAD_COUNT_BING = "thread.count.bing";
    public static final String THREAD_COUNT_RSS  = "thread.count.rss";


    public static final String EXECUTOR_FB_POST         = "FB_POST";
    public static final String EXECUTOR_FB_FEED         = "FBFEED";
    public static final String EXECUTOR_INSTAGRAM       = "INSTAGRAM";
    public static final String EXECUTOR_INSTAGRAMSEARCH = "INSTAGRAMSEARCH";

    public static final String EXECUTOR_TUMBLRBLOG = "TUMBLRBLOG";
    public static final String EXECUTOR_TUMBLRTAG  = "TUMBLRTAG";
    public static final String EXECUTOR_PINTEREST  = "PINTEREST";

    public static final String EXECUTOR_PINTERESTSEARCH = "PINTERESTSEARCH";
    public static final String EXECUTOR_TWITTER      = "TWITTER";
    public static final String EXECUTOR_TWITTERTWEET = "TWITTERTWEET";

    public static final String EXECUTOR_BING = "BING";
    public static final String EXECUTOR_RSS  = "RSS";


    //
//		// variables to store the thread counts.
    static int noOfTwitterSearchThread   = 1;
    static int noOfTwitterTweetThread    = 1;
    static int noOfPinterestSearchThread = 1;

    static int noOfPinterestThread  = 1;
    static int noOfBingSearchThread = 1;

    static int noOfInstagramSearchThread = 1;

    static int noOfInstagramThread  = 1;
    static int noOfFBFeedThread     = 10;
    static int noOfFBPostThread     = 10;
    static int noOfTumblrBlogThread = 1;
    static int noOfTumblrTagThread  = 1;

    static int noOfRSSThread = 1;
    static int EXPIRATION_TIME_IN_HOUR = 4;

    static {

        InputStream resourceAsStream = Main.class.getResourceAsStream("/config.properties");
        Properties prop = new Properties();

        try {
            prop.load(resourceAsStream);
            noOfTwitterSearchThread = (isNotNullOrEmpty(String.valueOf(prop.get(THREAD_COUNT_TWITTER))) ? Integer.parseInt((String) prop.get(THREAD_COUNT_TWITTER)) : 1);
            noOfTwitterTweetThread = (isNotNullOrEmpty(String.valueOf(prop.get(THREAD_COUNT_TWITTERTWEET))) ? Integer.parseInt((String) prop.get(THREAD_COUNT_TWITTERTWEET)) : 1);
            noOfPinterestSearchThread = (isNotNullOrEmpty(String.valueOf(prop.get(THREAD_COUNT_PINTERESTSEARCH))) ? Integer.parseInt((String) prop.get(THREAD_COUNT_PINTERESTSEARCH)) : 1);
            noOfPinterestThread = (isNotNullOrEmpty(String.valueOf(prop.get(THREAD_COUNT_PINTEREST))) ? Integer.parseInt((String) prop.get(THREAD_COUNT_PINTEREST)) : 1);
            noOfBingSearchThread = (isNotNullOrEmpty(String.valueOf(prop.get(THREAD_COUNT_BING))) ? Integer.parseInt((String) prop.get(THREAD_COUNT_BING)) : 1);
            noOfInstagramSearchThread = (isNotNullOrEmpty(String.valueOf(prop.get(THREAD_COUNT_INSTAGRAMSEARCH))) ? Integer.parseInt((String) prop.get(THREAD_COUNT_INSTAGRAMSEARCH)) : 1);
            noOfInstagramThread = (isNotNullOrEmpty(String.valueOf(prop.get(THREAD_COUNT_INSTAGRAM))) ? Integer.parseInt((String) prop.get(THREAD_COUNT_INSTAGRAM)) : 1);
            noOfFBFeedThread = (isNotNullOrEmpty(String.valueOf(prop.get(THREAD_COUNT_FBFEED))) ? Integer.parseInt((String) prop.get(THREAD_COUNT_FBFEED)) : 1);
            noOfFBPostThread = (isNotNullOrEmpty(String.valueOf(prop.get(THREAD_COUNT_FB_POST))) ? Integer.parseInt((String) prop.get(THREAD_COUNT_FB_POST)) : 1);
            noOfTumblrBlogThread = (isNotNullOrEmpty(String.valueOf(prop.get(THREAD_COUNT_TUMBLRBLOG))) ? Integer.parseInt((String) prop.get(THREAD_COUNT_TUMBLRBLOG)) : 1);
            noOfTumblrTagThread = (isNotNullOrEmpty(String.valueOf(prop.get(THREAD_COUNT_TUMBLRTAG))) ? Integer.parseInt((String) prop.get(THREAD_COUNT_TUMBLRTAG)) : 1);
            noOfRSSThread = (isNotNullOrEmpty(String.valueOf(prop.get(THREAD_COUNT_RSS))) ? Integer.parseInt((String) prop.get(THREAD_COUNT_RSS)) : 1);

        } catch (IOException e) {
            logger.error("Unable to load the properties file.", e);
        }
    }

    //
    public static void main(String[] args) throws Exception {
        System.out.println("Hello");


        System.err.println("noOfBingSearchThread===========================>" + noOfBingSearchThread);

        // start the various API threads

        final Map<String, ExecutorService> executorsMap = new HashMap<String, ExecutorService>();


        // initialize all the Executor Service
        ExecutorService bingExecutorService   = Executors.newFixedThreadPool(noOfBingSearchThread);
        ExecutorService fbFeedExecutorService = Executors.newFixedThreadPool(noOfFBFeedThread);
        ExecutorService fbPostExecutorService = Executors.newFixedThreadPool(noOfFBPostThread);

        ExecutorService instagramSearchSuckerExecutorService = Executors.newFixedThreadPool(noOfInstagramSearchThread);
        ExecutorService instagramThreadExecutorService       = Executors.newFixedThreadPool(noOfInstagramThread);
        ExecutorService pinterestSearchExecutorService       = Executors.newFixedThreadPool(noOfPinterestSearchThread);

        ExecutorService pinterestExecutorService  = Executors.newFixedThreadPool(noOfPinterestThread);
        ExecutorService rssExecutorService        = Executors.newFixedThreadPool(noOfRSSThread);
        ExecutorService tumblrBlogExecutorService = Executors.newFixedThreadPool(noOfTumblrBlogThread);

        ExecutorService noOfTumblrTagExecutorService = Executors.newFixedThreadPool(noOfTumblrTagThread);
        ExecutorService twitterSearchExecutorService = Executors.newFixedThreadPool(noOfTwitterSearchThread);
        ExecutorService twitterTweetExecutorService  = Executors.newFixedThreadPool(noOfTwitterTweetThread);

        // prepare map of executors and their thread pool count.
        executorsMap.put(EXECUTOR_BING, bingExecutorService);
        executorsMap.put(EXECUTOR_FB_FEED, fbFeedExecutorService);
        executorsMap.put(EXECUTOR_FB_POST, fbPostExecutorService);
        executorsMap.put(EXECUTOR_INSTAGRAMSEARCH, instagramSearchSuckerExecutorService);
        executorsMap.put(EXECUTOR_INSTAGRAM, instagramThreadExecutorService);
        executorsMap.put(EXECUTOR_PINTERESTSEARCH, pinterestSearchExecutorService);
        executorsMap.put(EXECUTOR_PINTEREST, pinterestExecutorService);
        executorsMap.put(EXECUTOR_RSS, rssExecutorService);
        executorsMap.put(EXECUTOR_TUMBLRBLOG, tumblrBlogExecutorService);
        executorsMap.put(EXECUTOR_TUMBLRTAG, noOfTumblrTagExecutorService);
        executorsMap.put(EXECUTOR_TWITTER, twitterSearchExecutorService);
        executorsMap.put(EXECUTOR_TWITTERTWEET, twitterTweetExecutorService);


        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                boolean startflag = true;
                while (true) {
                    for (String serviceName : executorsMap.keySet()) {
                        ExecutorService service = executorsMap.get(serviceName);
                        if (startflag || service.isTerminated() || service.isShutdown()) {

                            switch (serviceName) {
                                case EXECUTOR_BING:
                                    for (int count = 0; count < noOfBingSearchThread; count++) {
                                        service.execute(new BingSuckerThread());
                                    }
                                    break;
                                case EXECUTOR_FB_FEED:
                                    for (int count = 0; count < noOfFBFeedThread; count++) {
                                        service.execute(new FBFeedSuckerThread());
                                    }
                                    break;
                                case EXECUTOR_FB_POST:
                                    for (int count = 0; count < noOfFBPostThread; count++) {
                                        service.execute(new FBPostSuckerThread());
                                    }
                                    break;
                                case EXECUTOR_INSTAGRAMSEARCH:
                                    for (int count = 0; count < noOfInstagramSearchThread; count++) {
                                        service.execute(new InstagramSearchSuckerThread());
                                    }
                                    break;
                                case EXECUTOR_INSTAGRAM:
                                    for (int count = 0; count < noOfInstagramThread; count++) {
                                        service.execute(new InstagramSuckerThread());
                                    }
                                    break;
                                case EXECUTOR_PINTERESTSEARCH:
                                    for (int count = 0; count < noOfPinterestSearchThread; count++) {
                                        service.execute(new PinterestSearchSuckerThread());
                                    }
                                    break;
                                case EXECUTOR_PINTEREST:
                                    for (int count = 0; count < noOfPinterestThread; count++) {
                                        service.execute(new PinterestSuckerThread());
                                    }
                                    break;
                                case EXECUTOR_RSS:
                                    for (int count = 0; count < noOfRSSThread; count++) {
                                        service.execute(new RSSSuckerThread());
                                    }
                                    break;
                                case EXECUTOR_TUMBLRBLOG:
                                    for (int count = 0; count < noOfTumblrBlogThread; count++) {
                                        service.execute(new TumblrBlogSuckerThread());
                                    }
                                    break;
                                case EXECUTOR_TUMBLRTAG:
                                    for (int count = 0; count < noOfTumblrTagThread; count++) {
                                        service.execute(new TumblrTagSuckerThread());
                                    }
                                    break;
                                case EXECUTOR_TWITTER:
                                    for (int count = 0; count < noOfTwitterSearchThread; count++) {
                                        service.execute(new TwitterTweetSuckerThread());
                                    }
                                    break;
                                case EXECUTOR_TWITTERTWEET:
                                    for (int count = 0; count < noOfTwitterTweetThread; count++) {
                                        service.execute(new TwitterSearchSuckerThread());
                                    }
                                    break;
                            }
                        }
                    }

                    startflag = false;

                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });



        applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.scan("com.ngcomp.analytics.engine");

        final ServletHolder servletHolder = new ServletHolder(new DispatcherServlet(applicationContext));

        final ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(servletHolder, "/api/*");



        context.addFilter(HiddenHttpMethodFilter.class, "/*", null);
        context.addFilter(RequestFilter.class, "/*", null);

        final Server server = new Server(8083);

        server.setHandler(context);
        server.start();

        //Start Consumer Threads
        thread.start();

        server.join();

    }

//        // utility method to check if a string is not null and not empty
    static boolean isNotNullOrEmpty(String s) {
        if (s == null || s.trim().length() == 0)
            return false;

        return true;
    }

}

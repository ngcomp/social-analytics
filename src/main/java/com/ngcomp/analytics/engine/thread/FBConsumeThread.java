package com.ngcomp.analytics.engine.thread;

import com.google.gson.Gson;
import com.ngcomp.analytics.engine.conn.HBaseProxy;
import com.ngcomp.analytics.engine.model.FBFeed;
import com.ngcomp.analytics.engine.model.Trend;
import com.ngcomp.analytics.engine.thread.fb.helper.FBFeedParser;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: rparashar
 * Date: 8/31/13
 * Time: 11:08 AM
 */
public class FBConsumeThread implements Runnable {

    private ConnectionFactory connectionFactory;

    private HBaseProxy hBaseProxy;

    public FBConsumeThread(ConnectionFactory connectionFactory) throws IOException {
        System.out.println("One....");
        this.connectionFactory = connectionFactory;
        System.out.println("Two....");
        this.hBaseProxy = HBaseProxy.getInstance();
        System.out.println("Done....");
    }
    @Override
    public void run() {

        try {

            ExecutorService es = Executors.newFixedThreadPool(20);
            Connection conn = this.connectionFactory.newConnection(es);

            Channel channel = conn.createChannel();
            channel.basicQos(1);

            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume("FB_FEED_QUEUE", true, consumer);

            Gson gson = new Gson();

            while (true) {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                String message      = new String(delivery.getBody());

                FBFeed feed         = gson.fromJson(message, FBFeed.class);
                FBFeedParser f = new FBFeedParser(feed.getSourceO(), feed);

                Trend trend = f.parsePost();

                //hBaseProxy.postTrend(trend);
                f = null;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

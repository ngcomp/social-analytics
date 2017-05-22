package com.ngcomp.analytics.engine.config;

import com.ngcomp.analytics.engine.util.PropUtils;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

/**
 * User: Ram Parashar
 * Date: 9/28/13
 * Time: 3:10 PM
 */
public class RbtMQConnectionFactory {

    private static ConnectionFactory connectionFactory;

    static
    {
        connectionFactory = new ConnectionFactory();

        connectionFactory.setUsername(PropUtils.getVal("rbtmq.user.id" ));
        connectionFactory.setPassword(PropUtils.getVal("rbtmq.password"));
        connectionFactory.setHost    (PropUtils.getVal("rbtmq.host"    ));
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPort(5672);
    }

    public static Connection getConnection() throws IOException {
        return connectionFactory.newConnection();
    }

    public static ConnectionFactory getInstance(){
        return connectionFactory;
    }

    private  RbtMQConnectionFactory(){
    }

}

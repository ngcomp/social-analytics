package com.ngcomp.analytics.engine.config;
import com.ngcomp.analytics.engine.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * Created with IntelliJ IDEA.
 * TumblerUser: rparashar
 * Date: 7/20/13
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */

@Configuration
@PropertySource("classpath:config.properties")
public class AppConfig {

    @Autowired
    Environment environment;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory()
    {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        String host  = this.environment.getProperty(Constants.REDIS_HOST);
        Integer port = Integer.valueOf(this.environment.getProperty(Constants.REDIS_PORT));
        Integer db = Integer.valueOf(this.environment.getProperty(Constants.REDIS_DB));

        jedisConnectionFactory.setHostName(host);
        jedisConnectionFactory.setPort    (port);
        jedisConnectionFactory.setDatabase(db);
        jedisConnectionFactory.setUsePool(true);

        return jedisConnectionFactory;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate()
    {

        final StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();

        stringRedisTemplate.setConnectionFactory   (jedisConnectionFactory());
        stringRedisTemplate.setKeySerializer       (new StringRedisSerializer());
        stringRedisTemplate.setHashValueSerializer (new GenericToStringSerializer<String>(String.class));
        stringRedisTemplate.setValueSerializer     (new GenericToStringSerializer<String>(String.class));
        return stringRedisTemplate;

    }

//    @Bean
//    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
//    {
//        return new PropertySourcesPlaceholderConfigurer();
//    }

}
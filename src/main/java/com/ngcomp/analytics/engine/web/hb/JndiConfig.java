package com.ngcomp.analytics.engine.web.hb;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * JNDI Configuration for the applciation. It declares a bean named dataSource
 * which can be injected in the {@link HibernateConfig} configuration class.
 * JNDI name for the source is csdb.
 * 
 * @author dprasad
 * @since 0.5
 */
@Configuration
public class JndiConfig {

	@Value(DBConstants.DRIVER_CLASS)
	private String driverClassName;

	@Value(DBConstants.DATABASE_URL)
	private String databaseURL;

	@Value(DBConstants.USER_NAME)
	private String username;

	@Value(DBConstants.PASSWORD)
	private String password;

	@Value(DBConstants.MAX_ACTIVE)
	private int maxActive;

	@Value(DBConstants.MAX_IDLE)
	private int maxIdle;

	@Value(DBConstants.MAX_WAIT)
	private int maxWait;

	@Value(DBConstants.MIN_IDLE)
	private int minIdle;

	@Value(DBConstants.INITIAL_SIZE)
	private int initialSize;

	@Value(DBConstants.TIME_BETWEEN_EVICTION)
	private long timeBetweenEvictionRunsMillis;

	@Bean(name = "dataSource")
	public DataSource dataSource() throws Exception {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl            (databaseURL);
		dataSource.setUsername       (username);
		dataSource.setPassword       (password);
		dataSource.setTestOnBorrow   (true);
		dataSource.setTestWhileIdle  (true);
		dataSource.setValidationQuery("SELECT 1");
		dataSource.setMaxActive      (maxActive);
		dataSource.setMaxIdle        (maxIdle);
		dataSource.setMaxWait        (maxWait);
		dataSource.setMinIdle        (minIdle);
		dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

		return dataSource;
	}
}

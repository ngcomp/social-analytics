package com.ngcomp.analytics.engine.web.hb;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Hibernate Configuration for the application. This class
 * provides the session factory and reads the details of the connection from a
 * properties file.
 * 
 * It also declares dataSource, sessionFactory and transactionManager beans and
 * a uses the default {@link HibernateTransactionManager}. To include this
 * hibernate configuration into any of the annotated configuration classes
 * please use the code below:
 * 
 * <code>
 * 
 * @Import(HibernateConfig.class) </code>
 * 
 * @author dprasad
 */
@Configuration
@EnableTransactionManagement
@Import(JndiConfig.class)
@PropertySource({"classpath:config.properties"})
@ComponentScan(basePackages = "com.ngcomp.analytics.engine.web.hb")
public class HibernateConfig {

	@Value(DBConstants.HIBERNATE_DIALECT)
	private String hibernateDialect;

	@Autowired
	private DataSource dataSource;

	@Bean(name = "appProperty")
	public static PropertySourcesPlaceholderConfigurer appProperty() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean(name = "sessionFactory")
	public SessionFactory getSessionFactory() throws Exception {

		Properties properties = new Properties();
		properties.put (DBConstants.HIBERNATE_DIALECT_PROPERTY                      , hibernateDialect);
		properties.put (DBConstants.HIBERNATE_SHOW_SQL_PROPERTY                     , "false");
		properties .put(DBConstants.HIBERNATE_CURRENT_SESSION_CONTEXT_CLASS_PROPERTY, "thread");
		properties .put("dynamic-update","true");
		LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
		factory.setPackagesToScan(new String[] { DBConstants.DOMAIN_MODEL_PACKAGE });
		factory.setDataSource(dataSource);
		factory.setHibernateProperties(properties);
		factory.afterPropertiesSet();
		return factory.getObject();
	}

	@Bean(name = "transactionManager")
	public HibernateTransactionManager getTransactionManager() throws Exception {
		return new HibernateTransactionManager(getSessionFactory());
	}
}

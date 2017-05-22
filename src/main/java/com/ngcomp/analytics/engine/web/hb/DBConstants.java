package com.ngcomp.analytics.engine.web.hb;

public class DBConstants {
	public static final String HIBERNATE_CURRENT_SESSION_CONTEXT_CLASS_PROPERTY = "current_session_context_class";
	public static final String DOMAIN_MODEL_PACKAGE = "com.ngcomp.analytics.engine.domain";
	public static final String HIBERNATE_SHOW_SQL_PROPERTY = "hibernate.show_sql";

	public static final String HIBERNATE_DIALECT_PROPERTY = "hibernate.dialect";
	public static final String HIBERNATE_DIALECT = "${"+ HIBERNATE_DIALECT_PROPERTY + "}";
	
	public static final String DATABASE_URL_PROPERTY = "jdbc.databaseurl";
	public static final String DATABASE_URL="${"+DATABASE_URL_PROPERTY+"}";
	
	public static final String DRIVER_CLASS_NAME = "jdbc.driverClassName";
	public static final String DRIVER_CLASS="${"+DRIVER_CLASS_NAME+"}";
	
	public static final String DATABASE_USER_NAME_PROPERTY = "jdbc.username";
	public static final String USER_NAME="${"+DATABASE_USER_NAME_PROPERTY+"}";
	
	public static final String DATABASE_PASSWORD_PROPERTY = "jdbc.password";
	public static final String PASSWORD="${"+DATABASE_PASSWORD_PROPERTY+"}";
	
	public static final String DATABASE_TIME_BETWEEN_EVICTION_PROPERTY = "datasource.timeBetweenEvictionRunsMillis";
	public static final String TIME_BETWEEN_EVICTION="${"+DATABASE_TIME_BETWEEN_EVICTION_PROPERTY+"}";
	
	public static final String DATABASE_MAX_ACTIVE_PROPERTY = "datasource.max.active";
	public static final String MAX_ACTIVE="${"+DATABASE_MAX_ACTIVE_PROPERTY+"}";

	public static final String DATABASE_MAX_IDLE_PROPERTY = "datasource.max.idle";
	public static final String MAX_IDLE="${"+DATABASE_MAX_IDLE_PROPERTY+"}";
	
	public static final String DATABASE_MIN_IDLE_PROPERTY ="datasource.min.idle";
	public static final String MIN_IDLE="${"+DATABASE_MIN_IDLE_PROPERTY+"}";

	public static final String DATABASE_MAX_WAIT_PROPERTY = "datasource.max.wait";
	public static final String MAX_WAIT="${"+DATABASE_MAX_WAIT_PROPERTY+"}";

	public static final String DATABASE_INTIAL_SIZE_PROPERTY = "datasource.initial.size";
	public static final String INITIAL_SIZE="${"+DATABASE_INTIAL_SIZE_PROPERTY+"}";
		
}

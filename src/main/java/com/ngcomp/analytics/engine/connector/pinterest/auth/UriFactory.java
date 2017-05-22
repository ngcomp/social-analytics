package com.ngcomp.analytics.engine.connector.pinterest.auth;

public class UriFactory {
	//public static final String PINTEREST_BASE_URL = "http://openapi.etsy.com/svc/oembed/";
	
	public static final String PINTEREST_API_BASE_URL = "http://pinterestapi.co.uk";
	public static final String PINTEREST_URL = "http://www.pinterest.com";
	
	public static final String PINS_FOR_USER = PINTEREST_API_BASE_URL + "/{username}/pins";
	public static final String BOARDS_FOR_USER = PINTEREST_API_BASE_URL + "/{username}/boards";
	
	public static final String GET_PINS = PINTEREST_URL + "/search/pins/?q=";
	
	public static final String GET_PIN = PINTEREST_URL + "/pin/{pinId}";
	
}

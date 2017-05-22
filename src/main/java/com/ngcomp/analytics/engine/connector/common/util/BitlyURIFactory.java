package com.ngcomp.analytics.engine.connector.common.util;

public class BitlyURIFactory {
	public static String ROOT_BITLY = "https://api-ssl.bitly.com/v3/";
	public static String LINK_LOOKUP = ROOT_BITLY  + "link/lookup?url={url}&access_token={access_token}";
	public static String LINK_CLICKS = ROOT_BITLY + "link/clicks?link={link}&access_token={access_token}&unit=hour&units=-1&rollup=true&timezone=UTC";
	public static String LINK_SHARES = ROOT_BITLY + "link/shares?link={link}&access_token={access_token}&unit=hour&units=-1&rollup=true&timezone=UTC";
}


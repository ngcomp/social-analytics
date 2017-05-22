package com.ngcomp.analytics.engine.connector.pinterest.auth;

import java.util.Map;

public class UriConstructor {

	public UriConstructor() {
		super();
	} 
	
	public String url(String uri, Map<String, String> map) {
		try{
			for (Map.Entry<String, String> arg : map.entrySet()) {
				uri = uri.replaceAll("\\{"+arg.getKey()+"\\}", arg.getValue().toString());
			}
		}
		catch(NullPointerException e) {}
		
		return uri;
	}

}

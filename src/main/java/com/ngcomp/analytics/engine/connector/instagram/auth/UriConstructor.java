package com.ngcomp.analytics.engine.connector.instagram.auth;

import com.ngcomp.analytics.engine.connector.common.CommonCredentials;

import java.util.Map;

public class UriConstructor {
	String accessToken;
	String clientKey;

	private void setClientKey(String clientKey) {
		this.clientKey = clientKey;
	}

	public UriConstructor() {
		super();
	} 
	
	public UriConstructor(String accessToken) {
		super();
		setAccessToken(accessToken);
	} 
	
	public UriConstructor(String accessToken, String clientKey) {
		super();
		setAccessToken(accessToken);
		setClientKey(clientKey);
	} 

	

	private void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public String url(String uri, Map<String, String> map, boolean addAccessToken, boolean addClientKey) {
		try{
			for (Map.Entry<String, String> arg : map.entrySet()) {
				uri = uri.replaceAll("\\{"+arg.getKey()+"\\}", arg.getValue().toString());
			}
		}
		catch(NullPointerException e) {}
		
		
		if(addClientKey)
			uri += "?client_id="+ getClientKey();
		
		if(addAccessToken) {
			uri += "?access_token=" + getAccessToken();
		}
		return uri;
	}

	private String getClientKey() {
		if(this.clientKey != null && this.clientKey.length() > 0)
			return this.clientKey;
		return CommonCredentials.CLIENT_ID_INSTAGRAM;
	}
	

	private String getAccessToken() {
		if(this.accessToken != null && this.accessToken.length() > 0)
			return this.accessToken;
		return CommonCredentials.CLIENT_AUTH_TOKEN_INSTAGRAM;
	}
}

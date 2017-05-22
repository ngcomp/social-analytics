package com.ngcomp.analytics.engine.connector.instagram.auth;

import com.ngcomp.analytics.engine.connector.common.util.RESTPosterClient;
import com.ngcomp.analytics.engine.connector.instagram.UriFactory;

import java.util.HashMap;
import java.util.Map;

public class AccessToken {
	private static String token;
	
	public AccessToken(String token) {
		this.setTokenString(token);
	}
	
	public String getTokenString() {
		return token;
	}

	public void setTokenString(String token) {
		this.token = token;
	}

	public String toString() {
		return getTokenString();
	}
	
	
	public static void createAccessToken(){
		if(token == null || token.length() <= 0)
		{
			Map<String, String> map = new HashMap<String, String>();
			map.put("redirect_uri","'http://techieme.in");
			map.put("response_type","token");
			
			UriConstructor construct = new UriConstructor();
			String url = construct.url(UriFactory.Auth.GET_ACCESS_TOKEN, map, false, true);
			String requestRESTGet = RESTPosterClient.requestRESTGet(url);
			System.out.println();
			
		}
			
	}
	
	public static void main(String[]args){
		createAccessToken();
		System.out.println();
	}
}

package com.ngcomp.analytics.engine.connector.common.util;

import com.google.gson.Gson;
import com.ngcomp.analytics.engine.connector.common.CommonCredentials;
import com.ngcomp.analytics.engine.connector.common.model.BitlyClickResponse;
import com.ngcomp.analytics.engine.connector.common.model.BitlyShareResponse;
import com.ngcomp.analytics.engine.connector.common.model.BitlyURLLookUpResponse;

public class BitlyUtil {
	public static String getShortLink(String url, String accessToken){
		String urlTemp = BitlyURIFactory.LINK_LOOKUP.replace("{url}", url);
		urlTemp = urlTemp.replace("{access_token}", accessToken);
		String response = RESTPosterClient.requestRESTGet(urlTemp);
		Gson gson = new Gson();
		BitlyURLLookUpResponse respObject = gson.fromJson(response, BitlyURLLookUpResponse.class);
		if(respObject != null && respObject.getData() != null && respObject.getData().getLink_lookup().length > 0){
			return respObject.getData().getLink_lookup()[0].getAggregate_link();
		}
		return null;
	}
	
	public static int getClicks(String link, String accessToken){
		String urlTemp = BitlyURIFactory.LINK_CLICKS.replace("{link}", link);
		urlTemp = urlTemp.replace("{access_token}", accessToken);
		String response = RESTPosterClient.requestRESTGet(urlTemp);
		Gson gson = new Gson();
		BitlyClickResponse respObject = gson.fromJson(response, BitlyClickResponse.class);
		if(respObject != null && respObject.getData() != null ){
			return respObject.getData().getLink_clicks();
		}
		return -1;
	}
	
	public static int getShares(String link, String accessToken) {
		String urlTemp = BitlyURIFactory.LINK_SHARES.replace("{link}", link);
		urlTemp = urlTemp.replace("{access_token}", accessToken);
		String response = RESTPosterClient.requestRESTGet(urlTemp);
		Gson gson = new Gson();
		BitlyShareResponse respObject = gson.fromJson(response, BitlyShareResponse.class);
		if(respObject != null && respObject.getData() != null ){
			return respObject.getData().getTotal_shares();
		}
		return -1;
	}
	
	public static void main(String[]args){
		String shortLink = getShortLink("http://techieme.in/techieme/page/2/", CommonCredentials.BITLY_TOKEN);
		int clicks = getClicks("http://bit.ly/15YuNhi", CommonCredentials.BITLY_TOKEN);
		int shares = getShares("http://bit.ly/15YuNhi", CommonCredentials.BITLY_TOKEN);
		System.out.println(shortLink + " "+clicks+ " "+shares);
	}
}

package com.ngcomp.analytics.engine.connector.common.util;

import com.ngcomp.analytics.engine.connector.common.model.BitlyInfo;

public class ConstructBitly {

	public static BitlyInfo getBitlyInfo(String url, String accessToken){
		
		BitlyInfo info = new BitlyInfo();
		String shortLink = BitlyUtil.getShortLink(url, accessToken);
		if(shortLink != null && shortLink.length() > 0 ){
			int clicks = BitlyUtil.getClicks(shortLink, accessToken);
			int shares = BitlyUtil.getShares(shortLink, accessToken);
			info.setBitlyUrl(shortLink);
			info.setOriginalUrl(url);
			info.setClicks(clicks);
			info.setShares(shares);
			
		}
		
		return info;
	}
}

package com.ngcomp.analytics.engine.connector.fb;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;

public class FBConnector {

	public FacebookClient getFacebookClient(String ACCESS_CODE) {
		
		FacebookClient facebookClient = new DefaultFacebookClient(ACCESS_CODE) ;
		return facebookClient ;
	}
}

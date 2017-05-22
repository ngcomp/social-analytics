package com.ngcomp.analytics.engine.connector.fb;

import com.ngcomp.analytics.engine.connector.fb.model.LinkStat;
import com.restfb.FacebookClient;

public class Sample {

	
	public static void main(String[] args) {
		
		getCommentCount();
		getResponsesNow() ;
	}
	
	
	public static void getResponsesNow() {
		
		String ACCESS_CODE = "CAAEt4y3a0QwBAEhZAnoraSupv0FtwvN6bBjC0JN8gtQZCDO86QUvUZBQVVryueQZCjFd2N5FqsufKIeXiDxaGj2ZCSjJVZA6h84G9uRRUVHVGXOuCNXPkVENaakBGd8oiXQiZAo9MF3lV8VnZBfiY8No" ;
		FBConnector fbConnector = new FBConnector();
		FacebookClient fbClient = fbConnector.getFacebookClient(ACCESS_CODE) ;
		
		String url = "https://www.facebook.com/forbes";
		GetResponses responses = new GetResponses(fbClient) ;
		
        System.out.println("**************\n");
        LinkStat liskstat = responses.getLinkStat(url) ;
        System.out.println(liskstat);
	}

	
	
	public static void getCommentCount() {

		String ACCESS_CODE = "CAAEt4y3a0QwBAEhZAnoraSupv0FtwvN6bBjC0JN8gtQZCDO86QUvUZBQVVryueQZCjFd2N5FqsufKIeXiDxaGj2ZCSjJVZA6h84G9uRRUVHVGXOuCNXPkVENaakBGd8oiXQiZAo9MF3lV8VnZBfiY8No" ;
		FBConnector fbConnector = new FBConnector();
		FacebookClient fbClient = fbConnector.getFacebookClient(ACCESS_CODE) ;

		GetCommentInfo comentInfo = new GetCommentInfo(fbClient) ;

		int commentCount = comentInfo.getCommentCountByPostId("212160295532302_352111168203880") ;
		System.out.println("commentCount is " + commentCount);

		commentCount = comentInfo.getCommentCountByQuery("SELECT comment_info FROM stream WHERE actor_id = 14226545351 AND source_id = 14226545351 LIMIT 5000") ;
		System.out.println("commentCount is " + commentCount);
	}
}

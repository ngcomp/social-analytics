package com.ngcomp.analytics.engine.connector.fb;

import com.ngcomp.analytics.engine.connector.fb.model.CommentInfo;
import com.restfb.FacebookClient;

import java.util.List;

public class GetCommentInfo {

	FacebookClient facebookClient = null; 
			
	public GetCommentInfo(FacebookClient facebookClient) {
	
		this.facebookClient = facebookClient ;
	}
	
	
	public int getCommentCountByPostId(String postId) {
		
		String query = "SELECT comment_info FROM stream WHERE post_id = '" + postId + "' ";
		List<CommentInfo> comments = facebookClient.executeFqlQuery(query, CommentInfo.class);
    	return comments != null ? comments.size() : 0  ;
	}
	
	
	public int getCommentCountByQuery(String query) {
		
		List<CommentInfo> comments = facebookClient.executeFqlQuery(query, CommentInfo.class);
    	return comments != null ? comments.size() : 0  ;
	}
	
	
}



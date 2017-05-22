package com.ngcomp.analytics.engine.connector.fb;

import com.ngcomp.analytics.engine.connector.fb.model.LinkStat;
import com.restfb.FacebookClient;
import com.restfb.json.JsonObject;

import java.util.List;

public class GetResponses {

	FacebookClient facebookClient = null; 
	
	public GetResponses(FacebookClient facebookClient) {
	
		this.facebookClient = facebookClient ;
	}
	
	
	
	public JsonObject getResponsesNow(String url) {
		 
		String query = "SELECT share_count, like_count,  comment_count,  total_count,  commentsbox_count,  comments_fbid,  click_count  FROM  link_stat  WHERE  url='" + url + "'";
		List<JsonObject> list = facebookClient.executeFqlQuery(query, JsonObject.class);

        if (list.size() > 0) {
          System.out.print("Share Count::" + list.get(0).getString("share_count"));
//          System.out.print("like_count::" + list.get(0).getString("like_count"));
//          System.out.print("comment_count::" + list.get(0).getString("comment_count"));
//          System.out.print("click_count::" + list.get(0).getString("click_count"));*/
        	
        	return list.get(0) ;
        }
    	
        return null ;
	}
	
	
	
	
	public LinkStat getLinkStat(String url) {
		 
		String query = "SELECT share_count, like_count,  comment_count,  total_count,  commentsbox_count,  comments_fbid,  click_count  FROM  link_stat  WHERE  url='" + url + "'";
		List<LinkStat> list = facebookClient.executeFqlQuery(query, LinkStat.class);

        return list !=null && list.size() > 0 ? list.get(0) : null ;
    }
	
}

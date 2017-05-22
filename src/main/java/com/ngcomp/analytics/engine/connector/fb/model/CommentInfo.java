package com.ngcomp.analytics.engine.connector.fb.model;

import com.restfb.Facebook;
import com.restfb.types.FacebookType;

public class CommentInfo extends FacebookType {
	  
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Facebook("can_comment")
	private boolean canComment;

	  
	public boolean isCanComment() {
		return canComment;
	}


	public int getCommentCount() {
		return commentCount;
	}


	public String getCommentOrder() {
		return commentOrder;
	}


	@Facebook("comment_count")
	private int commentCount;
	  
	  
	@Facebook("comment_order")
	private String commentOrder;
	  
}


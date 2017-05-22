package com.ngcomp.analytics.engine.connector.fb.model;

import com.restfb.Facebook;
import com.restfb.types.FacebookType;


public class LinkStat extends FacebookType {
	 
	// share_count, like_count,  comment_count,  total_count,  commentsbox_count,  comments_fbid,  click_count
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Facebook("share_count")
	private int shareCount;

	@Facebook("like_count")
	private int likeCount;
	
	@Facebook("comment_count")
	private int commentCount;
	
	@Facebook("total_count")
	private int totalCount;
	
	@Facebook("commentsbox_count")
	private int commentsboxCount;
	
	@Facebook("comments_fbid")
	private int commentsFbid;
	
	@Facebook("click_count")
	private int clickCount;
	
	
	public int getShareCount() {
		return shareCount;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public int getCommentsboxCount() {
		return commentsboxCount;
	}

	public int getCommentsFbid() {
		return commentsFbid;
	}

	public int getClickCount() {
		return clickCount;
	}
	
	
	@Override
	public String toString() {
		return "LinkStat [shareCount=" + shareCount + ", likeCount="
				+ likeCount + ", commentCount=" + commentCount
				+ ", totalCount=" + totalCount + ", commentsboxCount="
				+ commentsboxCount + ", commentsFbid=" + commentsFbid
				+ ", clickCount=" + clickCount + "]";
	}
	
}

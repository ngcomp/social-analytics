package com.ngcomp.analytics.engine.model;

import com.restfb.Facebook;
import com.restfb.types.FacebookType;

public class FBComment extends FacebookType {

	@Facebook("id")
	private String id;
	
	@Facebook("from")
	private FBFrom from;

	
	@Facebook("message")
	private String message;
	
	@Facebook("can_remove")
	private String can_remove;
	
	
	@Facebook("created_time")
	private String created_time;
	
	@Facebook("like_count")
	private String like_count;
	
	
	@Facebook("user_likes")
	private String user_likes;


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public FBFrom getFrom() {
		return from;
	}


	public void setFrom(FBFrom from) {
		this.from = from;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public String getCan_remove() {
		return can_remove;
	}


	public void setCan_remove(String can_remove) {
		this.can_remove = can_remove;
	}


	public String getCreated_time() {
		return created_time;
	}


	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}


	public String getLike_count() {
		return like_count;
	}


	public void setLike_count(String like_count) {
		this.like_count = like_count;
	}


	public String getUser_likes() {
		return user_likes;
	}


	public void setUser_likes(String user_likes) {
		this.user_likes = user_likes;
	}
	
	
	@Override
	public String toString() {
		return "FBComment [id=" + id + ", from=" + from + ", message="
				+ message + ", can_remove=" + can_remove + ", created_time="
				+ created_time + ", like_count=" + like_count + ", user_likes="
				+ user_likes + "]";
	}
	
}

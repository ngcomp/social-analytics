package com.ngcomp.analytics.engine.connector.tumbler.model;

import java.util.ArrayList;
import java.util.List;

public class TumblrUser {

	private List<TumblrBlog> blogs;
	private String name;
	private int following;
	private int likes;

	public List<TumblrBlog> getBlogs() {
		if(null == blogs)
			blogs = new ArrayList<TumblrBlog>();
		return blogs;
	}

	public void setBlogs(List<TumblrBlog> blogs) {
		this.blogs = blogs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getFollowing() {
		return following;
	}

	public void setFollowing(int following) {
		this.following = following;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	@Override
	public String toString() {
		return "TumblrUser [blogs=" + blogs + ", name=" + name + ", following="
				+ following + ", likes=" + likes + "]";
	}
	
}

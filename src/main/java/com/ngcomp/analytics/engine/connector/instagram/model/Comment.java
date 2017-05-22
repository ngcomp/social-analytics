package com.ngcomp.analytics.engine.connector.instagram.model;

public class Comment {
	private long count;
	private CommentData[] data;

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public CommentData[] getData() {
		return data;
	}

	public void setData(CommentData[] data) {
		this.data = data;
	}

}
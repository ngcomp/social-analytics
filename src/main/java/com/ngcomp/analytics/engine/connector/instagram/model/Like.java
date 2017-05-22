package com.ngcomp.analytics.engine.connector.instagram.model;

public class Like {
	private long count;
	LikeData[] data;

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public LikeData[] getData() {
		return data;
	}

	public void setData(LikeData[] data) {
		this.data = data;
	}
}

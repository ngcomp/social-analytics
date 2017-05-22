package com.ngcomp.analytics.engine.connector.fb.dto;

import java.util.List;

public class Like {
	private long count;
	private List<LikeData> data;

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public List<LikeData> getData() {
		return data;
	}

	public void setData(List<LikeData> data) {
		this.data = data;
	}
}

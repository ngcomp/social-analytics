package com.ngcomp.analytics.engine.connector.instagram.model;

public class InstagramSelfFeedResponse{
	InstagramFeed[] data;
	Meta meta;
	
	public InstagramFeed[] getData() {
		return data;
	}
	public void setData(InstagramFeed[] data) {
		this.data = data;
	}
	public Meta getMeta() {
		return meta;
	}
	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}
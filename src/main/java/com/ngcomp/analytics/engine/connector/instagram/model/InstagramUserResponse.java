package com.ngcomp.analytics.engine.connector.instagram.model;

public class InstagramUserResponse{
	InstagramUser[] data;
	Meta meta;
	public InstagramUser[] getData() {
		return data;
	}
	public void setData(InstagramUser[] data) {
		this.data = data;
	}
	public Meta getMeta() {
		return meta;
	}
	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}
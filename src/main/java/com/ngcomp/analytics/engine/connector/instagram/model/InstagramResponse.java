package com.ngcomp.analytics.engine.connector.instagram.model;

public class InstagramResponse {
	public Meta getMeta() {
		return meta;
	}
	public void setMeta(Meta meta) {
		this.meta = meta;
	}
	public Response[] getData() {
		return data;
	}
	public void setData(Response[] data) {
		this.data = data;
	}
	Meta meta;
	Response[] data;
}




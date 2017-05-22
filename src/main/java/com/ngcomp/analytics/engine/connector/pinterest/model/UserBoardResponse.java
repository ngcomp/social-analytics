package com.ngcomp.analytics.engine.connector.pinterest.model;

public class UserBoardResponse {
	private Board[] body;
	private Meta meta;
	public Board[] getBody() {
		return body;
	}
	public void setBody(Board[] body) {
		this.body = body;
	}
	public Meta getMeta() {
		return meta;
	}
	public void setMeta(Meta meta) {
		this.meta = meta;
	}

}

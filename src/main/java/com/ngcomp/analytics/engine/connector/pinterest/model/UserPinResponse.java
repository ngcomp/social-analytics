package com.ngcomp.analytics.engine.connector.pinterest.model;

public class UserPinResponse {
	private PinObject[] body;
	private Meta meta;

	public PinObject[] getBody() {
		return body;
	}

	public void setBody(PinObject[] body) {
		this.body = body;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

}

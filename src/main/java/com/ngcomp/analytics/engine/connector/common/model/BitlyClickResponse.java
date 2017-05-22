package com.ngcomp.analytics.engine.connector.common.model;

public class BitlyClickResponse {
	private int status_code;
	private String status_txt;
	private BitlyClickData data;

	public int getStatus_code() {
		return status_code;
	}

	public void setStatus_code(int status_code) {
		this.status_code = status_code;
	}

	public String getStatus_txt() {
		return status_txt;
	}

	public void setStatus_txt(String status_txt) {
		this.status_txt = status_txt;
	}

	public BitlyClickData getData() {
		return data;
	}

	public void setData(BitlyClickData data) {
		this.data = data;
	}
}

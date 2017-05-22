package com.ngcomp.analytics.engine.connector.common.model;

public class BitlyShareResponse {
	private int status_code;
	private String status_txt;
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
	public BitlyShareData getData() {
		return data;
	}
	public void setData(BitlyShareData data) {
		this.data = data;
	}
	private BitlyShareData data;

}

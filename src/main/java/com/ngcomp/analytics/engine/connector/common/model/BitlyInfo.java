package com.ngcomp.analytics.engine.connector.common.model;

public class BitlyInfo {
	private String originalUrl;
	private String bitlyUrl;
	private int clicks;
	private int shares;

	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	public String getBitlyUrl() {
		return bitlyUrl;
	}

	public void setBitlyUrl(String bitlyUrl) {
		this.bitlyUrl = bitlyUrl;
	}

	public int getClicks() {
		return clicks;
	}

	public void setClicks(int clicks) {
		this.clicks = clicks;
	}

	public int getShares() {
		return shares;
	}

	public void setShares(int shares) {
		this.shares = shares;
	}

}

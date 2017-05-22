package com.ngcomp.analytics.engine.connector.instagram.model;

public class Media {
	private Resolution low_resolution;
	private Resolution standard_resolution;
	private Resolution thumbnail;

	public Resolution getLow_resolution() {
		return low_resolution;
	}

	public void setLow_resolution(Resolution low_resolution) {
		this.low_resolution = low_resolution;
	}

	public Resolution getStandard_resolution() {
		return standard_resolution;
	}

	public void setStandard_resolution(Resolution standard_resolution) {
		this.standard_resolution = standard_resolution;
	}

	public Resolution getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(Resolution thumbnail) {
		this.thumbnail = thumbnail;
	}

}

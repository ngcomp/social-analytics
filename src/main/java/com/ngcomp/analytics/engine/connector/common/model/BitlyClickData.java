package com.ngcomp.analytics.engine.connector.common.model;

public class BitlyClickData {
	private int units;
	private int tz_offset;
	private String unit;
	private int link_clicks;

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

	public int getTz_offset() {
		return tz_offset;
	}

	public void setTz_offset(int tz_offset) {
		this.tz_offset = tz_offset;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getLink_clicks() {
		return link_clicks;
	}

	public void setLink_clicks(int link_clicks) {
		this.link_clicks = link_clicks;
	}
}

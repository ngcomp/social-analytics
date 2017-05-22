package com.ngcomp.analytics.engine.connector.common.model;

public class BitlyShareData {
	private int offset;
	private int units;
	private int total_shares;
	private String unit;

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

	public int getTotal_shares() {
		return total_shares;
	}

	public void setTotal_shares(int total_shares) {
		this.total_shares = total_shares;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}

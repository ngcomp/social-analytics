package com.ngcomp.analytics.engine.connector.instagram.model;

public class Location{
	int id;
	String name;
	Double longitude;
	Double latitude;
	
	public int getId() {
		return id;
	}
	protected void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	protected void setName(String name) {
		this.name = name;
	}
	protected void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	protected void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
}

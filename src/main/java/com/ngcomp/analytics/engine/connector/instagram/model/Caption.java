package com.ngcomp.analytics.engine.connector.instagram.model;

public class Caption {
private long id;
public long getId() {
	return id;
}
public void setId(long id) {
	this.id = id;
}
public long getCreated_time() {
	return created_time;
}
public void setCreated_time(long created_time) {
	this.created_time = created_time;
}
public String getText() {
	return text;
}
public void setText(String text) {
	this.text = text;
}
private long created_time;
private String text;
}

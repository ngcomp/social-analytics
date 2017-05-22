package com.ngcomp.analytics.engine.connector.instagram.model;

public class InstagramUser{
	String username;          
	String website;           
	String profile_picture;   
	String full_name;         
	String bio;               
	String id;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getProfile_picture() {
		return profile_picture;
	}
	public void setProfile_picture(String profile_picture) {
		this.profile_picture = profile_picture;
	}
	public String getFull_name() {
		return full_name;
	}
	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}                
}
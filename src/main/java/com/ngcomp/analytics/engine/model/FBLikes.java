package com.ngcomp.analytics.engine.model;

import com.restfb.Facebook;
import com.restfb.types.FacebookType;

public class FBLikes extends FacebookType {

	@Facebook("id")
	private String id;
	
	@Facebook("name")
	private String name;

	
	
	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	@Override
	public String toString() {
		return "FBLikes [id=" + id + ", name=" + name + "]";
	}

}

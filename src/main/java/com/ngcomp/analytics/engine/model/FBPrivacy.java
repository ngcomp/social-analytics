package com.ngcomp.analytics.engine.model;

import com.restfb.Facebook;
import com.restfb.types.FacebookType;

public class FBPrivacy extends FacebookType {

	@Facebook("value")
	private String value;

	@Facebook("description")
	private String description;
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	@Override
	public String toString() {
		return "FBPrivacy [value=" + value + ", description=" + description
				+ "]";
	}
	
}

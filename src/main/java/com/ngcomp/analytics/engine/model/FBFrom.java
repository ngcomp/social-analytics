package com.ngcomp.analytics.engine.model;

import com.restfb.Facebook;
import com.restfb.types.FacebookType;

import java.util.List;

public class FBFrom extends FacebookType {

	@Facebook("id")
	private String id;
	
	@Facebook("name")
	private String name;

	
	@Facebook("category")
	private String category;
	
	@Facebook("category_list")
	private List<FBCategoryList> category_list;
	
	
	
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



	public List<FBCategoryList> getCategory_list() {
		return category_list;
	}



	public void setCategory_list(List<FBCategoryList> category_list) {
		this.category_list = category_list;
	}
	
	
	@Override
	public String toString() {
		return "FBFrom [id=" + id + ", name=" + name + ", category=" + category
				+ ", category_list=" + category_list + "]";
	}
}

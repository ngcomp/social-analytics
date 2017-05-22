package com.ngcomp.analytics.engine.model;

import com.restfb.Facebook;
import com.restfb.types.FacebookType;

public class FBShares extends FacebookType {

	@Facebook("count")
	private String count;

	public String getCount() {
		
		if (count == null || count.length() == 0 )
			return "0" ;
		
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	
	@Override
	public String toString() {
		return "FBShares [count=" + count + "]";
	}
	
	
}

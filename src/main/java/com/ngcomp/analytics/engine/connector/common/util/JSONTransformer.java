package com.ngcomp.analytics.engine.connector.common.util;

import com.google.gson.Gson;

/**
 * Common utility for handling JSON transformation.
 * 
 * @author dprasad
 * @since 0.1
 */
public class JSONTransformer {

	/**
	 * Method to get a JSON representation of any given POJO
	 * 
	 * @param o
	 * @return
	 */
	public static String getJSONString(Object o) {
		Gson gson = new Gson();
		String json = gson.toJson(o);
		return json;
	}

}

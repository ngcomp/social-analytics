package com.ngcomp.analytics.engine.connector.bing;

import com.google.gson.Gson;
import com.ngcomp.analytics.engine.connector.bing.model.BingData;

import java.util.List;

public class SampleBing {

	public static void main(String... strings) {
		BingSearch bs = new BingSearch();
	    List<BingData> bingSearch = bs.bingSearch(false, "Carlsberg", false, null);
        Gson gson = new Gson();
		for(BingData data : bingSearch){
            System.out.println(gson.toJson(data));
		}
	}
}


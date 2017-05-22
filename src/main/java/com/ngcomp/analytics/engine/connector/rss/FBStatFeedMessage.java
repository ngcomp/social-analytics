package com.ngcomp.analytics.engine.connector.rss;

import com.google.gson.Gson;
import com.ngcomp.analytics.engine.connector.common.model.FBStat;
import com.ngcomp.analytics.engine.connector.common.util.RESTPosterClient;

public class FBStatFeedMessage {

	private static final String BASE_URL_FB_STAT = "https://api.facebook.com/method/fql.query?query=select%20total_count,like_count,comment_count,share_count,click_count%20from%20link_stat%20where%20url='";
	private static final String OUTPUT_FORMAT = "'&format=json";

	public static FBStat getFBStats(String link) {
		String finalRequestURL = BASE_URL_FB_STAT + link + OUTPUT_FORMAT;
		String responseJSON = RESTPosterClient.requestRESTGet(finalRequestURL);
		FBStat[] fbStat = null;
		if (responseJSON != null && responseJSON.length() > 0) {
			Gson gson = new Gson();
			try {

				fbStat = gson.fromJson(responseJSON, FBStat[].class);
			} catch (Exception e) {
				// ignore
			}
		}

		if (fbStat != null && fbStat.length > 0)
			return fbStat[0];

		return null;

	}

	public static void main(String[] args) {
		System.out.println(getFBStats("https://www.facebook.com/imdb"));
	}

}

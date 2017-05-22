package com.ngcomp.analytics.engine.connector.tumbler;

import com.ngcomp.analytics.engine.connector.common.CommonCredentials;
import com.ngcomp.analytics.engine.connector.tumbler.model.TumblrPost;

import java.util.List;

/**
 * Documentation: https://github.com/tumblr/jumblr Date: 8/2/13 Time: 11:37 AM
 */
public class SampleTumbler {

	public static void main(String... strings) {
		getTag();
	}

	public static void getTag() {
		SearchTags search = new SearchTags(
				CommonCredentials.CONSUMER_KEY_TUMBLR,
				CommonCredentials.CONSUMER_SECRET_TUMBLR,
				CommonCredentials.ACCESS_TOKEN_TUMBLR,
				CommonCredentials.ACCESS_TOKEN_SECRET_TUMBLR);
		List<TumblrPost> posts = search.getTagInfo(false, "carlsberg", false,
				 CommonCredentials.BITLY_TOKEN);
		for (TumblrPost post : posts) {
			System.out.println("===================>" + post.toString());
		}
		System.out.println();
	}

}

package com.ngcomp.analytics.engine.connector.twitter;

import com.ngcomp.analytics.engine.connector.twitter.model.Tweet;

import java.util.List;

public class SampleTwitter {
	public static void main(String[] args) {
		searchTweets(false, "carlsberg",false, null);
	}



    public static void searchTweets(boolean constructStory, final String query, boolean downloadAndSaveImages, String newPath) {
		SearchTweets search = new SearchTweets("", "", "", "");
		List<Tweet> tweets = search.searchTweets(constructStory, query, 30,downloadAndSaveImages);

		for (Tweet tweet : tweets) {
			System.out.println(tweet);

		}
	}

}

package com.ngcomp.analytics.engine.connector.twitter;

import com.google.gson.Gson;
import com.ngcomp.analytics.engine.connector.common.BootStrapper;
import com.ngcomp.analytics.engine.connector.common.util.ConstructStory;
import com.ngcomp.analytics.engine.connector.common.util.JSONTransformer;
import com.ngcomp.analytics.engine.connector.twitter.model.Tweet;
import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author dprasad
 */
public class SearchTweets {

	private String consumerKey;
	private String consumerSecret;
	private String accessToken;
	private String accessTokenSecret;
	private String ownerScreenName;
	
	private static final String permalink =  "http://twitter.com/{username}/status/{tweetID}";

	public SearchTweets() {
		super();
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}

	public void setAccessTokenSecret(String accessTokenSecret) {
		this.accessTokenSecret = accessTokenSecret;
	}

	public SearchTweets(String consumerKey, String consumerSecret,
                        String accessToken, String accessTokenSecret) {
		super();
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.accessToken = accessToken;
		this.accessTokenSecret = accessTokenSecret;
	}

	public String ownerScreenName() {
		Twitter twitter = BootStrapper.bootStrapTwitterAPI(consumerKey, consumerSecret, accessToken, accessTokenSecret);
		ResponseList<Status> accountSettings;
		try {
			accountSettings = twitter.getUserTimeline();
			Status status = accountSettings.get(0);
			User user = status.getUser();
			if(user != null ){
				ownerScreenName = user.getScreenName();
			}
			Gson gson = new Gson();
			String json = gson.toJson(status);
			System.out.println(json);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ownerScreenName;
	}
	
	public List<Tweet> searchTweets(boolean constructStory, String queryString, int count, boolean downloadAndSaveImages ) {
		if(ownerScreenName == null || ownerScreenName.length() == 0 )
			ownerScreenName();
		
		Twitter twitter = BootStrapper.bootStrapTwitterAPI(consumerKey, consumerSecret, accessToken, accessTokenSecret);
		List<Tweet> tweetList = new ArrayList<Tweet>();
		try {
			Query query = new Query(queryString);
			if (count > -1)
				query.setCount(count);
			
			QueryResult result;
			do {
				result = twitter.search(query);

				List<Status> tweets = result.getTweets();
				for (Status tweet : tweets) {
					Tweet twt = getTweet( constructStory, tweet, downloadAndSaveImages);
					tweetList.add(twt);
					
				}
				if (count > -1)
					break;
			} while ((query = result.nextQuery()) != null);
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
		}
		return tweetList;
	}

	public List<Tweet> searchTweets(boolean constructStory, String queryString, boolean downloadAndSaveImages) {
		List<Tweet> tweetList = searchTweets(constructStory, queryString, -1, downloadAndSaveImages);
		return tweetList;
	}

	public String[] searchTweetsAsJSON(String queryString, boolean downloadAndSaveImages, String newPath) {
		String[] tweetList = searchTweetsAsJSON(queryString, -1, downloadAndSaveImages);
		return tweetList;
	}

	public String[] searchTweetsAsJSON(String queryString, int count, boolean downloadAndSaveImages) {
		Twitter twitter = BootStrapper.bootStrapTwitterAPI();
		String[] tweetList = null;
		try {
			Query query = new Query(queryString);

			if (count > -1)
				query.setCount(count);

			QueryResult result;
			do {
				result = twitter.search(query);
				int k = 0;
				List<Status> tweets = result.getTweets();
				tweetList = new String[tweets.size()];
				for (Status tweet : tweets) {
					tweetList[k++] = JSONTransformer.getJSONString(tweet);
				}
				if (count > -1)
					break;
			} while ((query = result.nextQuery()) != null);
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
		}
		return tweetList;
	}

	private Tweet getTweet(boolean constructStory,Status status, boolean downloadAndSaveImages) {
		Tweet tweet = new Tweet();
		tweet.setId(status.getId());
		tweet.setCreatedAt(status.getCreatedAt());
		tweet.setFavoriteCount(status.getFavoriteCount());
//		tweet.setIsoLanguageCode(status.getIsoLanguageCode());
		tweet.setPossiblySensitive(status.isPossiblySensitive());
		tweet.setRetweetCount(status.getRetweetCount());
		tweet.setSource(status.getSource());
		tweet.setText(status.getText());

		List<String> hashTags = new ArrayList<String>();
		HashtagEntity[] hashtagEntities = status.getHashtagEntities();
		for(HashtagEntity hashTag : hashtagEntities){
			hashTags.add(hashTag.getText());
		}

		tweet.setHashTags(hashTags);
		
		String screenName = status.getUser() != null ? status.getUser().getScreenName() : "";
		tweet.setScreenName(screenName);
		
		String perLink = permalink.replace("{username}", ownerScreenName);
		perLink = perLink.replace("{tweetID}", status.getId()+"");
		//tweet.setPermalink(perLink);
        tweet.setLink(perLink);
		tweet.setStory(ConstructStory.construct(constructStory, tweet.getText(), downloadAndSaveImages));
		return tweet;

	}
}

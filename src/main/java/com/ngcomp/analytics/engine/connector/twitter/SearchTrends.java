package com.ngcomp.analytics.engine.connector.twitter;

import com.ngcomp.analytics.engine.connector.common.BootStrapper;
import com.ngcomp.analytics.engine.connector.common.model.FBStat;
import com.ngcomp.analytics.engine.connector.common.util.ConstructStory;
import com.ngcomp.analytics.engine.connector.common.util.JSONTransformer;
import com.ngcomp.analytics.engine.connector.rss.FBStatFeedMessage;
import com.ngcomp.analytics.engine.connector.twitter.model.Trend;
import twitter4j.Location;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.List;

/**
 * Reference:
 * https://github.com/yusuke/twitter4j/tree/master/twitter4j-examples/
 * src/main/java/twitter4j/examples User: Ram Parashar Date: 8/2/13 Time: 12:50
 * PM
 */
public class SearchTrends {

	private String consumerKey;
	private String consumerSecret;
	private String accessToken;
	private String accessTokenSecret;
	private final static String YAHOO_APP_ID_URL = "yahooapis.com";
	
	public SearchTrends() {
		super();
	}

	public SearchTrends(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
		super();
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.accessToken = accessToken;
		this.accessTokenSecret = accessTokenSecret;
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

	public List<Trend> searchTrend(boolean constructStory, boolean downloadAndSaveImages, String newPath, String appId) {
		List<Trend> trendList = new ArrayList<Trend>();
		Twitter twitter = BootStrapper.bootStrapTwitterAPI(consumerKey, consumerSecret, accessToken, accessTokenSecret);
		ResponseList<Location> locations = null;
		try {
			locations = twitter.getAvailableTrends();
			for (Location location : locations) {
				trendList.add(getTrend(constructStory, location, downloadAndSaveImages, newPath, appId));
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}

		return trendList;
	}
	
	public String[] searchTrendAsJSON() {
		
		Twitter twitter = BootStrapper.bootStrapTwitterAPI();
		ResponseList<Location> locations = null;
		String[] trendList = null;
		try {
			locations = twitter.getAvailableTrends();
			trendList = new String[locations.size()];
			int k = 0;
			for (Location location : locations) {
				trendList[k++] = JSONTransformer.getJSONString(location);
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}

		return trendList;
	}
	
	
	private Trend getTrend(boolean constructStory, Location location, boolean downloadAndSaveImages, String newPath, String appId) {
		Trend trend = new Trend();
		trend.setWoeid(location.getWoeid());
		trend.setPlaceCode(location.getPlaceCode());
		trend.setCountryCode(location.getCountryCode());
		trend.setCountryName(location.getCountryName());
		trend.setName(location.getName());
		trend.setPlaceName(location.getPlaceName());
		trend.setUrl(location.getURL());
		FBStat fbStats = FBStatFeedMessage.getFBStats(location.getURL());
		trend.setFbStat(fbStats);
		
		// to handle trends with yahoo api urls
		String url = trend.getUrl();
		if(url != null && url.contains(YAHOO_APP_ID_URL));
			url = url +"?appid="+appId;
		trend.setStory(ConstructStory.construct(constructStory, url, downloadAndSaveImages));
		System.out.println(trend);
		return trend;

	}
	
}



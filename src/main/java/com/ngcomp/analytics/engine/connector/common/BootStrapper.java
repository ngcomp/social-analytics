package com.ngcomp.analytics.engine.connector.common;

import com.google.gson.Gson;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.tumblr.jumblr.JumblrClient;
import net.billylieurance.azuresearch.AzureSearchNewsQuery;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;

public class BootStrapper {

	public static JumblrClient bootStrapTumblrAPI() {
		JumblrClient tumblr = null;
		try {
			tumblr = new JumblrClient(CommonCredentials.CONSUMER_KEY_TUMBLR, CommonCredentials.CONSUMER_SECRET_TUMBLR);
			tumblr.setToken(CommonCredentials.ACCESS_TOKEN_TUMBLR, CommonCredentials.ACCESS_TOKEN_SECRET_TUMBLR);

		} catch (Exception e) {
			// not able to get tumblr API instance
		}
		return tumblr;
	}

	public static JumblrClient bootStrapTumblrAPI(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
		JumblrClient tumblr = null;
		try {
			tumblr = new JumblrClient(consumerKey, consumerSecret);
			tumblr.setToken(accessToken, accessTokenSecret);

		} catch (Exception e) {
			// not able to get tumblr API instance
		}
		return tumblr;
	}
	
	
	
	public static Twitter bootStrapTwitterAPI() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		Twitter twitter = null;
		TwitterFactory tf = null;
		try {
			cb.setDebugEnabled(true)
					.setOAuthConsumerKey(CommonCredentials.CONSUMER_KEY_TWITTER)
					.setOAuthConsumerSecret(CommonCredentials.CONSUMER_SECRET_TWITTER)
					.setOAuthAccessToken(CommonCredentials.ACCESS_TOKEN_TWITTER)
					.setOAuthAccessTokenSecret(CommonCredentials.ACCESS_TOKEN_SECRET_TWITTER);

			tf = new TwitterFactory(cb.build());
			twitter = tf.getInstance();

		} catch (Exception e) {
			// not able to get a twitter API instance.
		}
		return twitter;
	}
	
	public static Twitter bootStrapTwitterAPI(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		Twitter twitter = null;
		TwitterFactory tf = null;
		try {
			cb.setDebugEnabled(true)
					.setOAuthConsumerKey(consumerKey)
					.setOAuthConsumerSecret(consumerSecret)
					.setOAuthAccessToken(accessToken)
					.setOAuthAccessTokenSecret(accessTokenSecret);

			tf = new TwitterFactory(cb.build());
			twitter = tf.getInstance();

		} catch (Exception e) {
			// not able to get a twitter API instance.
		}
		return twitter;
	}
	
	public static AzureSearchNewsQuery bootStrapBingAPI() {
		AzureSearchNewsQuery bingClient = new AzureSearchNewsQuery();
		bingClient.setAppid(CommonCredentials.ACCOUNT_KEY_BING);
		return bingClient;
	}
	
	public static AzureSearchNewsQuery bootStrapBingAPI(String accountKeyBing) {
		AzureSearchNewsQuery bingClient = new AzureSearchNewsQuery();
		bingClient.setAppid(accountKeyBing);
		return bingClient;
	}
	
	public static FacebookClient bootStrapFacebookAPI(String ACCESS_CODE) {
		FacebookClient facebookClient = new DefaultFacebookClient(ACCESS_CODE) ;
		return facebookClient ;
	}
	
	public static void main(String...strings){
        FacebookClient facebookClient = new DefaultFacebookClient("");
        Connection<Post> myFeed = facebookClient.fetchConnection("Carlsberg/feed", Post.class);
        Connection<Page> fetchConnection = facebookClient.fetchConnection("Carlsberg/feed", Page.class);
        System.out.println(fetchConnection);
        //Carlsberg
        Gson gson = new Gson();
        for (List<Post> myFeedConnectionPage : myFeed){
            for (Post post : myFeedConnectionPage){
                System.out.println("Post: =>"  + gson.toJson(post));
            }
        }
    }
	
	
}

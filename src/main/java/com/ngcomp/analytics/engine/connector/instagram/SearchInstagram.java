package com.ngcomp.analytics.engine.connector.instagram;

import com.google.gson.Gson;
import com.ngcomp.analytics.engine.connector.common.util.ImageDownloader;
import com.ngcomp.analytics.engine.connector.common.util.RESTPosterClient;
import com.ngcomp.analytics.engine.connector.instagram.auth.UriConstructor;
import com.ngcomp.analytics.engine.connector.instagram.dto.InstagramDTO;
import com.ngcomp.analytics.engine.connector.instagram.model.*;

import java.util.HashMap;
import java.util.Map;

public class SearchInstagram {

	UriConstructor uriHolder = new UriConstructor();
	
	/**
	 * default constructor
	 */
	public SearchInstagram() {
		super();
	}
	
	String accessToken ;
	
	private String ownerScreenName= null;
	
	public String ownerScreenName() {
		if(ownerScreenName == null || ownerScreenName.length() == 0 )
		{
			ownerScreenName = getCurrentUser();
		}
		
		return ownerScreenName;
	}

	/**
	 * constructor to initialize instagram client with accessToken and clientKey
	 * @param accessToken
	 * @param clientKey
	 */
	public SearchInstagram(String clientKey, String accessToken) {
		this.accessToken = accessToken;
		uriHolder = new UriConstructor(accessToken, clientKey);
	}
	
	/**
	 * get tagged media, this simply means that the user passes a query (tag name) and the method returns an array of InstagramDTO
	 * @param tagName the tag to be searched for.
	 * 
	 * @param downloadAndSaveImages flag when true allows the system to download and save images.
	 * @return an array of InstagramDTO
	 */
	public InstagramDTO[] getTaggedMedia(String tagName, boolean downloadAndSaveImages) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("tag_name", tagName);
		String url = uriHolder.url(UriFactory.TaggedMedia.GET_RECENT_TAGED_MEDIA, map, true, false);
		InstagramDTO[] selfFeedData = getDataFromInstagram(url, downloadAndSaveImages);
		return selfFeedData;

	}
	
	/**
	 * get tagged media, this simply means that the user passes a query (tag name) and the method returns an array of InstagramDTO
	 *
	 * @param downloadAndSaveImages flag when true allows the system to download and save images.
	 * @return an array of InstagramDTO
	 */
	public InstagramDTO[] getUserMedia(String user, boolean downloadAndSaveImages) {
		
		Map<String, String> map = new HashMap<String, String>();
		String userId =  getUserIdForUsername(user);
		ownerScreenName = user;
		
		if(userId == null )
			return null;
		
		map.put("user_id", userId);
		String url = uriHolder.url(UriFactory.Users.GET_RECENT_MEDIA, map, true, false);
		InstagramDTO[] userMedia = getDataFromInstagram(url, downloadAndSaveImages);
		return userMedia;

	}
	
	/**
	 * Get popular media on Instagram (this is irrespective to a user or query string)
	 * @param destinationDirectory valid if we want to download and save the images
	 * @param downloadAndSaveImages flag when true allows teh system to downlaod and save images.
	 * 
	 * @return an array of InstagramDTO
	 */
	public InstagramDTO[] getPopularMedia(String destinationDirectory, boolean downloadAndSaveImages) {
		String url = uriHolder.url(UriFactory.Media.GET_POPULAR_MEDIA, null, false, true);
		InstagramDTO[] popularMediaData = getDataFromInstagram(url, downloadAndSaveImages);
		return popularMediaData;
	}

	/**
	 * Get Instagram feed for self
	 * @param destinationDirectory valid if we want to download and save the images
	 * @param downloadAndSaveImages flag when true allows teh system to downlaod and save images.
	 * 
	 * @return an array of InstagramDTO
	 */
	public InstagramDTO[] getFeedForSelf(String destinationDirectory, boolean downloadAndSaveImages) {
		String url = uriHolder.url(UriFactory.Users.GET_FEED, null, true, false);
		InstagramDTO[] selfFeedData = getDataFromInstagram(url, downloadAndSaveImages);
		return selfFeedData;

	}

	private InstagramDTO[] getDataFromInstagram(String url, boolean downloadAndSaveImages){
		String response = RESTPosterClient.requestRESTGet(url);
		System.out.println(response);
		Gson gson = new Gson();
		InstagramResponse resp = gson.fromJson(response,InstagramResponse.class);
		InstagramDTO[] usefulObject = getUsefulObject(resp);
		
		if (downloadAndSaveImages){
			for (InstagramDTO dto : usefulObject){
					String downloadAndSaveImage = ImageDownloader.downloadAndSaveImage(dto.getImageUrl());
					dto.setImageUrl(downloadAndSaveImage);
            }
        }
		
		return usefulObject;
	}

	private InstagramDTO[] getUsefulObject(InstagramResponse resp) {
		InstagramDTO[] array = new InstagramDTO[resp.getData().length];
		int k = 0;
		InstagramDTO i = null;
		for (Response data : resp.getData()) {

			i = new InstagramDTO();
			i.setCommentCount(String.valueOf(data.getComments().getCount()));
			i.setLikeCount(String.valueOf(data.getLikes().getCount()));
			i.setId(data.getId());
			i.setTags(data.getTags());
            i.setUrl(data.getLink());
            if(data.getUser() != null){
                //i.setUserName(data.getUser().getUsername());
                i.setUserName(data.getUser().getFull_name());
            }
            if(data.getCaption()!=null){
                i.setTitle(data.getCaption().getText());
            }
			i.setTagCount(data.getTags()	 == null ? 0 : data.getTags() .length);
			
			if (data.getImages() != null && data.getImages().getStandard_resolution() != null) {
				i.setImageUrl(data.getImages().getStandard_resolution().getUrl());
			}

			if (data.getVideos() != null && data.getVideos().getStandard_resolution() != null) {
				i.setVideoUrl(data.getVideos().getStandard_resolution() .getUrl());
			}
			array[k++] = i;
		}

		return array;

	}
	
	private String getCurrentUser(){
		String url = UriFactory.Users.GET_FEED;
		url = url + "?access_token=" + this.accessToken;
		String response = RESTPosterClient.requestRESTGet(url);
		Gson gson = new Gson();
		InstagramSelfFeedResponse selfFeed = gson.fromJson(response, InstagramSelfFeedResponse.class);
		if(selfFeed != null && selfFeed.getData() != null && selfFeed.getData().length > 0 &&  selfFeed.getData()[0] != null){
			InstagramFeed feed = selfFeed.getData()[0];
			InstagramUser user = feed.getUser();
			if(user != null)
				ownerScreenName = user.getUsername();
		}
		return ownerScreenName;
	}
	
	private String getUserIdForUsername(String username){
		if(username == null || username.length() == 0)
			return null;
		
		
		String url = UriFactory.Users.GET_USER_ID.replace("{username}",username);
		       url += "&access_token=" + this.accessToken;
		
		String response = RESTPosterClient.requestRESTGet(url);
		Gson gson = new Gson();
		InstagramUserResponse userResponse = gson.fromJson(response,InstagramUserResponse.class);
		if(userResponse != null)
			for(InstagramUser info : userResponse.getData()){
				if(username.equals(info.getUsername())){
					return info.getId();
				}
			}
		return null;
	}

}
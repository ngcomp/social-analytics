package com.ngcomp.analytics.engine.connector.instagram.auth;//package com.ngcomp.analytics.engine.connector.instagram.auth;
//
//import java.util.HashMap;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import java.lang.reflect.*;
//
//import com.ngcomp.analytics.engine.connector.instagram.model.User;
//
//public class InstagramSession {
//
//	/*String accessToken;
//	User currentUser;
//	UriConstructor uriConstructor;
//	HashMap<String, ArrayList<String>> pageMap;
//
//	public InstagramSession() {
//	}
//
//	*//**
//	 * Creates a new Instagram session
//	 *
//	 * @param accessToken
//	 *            the session's access token
//	 *//*
//	public InstagramSession(AccessToken accessToken) {
//		setAccessToken(accessToken.getTokenString());
//		this.uriConstructor = new UriConstructor(getAccessToken());
//	}
//
//	protected String getAccessToken() {
//		return accessToken;
//	}
//
//	protected void setAccessToken(String accessToken) {
//		this.accessToken = accessToken;
//	}
//
//	*//**
//	 * Finds and returns a user with the given id. Throws an InstagramException
//	 * if none is found or the user with that id cannot be accessed
//	 *
//	 * @param userId
//	 *            id of the user
//	 * @return The user with the id passed
//	 *//*
//	public User getUserById(int userId) throws Exception {
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("user_id", userId);
//		try {
//			JSONObject userObject = (new GetMethod()
//					.setMethodURI(uriConstructor.constructUri(
//							UriFactory.Users.GET_DATA, map, true))).call()
//					.getJSON();
//			if (userObject.has("data")) {
//				return new User(userObject.getJSONObject("data"),
//						getAccessToken());
//			} else {
//				throw new InstagramException("User with id = " + userId
//						+ " cannot be accessed" + " or may not exist");
//			}
//		} catch (InstagramException e) {
//			throw new InstagramException(
//					"User with id = "
//							+ userId
//							+ " cannot be accessed"
//							+ " or may not exist. This user may have deleted their account");
//		}
//	}
//
//	*//**
//	 * Finds and returns the most recent media published by the user with the id
//	 * passed.
//	 *
//	 * @param userId
//	 *            id of the user
//	 * @throws Exception
//	 *             , JSONException
//	 * @return List of recent media published by the user, within the page
//	 *         number passed
//	 *//*
//	public PaginatedCollection<Media> getRecentPublishedMedia(int userId)
//			throws Exception {
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("user_id", userId);
//		String uriString = uriConstructor.constructUri(
//				UriFactory.Users.GET_RECENT_MEDIA, map, true);
//		ArrayList<Media> media = new ArrayList<Media>();
//		PaginationIterator<Media> iterator = new PaginationIterator<Media>(
//				media, uriString) {
//			@Override
//			public void handleLoad(JSONArray mediaItems) throws JSONException {
//				for (int i = 0; i < mediaItems.length(); i++) {
//					list.add(Media.fromJSON(mediaItems.getJSONObject(i),
//							getAccessToken()));
//				}
//			}
//		};
//
//		return new PaginatedCollection<Media>(media, iterator);
//	}
//
//	*//**
//	 * Gets the recent media in the current user's feed
//	 *
//	 * @throws Exception
//	 *             , JSONException
//	 * @return List of recent media in the current user's feed
//	 *//*
//	public PaginatedCollection<Media> getFeed() throws Exception {
//		String uriString = uriConstructor.constructUri(
//				UriFactory.Users.GET_FEED, null, true);
//		ArrayList<Media> media = new ArrayList<Media>();
//		PaginationIterator<Media> iterator = new PaginationIterator<Media>(
//				media, uriString) {
//			@Override
//			public void handleLoad(JSONArray mediaItems) throws JSONException {
//				for (int i = 0; i < mediaItems.length(); i++) {
//					list.add(Media.fromJSON(mediaItems.getJSONObject(i),
//							getAccessToken()));
//				}
//			}
//		};
//		return new PaginatedCollection<Media>(media, iterator);
//	}
//
//	*//**
//	 * Gets the recent media that the current user has liked.
//	 *
//	 * @throws Exception
//	 *             , JSONException
//	 * @return List of recent media that the current user has liked
//	 *//*
//	public PaginatedCollection<Media> getLikedMedia() throws Exception {
//		String uriString = uriConstructor.constructUri(
//				UriFactory.Users.GET_LIKED_MEDIA, null, true);
//		ArrayList<Media> media = new ArrayList<Media>();
//		PaginationIterator<Media> iterator = new PaginationIterator<Media>(
//				media, uriString) {
//			@Override
//			public void handleLoad(JSONArray mediaItems) throws JSONException {
//				for (int i = 0; i < mediaItems.length(); i++) {
//					list.add(Media.fromJSON(mediaItems.getJSONObject(i),
//							getAccessToken()));
//				}
//			}
//		};
//		return new PaginatedCollection<Media>(media, iterator);
//	}
//
//	*//**
//	 * Gets the media with the id passed. Throws an InstagramException if no
//	 * media with that is is found.
//	 *
//	 * @param mediaId
//	 *            the id of the media to be returned
//	 * @throws Exception
//	 *             , JSONException
//	 * @return The media with the id passed
//	 *//*
//	public Media getMedia(String mediaId) throws Exception {
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("media_id", mediaId);
//		JSONObject object = (new GetMethod().setMethodURI(uriConstructor
//				.constructUri(UriFactory.Media.GET_MEDIA, map, true))).call()
//				.getJSON();
//		return Media.fromJSON(object.getJSONObject("data"), getAccessToken());
//	}
//
//	*//**
//	 * Searches for media by location and creation time.
//	 *
//	 * @param latitude
//	 *            latitude of location
//	 * @param longitude
//	 *            longitude of location
//	 * @param minTimestamp
//	 *            the min timestamp of media to be returned. Can be null if
//	 *            needed.
//	 * @param maxTimestamp
//	 *            the max timestamp of media to be returned. Can be null if
//	 *            needed.
//	 * @param distance
//	 *            the of the location. Can be null if needed.
//	 * @throws Exception
//	 *             , JSONException
//	 * @return List of recent media that meet the search parameters
//	 *//*
//	public List<Media> searchMedia(Object latitude, Object longitude,
//			Object minTimestamp, Object maxTimestamp, Object distance)
//			throws Exception {
//		ArrayList<Media> media = new ArrayList<Media>();
//		String uri = UriFactory.Media.SEARCH_MEDIA + "?access_token="
//				+ getAccessToken() + "&lat=" + latitude + "&lng=" + longitude
//				+ "&min_timestamp=" + minTimestamp + "&max_timestamp="
//				+ maxTimestamp + "&distance=" + distance;
//		JSONObject object = (new GetMethod().setMethodURI(uri)).call()
//				.getJSON();
//		JSONArray mediaItems = object.getJSONArray("data");
//		for (int i = 0; i < mediaItems.length(); i++) {
//			media.add(Media.fromJSON(mediaItems.getJSONObject(i),
//					getAccessToken()));
//		}
//		return media;
//	}
//
//	*//**
//	 * Finds and returns the most popular media on instagram.
//	 *
//	 * @throws Exception
//	 *             , JSONException
//	 * @return List of the most popular media on instagram.
//	 *//*
//	public List<Media> getPopularMedia() throws Exception {
//		JSONObject object = null;
//		ArrayList<Media> media = new ArrayList<Media>();
//		String uriString = uriConstructor.constructUri(
//				UriFactory.Media.GET_POPULAR_MEDIA, null, true);
//
//		object = (new GetMethod().setMethodURI(uriString)).call().getJSON();
//
//		JSONArray mediaItems = object.getJSONArray("data");
//		for (int i = 0; i < mediaItems.length(); i++) {
//			media.add(Media.fromJSON(mediaItems.getJSONObject(i),
//					getAccessToken()));
//		}
//		return media;
//	}
//
//	*//**
//	 * Searches for users by name.
//	 *
//	 * @param name
//	 *            the full name or username of the user to be returned
//	 * @throws Exception
//	 *             , JSONException
//	 * @return List of users who match the search criteria
//	 *//*
//	public List<User> searchUsersByName(String name) throws Exception {
//		ArrayList<User> users = new ArrayList<User>();
//		String uriString = uriConstructor.constructUri(
//				UriFactory.Users.SEARCH_USER_BY_NAME, null, true)
//				+ "&q="
//				+ name;
//		JSONArray userObjects = (new GetMethod().setMethodURI(uriString))
//				.call().getJSON().getJSONArray("data");
//		for (int i = 0; i < userObjects.length(); i++) {
//			users.add(new User(userObjects.getJSONObject(i), getAccessToken()));
//		}
//		return users;
//	}
//
//	*//**
//	 * Gets a list of users that the user, whose id is passed, follows.
//	 *
//	 * @param userId
//	 *            id of the user whose follow list is to be returned
//	 * @throws Exception
//	 *             , JSONException
//	 * @return List of users by page, that the user, whose id is passed,
//	 *         follows.
//	 *//*
//	public PaginatedCollection<User> getFollows(int userId) throws Exception {
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("user_id", userId);
//		String uriString = uriConstructor.constructUri(
//				UriFactory.Relationships.GET_FOLLOWS, map, true);
//		ArrayList<User> users = new ArrayList<User>();
//		PaginationIterator<User> iterator = new PaginationIterator<User>(users,
//				uriString) {
//			@Override
//			public void handleLoad(JSONArray userObjects) throws JSONException {
//				for (int i = 0; i < userObjects.length(); i++) {
//					list.add(new User(userObjects.getJSONObject(i),
//							getAccessToken()));
//				}
//			}
//		};
//		return new PaginatedCollection<User>(users, iterator);
//	}
//
//	public PaginatedCollection<User> getFollowers(int userId) throws Exception {
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("user_id", userId);
//		String uriString = uriConstructor.constructUri(
//				UriFactory.Relationships.GET_FOLLOWERS, map, true);
//		ArrayList<User> users = new ArrayList<User>();
//		PaginationIterator<User> iterator = new PaginationIterator<User>(users,
//				uriString) {
//			@Override
//			public void handleLoad(JSONArray userObjects) throws JSONException {
//				for (int i = 0; i < userObjects.length(); i++) {
//					list.add(new User(userObjects.getJSONObject(i),
//							getAccessToken()));
//				}
//			}
//		};
//		return new PaginatedCollection<User>(users, iterator);
//	}
//
//	public List<User> getFollowRequests() throws Exception, JSONException,
//			JSONException {
//		JSONObject object = null;
//		ArrayList<User> users = new ArrayList<User>();
//		String uriString = uriConstructor.constructUri(
//				UriFactory.Relationships.GET_FOLLOW_REQUESTS, null, true);
//
//		object = (new GetMethod().setMethodURI(uriString)).call().getJSON();
//
//		JSONArray userObjects;
//		userObjects = object.getJSONArray("data");
//		for (int i = 0; i < userObjects.length(); i++) {
//			users.add(new User(userObjects.getJSONObject(i), getAccessToken()));
//		}
//		return users;
//	}
//
//	public Relationship getRelationshipWith(int userId) throws Exception {
//		JSONObject object = null;
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("user_id", userId);
//		String uriString = uriConstructor.constructUri(
//				UriFactory.Relationships.GET_RELATIONSHIP_STATUS, map, true);
//
//		object = (new GetMethod().setMethodURI(uriString)).call().getJSON();
//
//		return new Relationship(object.getJSONObject("data"), getAccessToken());
//	}
//
//	public boolean modifyRelationship(int userId, Relationship.Action action)
//			throws Exception {
//		String actionString = "";
//		JSONObject object = null;
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("user_id", userId);
//		HashMap<String, Object> args = new HashMap<String, Object>();
//
//		switch (action) {
//		case BLOCK:
//			actionString = "block";
//			break;
//		case UNBLOCK:
//			actionString = "unblock";
//			break;
//		case APPROVE:
//			actionString = "approve";
//			break;
//		case DENY:
//			actionString = "deny";
//			break;
//		case FOLLOW:
//			actionString = "follow";
//			break;
//		case UNFOLLOW:
//			actionString = "unfollow";
//			break;
//		}
//
//		args.put("action", actionString);
//		String uriString = uriConstructor.constructUri(
//				UriFactory.Relationships.MUTATE_RELATIONSHIP, map, true);
//		object = (new PostMethod().setPostParameters(args)
//				.setMethodURI(uriString)).call().getJSON();
//
//		return object.getJSONObject("meta").getInt("code") == 200;
//	}
//
//	public Comment postComment(String mediaId, String text) throws Exception {
//		JSONObject object = null;
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("media_id", mediaId);
//		HashMap<String, Object> args = new HashMap<String, Object>();
//		args.put("text", text);
//		args.put("access_token", getAccessToken());
//		String uriString = uriConstructor.constructUri(
//				UriFactory.Comments.POST_MEDIA_COMMENT, map, false);
//		object = (new PostMethod().setPostParameters(args)
//				.setMethodURI(uriString)).call().getJSON();
//		return new Comment(object.getJSONObject("data"), getAccessToken());
//	}
//
//	public boolean removeComment(String mediaId, String commentId)
//			throws Exception {
//		JSONObject object = null;
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("media_id", mediaId);
//		map.put("comment_id", commentId);
//		String uriString = uriConstructor.constructUri(
//				UriFactory.Comments.DELETE_MEDIA_COMMENT, map, true);
//		object = (new DeleteMethod().setMethodURI(uriString)).call().getJSON();
//
//		return object.getJSONObject("meta").getInt("code") == 200;
//	}
//
//	public boolean likeMedia(String mediaId) throws Exception, JSONException {
//		JSONObject object = null;
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("media_id", mediaId);
//		HashMap<String, Object> args = new HashMap<String, Object>();
//		args.put("access_token", getAccessToken());
//		String uriString = uriConstructor.constructUri(
//				UriFactory.Likes.SET_LIKE, map, false);
//		object = (new PostMethod().setPostParameters(args)
//				.setMethodURI(uriString)).call().getJSON();
//		return object.getJSONObject("meta").getInt("code") == 200;
//	}
//
//	public boolean removeMediaLike(String mediaId) throws Exception {
//		JSONObject object = null;
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("media_id", mediaId);
//		String uriString = uriConstructor.constructUri(
//				UriFactory.Likes.REMOVE_LIKE, map, true);
//		object = (new DeleteMethod().setMethodURI(uriString)).call().getJSON();
//		return object.getJSONObject("meta").getInt("code") == 200;
//	}
//
//	public Tag getTag(String tagName) throws Exception {
//		JSONObject object = null;
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("tag_name", tagName);
//		String uriString = uriConstructor.constructUri(UriFactory.Tags.GET_TAG,
//				map, true);
//		object = (new GetMethod().setMethodURI(uriString)).call().getJSON();
//		return new Tag(object.getJSONObject("data"), getAccessToken());
//	}
//
//	public PaginatedCollection<Media> getRecentMediaForTag(String tagName)
//			throws Exception {
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("tag_name", tagName);
//		String uriString = uriConstructor.constructUri(
//				UriFactory.Tags.GET_RECENT_TAGED_MEDIA, map, true);
//		ArrayList<Media> media = new ArrayList<Media>();
//		PaginationIterator<Media> iterator = new PaginationIterator<Media>(
//				media, uriString) {
//			@Override
//			public void handleLoad(JSONArray mediaItems) throws JSONException {
//				for (int i = 0; i < mediaItems.length(); i++) {
//					list.add(Media.fromJSON(mediaItems.getJSONObject(i),
//							getAccessToken()));
//				}
//			}
//		};
//		return new PaginatedCollection<Media>(media, iterator);
//	}
//
//	public List<Tag> searchTags(String tagName) throws Exception {
//		JSONObject object = null;
//		String uriString = uriConstructor.constructUri(
//				UriFactory.Tags.SEARCH_TAGS, null, true) + "&q=" + tagName;
//		object = (new GetMethod().setMethodURI(uriString)).call().getJSON();
//		ArrayList<Tag> tags = new ArrayList<Tag>();
//		JSONArray tagItems = object.getJSONArray("data");
//		for (int i = 0; i < tagItems.length(); i++) {
//			tags.add(new Tag(tagItems.getJSONObject(i), getAccessToken()));
//		}
//		return tags;
//	}
//
//	public Location getLocation(int locationId) throws Exception {
//		JSONObject object = null;
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("location_id", locationId);
//		String uriString = uriConstructor.constructUri(
//				UriFactory.Locations.GET_LOCATION, map, true);
//		object = (new GetMethod().setMethodURI(uriString)).call().getJSON();
//		return new Location(object.getJSONObject("data"), getAccessToken());
//	}
//
//	public PaginatedCollection<Media> getRecentMediaFromLocation(int locationId)
//			throws Exception {
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("location_id", locationId);
//		String uriString = uriConstructor.constructUri(
//				UriFactory.Locations.GET_MEDIA_FROM_LOCATION, map, true);
//		ArrayList<Media> media = new ArrayList<Media>();
//		PaginationIterator<Media> iterator = new PaginationIterator<Media>(
//				media, uriString) {
//			@Override
//			public void handleLoad(JSONArray mediaItems) throws JSONException {
//				for (int i = 0; i < mediaItems.length(); i++) {
//					list.add(Media.fromJSON(mediaItems.getJSONObject(i),
//							getAccessToken()));
//				}
//			}
//		};
//		return new PaginatedCollection<Media>(media, iterator);
//	}*/
//}

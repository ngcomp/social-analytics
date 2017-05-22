package com.ngcomp.analytics.engine.connector.instagram;

public class UriFactory {
	public static final String API_ROOT = "https://api.instagram.com/v1";
	
	public class Users {
		public static final String GET_DATA = UriFactory.API_ROOT + "/users/{user_id}";
		public static final String GET_FEED = UriFactory.API_ROOT + "/users/self/feed";
		public static final String GET_RECENT_MEDIA = UriFactory.API_ROOT + "/users/{user_id}/media/recent";
		public static final String GET_LIKED_MEDIA = UriFactory.API_ROOT + "/users/self/media/liked";
		public static final String SEARCH_USER_BY_NAME = UriFactory.API_ROOT + "/users/search";
		public static final String SEARCH_USER_MEDIA = "http://instagram.com/{user_id}/";
		public static final String GET_USER_ID = UriFactory.API_ROOT +  "/users/search?q={username}";
	}
	
	public class Relationships {
		public static final String GET_FOLLOWS = UriFactory.API_ROOT + "/users/{user_id}/follows";
		public static final String GET_FOLLOWERS = UriFactory.API_ROOT + "/users/{user_id}/followed-by";
		public static final String GET_FOLLOW_REQUESTS = UriFactory.API_ROOT + "/users/self/requested-by";
		public static final String GET_RELATIONSHIP_STATUS = UriFactory.API_ROOT + "/users/{user_id}/relationship";
		public static final String MUTATE_RELATIONSHIP = UriFactory.API_ROOT + "/users/{user_id}/relationship";
	}
	
	public class Media {
		public static final String GET_MEDIA = UriFactory.API_ROOT + "/media/{media_id}";
		public static final String SEARCH_MEDIA = UriFactory.API_ROOT + "/media/search";
		public static final String GET_POPULAR_MEDIA = UriFactory.API_ROOT + "/media/popular/";
	}

	public class Comments {
		public static final String GET_MEDIA_COMMENTS = UriFactory.API_ROOT + "/media/{media_id}/comments";
		public static final String POST_MEDIA_COMMENT = UriFactory.API_ROOT + "/media/{media_id}/comments";
		public static final String DELETE_MEDIA_COMMENT = UriFactory.API_ROOT + "/media/{media_id}/comments/{comment_id}";
	}

	public class Likes {
		public static final String GET_LIKERS = UriFactory.API_ROOT + "/media/{media_id}/likes";
		public static final String SET_LIKE = UriFactory.API_ROOT + "/media/{media_id}/likes";
		public static final String REMOVE_LIKE = UriFactory.API_ROOT + "/media/{media_id}/likes";
	}

	public class TaggedMedia {
		public static final String GET_RECENT_TAGED_MEDIA = UriFactory.API_ROOT + "/tags/{tag_name}/media/recent";
	}

	public class Locations {
		public static final String GET_LOCATION = UriFactory.API_ROOT + "/locations/{location_id}";
		public static final String GET_MEDIA_FROM_LOCATION = UriFactory.API_ROOT + "/locations/{location_id}/media/recent";
		public static final String SEARCH_LOCATIONS = UriFactory.API_ROOT + "/locations/search";
	}
	
	public class Auth {
		public static final String USER_AUTHORIZATION = "https://api.instagram.com/oauth/authorize/?client_id={client_id}&redirect_uri={redirect_uri}&response_type={response_type}&scope={scope}";
		public static final String GET_ACCESS_TOKEN = "https://api.instagram.com/oauth/access_token";
	}
}

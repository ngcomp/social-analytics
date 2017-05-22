package com.ngcomp.analytics.engine.connector.fb;

import com.ngcomp.analytics.engine.connector.common.BootStrapper;
import com.ngcomp.analytics.engine.connector.common.CommonCredentials;
import com.ngcomp.analytics.engine.connector.common.util.ImageDownloader;
import com.ngcomp.analytics.engine.connector.common.util.JSONTransformer;
import com.ngcomp.analytics.engine.connector.fb.dto.Application;
import com.ngcomp.analytics.engine.connector.fb.dto.Like;
import com.ngcomp.analytics.engine.connector.fb.dto.LikeData;
import com.ngcomp.analytics.engine.connector.fb.dto.PostDTO;
import com.restfb.Connection;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.NamedFacebookType;
import com.restfb.types.Post;
import com.restfb.types.Post.Likes;

import java.util.ArrayList;
import java.util.List;

public class SearchFB {
	public List<String> getPostsAsJSON(String accessCode, String connection,
                                       boolean downloadAndSaveImages, String newPath) {

		FacebookClient facebookClient = BootStrapper
				.bootStrapFacebookAPI(accessCode);

		Connection<Post> myPost = facebookClient.fetchConnection(connection,
				Post.class, Parameter.with("limit", 10));

		List<String> jsonPosts = new ArrayList<String>();
		for (List<Post> myFeedConnectionPage : myPost) {
			for (Post post : myFeedConnectionPage) {

				jsonPosts.add(JSONTransformer.getJSONString(getPostDTO(post)));
				if (downloadAndSaveImages) {
					ImageDownloader.downloadAndSaveImage(post.getPicture());
					
				}
			}
		}

		return jsonPosts;
	}

	public static void main(String[] args) {
		SearchFB fb = new SearchFB();
		fb.getPostsAsJSON(CommonCredentials.ACCESS_CODE_FB, "Carlsberg/feed",
				false, null);
	}

	private PostDTO getPostDTO(Post post) {
		PostDTO dto = new PostDTO();
		dto.setId(post.getId());
		dto.setLink(post.getLink());
		dto.setUpdatedTime(post.getUpdatedTime());
		dto.setCreatedTime(post.getCreatedTime());
		dto.setPicture(post.getPicture());
		dto.setMessage(post.getMessage());
		dto.setType(post.getType());
		dto.setName(post.getName());

		NamedFacebookType application2 = post.getApplication();

		if (application2 != null) {
			Application application = new Application();
			application.setId(application2.getId());
			application.setName(application2.getName());
			dto.setApplication(application);
		}

		Likes likes = post.getLikes();
		if (likes != null) {
			Like like = new Like();
			like.setCount(likes.getCount());
			List<NamedFacebookType> data = likes.getData();
			if (data != null && data.size() > 0) {
				List<LikeData> dataLikes = new ArrayList<LikeData>();
				LikeData dataLike = null;
				for (NamedFacebookType d : data) {
					dataLike = new LikeData();
					dataLike.setId(d.getId());
					dataLike.setName(d.getName());
					dataLikes.add(dataLike);
				}
				like.setData(dataLikes);
			}
			dto.setLikes(like);
		}
		return dto;
	}
}

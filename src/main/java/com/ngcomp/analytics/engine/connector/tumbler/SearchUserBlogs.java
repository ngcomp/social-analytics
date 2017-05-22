package com.ngcomp.analytics.engine.connector.tumbler;

import com.ngcomp.analytics.engine.connector.common.BootStrapper;
import com.ngcomp.analytics.engine.connector.common.model.BitlyInfo;
import com.ngcomp.analytics.engine.connector.common.model.FBStat;
import com.ngcomp.analytics.engine.connector.common.model.Story;
import com.ngcomp.analytics.engine.connector.common.util.ConstructBitly;
import com.ngcomp.analytics.engine.connector.common.util.ConstructStory;
import com.ngcomp.analytics.engine.connector.rss.FBStatFeedMessage;
import com.ngcomp.analytics.engine.connector.tumbler.model.TumblrBlog;
import com.ngcomp.analytics.engine.connector.tumbler.model.TumblrPost;
import com.ngcomp.analytics.engine.connector.tumbler.model.TumblrUser;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.User;

import java.util.ArrayList;
import java.util.List;

public class SearchUserBlogs {

	private String consumerKey;
	private String consumerSecret;
	private String accessToken;
	private String accessTokenSecret;
	private String ownerScreenName;
	
	public SearchUserBlogs() {
		super();
	}

	public SearchUserBlogs(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
		super();
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.accessToken = accessToken;
		this.accessTokenSecret = accessTokenSecret;
	}

	public TumblrBlog getBlog(boolean constructStory, boolean downloadAndSaveImages, String blogName, String bitlyToken) {
		JumblrClient client = BootStrapper.bootStrapTumblrAPI(consumerKey, consumerSecret, accessToken, accessTokenSecret);
		Blog blog = client.blogInfo(blogName);
		return getBlogInfo(constructStory, blog, downloadAndSaveImages, bitlyToken);
	}
	
	/**
	 * Method to return a blog and its posts given theh blogname.
	 * 
	 * @param constructStory
	 * @param blog
	 * @param downloadAndSaveImages
	 * @param bitlyToken
	 * @return
	 */
	private TumblrBlog getBlogInfo(boolean constructStory, Blog blog, boolean downloadAndSaveImages, String bitlyToken) {
		TumblrBlog tBlog = new TumblrBlog();
		tBlog.setCreatedAt  (blog.getUpdated());
		tBlog.setName       (blog.getName());
		tBlog.setPosts      (blog.getPostCount());
		tBlog.setTitle      (blog.getTitle());
		tBlog.setDescription(blog.getDescription());
		// tBlog.setOwned(true); // fetching blogs for the user so owned is



		String blogURL = "http://" + tBlog.getName() + ".tumblr.com";
        tBlog.setUrl(blogURL);

		// get fb stat for the blog
		FBStat fbStats = FBStatFeedMessage.getFBStats(blogURL);
		tBlog.setFbStat(fbStats);

		// get bitly stat for the blog
		BitlyInfo bitlyInfo = ConstructBitly.getBitlyInfo(blogURL, bitlyToken);
		tBlog.setBitlyInfo(bitlyInfo);

		// add the list of posts to this blog.
		List<TumblrPost> blogPosts = getBlogPosts(blog.getName(), constructStory, downloadAndSaveImages, bitlyToken);
		tBlog.setPostList(blogPosts);

		// construct a story out of the blog
		Story story = ConstructStory.construct(constructStory, blog.getDescription(), downloadAndSaveImages);
		tBlog.setStory(story);
		return tBlog;

	}

	/**
	 * Method to fetch blogs for the current user.
	 * 
	 * @param constructStory
	 * @param downloadAndSaveImages
	 * @param bitlyToken
	 * @return
	 */
	public TumblrUser getUserBlogPosts(boolean constructStory, boolean downloadAndSaveImages, String bitlyToken) {
		JumblrClient client = BootStrapper.bootStrapTumblrAPI(consumerKey, consumerSecret, accessToken, accessTokenSecret);
		User user = client.user();
		TumblrUser tumblrUser = getUserInfo(constructStory, user, downloadAndSaveImages, bitlyToken);
		return tumblrUser;
	}

	private List<TumblrPost> getBlogPosts(String blogName, boolean constructStory, boolean downloadAndSaveImages, String bitlyToken) {
		JumblrClient client = BootStrapper.bootStrapTumblrAPI(consumerKey, consumerSecret, accessToken, accessTokenSecret);
		List<Post> blogPosts = client.blogPosts(blogName);

		List<TumblrPost> tumblrPosts = new ArrayList<TumblrPost>();

		for (Post post : blogPosts) {
			TumblrPost postT = SearchTags.getPost(constructStory, post, downloadAndSaveImages, bitlyToken);
			tumblrPosts.add(postT);
		}

		return tumblrPosts;
	}

	private TumblrUser getUserInfo(boolean constructStory, User user, boolean downloadAndSaveImages, String bitlyToken) {

		TumblrUser tUser = new TumblrUser();
		tUser.setLikes(user.getLikeCount());
		tUser.setFollowing(user.getFollowingCount());

		List<TumblrBlog> blogs = new ArrayList<TumblrBlog>(user.getBlogs() .size());

		for (Blog blog : user.getBlogs()) {
			blogs.add(getBlogInfo(constructStory, blog, downloadAndSaveImages, bitlyToken));
			TumblrBlog blogT = getBlogInfo(constructStory, blog, downloadAndSaveImages, bitlyToken);
			blogs.add(blogT);
		}

		tUser.getBlogs().addAll(blogs);
		return tUser;
	}
	
	public String ownerScreenName() {
		if(ownerScreenName == null || ownerScreenName.length() == 0 )
		{
			JumblrClient client = BootStrapper.bootStrapTumblrAPI(consumerKey, consumerSecret, accessToken, accessTokenSecret);
			User user = client.user();
			ownerScreenName = user.getName();
		}
		
		return ownerScreenName;
	}

}

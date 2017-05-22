package com.ngcomp.analytics.engine.connector.tumbler;

import com.ngcomp.analytics.engine.connector.common.BootStrapper;
import com.ngcomp.analytics.engine.connector.common.model.BitlyInfo;
import com.ngcomp.analytics.engine.connector.common.model.FBStat;
import com.ngcomp.analytics.engine.connector.common.model.Story;
import com.ngcomp.analytics.engine.connector.common.util.ConstructBitly;
import com.ngcomp.analytics.engine.connector.common.util.ConstructStory;
import com.ngcomp.analytics.engine.connector.rss.FBStatFeedMessage;
import com.ngcomp.analytics.engine.connector.tumbler.model.TumblrPost;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchTags {
	
	private String consumerKey;
	private String consumerSecret;
	private String accessToken;
	private String accessTokenSecret;
	private List<String> blogNames;
	private String ownerScreenName;
	
	public String ownerScreenName() {
		if(ownerScreenName == null || ownerScreenName.length() == 0 )
		{
			JumblrClient client = BootStrapper.bootStrapTumblrAPI(consumerKey, consumerSecret, accessToken, accessTokenSecret);
			User user = client.user();
			ownerScreenName = user.getName();
		}
		
		return ownerScreenName;
	}
	
	public List<String> getBlogNames() {
		return blogNames;
	}

	public void setBlogNames(List<String> blogNames) {
		this.blogNames = blogNames;
	}

	public SearchTags() {
		super();
	}

	public SearchTags(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
		super();
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.accessToken = accessToken;
		this.accessTokenSecret = accessTokenSecret;
	}

	public List<TumblrPost> getTagInfo(boolean constructStory, String query, boolean downloadAndSaveImages, String bitlyToken){
		JumblrClient client = BootStrapper.bootStrapTumblrAPI(consumerKey, consumerSecret,accessToken, accessTokenSecret);
		
		prepareBlogsListForUser(client);
		
		List<Post> posts = client.tagged(query);
		List<TumblrPost> tumblrPosts = new ArrayList<TumblrPost>();
		for(Post post : posts){
			TumblrPost postT = getPost(constructStory, post, downloadAndSaveImages, bitlyToken);
            if(postT == null)continue;
//            System.out.println(postT.toString());
			tumblrPosts.add(postT);
		}
		
		return tumblrPosts;
	}
	
	public void prepareBlogsListForUser(JumblrClient client){
		List<Blog> blogs = client.user().getBlogs();
		if(blogNames == null || blogNames.size() == 0){
			blogNames = new ArrayList<String>();
			for(Blog blog : blogs){
				blogNames.add(blog.getName());
			}
		}
		System.out.println();
	}
	
	public static TumblrPost getPost(boolean constructStory, Post post, boolean downloadAndSaveImages, String bitlyToken){
        
		// populate the common properties for each type of post.
        TumblrPost tPost = new TumblrPost();
        tPost.setBlog_name(post.getBlogName());
        tPost.setDate(post.getDateGMT());
        tPost.setFormat(post.getFormat());
        tPost.setId(post.getId());
        tPost.setPost_url(post.getPostUrl());
        tPost.setDate(post.getDateGMT());
        tPost.setSource_url(post.getSourceUrl());
        tPost.setSource(post.getSourceUrl());
        tPost.setTags(post.getTags());


        Story story = ConstructStory.construct(constructStory, tPost.getPost_url(), downloadAndSaveImages);
        tPost.setStory(story);

        // FB Statistics
        FBStat fbStats = FBStatFeedMessage.getFBStats(tPost.getPost_url());

        //Bit.ly Statistics
        BitlyInfo bitlyInfo = ConstructBitly.getBitlyInfo(tPost.getPost_url(), bitlyToken);
        tPost.setBitlyInfo(bitlyInfo);

        String dateFormat =  "YYYY-MM-dd HH:mm:SS Z";
        //"2013-09-19 14:14:21 GMT";

        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        try {
            Date date = format.parse(post.getDateGMT());
            tPost.setCreatedAt(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tPost.setFbStat(fbStats);
        // populate the common properties for each type of post ends. 
		
		if(post instanceof AnswerPost){
			
			tPost.setPostType("answer");
			AnswerPost ap = (AnswerPost) post;
			tPost.setAskingName(ap.getAskingName());
			tPost.setAskingURL(ap.getAskingUrl());
			tPost.setQuestion(ap.getQuestion());
			tPost.setAnswer(ap.getAnswer());
			
		}else if (post instanceof AudioPost){
			
			tPost.setPostType("audio");
			AudioPost ap = (AudioPost) post;
			tPost.setCaption(ap.getCaption());
			tPost.setPlays(ap.getPlayCount());
			tPost.setAlbum_art(ap.getAlbumArtUrl());
			tPost.setArtist(ap.getArtistName());
			tPost.setAlbum(ap.getAlbumName());
			tPost.setTrack_name(ap.getTrackName());
			tPost.setTrack_number(ap.getTrackNumber());
			tPost.setYear(ap.getYear());
			
		}else if(post instanceof PhotoPost){
			
			tPost.setPostType("photo");
			PhotoPost pp = (PhotoPost) post;
			tPost.setCaption(pp.getCaption());
			
		}else if(post instanceof ChatPost){
			
			tPost.setPostType("chat");
			ChatPost cp = (ChatPost) post;
			tPost.setTitle(cp.getTitle());
			tPost.setBody(cp.getBody());
			
		}else if(post instanceof LinkPost){
			
			tPost.setPostType("link");
			LinkPost lp = (LinkPost) post;
			tPost.setTitle(lp.getTitle());
			tPost.setDescription(lp.getDescription());
			tPost.setTitle(lp.getTitle());
			tPost.setUrl(lp.getLinkUrl());
			
		}else if(post instanceof QuotePost){
			
			tPost.setPostType("quote");
			QuotePost qp = (QuotePost) post;
			tPost.setSource(qp.getSource());
			tPost.setText(qp.getText());
			
		}else if(post instanceof TextPost){
			
			tPost.setPostType("text");
			TextPost tp = (TextPost) post;
			tPost.setTitle(tp.getTitle());
			tPost.setBody(tp.getBody());
			
		}else if(post instanceof VideoPost){
			
			tPost.setPostType("video");
			VideoPost vp = (VideoPost) post;
			tPost.setCaption(vp.getCaption());
			
		}else if(post instanceof UnknownTypePost){
			tPost.setPostType("unknown");
		}else   {
			tPost.setPostType("null");
		}
		
		return tPost;
	}
}

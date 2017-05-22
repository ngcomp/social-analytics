package com.ngcomp.analytics.engine.connector.tumbler.model;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.ngcomp.analytics.engine.MyMain;
import com.ngcomp.analytics.engine.conn.JedisConnectionPool;
import com.ngcomp.analytics.engine.connector.common.model.Data;
import com.ngcomp.analytics.engine.connector.common.model.Story;
import com.ngcomp.analytics.engine.util.Constants;
import com.ngcomp.analytics.engine.util.PortalUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import redis.clients.jedis.Jedis;
import scala.actors.threadpool.Arrays;

import java.util.*;

import static com.ngcomp.analytics.engine.util.Constants.CF;

public class TumblrBlog extends Data {

	private String name;
	private String title;
	private String description;
	private int posts;
	private int likes;
	private Story story;
	private long createdAt ;
	private List<TumblrPost> postList = new ArrayList<TumblrPost>();
	

	public List<TumblrPost> getPostList() {
		return postList;
	}

	public void setPostList(List<TumblrPost> postList) {
		this.postList = postList;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public String getName() {
		return name;
	}

	public Story getStory() {
		return story;
	}

	public void setStory(Story story) {
		this.story = story;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPosts() {
		return posts;
	}

	public void setPosts(int posts) {
		this.posts = posts;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}
	
	public Map<String, String[]> getHBaseRowMap() {

		Gson gson = new Gson();

		String[] quals = new String[14];
		String[] vals = new String[14];

		quals[0] = "sourceId"; vals[0] = this.getSourceId();
		quals[1] = "marketId"; vals[1] = this.getMarketId();
		quals[2] = "brandId"; vals[2] = String.valueOf(this.getBrandId());
		quals[3] = "owned"; vals[3] = String.valueOf(this.getOwned());
		quals[4] = "topic"; vals[4] = this.getTopic();
		quals[5] = "post"; vals[5] = gson.toJson(this, TumblrBlog.class);
		quals[6] = "originalTokens"; vals[6] = StringUtils.join(this.getOriginalTokens(), ",");
		quals[7] = "artificialTokens"; vals[7] = StringUtils.join(this.getArtificialTokens(), ",");

		quals[8] = "likeCount"; vals[8] = String.valueOf(this.getLikeCount());
		quals[9] = "commentCount"; vals[9] = String.valueOf(this.getCommentCount());
		quals[10] = "shareCount"; vals[10] = String.valueOf(this.getShareCount());
		quals[11] = "article"; vals[11] = this.getName();
		quals[12] = "createdAt"; vals[12] = String.valueOf(createdAt);
		quals[13] = "extraWeight"; vals[13] = String.valueOf(this.getExtraWeight());

		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("quals", quals);
		map.put("vals", vals);

		return map;
	}
	
	 public Long getRowKey() {
	        return Long.MAX_VALUE - createdAt;
	  }

	@Override
	public String toString() {
		return "TumblrBlog [name=" + name + ", title=" + title
				+ ", description=" + description + ", posts=" + posts
				+ ", likes=" + likes + "]";
	}
	
	public void init(Result result){

        Gson gson = new Gson();

        this.setSourceId(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("sourceId"))));
        this.setMarketId(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("marketId"))));
        this.setBrandId(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("brandId"))));

        this.setTopic(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("topic"))));
        this.setPost(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("post"))));

        String tokens = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("originalTokens")));
        if(!Strings.isNullOrEmpty(tokens)){
            this.setOriginalTokens(new HashSet(Arrays.asList(tokens.split(","))));
        }

        tokens = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("artificialTokens")));
        if(!Strings.isNullOrEmpty(tokens)){
            this.setArtificialTokens(new HashSet(Arrays.asList(tokens.split(","))));
        }
        String s = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("likeCount")));
        if(!Strings.isNullOrEmpty(s)){
            this.setLikeCount(s);
        }

        s = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("commentCount")));
        if(!Strings.isNullOrEmpty(s)){
            this.setCommentCount(s);
        }


        s = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("shareCount")));
        if(!Strings.isNullOrEmpty(s)){
            this.setShareCount(s);
        }
//        String  story = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("article")));
//        if(story!=null){
//            this.setStory(gson.fromJson(story, Story.class));
//        }

        //TODO
        this.setDate(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("createdAt"))));

        String extraWeight = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("extraWeight")));
        if(!Strings.isNullOrEmpty(extraWeight)){
            this.setExtraWeight(Double.valueOf(extraWeight));
        }

        this.setRelevanceScore();

        String owned = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("owned")));
        if(!Strings.isNullOrEmpty(owned)){
            this.setOwned(Boolean.valueOf(owned));
        }
 }
        
    public void setRelevanceScore(){
        Jedis jedis = JedisConnectionPool.getResource();
        Map<String, String> constants = jedis.hgetAll(this.sourceId + "_SCORES");
        this.setRelevanceScore(PortalUtils.getFloatMap(constants));
        JedisConnectionPool.returnResource(jedis);
    }

    public void setRelevanceScore(Map<String, Float> constants){

        this.setWeights();

        Float amplifier = constants.get(Constants.AMPLIFIER_CHANNEL);
        Float decay  = constants.get(Constants.DECAY);
        Float zScore = constants.get(Constants.Z_SCORE);

        Float w1 = constants.get(Constants.W1);
        Float w2 = constants.get(Constants.W2);
        Float w3 = constants.get(Constants.W3);

        Float avgStoryDecay = constants.get(MyMain.AVG_STORY_DECAY);

        this.relevanceScore =  amplifier * Math.exp(-decay - 5) * zScore * (w1* Float.valueOf(this.likeCount) + w2 * Float.valueOf(this.commentCount) + w3 * Float.valueOf(this.shareCount)) + this.extraWeight;
    }
    
    private void setWeights(){
        if(Strings.isNullOrEmpty(this.likeCount)){
            this.likeCount = "0";
        }

        if(Strings.isNullOrEmpty(this.commentCount)){
            this.commentCount = "0";
        }

        if(Strings.isNullOrEmpty(this.shareCount)){
            this.shareCount = "0";
        }
    }
}

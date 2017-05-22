package com.ngcomp.analytics.engine.connector.twitter.model;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.ngcomp.analytics.engine.MyMain;
import com.ngcomp.analytics.engine.conn.JedisConnectionPool;
import com.ngcomp.analytics.engine.connector.common.model.Data;
import com.ngcomp.analytics.engine.connector.common.model.Story;
import com.ngcomp.analytics.engine.util.Constants;
import com.ngcomp.analytics.engine.util.PortalUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.ngcomp.analytics.engine.util.Constants.CF;

public class Tweet extends Data {
    private String platformId;
	private Date createdAt ;
	private long id;
	private String text;
	private String source;
	private long favoriteCount;
	private long retweetCount;
	private boolean isPossiblySensitive;
	private String isoLanguageCode;
	private String screenName;
	private Story story;
	private String permalink;
	private List<String> hashTags;
    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public List<String> getHashTags() {
		return hashTags;
	}

	public void setHashTags(List<String> hashTags) {
		this.hashTags = hashTags;
	}

	public String getPermalink() {
		return permalink;
	}

	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}

	public String getMedia() {
        return super.media;
    }

    public void setMedia(String media) {
        super.media = media;
    }

    public String getMediaType() {
        return super.mediaType;
    }

    public void setMediaType(String mediaType) {
        super.mediaType = mediaType;
    }


    public String getType() {
        return super.getType();
    }

    public void setType(String type) {
        super.setType(type);
    }

	public Story getStory() {
		return story;
	}

	public void setStory(Story story) {
		this.story = story;
	}

	public Long getRowKey() {
        return Long.MAX_VALUE - createdAt.getTime();
	}


	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public long getFavoriteCount() {
		return favoriteCount;
	}

	public void setFavoriteCount(long favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	public long getRetweetCount() {
		return retweetCount;
	}

	public void setRetweetCount(long retweetCount) {
		this.retweetCount = retweetCount;
	}

	public boolean isPossiblySensitive() {
		return isPossiblySensitive;
	}

	public void setPossiblySensitive(boolean isPossiblySensitive) {
		this.isPossiblySensitive = isPossiblySensitive;
	}

	public String getIsoLanguageCode() {
		return isoLanguageCode;
	}

	public void setIsoLanguageCode(String isoLanguageCode) {
		this.isoLanguageCode = isoLanguageCode;
	}

    @Override
    public String toString() {
        return "Tweet{" +
                "platformId='" + platformId + '\'' +
                ", createdAt=" + createdAt +
                ", id=" + id +
                ", text='" + text + '\'' +
                ", source='" + source + '\'' +
                ", favoriteCount=" + favoriteCount +
                ", retweetCount=" + retweetCount +
                ", isPossiblySensitive=" + isPossiblySensitive +
                ", isoLanguageCode='" + isoLanguageCode + '\'' +
                ", screenName='" + screenName + '\'' +
                ", story=" + story +
                ", permalink='" + permalink + '\'' +
                ", hashTags=" + hashTags +
                '}';
    }

    //	public Map<String, String[]> getHBaseRowMap() {
//
//		Gson gson = new Gson();
//
//		String[] quals = new String[14];
//		String[] vals = new String[14];
//
//		quals[0] = "sourceId"; vals[0] = this.getSourceId();
//		quals[1] = "marketId"; vals[1] = this.getMarketId();
//		quals[2] = "brandId"; vals[2] = String.valueOf(this.getBrandId());
//		quals[3] = "owned"; vals[3] = String.valueOf(this.getOwned());
//		quals[4] = "topic"; vals[4] = this.getTopic();
//		quals[5] = "post"; vals[5] = gson.toJson(this, Tweet.class);
//		quals[6] = "originalTokens"; vals[6] = StringUtils.join(this.getOriginalTokens(), ",");
//		quals[7] = "artificialTokens"; vals[7] = StringUtils.join(this.getArtificialTokens(), ",");
//		quals[8] = "likeCount"; vals[8] = String.valueOf(this.getLikeCount());
//		quals[9] = "commentCount"; vals[9] = String.valueOf(this.getCommentCount());
//		quals[10] = "shareCount"; vals[10] = String.valueOf(this.getShareCount());
//		quals[11] = "article"; vals[11] = this.getText();
//		quals[12] = "createdAt"; vals[12] = String.valueOf(this.getCreatedAt());
//		quals[13] = "extraWeight"; vals[13] = String.valueOf(this.getExtraWeight());
//        quals[13] = "relevanceScore"; vals[13] = String.valueOf(this.getRelevanceScore());
//
//		Map<String, String[]> map = new HashMap<String, String[]>();
//		map.put("quals", quals);
//		map.put("vals", vals);
//
//		return map;
//	}
	
	
	 public void init(Result result){

	        Gson gson = new Gson();

	        this.setSourceId(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("sourceId"))));
	        this.setMarketId(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("marketId"))));
	        this.setBrandId(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("brandId"))));

//	        this.setTopic(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("topic"))));
//	        this.setPost(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("post"))));

            String tokens = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("originalTokens")));
            if(!Strings.isNullOrEmpty(tokens)){
                JSONArray jsonArray = (JSONArray) JSONValue.parse(tokens);
                this.setOriginalTokens(PortalUtils.getTokenSet(jsonArray));
            }

            tokens = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("artificialTokens")));
            if(!Strings.isNullOrEmpty(tokens)){
              JSONArray jsonArray = (JSONArray)JSONValue.parse(tokens);
              this.setArtificialTokens(PortalUtils.getTokenSet(jsonArray));
            }

//	        String tokens = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("originalTokens")));
//	        if(!Strings.isNullOrEmpty(tokens)){
//	            this.setOriginalTokens(Arrays.asList(tokens.split(",")));
//	        }
//
//	        tokens = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("artificialTokens")));
//	        if(!Strings.isNullOrEmpty(tokens)){
//	            this.setArtificialTokens(new HashSet(Arrays.asList(tokens.split(","))));
//	        }

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

//	        String  story = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("article")));
//
//	        if(story!=null){
//                try{
//	                this.setStory(gson.fromJson(story, Story.class));
//                }catch(Exception ex){
//                    Story aStory = new Story();
//                    aStory.setActualStory(story);
//                }
//	        }

	        //TODO
//	        this.setDate(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("createdAt"))));

	        String extraWeight = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("extraWeight")));
	        if(!Strings.isNullOrEmpty(extraWeight)){
	            this.setExtraWeight(Double.valueOf(extraWeight));
	        }

	        this.setRelevanceScore();

//	        String owned = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("owned")));
//	        if(!Strings.isNullOrEmpty(owned)){
//	            this.setOwned(Boolean.valueOf(owned));
//	        }
	 }
	        
	    public void setRelevanceScore(){
	        Jedis jedis = JedisConnectionPool.getResource();
	        Map<String, String> constants = jedis.hgetAll(this.sourceId + "_SCORES");
            try{
	            this.setRelevanceScore(PortalUtils.getFloatMap(constants));
            }catch (NumberFormatException | NullPointerException ex){
                this.relevanceScore = 0.0;
            }
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

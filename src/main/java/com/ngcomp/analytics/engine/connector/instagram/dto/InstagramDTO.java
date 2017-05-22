package com.ngcomp.analytics.engine.connector.instagram.dto;

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

import java.util.Map;

import static com.ngcomp.analytics.engine.util.Constants.CF;

public class InstagramDTO extends Data {

    private String createdAt;
	private String id;
	private String videoUrl;
	private String imageUrl;
	private String imageLocationLocal;
	private Story story ;
	private String[] tags;
	private int tagCount;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }



    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    private String platformId;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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
	
	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public int getTagCount() {
		return tagCount;
	}

	public void setTagCount(int tagCount) {
		this.tagCount = tagCount;
	}

	public Story getStory() {
		return story;
	}

	public void setStory(Story story) {
		this.story = story;
	}

	public String getRowKey() {
		return id;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getImageLocationLocal() {
		return imageLocationLocal;
	}
	public void setImageLocationLocal(String imageLocationLocal) {
		this.imageLocationLocal = imageLocationLocal;
	}

	public String getVideoUrl() {
		return videoUrl;
	}
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
    @Override
    public String toString() {
        return "InstagramDTO{" +
                "id='" + id + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", imageLocationLocal='" + imageLocationLocal + '\'' +
                ", story=" + story +
                '}';
    }

    public void init(Result result){

        Gson gson = new Gson();

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
        String s = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("likeCount")));
        if(!Strings.isNullOrEmpty(s) && !"null".equals(s.trim())){
            this.setLikeCount(s);
        }

        s = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("commentCount")));
        if(!Strings.isNullOrEmpty(s) && !"null".equals(s.trim())){
            this.setCommentCount(s);
        }

        s = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("shareCount")));
        if(!Strings.isNullOrEmpty(s) && !"null".equals(s.trim())){
            this.setShareCount(s);
        }


        String extraWeight = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("extraWeight")));
        if(!Strings.isNullOrEmpty(extraWeight)){
            this.setExtraWeight(Double.valueOf(extraWeight));
        }

        this.setRelevanceScore();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstagramDTO)) return false;

        InstagramDTO that = (InstagramDTO) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (imageLocationLocal != null ? !imageLocationLocal.equals(that.imageLocationLocal) : that.imageLocationLocal != null)
            return false;
        if (imageUrl != null ? !imageUrl.equals(that.imageUrl) : that.imageUrl != null) return false;
        if (story != null ? !story.equals(that.story) : that.story != null) return false;
        if (videoUrl != null ? !videoUrl.equals(that.videoUrl) : that.videoUrl != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (videoUrl != null ? videoUrl.hashCode() : 0);
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        result = 31 * result + (imageLocationLocal != null ? imageLocationLocal.hashCode() : 0);
        result = 31 * result + (story != null ? story.hashCode() : 0);
        return result;
    }
}

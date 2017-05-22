package com.ngcomp.analytics.engine.connector.twitter.model;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.ngcomp.analytics.engine.MyMain;
import com.ngcomp.analytics.engine.conn.JedisConnectionPool;
import com.ngcomp.analytics.engine.connector.common.model.Data;
import com.ngcomp.analytics.engine.connector.common.model.FBStat;
import com.ngcomp.analytics.engine.connector.common.model.Story;
import com.ngcomp.analytics.engine.util.Constants;
import com.ngcomp.analytics.engine.util.PortalUtils;
import net.sf.json.JSONObject;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import redis.clients.jedis.Jedis;
import scala.actors.threadpool.Arrays;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;

public class Trend extends Data {
	private int woeid;
	private String countryName;
	private String countryCode;
	private String placeName;
	private int placeCode;
	private String name;
	private String url;
	private Story story;
	public Story getStory() {
		return story;
	}

	public void setStory(Story story) {
		this.story = story;
	}

	public FBStat getFbStat() {
		return fbStat;
	}

	public void setFbStat(FBStat fbStat) {
		this.fbStat = fbStat;
	}
	private FBStat fbStat;
	private Long rowKey;
	private Date createdAt = new Date();
	
	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	 public Long getRowKey() {
	        return Long.MAX_VALUE - woeid;
	  }

	public void setRowKey(Long rowKey) {
		this.rowKey = rowKey;
	}

	public int getWoeid() {
		return woeid;
	}

	public void setWoeid(int woeid) {
		this.woeid = woeid;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public int getPlaceCode() {
		return placeCode;
	}

	public void setPlaceCode(int placeCode) {
		this.placeCode = placeCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public String toString() {
		return "Trend [woeid=" + woeid + ", countryName=" + countryName
				+ ", placeName=" + placeName + ", name=" + name + ", url="
				+ url + "]";
	}
//	public Map<String, String[]> getHBaseRowMap() {
//
//		Gson gson = new Gson();
//
//		String[] quals = new String[15];
//		String[] vals = new String[15];
//
//		quals[0] = "sourceId"; vals[0] = this.getSourceId();
//		quals[1] = "marketId"; vals[1] = this.getMarketId();
//		quals[2] = "brandId"; vals[2] = String.valueOf(this.getBrandId());
//		quals[3] = "owned"; vals[3] = String.valueOf(this.getOwned());
//		quals[4] = "topic"; vals[4] = this.getTopic();
//		quals[5] = "post"; vals[5] = gson.toJson(this, Trend.class);
//		quals[6] = "originalTokens"; vals[6] = StringUtils.join(this.getOriginalTokens(), ",");
//		quals[7] = "artificialTokens"; vals[7] = StringUtils.join(this.getArtificialTokens(), ",");
//
//		quals[8] = "likeCount"; vals[8] = String.valueOf(this.getLikeCount());
//		quals[9] = "commentCount"; vals[9] = String.valueOf(this.getCommentCount());
//		quals[10] = "shareCount"; vals[10] = String.valueOf(this.getShareCount());
//		quals[11] = "article"; vals[11] = this.getName();
//		quals[12] = "createdAt"; vals[12] = String.valueOf(this.getCreatedAt());
//		quals[13] = "extraWeight"; vals[13] = String.valueOf(this.getExtraWeight());
//        quals[14] = "relevanceScore"; vals[14] = String.valueOf(this.getRelevanceScore());
//
//		Map<String, String[]> map = new HashMap<String, String[]>();
//		map.put("quals", quals);
//		map.put("vals", vals);
//
//		return map;
//	}
	
	 public void init(Result result){

	        Gson gson = new Gson();

            JSONObject jsonO = PortalUtils.getJSONObject(result.getFamilyMap(Bytes.toBytes("cf")));

//	        this.setSourceId(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("sourceId"))));
//	        this.setMarketId(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("marketId"))));
//	        this.setBranchId(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("brandId"))));

//	        this.setTopic(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("topic"))));
//	        this.setPost(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("post"))));

	        String tokens = jsonO.getString("originalTokens");
	        if(!Strings.isNullOrEmpty(tokens)){
	            this.setOriginalTokens(Arrays.asList(tokens.split(",")));
	        }

	        if(jsonO.containsKey("artificialTokens")){
                tokens = jsonO.getString("artificialTokens");
                if(!Strings.isNullOrEmpty(tokens)){
                    this.setArtificialTokens(new HashSet(Arrays.asList(tokens.split(","))));
                }
            }
	        String s = jsonO.getString("likeCount");
	        if(!Strings.isNullOrEmpty(s)){
	            this.setLikeCount(s);
	        }

	        s = jsonO.getString("commentCount");
	        if(!Strings.isNullOrEmpty(s)){
	            this.setCommentCount(s);
	        }


	        s = jsonO.getString("shareCount");
	        if(!Strings.isNullOrEmpty(s)){
	            this.setShareCount(s);
	        }

//            String  storyS = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("article")));
//            if(storyS!=null){
//                try{
//                    this.setStory(gson.fromJson(storyS, Story.class));
//
//                }catch(Exception ex){
//                    Story story = new Story();
//                    story.setActualStory(storyS);
//                    this.setStory(story);
//                }
//            }

	        //TODO
//	        this.setDate(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("createdAt"))));

	        String extraWeight = jsonO.getString("extraWeight");
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
        if (!(o instanceof Trend)) return false;

        Trend trend = (Trend) o;

        if (placeCode != trend.placeCode) return false;
        if (woeid != trend.woeid) return false;
        if (countryCode != null ? !countryCode.equals(trend.countryCode) : trend.countryCode != null) return false;
        if (countryName != null ? !countryName.equals(trend.countryName) : trend.countryName != null) return false;
        if (createdAt != null ? !createdAt.equals(trend.createdAt) : trend.createdAt != null) return false;
        if (fbStat != null ? !fbStat.equals(trend.fbStat) : trend.fbStat != null) return false;
        if (name != null ? !name.equals(trend.name) : trend.name != null) return false;
        if (placeName != null ? !placeName.equals(trend.placeName) : trend.placeName != null) return false;
        if (rowKey != null ? !rowKey.equals(trend.rowKey) : trend.rowKey != null) return false;
        if (story != null ? !story.equals(trend.story) : trend.story != null) return false;
        if (url != null ? !url.equals(trend.url) : trend.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = woeid;
        result = 31 * result + (countryName != null ? countryName.hashCode() : 0);
        result = 31 * result + (countryCode != null ? countryCode.hashCode() : 0);
        result = 31 * result + (placeName != null ? placeName.hashCode() : 0);
        result = 31 * result + placeCode;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (story != null ? story.hashCode() : 0);
        result = 31 * result + (fbStat != null ? fbStat.hashCode() : 0);
        result = 31 * result + (rowKey != null ? rowKey.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        return result;
    }
}

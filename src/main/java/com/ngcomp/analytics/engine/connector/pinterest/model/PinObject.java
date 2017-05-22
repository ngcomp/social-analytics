package com.ngcomp.analytics.engine.connector.pinterest.model;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.ngcomp.analytics.engine.MyMain;
import com.ngcomp.analytics.engine.conn.JedisConnectionPool;
import com.ngcomp.analytics.engine.connector.common.model.Data;
import com.ngcomp.analytics.engine.connector.common.model.Story;
import com.ngcomp.analytics.engine.util.Constants;
import com.ngcomp.analytics.engine.util.PortalUtils;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.xmlrpc.XmlRpcException;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Map;

import static com.ngcomp.analytics.engine.util.Constants.CF;

public class PinObject extends Data {

    @Override
    public String toString() {
        return "PinObject{" +
                "createdAt='" + createdAt + '\'' +
                ", href='" + href + '\'' +
                ", board=" + board +
                ", src='" + src + '\'' +
                ", desc='" + desc + '\'' +
                ", domain='" + domain + '\'' +
                ", pinUrl='" + pinUrl + '\'' +
                ", story=" + story +
                ", pinterestRepin='" + pinterestRepin + '\'' +
                ", id='" + id + '\'' +
                ", platformId='" + platformId + '\'' +
                ", owner='" + owner + '\'' +
                ", pinterestLikes='" + pinterestLikes + '\'' +
                '}';
    }

    private String createdAt;
	private String href;
	private Board board;
	private String src;
	private String desc;
	private String domain;
	private String pinUrl;
	private Story story;
	private String pinterestRepin;
    private String id;
    private String platformId;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    private String owner;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String pinterestLikes;

    public String getPinterestLikes() {
        return pinterestLikes;
    }

    public void setPinterestLikes(String pinterestLikes) {
        this.pinterestLikes = pinterestLikes;
    }

    public String getMediaType() {
        return super.mediaType;
    }

    public void setMediaType(String mediaType) {
        super.mediaType = mediaType;
    }

    public String getMedia() {
        return super.media;
    }

    public void setMedia(String media) {
        super.media = media;
    }

	public String getPinterestRepin() {
		return pinterestRepin;
	}
	public void setPinterestRepin(String pinterestRepin) {
		this.pinterestRepin = pinterestRepin;
	}
	public Story getStory() {
		return story;
	}
	public void setStory(Story story) {
		this.story = story;
	}
	public String getPinUrl() {
		return pinUrl;
	}
	public void setPinUrl(String pinUrl) {
		this.pinUrl = pinUrl;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public Board getBoard() {
		return board;
	}
	public void setBoard(Board board) {
		this.board = board;
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
//		quals[2] = "brandId"; vals[2] = this.getBrandId();
//		quals[3] = "owned"; vals[3] = String.valueOf(this.getOwned());
//		quals[4] = "topic"; vals[4] = this.getTopic();
//		quals[5] = "post"; vals[5] = gson.toJson(this, PinObject.class);
//		quals[6] = "originalTokens"; vals[6] = StringUtils.join(this.getOriginalTokens(), ",");
//		quals[7] = "artificialTokens"; vals[7] = StringUtils.join(this.getArtificialTokens(), ",");
//		quals[8 ] = "likeCount"   ; vals[8 ] = String.valueOf(this.getLikeCount());
//        quals[9 ] = "commentCount"; vals[9 ] = String.valueOf(this.getCommentCount());
//        quals[10] = "shareCount" ;  vals[10] = String.valueOf(this.getShareCount());
//		quals[11] = "article"; vals[11] = this.getStory()!=null? this.getStory().getActualStory():"";
//		quals[12] = "createdAt"; vals[12] = String.valueOf(this.getPinUrl());
//		quals[13] = "extraWeight"; vals[13] = String.valueOf(this.getExtraWeight());
//        quals[14] = "relevanceScore"; vals[14] = String.valueOf(this.getRelevanceScore());
//
//		Map<String, String[]> map = new HashMap<String, String[]>();
//		map.put("quals", quals);
//		map.put("vals", vals);
//		return map;
//	}


	public void init(Result result) throws XmlRpcException, BoilerpipeProcessingException, IOException {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PinObject)) return false;

        PinObject pinObject = (PinObject) o;

        if (board != null ? !board.equals(pinObject.board) : pinObject.board != null) return false;
        if (desc != null ? !desc.equals(pinObject.desc) : pinObject.desc != null) return false;
        if (domain != null ? !domain.equals(pinObject.domain) : pinObject.domain != null) return false;
        if (href != null ? !href.equals(pinObject.href) : pinObject.href != null) return false;
        if (pinUrl != null ? !pinUrl.equals(pinObject.pinUrl) : pinObject.pinUrl != null) return false;
        if (src != null ? !src.equals(pinObject.src) : pinObject.src != null) return false;
        if (story != null ? !story.equals(pinObject.story) : pinObject.story != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = href != null ? href.hashCode() : 0;
        result = 31 * result + (board != null ? board.hashCode() : 0);
        result = 31 * result + (src != null ? src.hashCode() : 0);
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        result = 31 * result + (pinUrl != null ? pinUrl.hashCode() : 0);
        result = 31 * result + (story != null ? story.hashCode() : 0);
        return result;
    }
}

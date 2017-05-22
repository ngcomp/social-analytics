package com.ngcomp.analytics.engine.model;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.ngcomp.analytics.engine.MyMain;
import com.ngcomp.analytics.engine.conn.JedisConnectionPool;
import com.ngcomp.analytics.engine.util.PortalUtils;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.xmlrpc.XmlRpcException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import redis.clients.jedis.Jedis;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.util.*;

import static com.ngcomp.analytics.engine.util.Constants.CF;


public class Trend implements Comparable<Trend> {

    private static final String FACEBOOK_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss+SSSS";

    public Trend(){};


    private Set<String> originalTokens   = new HashSet<String>();
    private Set<String> artificialTokens = new HashSet<String>();


    public Trend(String url, String story, String media, String media_type, String media_hash, String sourceID, String marketID, String brandID, String attribution, String platformID, Long score, String TS, String privacy, String active, String owned, String likes, String comments, String shares, String topic, String article) {
        this.url = url;
        this.story = story;
        this.media = media;
        this.media_type = media_type;
        this.media_hash = media_hash;
        this.sourceID = sourceID;
        this.marketID = marketID;
        this.brandID = brandID;
        this.attribution = attribution;
        this.platformID = platformID;
        this.score = score;
        this.TS = TS;
        this.privacy = privacy;
        this.active = active;
        this.owned = owned;
        this.likes = likes;
        this.comments = comments;
        this.shares = shares;
        this.topic = topic;
        this.article = article;
        this.extraWeight = 0.0;
    }

    private Long storyId;
    private String url ;
	private String story ;
	private String media ;
	private String media_type ;
	private String media_hash ;
	private String sourceID ;
	private String marketID ;
	private String brandID ;
	private String attribution ;
	private String platformID ;
	private Long score ;
	private String TS ;
	private String privacy ;
	private String active ;
	private String owned ;
	private String likes ;
	private String comments ;
	private String shares ;
	private String topic ;
	private String article ;
    private Boolean isHidden;
    private Double extraWeight = 0.0;

    private Double relevanceScore;

    public void setRelevanceScore(Map<String, Float> constants){

        Float amplifier = constants.get(MyMain.AMPLIFIER_CHANNEL);
        Float decay  = constants.get(MyMain.DECAY);
        Float zScore = constants.get(MyMain.Z_SCORE);

        Float w1 = constants.get(MyMain.W1);
        Float w2 = constants.get(MyMain.W2);
        Float w3 = constants.get(MyMain.W3);

        Float avgStoryDecay = constants.get(MyMain.AVG_STORY_DECAY);

//        System.out.println("amplifier=>"  + amplifier + " Decay=>" + decay +  " zScore=>" + zScore + " W1=>" +  w1 + " W2=>" + w2 + " W3=>" + w3 + " AVGDecay" + avgStoryDecay);
//
//        System.out.println("Likes=>" + this.getLikes() + " Shares=>"+ this.getShareCount() + " Comments=>"+ this.getCommentCount() + " ExtraWeight=>" + this.extraWeight);
//
//        System.out.println("Amplifier=>" + amplifier + " " + Math.exp(-decay - 5)  + " " +  zScore  + " " +  (w1 + " " + Float.valueOf(this.getLikes())  + " " +  w2  + " " +  Float.valueOf(this.getCommentCount())  + " " +  w3  + " " +  Float.valueOf(this.getShareCount()))  + " " +  this.extraWeight);

        this.relevanceScore =  amplifier * Math.exp(-decay - 5) * zScore * (w1* Float.valueOf(this.getLikes()) + w2 * Float.valueOf(this.getCommentCount()) + w3 * Float.valueOf(this.getShareCount())) + this.extraWeight;
    }

    public long getHBKey() throws DatatypeConfigurationException {
        return Long.MAX_VALUE - this.getLongTime();
    }

    //"2013-04-12T13:38:48+02:00"
    public Long getLongTime() throws DatatypeConfigurationException {

        if(!Strings.isNullOrEmpty(this.TS)){
            DateTimeFormatter dtf = DateTimeFormat.forPattern(FACEBOOK_DATE_FORMAT);
            DateTime dateTime = dtf.parseDateTime(this.TS);
            return dateTime.getMillis();
        }else{
            return null;
        }
    }

    public Integer getShareCount(){
        if(Strings.isNullOrEmpty(this.shares) || "null".equals(this.shares)){
            return 0;
        }else{
            return Integer.parseInt(this.shares);
        }
    }


    public Integer getCommentCount(){
        if(Strings.isNullOrEmpty(this.comments) || "null".equals(this.comments)){
            return 0;
        }else{
            return Integer.parseInt(this.comments);
        }
    }

    public void init(Result result){

        Gson gson = new Gson();

        this.setUrl(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("url"))));
        this.setStory(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("story"))));
        this.setMedia(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("media"))));
        this.setMedia_type(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("media_type"))));
        this.setMedia_hash(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("media_hash"))));
        this.setSourceID(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("sourceID"))));
        this.setMarketID(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("marketID"))));
        this.setBrandID(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("brandID"))));
        this.setAttribution(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("attribution"))));
        this.setPlatformID(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("platformID"))));

        String score = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("score")));
        if(!Strings.isNullOrEmpty(score) && !"null".equals(score)){
            this.setScore(Long.valueOf(score));
        }

        this.setTS(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("TS"))));
        this.setPrivacy(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("privacy"))));
        this.setActive(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("active"))));
        this.setOwned(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("owned"))));

        this.setLikes(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("likes"))));

        this.setComments(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("comments"))));
        this.setShares(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("shares"))));
        this.setTopic(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("topic"))));
        this.setArticle(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("article"))));

        String tokens = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("originalTokens")));
        if(tokens!= null && !Strings.isNullOrEmpty(tokens)){
            List<String> list = Arrays.asList(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("originalTokens"))).split(","));
            this.setOriginalTokens(new HashSet<String>(list));
        }

        tokens = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("artificialTokens")));
        if(tokens!= null && !Strings.isNullOrEmpty(tokens)){
            List<String> list = Arrays.asList(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("artificialTokens"))).split(","));
            this.setArtificialTokens(new HashSet<String>(list));
        }
        score = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("relevanceScore")));

        if(!Strings.isNullOrEmpty(score)){
            this.relevanceScore = Double.valueOf(score);
        }else{
            this.setRelevanceScore();
        }
        System.out.println(this.relevanceScore);
    }


    public void setRelevanceScore(){
        Jedis jedis = JedisConnectionPool.getResource();

        Map<String, String> constants = jedis.hgetAll(this.sourceID + "_SCORES");
        this.setRelevanceScore(PortalUtils.getFloatMap(constants));
        JedisConnectionPool.returnResource(jedis);
    }


    public Map<String, String[]> getKeyValMap() throws DatatypeConfigurationException {

        String[] quals = new String[24];
        String[] vals  = new String[24];

        quals[0 ] = "url"        ; vals[0 ] = this.getUrl();
        quals[1 ] = "story"      ; vals[1 ] = this.getStory();
        quals[2 ] = "media"      ; vals[2 ] = this.getMedia();
        quals[3 ] = "media_type" ; vals[3 ] = this.getMedia_type();
        quals[4 ] = "media_hash" ; vals[4 ] = this.getMedia_hash();

        quals[5 ] = "sourceID"   ; vals[5 ] = this.getSourceID();
        quals[6 ] = "marketID"   ; vals[6 ] = this.getMarketID();
        quals[7 ] = "brandID"    ; vals[7 ] = this.getBrandID();
        quals[8 ] = "attribution"; vals[8 ] = this.getAttribution();

        quals[9 ] = "platformID" ; vals[9 ] = this.getPlatformID();
        quals[10] = "score"      ; vals[10] = String.valueOf(this.getScore());
        quals[11] = "TS"         ; vals[11] = this.getTS();
        quals[12] = "privacy"    ; vals[12] = this.getPrivacy();
        quals[13] = "active"     ; vals[13] = this.getActive();

        quals[14] = "owned"      ; vals[14] = this.getOwned();
        quals[15] = "likes"      ; vals[15] = this.getLikes();
        quals[16] = "comments"   ; vals[16] = this.getComments();
        quals[17] = "shares"     ; vals[17] = this.getShares();
        quals[18] = "topic"      ; vals[18] = this.getTopic();
        quals[19] = "article"    ; vals[19] = this.getArticle();
        quals[20] = "originalTokens"   ; vals[20] = Arrays.toString(this.getOriginalTokens().toArray());
        quals[21] = "artificialTokens" ; vals[21] = Arrays.toString(this.getArtificialTokens().toArray());
        quals[22] = "relevanceScore"   ; vals[22] = String.valueOf(this.getRelevanceScore());
        quals[22] = "createdAt"        ; vals[22] = String.valueOf(this.getLongTime());


        Map<String, String[]> map = new HashMap<String, String[]>();
        map.put("quals", quals);
        map.put("vals" , vals);

        return map;
    }


    public Boolean getIsHidden() {
        //System.out.println("Came Here." + this.isHidden);
        if(this.isHidden == null || this.isHidden.equals("null") || this.isHidden == false){
            return false;
        }else{
            return true;
        }
    }

    public void setIsHidden(Boolean hidden) {
        this.isHidden = hidden;
    }

    public Double getExtraWeight() {
        return extraWeight;
    }

    public void setExtraWeight(Double extraWeight) {
        this.extraWeight = extraWeight;
    }

    public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getStory() {
		return story;
	}
	public void setStory(String story) {
		this.story = story;
	}
	public String getMedia() {
		return media;
	}
	public void setMedia(String media) {
		this.media = media;
	}
	public String getMedia_type() {
		return media_type;
	}
	public void setMedia_type(String media_type) {
		this.media_type = media_type;
	}
	public String getMedia_hash() {
		return media_hash;
	}
	public void setMedia_hash(String media_hash) {
		this.media_hash = media_hash;
	}
	public String getSourceID() {
		return sourceID;
	}
	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}
	public String getAttribution() {
		return attribution;
	}
	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}
	public String getPlatformID() {
		return platformID;
	}
	public void setPlatformID(String platformID) {
		this.platformID = platformID;
	}
	public Long getScore() {
		return score;
	}
	public void setScore(Long score) {
		this.score = score;
	}
	public String getTS() {
		return TS;
	}
	public void setTS(String tS) {
		TS = tS;
	}
	public String getPrivacy() {
		return privacy;
	}
	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getOwned() {
		return owned;
	}
	public void setOwned(String owned) {
		this.owned = owned;
	}
	public String getLikes() {
        if(Strings.isNullOrEmpty(this.likes) || "null".equals(this.likes)){
            return "0";
        }
		return likes;
	}
	public void setLikes(String likes) {
		this.likes = likes;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getShares() {
		return shares;
	}
	public void setShares(String shares) {
		this.shares = shares;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getArticle() {
		return article;
	}
	public void setArticle(String article) {
		this.article = article;
	}
	public String getMarketID() {
		return marketID;
	}
	public void setMarketID(String marketID) {
		this.marketID = marketID;
	}
	public String getBrandID() {
		return brandID;
	}
	public void setBrandID(String brandID) {
		this.brandID = brandID;
	}

    public Double getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(Double relevanceScore) {
        this.relevanceScore = relevanceScore;
    }

    public Set<String> getOriginalTokens() {
        return originalTokens;
    }

    public void setOriginalTokens(String message) throws IOException, XmlRpcException, BoilerpipeProcessingException {
        List<String> tokens = PortalUtils.getKeywords(message);
        if(tokens != null){
            this.originalTokens = new HashSet<String>(tokens);
        }
    }

    public void setOriginalTokens(Set<String> originalTokens) {
        this.originalTokens = originalTokens;
    }

    public Set<String> getArtificialTokens() {
        return artificialTokens;
    }

    public void setArtificialTokens(Set<String> artificialTokens) {
        this.artificialTokens = artificialTokens;
    }

    @Override
    public String toString() {
        return "Trend{" +
                "url='" + url + '\'' +
                ", story='" + story + '\'' +
                ", media='" + media + '\'' +
                ", media_type='" + media_type + '\'' +
                ", media_hash='" + media_hash + '\'' +
                ", sourceID='" + sourceID + '\'' +
                ", marketID='" + marketID + '\'' +
                ", brandID='" + brandID + '\'' +
                ", attribution='" + attribution + '\'' +
                ", platformID='" + platformID + '\'' +
                ", score='" + score + '\'' +
                ", TS='" + TS + '\'' +
                ", privacy='" + privacy + '\'' +
                ", active='" + active + '\'' +
                ", owned='" + owned + '\'' +
                ", likes='" + likes + '\'' +
                ", comments='" + comments + '\'' +
                ", shares='" + shares + '\'' +
                ", topic='" + topic + '\'' +
                //", article='" + article + '\'' +
                '}';
    }

    public Long getStoryId() {
        return storyId;
    }

    public void setStoryId(Long storyId) {
        this.storyId = storyId;
    }

    @Override
    public int compareTo(Trend compareTrend) {
        return Double.compare(compareTrend.getRelevanceScore(), this.relevanceScore);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trend)) return false;

        Trend trend = (Trend) o;

        if (TS != null ? !TS.equals(trend.TS) : trend.TS != null) return false;
        if (active != null ? !active.equals(trend.active) : trend.active != null) return false;
        if (article != null ? !article.equals(trend.article) : trend.article != null) return false;
        if (artificialTokens != null ? !artificialTokens.equals(trend.artificialTokens) : trend.artificialTokens != null)
            return false;
        if (attribution != null ? !attribution.equals(trend.attribution) : trend.attribution != null) return false;
        if (brandID != null ? !brandID.equals(trend.brandID) : trend.brandID != null) return false;
        if (comments != null ? !comments.equals(trend.comments) : trend.comments != null) return false;
        if (extraWeight != null ? !extraWeight.equals(trend.extraWeight) : trend.extraWeight != null) return false;
        if (isHidden != null ? !isHidden.equals(trend.isHidden) : trend.isHidden != null) return false;
        if (likes != null ? !likes.equals(trend.likes) : trend.likes != null) return false;
        if (marketID != null ? !marketID.equals(trend.marketID) : trend.marketID != null) return false;
        if (media != null ? !media.equals(trend.media) : trend.media != null) return false;
        if (media_hash != null ? !media_hash.equals(trend.media_hash) : trend.media_hash != null) return false;
        if (media_type != null ? !media_type.equals(trend.media_type) : trend.media_type != null) return false;
        if (originalTokens != null ? !originalTokens.equals(trend.originalTokens) : trend.originalTokens != null)
            return false;
        if (owned != null ? !owned.equals(trend.owned) : trend.owned != null) return false;
        if (platformID != null ? !platformID.equals(trend.platformID) : trend.platformID != null) return false;
        if (privacy != null ? !privacy.equals(trend.privacy) : trend.privacy != null) return false;
        if (relevanceScore != null ? !relevanceScore.equals(trend.relevanceScore) : trend.relevanceScore != null)
            return false;
        if (score != null ? !score.equals(trend.score) : trend.score != null) return false;
        if (shares != null ? !shares.equals(trend.shares) : trend.shares != null) return false;
        if (sourceID != null ? !sourceID.equals(trend.sourceID) : trend.sourceID != null) return false;
        if (story != null ? !story.equals(trend.story) : trend.story != null) return false;
        if (storyId != null ? !storyId.equals(trend.storyId) : trend.storyId != null) return false;
        if (topic != null ? !topic.equals(trend.topic) : trend.topic != null) return false;
        if (url != null ? !url.equals(trend.url) : trend.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = originalTokens != null ? originalTokens.hashCode() : 0;
        result = 31 * result + (artificialTokens != null ? artificialTokens.hashCode() : 0);
        result = 31 * result + (storyId != null ? storyId.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (story != null ? story.hashCode() : 0);
        result = 31 * result + (media != null ? media.hashCode() : 0);
        result = 31 * result + (media_type != null ? media_type.hashCode() : 0);
        result = 31 * result + (media_hash != null ? media_hash.hashCode() : 0);
        result = 31 * result + (sourceID != null ? sourceID.hashCode() : 0);
        result = 31 * result + (marketID != null ? marketID.hashCode() : 0);
        result = 31 * result + (brandID != null ? brandID.hashCode() : 0);
        result = 31 * result + (attribution != null ? attribution.hashCode() : 0);
        result = 31 * result + (platformID != null ? platformID.hashCode() : 0);
        result = 31 * result + (score != null ? score.hashCode() : 0);
        result = 31 * result + (TS != null ? TS.hashCode() : 0);
        result = 31 * result + (privacy != null ? privacy.hashCode() : 0);
        result = 31 * result + (active != null ? active.hashCode() : 0);
        result = 31 * result + (owned != null ? owned.hashCode() : 0);
        result = 31 * result + (likes != null ? likes.hashCode() : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        result = 31 * result + (shares != null ? shares.hashCode() : 0);
        result = 31 * result + (topic != null ? topic.hashCode() : 0);
        result = 31 * result + (article != null ? article.hashCode() : 0);
        result = 31 * result + (isHidden != null ? isHidden.hashCode() : 0);
        result = 31 * result + (extraWeight != null ? extraWeight.hashCode() : 0);
        result = 31 * result + (relevanceScore != null ? relevanceScore.hashCode() : 0);
        return result;
    }
}

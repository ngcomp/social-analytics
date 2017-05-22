package com.ngcomp.analytics.engine.connector.rss.model;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.ngcomp.analytics.engine.MyMain;
import com.ngcomp.analytics.engine.conn.JedisConnectionPool;
import com.ngcomp.analytics.engine.connector.common.model.Data;
import com.ngcomp.analytics.engine.connector.common.model.FBStat;
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

public class FeedMessage extends Data{

    String platformId;
    String title;
    String description;
    String link;
    String author;
    String guid;
    Story story;
    FBStat fbStat;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    private String createdAt;




    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
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


    public String getRowKey() {
        return link;
    }

    public FBStat getFbStat() {
        return fbStat;
    }

    public void setFbStat(FBStat fbStat) {
        this.fbStat = fbStat;
    }

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

//    public Map<String, String[]> getHBaseRowMap() {
//
//        Gson gson = new Gson();
//
//        String[] quals = new String[15];
//        String[] vals = new String[15];
//
//        quals[0] = "sourceId"; vals[0] = this.getSourceId();
//        quals[1] = "marketId"; vals[1] = this.getMarketId();
//        quals[2] = "brandId" ; vals[2] = String.valueOf(this.getBrandId());
//        quals[3] = "owned"   ; vals[3] = String.valueOf(this.getOwned());
//        quals[4] = "topic"   ; vals[4] = this.getTopic();
//        quals[5] = "post"    ; vals[5] = gson.toJson(this, FeedMessage.class);
//        quals[6] = "originalTokens"  ; vals[6] = StringUtils.join(this.getOriginalTokens()  , ",");
//        quals[7] = "artificialTokens"; vals[7] = StringUtils.join(this.getArtificialTokens(), ",");
//
//        quals[8 ] = "likeCount"   ; vals[8 ] = String.valueOf(this.getLikeCount());
//        quals[9 ] = "commentCount"; vals[9 ] = String.valueOf(this.getCommentCount());
//        quals[10] = "shareCount"  ; vals[10] = String.valueOf(this.getShareCount());
//        quals[11] = "article"     ; vals[11] = this.getDescription();
//        quals[12] = "createdAt"   ; vals[12] = String.valueOf(new Date().getTime());
//        quals[13] = "extraWeight" ; vals[13] = String.valueOf(this.getExtraWeight());
//        quals[13] = "relevanceScore" ; vals[13] = String.valueOf(this.getRelevanceScore());
//
//        Map<String, String[]> map = new HashMap<String, String[]>();
//        map.put("quals", quals);
//        map.put("vals", vals);
//
//        return map;
//    }
    
    

    @Override
    public String toString() {
        return "FeedMessage [title=" + title + ", description=" + description
                + ", link=" + link + ", author=" + author + ", guid=" + guid
                + ", fbStat=" + fbStat + "]";
    }

    
    public void init(Result result){

        Gson gson = new Gson();

//        this.setSourceId(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("sourceId"))));
//        this.setMarketId(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("marketId"))));
//        this.setBranchId(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("brandId"))));

//        this.setTopic(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("topic"))));
//        this.setPost(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("post"))));

        String tokens = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("originalTokens")));
        if(!Strings.isNullOrEmpty(tokens)){
            JSONArray jsonArray = (JSONArray)JSONValue.parse(tokens);
            this.setOriginalTokens(PortalUtils.getTokenSet(jsonArray));
        }

        tokens = Bytes.toString     (result.getValue(Bytes.toBytes(CF), Bytes.toBytes("artificialTokens")));
        if(!Strings.isNullOrEmpty(tokens)){
            JSONArray jsonArray = (JSONArray)JSONValue.parse(tokens);
            this.setArtificialTokens(PortalUtils.getTokenSet(jsonArray));
        }

        String s = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("likeCount")));
        if(!Strings.isNullOrEmpty(s)){
            this.setLikeCount(s);
        }

        String guid = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("guid")));
        if(!Strings.isNullOrEmpty(guid)){
            this.guid = guid;

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
//        try{
//            if(story!=null){
//                this.setStory(gson.fromJson(story, Story.class));
//            }
//        }catch(JsonParseException ex){
//            Story tStory = new Story();
//            tStory.setActualStory(story);
//            this.setStory(tStory);
//        }

        //TODO
//        this.setDate(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("createdAt"))));

        String extraWeight = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("extraWeight")));
        if(!Strings.isNullOrEmpty(extraWeight)){
            this.setExtraWeight(Double.valueOf(extraWeight));
        }

        try{
            this.setRelevanceScore();
        }catch(NullPointerException ex){
            this.relevanceScore = 0.0;
        }

//        String owned = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("owned")));
//        if(!Strings.isNullOrEmpty(owned)){
//            this.setOwned(Boolean.valueOf(owned));
//        }
    }


    public void setRelevanceScore(Map<String, Float> constants){

        Float amplifier = constants.get(Constants.AMPLIFIER_CHANNEL);
        Float decay  = constants.get(Constants.DECAY);
        Float zScore = constants.get(MyMain.Z_SCORE);

        Float w1 = constants.get(Constants.W1);
        Float w2 = constants.get(Constants.W2);
        Float w3 = constants.get(Constants.W3);

        Float avgStoryDecay = constants.get(Constants.AVG_STORY_DECAY);

        this.relevanceScore =  amplifier * Math.exp(-decay - 5) * zScore * (w1* Float.valueOf(super.getLikeCount()) + w2 * Float.valueOf(super.getCommentCount()) + w3 * Float.valueOf(super.getShareCount())) + super.getExtraWeight();
    }
    
    public void setRelevanceScore(){
        Jedis jedis = JedisConnectionPool.getResource();
        Map<String, String> constants = jedis.hgetAll(super.getSourceId() + "_SCORES");
        if(constants == null || constants.isEmpty()){
            PortalUtils.setConstantsInRedis(super.getSourceId());
        }
        JedisConnectionPool.returnResource(jedis);
        this.setRelevanceScore(PortalUtils.getFloatMap(constants));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeedMessage)) return false;

        FeedMessage that = (FeedMessage) o;

        if (!author.equals(that.author)) return false;
        if (!description.equals(that.description)) return false;
        if (!fbStat.equals(that.fbStat)) return false;
        if (!guid.equals(that.guid)) return false;
        if (!link.equals(that.link)) return false;
        if (!story.equals(that.story)) return false;
        if (!title.equals(that.title)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + link.hashCode();
        result = 31 * result + author.hashCode();
        result = 31 * result + guid.hashCode();
        result = 31 * result + story.hashCode();
        result = 31 * result + fbStat.hashCode();
        return result;
    }
}
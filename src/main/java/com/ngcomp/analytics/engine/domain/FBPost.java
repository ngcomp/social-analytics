package com.ngcomp.analytics.engine.domain;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.ngcomp.analytics.engine.MyMain;
import com.ngcomp.analytics.engine.conn.JedisConnectionPool;
import com.ngcomp.analytics.engine.util.Constants;
import com.ngcomp.analytics.engine.util.PortalUtils;
import com.restfb.types.Post;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.xmlrpc.XmlRpcException;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.*;

import static com.ngcomp.analytics.engine.util.Constants.CF;

/**
 * User: Ram Parashar
 * Date: 8/9/13
 * Time: 7:39 PM
 */
public class FBPost extends Post implements Comparable<FBPost> {



//    public Map<String, String[]> getHBaseRowMap(){
//
//        Gson gson = new Gson();
//
//        String[] quals = new String[17];
//        String[] vals  = new String[17];
//
//        quals[0 ] = "sourceId"; vals[0 ] = this.getSourceId();
//        quals[1 ] = "marketId"; vals[1 ] = this.getMarketId();
//        quals[2 ] = "brandId" ; vals[2 ] = this.getBrandId();
//        quals[3 ] = "owned"   ; vals[3 ] = String.valueOf(this.getOwned());
//        quals[4 ] = "topic"   ; vals[4 ] = this.getTopic();
//        quals[5 ] = "post"    ; vals[5 ] = gson.toJson(this.getPost(), Post.class);
//        quals[6 ] = "originalTokens"  ; vals[6 ] = StringUtils.join(this.getOriginalTokens()  , ",");
//        quals[7 ] = "artificialTokens"; vals[7 ] = StringUtils.join(this.getArtificialTokens(), ",");
//
//        quals[8 ] = "likeCount"   ; vals[8 ] = this.getLikeCount();
//        quals[9 ] = "commentCount"; vals[9 ] = this.getCommentCount();
//        quals[10] = "shareCount" ;  vals[10] = this.getShareCount();
//        quals[11] = "article"    ;  vals[11] = this.getArticle();
//        quals[12] = "createdAt"  ;  vals[12] = String.valueOf(this.getCreatedAt());
//        quals[13] = "extraWeight";  vals[13] = String.valueOf(this.getExtraWeight());
//        quals[14] = "isHidden"   ;  vals[14] = String.valueOf(this.isHidden);
//        quals[15] = "type"       ;  vals[15] = String.valueOf(this.type);
//        quals[16] = "relevanceScore"; vals[16] = String.valueOf(this.getRelevanceScore());
//
//        Map<String, String[]> map = new HashMap<String, String[]>();
//        map.put("quals", quals);
//        map.put("vals" , vals);
//
//        return map;
//    }
                          private String platformId;

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public void init(Result result){
        this.init(result, null);
    }

    public void init(Result result, String key){
        Gson gson = new Gson();

        if(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("createdAt")))!= null)
            this.setCreatedAt(Long.valueOf(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("createdAt")))));

        this.setSourceId(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("sourceId"))));
        this.setMarketId(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("marketId"))));
        this.setBrandId(Bytes.toString(result.getValue (Bytes.toBytes(CF), Bytes.toBytes("brandId"))));

        this.setOwned(Boolean.valueOf(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("owned")))));
        this.setTopic(Bytes.toString (result.getValue(Bytes.toBytes(CF), Bytes.toBytes("topic"))));

        try{
            String p = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("post")));
            if(!Strings.isNullOrEmpty(p)) {
                this.setPost(gson.fromJson(p, Post.class));
            }
        }catch(Exception ex){
            //TODO Handle this Exception.
        }

        String tokens = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("originalTokens")));
        if(!Strings.isNullOrEmpty(tokens)){
            JSONArray jsonArray = (JSONArray) JSONValue.parse(tokens);
            this.setOriginalTokens(PortalUtils.getTokenSet(jsonArray));
        }


        tokens = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("artificialTokens")));
        if(!Strings.isNullOrEmpty(tokens)){
            JSONArray jsonArray = (JSONArray) JSONValue.parse(tokens);
            this.setArtificialTokens(PortalUtils.getTokenSet(jsonArray));
        }

//        String origTokens = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("originalTokens")));
//        if(!Strings.isNullOrEmpty(origTokens)){
//            this.setOriginalTokens(Arrays.asList(origTokens.split(",")));
//        }

//        String artificialTokens = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("artificialTokens")));
//        if(!Strings.isNullOrEmpty(artificialTokens)){
//            this.setArtificialTokens(Arrays.asList(artificialTokens.split(",")));
//        }

        this.setLikeCount   (Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("likeCount"))));
        this.setCommentCount(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("commentCount"))));
        this.setShareCount  (Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("shareCount"))));
        this.setArticle     (Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("article"))));

        this.setType(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("type"))));

        String extraWeight = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("extraWeight")));
        if(!Strings.isNullOrEmpty(extraWeight)){
            this.setExtraWeight(Double.valueOf(extraWeight));
        }

        if(Boolean.valueOf(Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("isHidden"))))){
           this.isHidden = true;
        }else{
           this.isHidden = false;
        }

        if(key!=null){
            this.key = Long.valueOf(key);
        }

//        this.setKey();

        this.setRelevanceScore();
    }

    public void setRelevanceScore(){
        Jedis jedis = JedisConnectionPool.getResource();
        Map<String, String> constants = jedis.hgetAll(this.sourceId + "_SCORES");
        this.setRelevanceScore(PortalUtils.getFloatMap(constants));
        JedisConnectionPool.returnResource(jedis);
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    private Post post;

    private String title;

    private String type;
    private String sourceId;
    private String marketId;
    private String brandId;

    private transient String credentials;

    private Boolean owned;
    private String topic;

    private Set<String> originalTokens;
    private Set<String> artificialTokens;

    private String likeCount ;
    private String commentCount ;
    private String shareCount ;
    private String article ;
    private Boolean isHidden;
    private Double extraWeight = 0.0;

    private Long createdAt;

    private String media;
    private String mediaType;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMedia() {
        return this.media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getMediaType() {
        return this.mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }


    public Long getKey() {
        return key;
    }

    public void setKey() {
        this.key = this.getRowKey();
    }

    public void setKey(Long key) {
        this.key = key;
    }

    private Long key;


    private String active;



    private Double relevanceScore;

    public List<String> getKeyWords(){

        List keywords = new LinkedList();

        if(this.originalTokens != null){
            keywords.addAll(this.originalTokens);
        }

        if(this.artificialTokens!= null){
            keywords.addAll(this.artificialTokens);
        }

        return keywords;
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

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public Boolean getOwned() {
        return owned;
    }

    public void setOwned(Boolean owned) {
        this.owned = owned;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Set<String> getOriginalTokens() {
        return originalTokens;
    }

    public void setOriginalTokens(String message) throws IOException, XmlRpcException, BoilerpipeProcessingException {
        this.originalTokens = new HashSet(PortalUtils.getKeywords(message));
        this.originalTokens.add(this.topic);
    }


    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        if(Strings.isNullOrEmpty(likeCount) || likeCount.trim().equals("null")){
            this.likeCount = "0";
        }else{
            this.likeCount = likeCount;
        }

    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {

        if(Strings.isNullOrEmpty(commentCount) || commentCount.trim().equals("null")){
            this.commentCount = "0";
        }else{
            this.commentCount = commentCount;
        }


    }

    public String getShareCount() {
        return shareCount;
    }

    public void setShareCount(String shareCount) {

        if(Strings.isNullOrEmpty(shareCount) || shareCount.trim().equals("null")){
            this.shareCount = "0";
        }else{
            this.shareCount = shareCount;
        }
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public Boolean getHidden() {
        if(this.isHidden==null || !this.isHidden){
            return false;
        }else{
            return true;
        }
    }

    public void setHidden(Boolean hidden) {
        isHidden = hidden;
    }

    public Double getExtraWeight() {
        return this.extraWeight;
    }

    public void setExtraWeight(Double extraWeight) {
        this.extraWeight = extraWeight;
    }

    public Double getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(Double relevanceScore) {
        this.relevanceScore = relevanceScore;
    }

    public Set<String> getArtificialTokens() {
        return artificialTokens;
    }

    public void setArtificialTokens(Set<String> artificialTokens) {
        this.artificialTokens = artificialTokens;
    }

    public void setOriginalTokens(Set<String> originalTokens) {
        this.originalTokens = originalTokens;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getRowKey() {
        return Long.MAX_VALUE - createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public boolean equals(Object o) {

        //System.out.println("oooo");

        if (this == o) return true;
        if (!(o instanceof FBPost)) return false;
        FBPost fbPost = (FBPost) o;


        if (active != null ? !active.equals(fbPost.active) : fbPost.active != null) return false;
        if (article != null ? !article.equals(fbPost.article) : fbPost.article != null) return false;
        if (artificialTokens != null ? !artificialTokens.equals(fbPost.artificialTokens) : fbPost.artificialTokens != null)
            return false;
        if (brandId != null ? !brandId.equals(fbPost.brandId) : fbPost.brandId != null) return false;
        if (commentCount != null ? !commentCount.equals(fbPost.commentCount) : fbPost.commentCount != null)
            return false;
        if (createdAt != null ? !createdAt.equals(fbPost.createdAt) : fbPost.createdAt != null) return false;
        if (credentials != null ? !credentials.equals(fbPost.credentials) : fbPost.credentials != null) return false;
        if (extraWeight != null ? !extraWeight.equals(fbPost.extraWeight) : fbPost.extraWeight != null) return false;
        if (isHidden != null ? !isHidden.equals(fbPost.isHidden) : fbPost.isHidden != null) return false;
        if (likeCount != null ? !likeCount.equals(fbPost.likeCount) : fbPost.likeCount != null) return false;
        if (marketId != null ? !marketId.equals(fbPost.marketId) : fbPost.marketId != null) return false;
        if (originalTokens != null ? !originalTokens.equals(fbPost.originalTokens) : fbPost.originalTokens != null)
            return false;
        if (owned != null ? !owned.equals(fbPost.owned) : fbPost.owned != null) return false;
        //if (post != null ? !post.equals(fbPost.post) : fbPost.post != null) return false;
        if (relevanceScore != null ? !relevanceScore.equals(fbPost.relevanceScore) : fbPost.relevanceScore != null)
            return false;
        if (shareCount != null ? !shareCount.equals(fbPost.shareCount) : fbPost.shareCount != null) return false;
        if (sourceId != null ? !sourceId.equals(fbPost.sourceId) : fbPost.sourceId != null) return false;
        if (topic != null ? !topic.equals(fbPost.topic) : fbPost.topic != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        //int result = super.hashCode();
        int result = 1;

        result = 31 * result + (post != null ? post.hashCode() : 0);
        result = 31 * result + (sourceId != null ? sourceId.hashCode() : 0);
        result = 31 * result + (marketId != null ? marketId.hashCode() : 0);
        result = 31 * result + (brandId != null ? brandId.hashCode() : 0);
        result = 31 * result + (credentials != null ? credentials.hashCode() : 0);
        result = 31 * result + (owned != null ? owned.hashCode() : 0);
        result = 31 * result + (topic != null ? topic.hashCode() : 0);
        result = 31 * result + (originalTokens != null ? originalTokens.hashCode() : 0);
        result = 31 * result + (artificialTokens != null ? artificialTokens.hashCode() : 0);
        result = 31 * result + (likeCount != null ? likeCount.hashCode() : 0);
        result = 31 * result + (commentCount != null ? commentCount.hashCode() : 0);
        result = 31 * result + (shareCount != null ? shareCount.hashCode() : 0);
        result = 31 * result + (article != null ? article.hashCode() : 0);
        result = 31 * result + (isHidden != null ? isHidden.hashCode() : 0);
        result = 31 * result + (extraWeight != null ? extraWeight.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (active != null ? active.hashCode() : 0);
        result = 31 * result + (relevanceScore != null ? relevanceScore.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FBPost{" +
                "post=" + post +
                ", sourceId='" + sourceId + '\'' +
                ", marketId='" + marketId + '\'' +
                ", brandId='" + brandId + '\'' +
                ", credentials='" + credentials + '\'' +
                ", owned=" + owned +
                ", topic='" + topic + '\'' +
                ", originalTokens=" + originalTokens +
                ", artificialTokens=" + artificialTokens +
                ", likeCount='" + likeCount + '\'' +
                ", commentCount='" + commentCount + '\'' +
                ", shareCount='" + shareCount + '\'' +
                ", article='" + article + '\'' +
                ", isHidden=" + isHidden +
                ", extraWeight=" + extraWeight +
                ", createdAt=" + createdAt +
                ", active='" + active + '\'' +
                ", relevanceScore=" + relevanceScore +
                '}';
    }

    @Override
    public int compareTo(FBPost compareTrend) {
        return Double.compare(compareTrend.getRowKey(), this.getRowKey());
    }
}

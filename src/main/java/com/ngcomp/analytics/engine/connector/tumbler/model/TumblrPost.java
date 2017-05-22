package com.ngcomp.analytics.engine.connector.tumbler.model;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.ngcomp.analytics.engine.MyMain;
import com.ngcomp.analytics.engine.conn.JedisConnectionPool;
import com.ngcomp.analytics.engine.connector.common.model.Data;
import com.ngcomp.analytics.engine.connector.common.model.FBStat;
import com.ngcomp.analytics.engine.connector.common.model.Story;
import com.ngcomp.analytics.engine.util.Constants;
import com.ngcomp.analytics.engine.util.PortalUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ngcomp.analytics.engine.util.Constants.CF;

public class TumblrPost extends Data {
    private String platformId;
	private Long id;
	private String blog_name;
	private String post_url;
	private String format;
	private String date;
	private String slug;
	private Story story;
	private FBStat fbStat;
	private Date createdAt;
	private String text;
	private String source;
	private String source_url;
	private List<String> tags;
	private String postType;
	// Properties specific to answer posts starts
	private String askingName;
	private String askingURL;
	private String question;
	private String answer;
	// Properties specific to answer posts ends
	
	// Properties specific to audio posts starts
	private String caption;
	private Integer plays;
	private String album_art;
	private String artist;
	private String album;
	private String track_name;
	private String external_url;
	private Integer track_number;
	private Integer year;
	// Properties specific to audio posts ends
	
	// Properties specific to chat posts starts
		private String body;
	// Properties specific to chat posts ends


    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getCaption() {
		return caption;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public Integer getPlays() {
		return plays;
	}

	public void setPlays(Integer plays) {
		this.plays = plays;
	}

	public String getAlbum_art() {
		return album_art;
	}

	public void setAlbum_art(String album_art) {
		this.album_art = album_art;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getTrack_name() {
		return track_name;
	}

	public void setTrack_name(String track_name) {
		this.track_name = track_name;
	}

	public String getExternal_url() {
		return external_url;
	}

	public void setExternal_url(String external_url) {
		this.external_url = external_url;
	}

	public Integer getTrack_number() {
		return track_number;
	}

	public void setTrack_number(Integer track_number) {
		this.track_number = track_number;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getAskingName() {
		return askingName;
	}

	public String getAskingURL() {
		return askingURL;
	}

	public void setAskingURL(String askingURL) {
		this.askingURL = askingURL;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public void setAskingName(String askingName) {
		this.askingName = askingName;
	}

	public String getPostType() {
		return postType;
	}

	public void setPostType(String postType) {
		this.postType = postType;
	}

	public String getText() {
		return text;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
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

	public String getSource_url() {
		return source_url;
	}

	public void setSource_url(String source_url) {
		this.source_url = source_url;
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


    public Story getStory() {
		return story;
	}

	public void setStory(Story story) {
		this.story = story;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public FBStat getFbStat() {
		return fbStat;
	}

	public void setFbStat(FBStat fbStat) {
		this.fbStat = fbStat;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBlog_name() {
		return blog_name;
	}

	public void setBlog_name(String blog_name) {
		this.blog_name = blog_name;
	}

	public String getPost_url() {
		return post_url;
	}

	public void setPost_url(String post_url) {
		this.post_url = post_url;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "TumblrPost [blog_name=" + blog_name + ", post_url=" + post_url
				+ ", format=" + format + ", slug=" + slug + "]";
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
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
		quals[5] = "post"; vals[5] = gson.toJson(this, TumblrPost.class);
		quals[6] = "originalTokens"; vals[6] = StringUtils.join(this.getOriginalTokens(), ",");
		quals[7] = "artificialTokens"; vals[7] = StringUtils.join(this.getArtificialTokens(), ",");

		quals[8] = "likeCount"; vals[8] = String.valueOf(this.getLikeCount());
		quals[9] = "commentCount"; vals[9] = String.valueOf(this.getCommentCount());
		quals[10] = "shareCount"; vals[10] = String.valueOf(this.getShareCount());
		quals[11] = "article"; vals[11] = this.getStory() != null ? this.getStory().getActualStory(): this.getBlog_name();
		quals[12] = "createdAt"; vals[12] = String.valueOf(this.getCreatedAt());
		quals[13] = "extraWeight"; vals[13] = String.valueOf(this.getExtraWeight());

		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("quals", quals);
		map.put("vals", vals);

		return map;
	}

	public Long getRowKey() {
		
		return Long.MAX_VALUE - createdAt.getTime();
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

        String storyS = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("article")));
        if(storyS!=null){
            try{
                    this.setStory(gson.fromJson(storyS, Story.class));

            }catch(Exception ex){
                Story story = new Story();
                story.setActualStory(storyS);
                 this.setStory(story);
            }
        }

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
        if(Strings.isNullOrEmpty(this.likeCount) || "null".equals(this.likeCount)){
            this.likeCount = "0";
        }

        if(Strings.isNullOrEmpty(this.commentCount) || "null".equals(this.commentCount)){
            this.commentCount = "0";
        }

        if(Strings.isNullOrEmpty(this.shareCount)  || "null".equals(this.shareCount) ){
            this.shareCount = "0";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TumblrPost)) return false;

        TumblrPost that = (TumblrPost) o;

        if (blog_name != null ? !blog_name.equals(that.blog_name) : that.blog_name != null) return false;
        if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (fbStat != null ? !fbStat.equals(that.fbStat) : that.fbStat != null) return false;
        if (format != null ? !format.equals(that.format) : that.format != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (post_url != null ? !post_url.equals(that.post_url) : that.post_url != null) return false;
        if (slug != null ? !slug.equals(that.slug) : that.slug != null) return false;
        if (story != null ? !story.equals(that.story) : that.story != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (blog_name != null ? blog_name.hashCode() : 0);
        result = 31 * result + (post_url != null ? post_url.hashCode() : 0);
        result = 31 * result + (format != null ? format.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (slug != null ? slug.hashCode() : 0);
        result = 31 * result + (story != null ? story.hashCode() : 0);
        result = 31 * result + (fbStat != null ? fbStat.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        return result;
    }
}

package com.ngcomp.analytics.engine.connector.bing.model;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.ngcomp.analytics.engine.MyMain;
import com.ngcomp.analytics.engine.conn.JedisConnectionPool;
import com.ngcomp.analytics.engine.connector.common.model.Data;
import com.ngcomp.analytics.engine.connector.common.model.FBStat;
import com.ngcomp.analytics.engine.connector.common.model.Story;
import com.ngcomp.analytics.engine.util.Constants;
import com.ngcomp.analytics.engine.util.PortalUtils;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import net.sf.json.JSONObject;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import redis.clients.jedis.Jedis;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static com.ngcomp.analytics.engine.util.Constants.CF;

/**
 * 
 * @author dprasad
 */
public class BingData extends Data {
    private static final Logger logger = Logger.getLogger(BingData.class);

	private String description;
	private Date createdAt;


    private String platformId;
	private String url;
	private String id;
	private String title;
	private String source;
	private Story story;

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

    public String getType(){
        return super.getType();
    }

    public void setType(String type){
        super.setType(type);
    }


    public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getSourceID(){
        return super.getSourceId();
    }

    public String getMarketID(){
        return super.getMarketId();
    }

    public String getBrandID(){
        return super.getBrandId();
    }


    public Double getRelevanceScore(){
        return relevanceScore;
    }

    public String getStoryId() throws DatatypeConfigurationException, ParseException {
        return String.valueOf(getHBKey());
    }

    public Set<String> getOriginalTokens(){
        return super.getOriginalTokens();
    }

    private Double relevanceScore;

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


    public void init(Result result) throws XmlRpcException, BoilerpipeProcessingException, IOException, InvocationTargetException, IllegalAccessException {

        Gson gson = new Gson();

        JSONObject jsonO = PortalUtils.getJSONObject(result.getFamilyMap(Bytes.toBytes("cf")));

        this.setId     (jsonO.getString("id"));

        this.setBrandId(jsonO.getString("brandId"));
//
        if(jsonO.containsKey("commentCount")){
            String s = jsonO.getString("commentCount");
            if(!Strings.isNullOrEmpty(s)){
                this.setCommentCount(s);
            }
        }
//
        this.setDate       (jsonO.getString("createdAt"));
        this.setDescription(jsonO.getString("description"));

        String extraWeight = jsonO.getString("extraWeight");
        if(!Strings.isNullOrEmpty(extraWeight)){
            this.setExtraWeight(Double.valueOf(extraWeight));
        }

        if(jsonO.containsKey("fbStat")){
            String fbStat = jsonO.getString("fbStat");
            if(fbStat!=null){
                this.setFbStat(gson.fromJson(fbStat, FBStat.class));
            }
        }

        if(jsonO.containsKey("id")) this.setId(jsonO.getString("id"));

        this.setMarketId(jsonO.getString("marketId"));

        String tokens = jsonO.getString("originalTokens");
        if(!Strings.isNullOrEmpty(tokens)){
            JSONArray jsonArray = (JSONArray) JSONValue.parse(tokens);
            this.setOriginalTokens(PortalUtils.getTokenSet(jsonArray));
        }

        if(jsonO.containsKey("owned"))this.setOwned(Boolean.valueOf((String)jsonO.get(("owned"))));


        if(jsonO.containsKey("shareCount")){
            String s = jsonO.getString("shareCount");
            if(!Strings.isNullOrEmpty(s)){
                this.setShareCount(s);
            }
        }
        this.setSourceId(jsonO.getString("sourceId"));


        this.setTopic(jsonO.getString("topic"));
//        this.setPost(jsonO.getString("post"));

        if(jsonO.containsKey("artificialTokens")){
            tokens = Bytes.toString     (result.getValue(Bytes.toBytes(CF), Bytes.toBytes("artificialTokens")));
            if(!Strings.isNullOrEmpty(tokens)){
                JSONArray jsonArray = (JSONArray)JSONValue.parse(tokens);
                this.setArtificialTokens(PortalUtils.getTokenSet(jsonArray));
            }
        }

        if(jsonO.containsKey("likeCount")){
            String s = jsonO.getString("likeCount");
            if(!Strings.isNullOrEmpty(s)){
                this.setLikeCount(s);
            }
        }


//        String  story = jsonO.getString("story");
//        if(story!=null){
//            this.setStory(gson.fromJson(story, Story.class));
//        }

        try{
            this.setRelevanceScore();
        }catch (NullPointerException npex){
            logger.error(npex.getMessage());
            this.relevanceScore = 0.0;
        }

    }


    public long getHBKey() throws DatatypeConfigurationException, ParseException {
        return Long.MAX_VALUE - this.getLongTime();
    }

    final String pattern       = "yyyy-MM-dd'T'hh:mm:ss";
    final SimpleDateFormat parser = new SimpleDateFormat(pattern);

    public Long getLongTime() throws DatatypeConfigurationException, ParseException {

        if(this.getCreatedAt()!=null){
            return this.createdAt.getTime();
        }else{
            if(!Strings.isNullOrEmpty(this.getDate())){
                if(this.getDate().contains(":")){
                    return parser.parse(this.getDate()).getTime();
                }else{
                    return Long.valueOf(this.getDate());
                }
            }else{
                return null;
            }
        }
    }

	public String getRowKey() {
		return String.valueOf(Long.MAX_VALUE - this.createdAt.getTime());
	}

	public Story getStory() {
		return story;
	}

	public void setStory(Story story) {
		this.story = story;
	}

    public void setStory() {
        if(this.story == null && this.description!=null){
            Story story = new Story();
            story.setActualStory(this.description);
            this.story = story;
        }
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

    @Override
    public String toString() {
        return "BingData{" +
                "description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", url='" + url + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", source='" + source + '\'' +
                ", story=" + story +
                ", relevanceScore=" + relevanceScore +
                ", pattern='" + pattern + '\'' +
                ", parser=" + parser +
                '}';
    }
}

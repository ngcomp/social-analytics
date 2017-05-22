package com.ngcomp.analytics.engine.model;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.ngcomp.analytics.engine.util.PortalUtils;
import com.restfb.Facebook;
import com.restfb.types.FacebookType;
import com.restfb.types.User;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.ngcomp.analytics.engine.util.Constants.CF;

public class FBFeed extends FacebookType {

	// id,message,type,comments,likes,shares,createdAt,from,application,privacy,is_hidden,source,
	// object_id,link&since=$since&until=$until&limit=5000", array("access_token"=>$source->credentials));

    private String platformId;
    private Source sourceO;
    private String topic;
    private String attribution;
    private String sourceId;
    private Double relevanceScore;

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Double getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(Double relevanceScore) {
        this.relevanceScore = relevanceScore;
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
            JSONArray jsonArray = (JSONArray) JSONValue.parse(tokens);
            this.setOriginalTokens(PortalUtils.getTokenSet(jsonArray));
        }

        tokens = Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("artificialTokens")));
        if(!Strings.isNullOrEmpty(tokens)){
            JSONArray jsonArray = (JSONArray)JSONValue.parse(tokens);
            this.setArtificialTokens(PortalUtils.getTokenSet(jsonArray));
        }
    }


    @Facebook("id")
	private String id;
	
	@Facebook("message")
	private String message ;
	
	@Facebook("type")
	private String type ;
	
	@Facebook("comments")
	private List<FBComment> comments ;
	
	@Facebook("likes")
	private List<FBLikes> likes ;
	
	@Facebook("shares")
	private FBShares shares ;
	
	@Facebook("created_time")
	private String createdAt;
	
	@Facebook("from")
	private FBFrom from ;
	
	@Facebook("application")
	private FBApplication application ;
	
	@Facebook("privacy")
	private FBPrivacy privacy ;
	
	// is_hidden
	@Facebook("source")
	private String source ;

    @Facebook
    private User me;

    @Facebook("object_id")
	private String object_id ;
	
	@Facebook("link")
	private String link ;

    private String media;
    private String mediaType;
    private String url;
    private String title;

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public Set<String> getOriginalTokens() {
        return originalTokens;
    }

    public void setOriginalTokens(Set<String> originalTokens) {
        this.originalTokens = new HashSet<String>();
        this.originalTokens = originalTokens;
    }

    public Set<String> getArtificialTokens() {
        return artificialTokens;
    }

    public void setArtificialTokens(Set<String> artificialTokens) {
        this.artificialTokens = artificialTokens;
    }

    private Set<String> originalTokens;
    private Set<String> artificialTokens;

    public String getAttribution() {
        return attribution;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    private String owned;

    public String getOwned() {
        return owned;
    }

    public void setOwned(String owned) {
        this.owned = owned;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMedia() {
        return media;
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

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public FBFrom getFrom() {
		return from;
	}
	public void setFrom(FBFrom from) {
		this.from = from;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getObject_id() {
		return object_id;
	}
	public void setObject_id(String object_id) {
		this.object_id = object_id;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}

	public List<FBComment> getComments() {
		return comments;
	}
	public void setComments(List<FBComment> comments) {
		this.comments = comments;
	}
	public List<FBLikes> getLikes() {
		return likes;
	}
	public FBShares getShares() {
		return shares;
	}
	public FBApplication getApplication() {
		return application;
	}
	public void setApplication(FBApplication application) {
		this.application = application;
	}
	public void setLikes(List<FBLikes> likes) {
		this.likes = likes;
	}
	public void setShares(FBShares shares) {
		this.shares = shares;
	}
	public FBPrivacy getPrivacy() {
		return privacy;
	}
	public void setPrivacy(FBPrivacy privacy) {
		this.privacy = privacy;
	}

    public Source getSourceO() {
        return sourceO;
    }

    public void setSourceO(Source sourceO) {
        this.sourceO = sourceO;
    }

    public User getMe() {
        return me;
    }

    public void setMe(User me) {
        this.me = me;
    }

    @Override
	public String toString() {
		return "FBFeed [id=" + id + ", message=" + message + ", type=" + type
				+ ", comments=" + comments + ", likes=" + likes + ", shares="
				+ shares + ", createdAt=" + createdAt + ", from=" + from
				+ ", application=" + application + ", privacy=" + privacy
				+ ", source=" + source + ", object_id=" + object_id + ", link="
				+ link + "]";
	}
	
	
	
	
}

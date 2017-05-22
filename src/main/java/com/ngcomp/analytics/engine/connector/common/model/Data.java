package com.ngcomp.analytics.engine.connector.common.model;

import com.google.common.base.Strings;
import com.ngcomp.analytics.engine.util.PortalUtils;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import org.apache.xmlrpc.XmlRpcException;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Parent class for all the DTO to accomodate the common fields.
 * 
 * @author dprasad
 * 
 */
public class Data {
	@Override
	public String toString() {
		return "Data [fbStat=" + fbStat + "]";
	}

    protected String media;
    protected String mediaType;
    protected String title;
    protected String url;
    private String description;
    private String type;
	private String credentials;
	private String topic;
	private boolean owned;
	private String brandId;
	private String marketId;
	protected String sourceId;
	private Set<String> originalTokens;
	private Set<String> artificialTokens;
	private Boolean isHidden;
	protected Double extraWeight = 0.0;
	private FBStat fbStat;
	private BitlyInfo bitlyInfo;
	protected String likeCount;
	protected String commentCount;
	protected String shareCount;
	protected Double relevanceScore;
	protected String date;
	private String attribution;
	
    public String getAttribution() {
		return attribution;
	}

	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
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

    public BitlyInfo getBitlyInfo() {
		return bitlyInfo;
	}
	
	public void setBitlyInfo(BitlyInfo bitlyInfo) {
		this.bitlyInfo = bitlyInfo;
	}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Double getRelevanceScore() {
		return relevanceScore;
	}

	public void setRelevanceScore(Double relevanceScore) {
		this.relevanceScore = relevanceScore;
	}

	private String post;

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(String likeCount) {
		if (Strings.isNullOrEmpty(likeCount) || likeCount.trim().equals("null")) {
			this.likeCount = "0";
		} else {
			this.likeCount = likeCount;
		}

	}

	public String getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(String commentCount) {
		this.commentCount = commentCount;
	}

	public String getShareCount() {
		return shareCount;
	}

	public void setShareCount(String shareCount) {
		this.shareCount = shareCount;
	}

	public FBStat getFbStat() {
		return fbStat;
	}

	public void setFbStat(FBStat fbStat) {
		this.fbStat = fbStat;
	}

	public Boolean getIsHidden() {
		return isHidden;
	}

	public void setIsHidden(Boolean isHidden) {
		this.isHidden = isHidden;
	}

	public Double getExtraWeight() {
		return extraWeight;
	}

	public void setExtraWeight(Double extraWeight) {
		this.extraWeight = extraWeight;
	}

	public Set<String> getOriginalTokens() {
		return originalTokens;
	}

    public void setOriginalTokens(List<String> originalTokens) {
        this.originalTokens = new HashSet<String>();
        this.originalTokens.addAll(originalTokens);
        this.originalTokens.add(this.topic);
    }

	public void setOriginalTokens(Set<String> originalTokens) {
		this.originalTokens = originalTokens;
	}

	public void setOriginalTokens(String message) throws IOException, XmlRpcException, BoilerpipeProcessingException {
        this.originalTokens = new HashSet<String>();
        if(!Strings.isNullOrEmpty(message)){
            this.originalTokens.addAll(PortalUtils.getKeywords(message));
        }
        this.originalTokens.add(this.topic);
	}

	public Set<String> getArtificialTokens() {
		return artificialTokens;
	}

    public void setArtificialTokens(String message) throws IOException, XmlRpcException, BoilerpipeProcessingException {
        this.artificialTokens = new HashSet(PortalUtils.getKeywords(message));
    }


    public void setArtificialTokens(Set<String> artificialTokens) {
		this.artificialTokens = artificialTokens;
	}

    public void setArtificialTokens(List<String> originalTokens) {
        this.artificialTokens = new HashSet<String>();
        this.artificialTokens.addAll(originalTokens);
    }


    public String getCredentials() {
		return credentials;
	}

	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public Boolean getOwned() {
		return owned;
	}

	public void setOwned(boolean owned) {
		this.owned = owned;
	}

	public String getBrandId() {
		return brandId;
	}

	public String getMarketId() {
		return marketId;
	}

	public void setMarketId(String marketId) {
		this.marketId = marketId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

    public boolean isOwned() {
        return owned;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public Boolean getHidden() {
        return isHidden;
    }

    public void setHidden(Boolean hidden) {
        isHidden = hidden;
    }
}

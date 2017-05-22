package com.ngcomp.analytics.engine.model;

public class Source {

	private String id ;
	private String marketId ;
	private String brandId ;
	private String platform ;
	private String identifier ;
	private String credential ;
	private String owned;
	private String priority;
	private String topic;
	private String ts;
	private String attribution;

    public Source() {
    }

    /**
     *
     * @param id
     * @param marketId
     * @param brandId
     * @param platform
     * @param identifier
     * @param credential
     * @param owned
     * @param priority
     * @param topic
     * @param ts
     * @param attribution
     */
	public Source(String id, String marketId, String brandId, String platform,
                  String identifier, String credential, String owned,
                  String priority, String topic, String ts, String attribution) {
		super();
		this.id = id;
		this.marketId = marketId;
		this.brandId = brandId;
		this.platform = platform;
		this.identifier = identifier;
		this.credential = credential;
		this.owned = owned;
		this.priority = priority;
		this.topic = topic;
		this.ts = ts;
		this.attribution = attribution;
	}
	
	
	public String getId() {
		return id;
	}


	public String getMarketId() {
		return marketId;
	}


	public String getBrandId() {
		return brandId;
	}


	public String getPlatform() {
		return platform;
	}


	public String getIdentifier() {
		return identifier;
	}


	public String getCredential() {
		return credential;
	}


	public String getOwned() {
		return owned;
	}


	public String getPriority() {
		return priority;
	}


	public String getTopic() {
		return topic;
	}


	public String getTs() {
		return ts;
	}


	public String getAttribution() {
		return attribution;
	}


	
	@Override
	public String toString() {
		return "Source [id=" + id + ", marketId=" + marketId + ", brandId="
				+ brandId + ", platform=" + platform + ", identifier="
				+ identifier + ", credential=" + credential + ", owned="
				+ owned + ", priority=" + priority + ", topic=" + topic
				+ ", ts=" + ts + ", attribution=" + attribution + "]";
	}
	
}

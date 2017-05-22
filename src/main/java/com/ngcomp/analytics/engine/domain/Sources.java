package com.ngcomp.analytics.engine.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "sources")
public class Sources implements Serializable {

	private static final long serialVersionUID = -329211977029380182L;

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "marketID")
	private Long marketID;

	@Column(name = "brandID")
	private Long brandID;

	@Column(name = "platform")
	private String platform;

	@Column(name = "identifier")
	private String identifier;

	@Column(name = "credentials")
	private String credentials;

	@Column(name = "owned")
	private int owned;

	@Column(name = "priority")
	private int priority;

	@Column(name = "topic")
	private String topic;

	@Column(name = "TS")
	private Long TS;

    @Column(name = "order_by_col")
    private String orderByCol;

    @Column(name = "attribution")
	private String attribution;

    @Column(name = "ignore_indexing")
    private Boolean ignoreIndexing;
    
    @Column(name = "stat_ts")
    private Long statTS;
    
    public Long getStatTS() {
		return statTS;
	}
    
    @Column(name = "stat_interval")
    private int statInterval;
    
    @Column(name = "data_interval")
    private int dataInterval;

	public int getStatInterval() {
		return statInterval;
	}

	public void setStatInterval(int statInterval) {
		this.statInterval = statInterval;
	}

	public int getDataInterval() {
		return dataInterval;
	}

	public void setDataInterval(int dataInterval) {
		this.dataInterval = dataInterval;
	}

	public void setStatTS(Long statTS) {
		this.statTS = statTS;
	}

	public Long getDataTS() {
		return dataTS;
	}

	public void setDataTS(Long dataTS) {
		this.dataTS = dataTS;
	}

	@Column(name = "data_ts")
    private Long dataTS;

    public Boolean getIgnoreIndexing() {
        return ignoreIndexing;
    }

    public void setIgnoreIndexing(Boolean ignoreIndexing) {
        this.ignoreIndexing = ignoreIndexing;
    }

    public Long getId() {
		return id;
	}

    public String getOrderByCol() {
        return orderByCol;
    }

    public void setOrderByCol(String orderByCol) {
        this.orderByCol = orderByCol;
    }

    public void setId(Long id) {
		this.id = id;
	}

	public Long getMarketID() {
		return marketID;
	}

	public void setMarketID(Long marketID) {
		this.marketID = marketID;
	}

	public Long getBrandID() {
		return brandID;
	}

	public void setBrandID(Long brandID) {
		this.brandID = brandID;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getCredentials() {
		return credentials;
	}

	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	public int getOwned() {
		return owned;
	}

	public void setOwned(int owned) {
		this.owned = owned;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public Long getTS() {
		return TS;
	}

	public void setTS(Long tS) {
		TS = tS;
	}

	public String getAttribution() {
		return attribution;
	}

	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}

}

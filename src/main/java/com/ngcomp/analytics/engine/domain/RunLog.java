package com.ngcomp.analytics.engine.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "api_logs")
public class RunLog implements Serializable {

	@Override
	public String toString() {
		return "RunLog [sourceID=" + sourceID + ", startTime="
				+ startTime + ", endTime=" + endTime + ", result=" + result
				+ "]";
	}

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@Column(name = "SOURCE_ID")
	private String sourceID;

	@Column(name = "START_TIME")
	private long startTime;

	@Column(name = "END_TIME")
	private long endTime;

	@Column(name = "RESULT")
	private String result;

	@Column(name = "CREATED_AT")
	private long createdAt;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Column(name = "MESSAGE")
    private String message;


    public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getSourceID() {
		return sourceID;
	}
	
	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}
	
	public String getResult() {
		return result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
}

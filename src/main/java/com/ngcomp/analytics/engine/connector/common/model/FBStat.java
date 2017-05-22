package com.ngcomp.analytics.engine.connector.common.model;

import java.io.Serializable;

public class FBStat implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -586997357146769314L;
	private long total_count;
	private long like_count;
	private long comment_count;
	private long share_count;
	private long click_count;

	@Override
	public String toString() {
		return "FBStat [total_count=" + total_count + ", like_count="
				+ like_count + ", comment_count=" + comment_count
				+ ", share_count=" + share_count + ", click_count="
				+ click_count + "]";
	}

	public long getTotal_count() {
		return total_count;
	}

	public void setTotal_count(long total_count) {
		this.total_count = total_count;
	}

	public long getLike_count() {
		return like_count;
	}

	public void setLike_count(long like_count) {
		this.like_count = like_count;
	}

	public long getComment_count() {
		return comment_count;
	}

	public void setComment_count(long comment_count) {
		this.comment_count = comment_count;
	}

	public long getShare_count() {
		return share_count;
	}

	public void setShare_count(long share_count) {
		this.share_count = share_count;
	}

	public long getClick_count() {
		return click_count;
	}

	public void setClick_count(long click_count) {
		this.click_count = click_count;
	}

}

package com.ngcomp.analytics.engine.connector.common.model;

/**
 * Common model object for all the content of a given link. Content here is an
 * object containing old text (HTML page as a string containing the Internet
 * links for the images) and the new text (HTML page as a string containing the
 * local system links for images).
 * 
 * @author dprasad
 * @since 0.1
 */
public class Content {

	private String contentLink;
	private String oldText;
	private String newText;

	public String getContentLink() {
		return contentLink;
	}

	public void setContentLink(String contentLink) {
		this.contentLink = contentLink;
	}

	public String getOldText() {
		return oldText;
	}

	public void setOldText(String oldText) {
		this.oldText = oldText;
	}

	public String getNewText() {
		return newText;
	}

	public void setNewText(String newText) {
		this.newText = newText;
	}
}

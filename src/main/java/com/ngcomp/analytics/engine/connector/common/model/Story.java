package com.ngcomp.analytics.engine.connector.common.model;

import java.util.List;

/**
 * Common model object for all story of a given link. Story here is an
 * object containing old text (HTML page as a string containing the Internet
 * links for the images) and the new text (HTML page as a string containing the
 * local system links for images) and a list of {@link  Content}.
 * 
 * @author dprasad
 * @since 0.1
 */
public class Story {

	private List<Content> linkContents;
	private String actualStory;
	private String newStory;
	private String storyLink;

	public String getNewStory() {
		return newStory;
	}

	public void setNewStory(String newStory) {
		this.newStory = newStory;
	}


	public String getActualStory() {
		return actualStory;
	}

	public void setActualStory(String actualStory) {
		this.actualStory = actualStory;
	}

	public String getStoryLink() {
		return storyLink;
	}

	public void setStoryLink(String storyLink) {
		this.storyLink = storyLink;
	}

	public List<Content> getLinkContents() {
		return linkContents;
	}

	public void setLinkContents(List<Content> linkContents) {
		this.linkContents = linkContents;
	}

    @Override
    public String toString() {
        return "Story{" +
                "linkContents=" + linkContents +
                ", actualStory='" + actualStory + '\'' +
                ", newStory='" + newStory + '\'' +
                ", storyLink='" + storyLink + '\'' +
                '}';
    }
}

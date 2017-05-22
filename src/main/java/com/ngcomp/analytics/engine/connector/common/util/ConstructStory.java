package com.ngcomp.analytics.engine.connector.common.util;

import com.ngcomp.analytics.engine.connector.common.model.Content;
import com.ngcomp.analytics.engine.connector.common.model.Story;
import org.apache.commons.validator.UrlValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Common utility class to construct a story, out of a link. 
 * @author dprasad
 * @since 0.1
 */
public class ConstructStory {

	/**
	 * Method to perform the following steps:
	 * <ul>
	 * 		<li>Get the content for the given link.</li>
	 * 		<li>If required, download and save all the images in the content to a new disk location.</li>
	 * 		<li>Modify the content and replace the Internet links of images with the local links</li>
	 * 		<li>Fetch the HTML pages for all the links in the content.</li>
	 * 		<li>If required, download and save all the images in each HTML page to a new disk location.</li>
	 * 		<li>Modify the text for each HTML page and and replace the Internet links of images with the local links</li>
	 *  	<li>Create a {@link Story} out of this and return the story.</li>
	 * </ul>
	 * @param storyLink
	 * @param downloadAndSaveImages
	 * @param newPath
	 */
	public static Story construct(boolean constructStory, String storyLink, boolean downloadAndSaveImages) {
		// download the content from storyLink
		Story story = new Story();
		if(!constructStory)
			return null;
		try{
			if (storyLink == null || storyLink.length() == 0)
				return null;

			String pageContentFromURL =null;
			if(isValidURL(storyLink)){
				story.setStoryLink(storyLink);
				pageContentFromURL = LinkParser.getPageContentFromURL(storyLink);
			}else{
				pageContentFromURL = storyLink;
			}
			
			if (pageContentFromURL != null && pageContentFromURL.length() > 0) {
				story.setActualStory(pageContentFromURL);
			}
			
			// search for images in the content
			List<String> imageLinks = LinkParser.getImageLinks(story.getActualStory());
			
			if(downloadAndSaveImages){
				story.setNewStory(downLoadAndSaveImagesPath(imageLinks, story.getActualStory()));
            }
			// search for links in the content

            //Commented by RAMP
//			List<String> linksInContent = LinkParser.getLinks(story.getActualStory());
//			story.setLinkContents(processLinksToGetContent(linksInContent, downloadAndSaveImages));
			
		}catch(Exception e){
			// do nothing 
		}
		
		return story;
	}
	
	
	private static List<Content> processLinksToGetContent(List<String> linksInContent, boolean downloadAndSaveImages){
		
		List<Content> contents = new ArrayList<Content>();
		
		Content linkContent = null;
		for (String link : linksInContent) {
			linkContent = new Content();
			linkContent.setContentLink(link);
			linkContent.setOldText(LinkParser.getPageContentFromURL(link));
			
			// search for images in the content
			List<String> imageLinks = LinkParser.getImageLinks(linkContent.getOldText());
			
			if(downloadAndSaveImages)
				linkContent.setNewText(downLoadAndSaveImagesPath(imageLinks, linkContent.getOldText()));
			
			contents.add(linkContent);
		}
		return contents;
	}
	
	private static String downLoadAndSaveImagesPath(List<String> imageLinks, String content){
		String newPath = null;
		//download and store the images and modify the content with new links
		for(String imageUrl :  imageLinks) {
			newPath = ImageDownloader.downloadAndSaveImage(imageUrl);
			content.replaceFirst(imageUrl, newPath);
		}
		return content;
	}
	
	private static boolean isValidURL(String urlString){
		String[] schemes = {"http","https", "ftp"};
		UrlValidator urlValidator = new UrlValidator(schemes);
		return urlValidator.isValid(urlString);
	}
}

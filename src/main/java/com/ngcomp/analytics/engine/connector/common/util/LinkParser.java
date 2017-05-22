package com.ngcomp.analytics.engine.connector.common.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Common Utility class to process links in a text.
 * @author dprasad
 * @since 0.1
 */
public class LinkParser {
	/**
	 * Method to get the anchor links from a string representing a page content.
	 * @param content
	 * @return list of links.
	 */
	public static List<String> getLinks(String content) {

		if (content != null && content.length() > 0) {
			
			// parse the document and fetch all the anchor tags
			Document doc = Jsoup.parse(content, "UTF-8");
			Elements elements = doc.select("a[href]");
			
			// find the href values for each link and add it to the list
			List<String> links = new LinkedList<String>();
			for (Element element : elements) {
				String link = element.attr("href");
				if(link!= null && (link.startsWith("http://") || (link.startsWith("http://"))))
					links.add(element.attr("href"));
			}
			return links;
		}
		return null;
	}

	/**
	 * Method to get the list of image links in a reverse sorted manner
	 * @param content
	 * @return imageLinks
	 */
	public static List<String> getImageLinks(String content) {
		if(content != null && content.length() > 0)
		{
			// parse the content into a document.
			Document doc = Jsoup.parse(content, "UTF-8");
			
			// fetch all the image src from teh img tag
			Elements elements = doc.select("img[src]");
			
			// prepare a list of image src links 
			List<String> imageLinks = new LinkedList<String>();
			for (Element element : elements) {
				imageLinks.add(element.attr("src"));
			}
			
			// sort is so that the smallest is at the top and the largest is at the bottom
			Collections.sort(imageLinks);
			
			// reverse the list so that the danger of overwriting the same URL again doesn't happen.
			// if we replace the smaller image links first, there is a chance of replacing the part of a bigger URL, so replace the big ones first.  
			Collections.reverse(imageLinks);
			return imageLinks;
			
		}else
			return null;
	}

	/**
	 * Method to replace the Internet URL with the local URLs for a list of image links and return the new content. 
	 * @param content
	 * @param oldLinks
	 * @param newLocation
	 * @return newContent
	 */
	public static String replaceImageLinks(String content,
                                           List<String> oldLinks, String newLocation) {

		String newContent = content;
		String newLink = null;
		for (String oldLink : oldLinks) {
			newLink = newLocation + oldLink.substring(oldLink.lastIndexOf("/"));
			newContent = newContent.replaceAll(oldLink, newLink);
		}
		return newContent;
	}


	/**
	 * Method to get the content of a page when a page URL is given.
	 * @param pageURL
	 * @return String representation of the page
	 */
	public static String getPageContentFromURL(String pageURL) {


		if (pageURL == null || pageURL.length() == 0){
			return null;
        }

		Document page = null;

		try {
			page = Jsoup.connect(pageURL).ignoreContentType(true).get();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (page != null && page.toString().length() > 0){
			return page.toString();
        }
		return null;
	}


}

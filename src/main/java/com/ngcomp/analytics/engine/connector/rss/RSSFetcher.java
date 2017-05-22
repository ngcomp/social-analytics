package com.ngcomp.analytics.engine.connector.rss;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RSSFetcher {

	private String urlstr = null ;
	
	
	public RSSFetcher(String url) {
		super();
		this.urlstr = url;
	}

	
	
	public List<SyndEntry> fetchData() {
		
		List<SyndEntry> entriesRSS = new ArrayList<SyndEntry>();
		
		try {
			URL url = new URL(urlstr);
	        HttpURLConnection httpcon = (HttpURLConnection)url.openConnection();
	        // Reading the feed
	        SyndFeedInput input = new SyndFeedInput();
	        SyndFeed feed = input.build(new XmlReader(httpcon));
	        List entries = feed.getEntries();
	        Iterator itEntries = entries.iterator();
	 
	        while (itEntries.hasNext()) {
	            SyndEntry entry = (SyndEntry) itEntries.next();
	            /*System.out.println("Title: " + entry.getTitle());
	            System.out.println("Link: " + entry.getLink());
	            System.out.println("Author: " + entry.getAuthor());
	            System.out.println("Publish Date: " + entry.getPublishedDate());
	            System.out.println("Description: " + entry.getDescription().getValue());
	            System.out.println();*/
	            
	            entriesRSS.add(entry) ;
	            
	        }
		} catch (Exception ex) {
			
			ex.printStackTrace() ;
			return null ;
			
		}
		
		return entriesRSS;
	}
	
}

package com.ngcomp.analytics.engine.connector.rss;

import com.ngcomp.analytics.engine.connector.common.util.ConstructBitly;
import com.ngcomp.analytics.engine.connector.common.util.ConstructStory;
import com.ngcomp.analytics.engine.connector.rss.model.Feed;
import com.ngcomp.analytics.engine.connector.rss.model.FeedMessage;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class RSSFeedParser {
	
	static final String TITLE = "title";
	static final String DESCRIPTION = "description";
	static final String CHANNEL   = "channel";
	static final String LANGUAGE  = "language";
	static final String COPYRIGHT = "copyright";
	static final String LINK      = "link";
	static final String AUTHOR    = "creator";
	static final String ITEM      = "item";
	static final String PUB_DATE  = "pubDate";
	static final String GUID      = "guid";

	final URL url;

	public RSSFeedParser(String feedUrl) throws MalformedURLException {
        this.url = new URL(feedUrl);
	}

	private Feed feed(){
		Feed feed = null;
		try {
			boolean isFeedHeader = true;
			// Set header values initially to the empty string
			String description = "";
			String title = "";
			String link = "";
			String language = "";
			String copyright = "";
			String author = "";
			String pubdate = "";
			String guid = "";

			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader
			InputStream in = read();
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			// Read the XML document
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement()) {
					String localPart = event.asStartElement().getName().getLocalPart();

					switch (localPart) {
					case ITEM:
						if (isFeedHeader) {
							isFeedHeader = false;
							feed = new Feed(title, link, description, language, copyright, pubdate);
						}
						event = eventReader.nextEvent();
						break;
					case TITLE:
						title       = getCharacterData(event, eventReader);
						break;
					case DESCRIPTION:
						description = getCharacterData(event, eventReader);
						break;
					case LINK:
						link      = getCharacterData(event, eventReader);
						break;
					case GUID:
						guid      = getCharacterData(event, eventReader);
						break;
					case LANGUAGE:
						language  = getCharacterData(event, eventReader);
						break;
					case AUTHOR:
						author    = getCharacterData(event, eventReader);
						break;
					case PUB_DATE:
						pubdate   = getCharacterData(event, eventReader);
						break;
					case COPYRIGHT:
						copyright = getCharacterData(event, eventReader);
						break;
					}
				} else if (event.isEndElement()) {
					if (event.asEndElement().getName().getLocalPart() == (ITEM)) {
						FeedMessage message = new FeedMessage();
						message.setAuthor(author);
						message.setDescription(description);
						message.setGuid(guid);
						message.setLink(link);
						message.setTitle(title);
						message.setFbStat(FBStatFeedMessage.getFBStats(link));
                        System.out.println(message.toString());
						feed.getMessages().add(message);
						event = eventReader.nextEvent();
						continue;
					}
				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
		return feed;
	}
	
	
	public Feed readFeed(boolean constructStory,boolean downloadAndSaveImages, String bitlyAccessToken) {
		Feed feed = feed();
        System.out.println(feed);
		for(FeedMessage message : feed.getMessages()){
			message.setStory(ConstructStory.construct(constructStory, message.getLink(), downloadAndSaveImages));
		}
		
		for(FeedMessage message : feed.getMessages()){
			message.setBitlyInfo(ConstructBitly.getBitlyInfo(message.getLink(), bitlyAccessToken));
		}
		
		return feed;
	}

	private String getCharacterData(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
		String result = "";
		event = eventReader.nextEvent();
		if (event instanceof Characters) {
			result = event.asCharacters().getData();
		}
		return result;
	}

	private InputStream read() {
		try {
			return url.openStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}

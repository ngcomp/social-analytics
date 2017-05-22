package com.ngcomp.analytics.engine.connector.bing;

import com.ngcomp.analytics.engine.connector.bing.model.BingData;
import com.ngcomp.analytics.engine.connector.common.BootStrapper;
import com.ngcomp.analytics.engine.connector.common.model.FBStat;
import com.ngcomp.analytics.engine.connector.common.model.Story;
import com.ngcomp.analytics.engine.connector.common.util.ConstructStory;
import com.ngcomp.analytics.engine.connector.common.util.JSONTransformer;
import com.ngcomp.analytics.engine.connector.rss.FBStatFeedMessage;
import net.billylieurance.azuresearch.AzureSearchNewsQuery;
import net.billylieurance.azuresearch.AzureSearchNewsResult;
import net.billylieurance.azuresearch.AzureSearchResultSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author dprasad
 */
public class BingSearch {

	private String accountKey;

	public BingSearch() {
		super();
	}

	public String getAccountKey() {
		return accountKey;
	}

	public void setAccountKey(String accountKey) {
		this.accountKey = accountKey;
	}

	public BingSearch(String accountKey) {
		super();
		this.accountKey = accountKey;
	}

	public List<BingData> bingSearch(boolean constructStory, final String query, boolean downloadAndSaveImages, String newPath) {
		List<BingData> list = new ArrayList<BingData>();
		try {
			AzureSearchNewsQuery bingClient = null;
			if (accountKey != null && accountKey.length() > 0)    {
				bingClient = BootStrapper.bootStrapBingAPI(accountKey);
            }else{
				bingClient = BootStrapper.bootStrapBingAPI();
            }

			bingClient.setQuery(query);
			bingClient.doQuery();

			AzureSearchResultSet<AzureSearchNewsResult> searchResults = bingClient.getQueryResult();

			for (AzureSearchNewsResult result : searchResults) {
				BingData data = getData(result);

				Story story = ConstructStory.construct(constructStory, data.getUrl(), downloadAndSaveImages);
				FBStat stat = FBStatFeedMessage.getFBStats(data.getUrl());
				data.setFbStat(stat);
				data.setStory(story);
				list.add(data);
			}
		} catch (Exception e) {
			// do nothing
		}
		return list;
	}

	public List<String> bingSearchAsJSON(boolean constructStory,
                                         final String query, boolean downloadAndSaveImages, String newPath) {
		AzureSearchNewsQuery bingClient = null;
		if (accountKey != null && accountKey.length() > 0)
			bingClient = BootStrapper.bootStrapBingAPI(accountKey);

		else
			bingClient = BootStrapper.bootStrapBingAPI();

		bingClient.setQuery(query);
		bingClient.doQuery();

		AzureSearchResultSet<AzureSearchNewsResult> searchResults = bingClient
				.getQueryResult();

		List<String> bingData = new ArrayList<String>();
		for (AzureSearchNewsResult result : searchResults) {
			BingData data = getData(result);
			Story story = ConstructStory.construct(constructStory, data.getUrl(), downloadAndSaveImages);
			data.setStory(story);
			String jsonString = JSONTransformer.getJSONString(data);
			bingData.add(jsonString);
		}

		return bingData;
	}

	private BingData getData(AzureSearchNewsResult result) {
		BingData data = new BingData();
		data.setId(result.getId());
		data.setTitle(result.getTitle());
		data.setSource(result.getSource());
		data.setCreatedAt(parsedDate(result.getDate()));
		data.setDescription(result.getDescription());
		data.setUrl(result.getUrl());
		return data;
	}

	// 2013-09-25T05:56:38Z
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:SS'Z'";
	
	private static Date parsedDate(String date){
		SimpleDateFormat s = new SimpleDateFormat(DATE_FORMAT);
		Date d = null;
		try {
			d = s.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}
	
	public static void main(String... strings) {

		AzureSearchNewsQuery aq = new AzureSearchNewsQuery();
		aq.setAppid("API_KEY");
		aq.setQuery("Oklahoma Sooners");

		aq.doQuery();
		AzureSearchResultSet<AzureSearchNewsResult> ars = aq.getQueryResult();
		for (AzureSearchNewsResult anr : ars) {

			System.out.println(anr.getTitle());
			System.out.println(anr.getSource());
			System.out.println(anr.getDate());
			System.out.println(anr.getDescription());
			System.out.println(anr.getUrl());
		}
	}

}

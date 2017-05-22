package com.ngcomp.analytics.engine.connector.fb;

import com.ngcomp.analytics.engine.model.FBFeed;
import com.ngcomp.analytics.engine.model.Source;
import com.restfb.DefaultJsonMapper;
import com.restfb.JsonMapper;
import com.restfb.exception.FacebookJsonMappingException;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonException;
import com.restfb.json.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class FBURLConnector {

	
	private Source source ;
	
	
	public FBURLConnector(Source source) {
		super();
		this.source = source;
	}


	public JsonObject getFeeds(String urlS) {

		URL url;
		try {

            url = new URL(urlS);

			StringBuffer data = new StringBuffer() ; 
	        URLConnection yc = url.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
	        String inputLine;

	        while ((inputLine = in.readLine()) != null) {
	            data.append(inputLine + "\n") ;
	        }

	        in.close();
	        
	        String json = data.toString() ;
	        JsonObject jsonObject = null;

            System.out.println(json);

	        try {
    	          jsonObject = new JsonObject(json);
	        } catch (JsonException e) {
	          throw new FacebookJsonMappingException("The connection JSON you provided was invalid: " + json, e);
	        }

	        // Pull out data
	        JsonArray jsonData = jsonObject.getJsonArray("data");
	        JsonMapper mapper = new DefaultJsonMapper() ;
	        
	        for (int i = 0; i < jsonData.length(); i++) {
	        	FBFeed feed = mapper.toJavaObject(jsonData.get(i).toString(), FBFeed.class);
	        }

            if(jsonObject.has("paging")){
                return jsonObject.getJsonObject("paging");
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		return null;
	}


	
	


}

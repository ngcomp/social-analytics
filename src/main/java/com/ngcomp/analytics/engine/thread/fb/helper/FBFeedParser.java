package com.ngcomp.analytics.engine.thread.fb.helper;

import com.google.common.base.Strings;
import com.ngcomp.analytics.engine.model.FBFeed;
import com.ngcomp.analytics.engine.model.Source;
import com.ngcomp.analytics.engine.model.Trend;
import com.restfb.DefaultJsonMapper;
import com.restfb.JsonMapper;
import org.hsqldb.lib.StringConverter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

/**
 * User: Ram Parashar
 * Date: 8/27/13
 * Time: 10:34 PM
 */
public class FBFeedParser { //implements Runnable{

    private Source source;
    private FBFeed fbFeed;

    public FBFeedParser(Source source, FBFeed fbFeed){
        this.source = source;
        this.fbFeed = fbFeed;
    }

    public Trend parsePost() {

        String pageID = source.getIdentifier();
        JsonMapper mapper = new DefaultJsonMapper() ;

        String url = null ;

        if(fbFeed.getLink() != null )
        {
            url = fbFeed.getLink();
        }
        else
        {
            String[] IDs = fbFeed.getId().split("_") ;
            String ID = IDs[1] ;
            url = "https://www.facebook.com/" + pageID +"/posts/" + ID ;
        }


        String attribution = null ;
        int owned = -1 ;

        if (fbFeed.getFrom() != null && fbFeed.getFrom().getId().equals(source.getIdentifier() )) {

            //this is the brand posting
            //attribution
            attribution = source.getAttribution() + " " +  fbFeed.getPrivacy().getDescription() ;
            owned = 1;
        }
        else {

            attribution = fbFeed.getFrom().getName() ;
            owned = 0;
        }

        //story
        String story = fbFeed.getMessage() ;
        String media = "";
        String media_type = "";
        String media_hash = "0";


        //media
        if (fbFeed.getObject_id() != null && fbFeed.getObject_id().length() > 0 ) {

            String urlObjectId = "https://graph.facebook.com/"  + fbFeed.getObject_id() + "?access_token=" + source.getCredential() ;
            String objData = getURLData(urlObjectId) ;

            FBFeed feed = null;
            try {
                if(!Strings.isNullOrEmpty(objData)){
                    feed = mapper.toJavaObject(objData, FBFeed.class);
                    media = feed.getSource()  ;
                    media_hash = calculateStringHash(media, "UTF-8") ;
                }
            } catch (Exception e) {

                e.printStackTrace();
            }

            if(feed!=null){
                media_type = checkFileType(feed.getSource()); //image | video
            }
        }
        else {

            media = "";
            media_type = "";
            media_hash = "0";
        }


        //sourceID
        String sourceID = source.getId() ;

        // MarketId
        String marketID = source.getMarketId() ;

        // BrandID
        String brandID = source.getBrandId() ;

        int comments = 0;
        int    likes = 0;
        int   shares = 0;
        long   score = 0 ;

        //score
        if (fbFeed.getLikes()    != null) likes = fbFeed.getLikes().size() ;

        if (fbFeed.getComments() != null) comments = fbFeed.getComments().size() ;

        if (fbFeed.getShares()   != null) shares = Integer.parseInt(fbFeed.getShares().getCount() );

        score = comments + likes + shares;

        //TS
        String TS = fbFeed.getCreatedAt() ;

        //privacy
        String privacy = fbFeed.getPrivacy().getDescription() ;

        //active
        int  active = 1;

        Trend trend = new Trend(url, story, media, media_type, media_hash, sourceID, marketID,
                brandID, attribution, null, score, TS, privacy, String.valueOf(active), String.valueOf(owned),
                String.valueOf(likes), String.valueOf(comments), String.valueOf(shares), null, getURLData(url));

        return trend;

    }

    private static final String checkFileType(String path) {
        URL url = null;
        BufferedReader reader = null;
        try{
            url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setDoOutput(true);

            connection.setReadTimeout(15000);
            connection.connect();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            Map<String, List<String>> map = connection.getHeaderFields();
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {

                if ( entry.getKey() != null && entry.getKey().equals("Content-Type")
                        &&  entry.getValue() != null
                        && entry.getValue().size() > 0
                        && entry.getValue().get(0).indexOf("video") != -1) {

                        return "video" ;
                }

            }

            reader.close() ;

        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        return "image" ;
    }

    private static final String calculateStringHash(String text, String encoding) throws Exception {
        if(text.getBytes(encoding)!= null){
            MessageDigest md = MessageDigest.getInstance("MD5");
            return StringConverter.byteToHex(md.digest(text.getBytes(encoding)));
        }else{
            return null;
        }
    }


    private static final String getURLData(String urlStr) {

        URL oracle;
        StringBuffer data = new StringBuffer() ;
        try {

            oracle = new URL(urlStr);
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                data.append(inputLine + "\n") ;
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data.toString() ;
    }
}

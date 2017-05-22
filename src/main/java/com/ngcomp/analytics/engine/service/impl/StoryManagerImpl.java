package com.ngcomp.analytics.engine.service.impl;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.ngcomp.analytics.engine.conn.HBaseProxy;
import com.ngcomp.analytics.engine.connector.bing.model.BingData;
import com.ngcomp.analytics.engine.connector.instagram.dto.InstagramDTO;
import com.ngcomp.analytics.engine.connector.pinterest.model.PinObject;
import com.ngcomp.analytics.engine.connector.rss.model.FeedMessage;
import com.ngcomp.analytics.engine.connector.tumbler.model.TumblrBlog;
import com.ngcomp.analytics.engine.connector.tumbler.model.TumblrPost;
import com.ngcomp.analytics.engine.connector.twitter.model.Tweet;
import com.ngcomp.analytics.engine.model.Trend;
import com.ngcomp.analytics.engine.service.StoryManager;
import com.ngcomp.analytics.engine.util.PortalUtils;
import com.ngcomp.analytics.engine.web.hb.SourcesDAO;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * User: Ram Parashar
 * Date: 9/5/13
 * Time: 7:56 AM
 */

@Controller("storyManager")
public class StoryManagerImpl implements StoryManager {

    private static final Logger logger = Logger.getLogger(StoryManager.class);

    @Autowired
    @Qualifier("sourcesDAO")
    SourcesDAO sourcesDAO;


    /**
     *
     * @param sourceId
     * @param storyId
     * @throws IOException
     */
    @Override
    public void hideStory(String sourceId, String storyId) throws IOException {

        HBaseProxy hBaseProxy = HBaseProxy.getInstance();
        HTable         hTable = hBaseProxy.getHTable(sourceId);
        try{
            Get               get = new Get(Bytes.toBytes(storyId));

            Result result = hTable.get(get);

            JSONObject jsonO = PortalUtils.getJSONObject(result.getFamilyMap(Bytes.toBytes("cf")));
            jsonO.put("is_hidden", true);

            Map<String, String[]> map = PortalUtils.getHBaseKeyValMap(jsonO);

            hBaseProxy.put(hTable, storyId,  sourceId, map.get("quals"), map.get("vals"));

        }finally{
            hTable.close();
        }
    }


    /**
     *
     * @param sourceId
     * @param storyId
     * @throws IOException
     */
    @Override
    public void unHideStory(String sourceId, String storyId) throws IOException {
        HBaseProxy hBaseProxy = HBaseProxy.getInstance();
        HTable         hTable = hBaseProxy.getHTable(sourceId);
        try{
            Get               get = new Get(Bytes.toBytes(storyId));

            Result result = hTable.get(get);

            JSONObject jsonO = PortalUtils.getJSONObject(result.getFamilyMap(Bytes.toBytes("cf")));
            jsonO.put("is_hidden", false);

            Map<String, String[]> map = PortalUtils.getHBaseKeyValMap(jsonO);

            hBaseProxy.put(hTable, storyId,  sourceId, map.get("quals"), map.get("vals"));

        }finally{
            hTable.close();
        }
    }


    /**
     *
     * @param sourceId
     * @param storyId
     * @param weight
     * @throws IOException
     */
    @Override
    public void voteUpStory(String sourceId, String storyId, String weight) throws IOException {
        HBaseProxy hBaseProxy = HBaseProxy.getInstance();
        HTable         hTable = hBaseProxy.getHTable(sourceId);
        try{
            Get               get = new Get(Bytes.toBytes(storyId));

            Result result = hTable.get(get);

            JSONObject jsonO = PortalUtils.getJSONObject(result.getFamilyMap(Bytes.toBytes("cf")));
            if(jsonO.containsKey("relevanceScore")){
                jsonO.put("relevanceScore", jsonO.getDouble("relevanceScore"));
            }else{
                jsonO.put("relevanceScore", new Double(weight));
            }
            Map<String, String[]> map = PortalUtils.getHBaseKeyValMap(jsonO);

            hBaseProxy.put(hTable, storyId,  sourceId, map.get("quals"), map.get("vals"));

        }finally{
            hTable.close();
        }
    }

    @Override
    public void voteDownStory(String sourceId, String storyId, String weight) throws IOException {

        HBaseProxy hBaseProxy = HBaseProxy.getInstance();
        HTable         hTable = hBaseProxy.getHTable(sourceId);
        try{
            Get               get = new Get(Bytes.toBytes(storyId));

            Result result = hTable.get(get);

            JSONObject jsonO = PortalUtils.getJSONObject(result.getFamilyMap(Bytes.toBytes("cf")));
            if(jsonO.containsKey("relevanceScore")){
                jsonO.put("relevanceScore", jsonO.getDouble("relevanceScore") - new Double(weight));
            }else{
                jsonO.put("relevanceScore", -(new Double(weight)));
            }
            Map<String, String[]> map = PortalUtils.getHBaseKeyValMap(jsonO);

            hBaseProxy.put(hTable, storyId,  sourceId, map.get("quals"), map.get("vals"));

        }finally{
            hTable.close();
        }
    }

    @Override
    public JSONObject relatedStories(String sourceId, String keyWords, String match,
                                     String sort    , Boolean owned,
                                     Long fromTime  , Long toTime,
                                     Integer results, String startKey) throws DatatypeConfigurationException, IOException, XmlRpcException, BoilerpipeProcessingException, InvocationTargetException, IllegalAccessException {


        JSONObject jsonO = new JSONObject();
        JSONArray  jsonA = new JSONArray();
        HTable hTable = null;
        try{

            Gson gson = new Gson();

            List<String> keyWordList = new ArrayList<String>();

            if(keyWords!=null){
                for(String keyword : keyWords.split(",")){
                    keyWordList.add(keyword.toLowerCase());
                }
            }

            HBaseProxy hBaseProxy = HBaseProxy.getInstance();
                           hTable = hBaseProxy.getHTable(sourceId);

            int counter = 0;
            ResultScanner scanner = hBaseProxy.getResultScanner(hTable, null, null, 100, 100);
            for (Result result : scanner) {

                JSONObject json = PortalUtils.getJSONObject(result.getFamilyMap(Bytes.toBytes("cf")));
                if(skipOwned(owned, json))continue;

                if(keyWordList.size() > 0) {
                    if(checkKeywords(jsonA, json, keyWordList, match)){
                        if(counter == 0){
                            jsonO.put("startKey"  , Bytes.toString(result.getRow()));
                        }
                        counter++;
                    }
                }else{
                    jsonA.add(PortalUtils.fixKeywordsInJSON(json));
                    counter ++;
                }
                if(counter >= results){
                    jsonO.put("endKey"  , Bytes.toString(result.getRow()));
                    break;
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            hTable.close();
        }
        jsonO.put("data"    , jsonA);

        return jsonO;
    }


    @Override
    public JSONObject allStories(String brandId, String marketId, String keywords, String match, String sort, Boolean owned, String active, Long fromTime, Long toTime, Integer results, String startKey) throws IOException {

        logger.debug("BrandId=>" + brandId + " MarketId=>" + marketId + " Keywords=>" + keywords + " Match=>" + match);

        List<String> keyWordList = new ArrayList<String>();

        if(keywords!=null){
            for(String keyword : keywords.split(",")){
                keyWordList.add(keyword.toLowerCase());
            }
        }
        System.out.println("Size--------------------->" + keyWordList.size());

        if(Strings.isNullOrEmpty(brandId) && Strings.isNullOrEmpty(marketId)){
            throw new InputMismatchException("One of the params BrandId or SourceId must be present.");
        }

        JSONArray       jsonA = new JSONArray();
        HBaseProxy hBaseProxy = HBaseProxy.getInstance();
        JSONObject    jsonO = new JSONObject();
        HTable       hTable = null;
        try{
            if(Strings.isNullOrEmpty(startKey) || startKey.equals("null")){
                startKey = null;
            }
            //String tName = String.valueOf(PortalUtils.getPastHourRoundedOff() - 3600);
                           hTable = hBaseProxy.getHTable(PortalUtils.getTableName());
            ResultScanner scanner = hBaseProxy.getResultScanner(hTable, startKey, null, 100, 100);
            int counter = 0;
            for (Result result : scanner) {

                JSONObject     json = PortalUtils.getJSONObject(result.getFamilyMap(Bytes.toBytes("cf")));

                if(skipOwned(owned, json)){
                    continue;
                }

                if(json.containsKey("brandId") && !Strings.isNullOrEmpty(brandId) && brandId!=null && json.getString("brandId").equals(brandId)){
                    if(checkKeywords(jsonA, json, keyWordList, match)){
                        if(counter == 0){
                            jsonO.put("startKey"  , Bytes.toString(result.getRow()));
                        }
                        counter++;
                    }
                }

                if(!Strings.isNullOrEmpty(marketId) && marketId!=null && json.getString("marketId").equals(marketId)){
                    if(checkKeywords(jsonA, json, keyWordList, match)){
                        if(counter == 0){
                            jsonO.put("startKey"  , Bytes.toString(result.getRow()));
                        }
                        counter++;
                    }
                }

                if(counter >= results){
                    jsonO.put("endKey"  , Bytes.toString(result.getRow()));
                    break;
                }
            }
        }catch (Exception ex){
             ex.printStackTrace();
        }finally {
            if(hTable!=null){
                hTable.close();
            }
        }
        jsonO.put("data", jsonA.toString());
        System.out.println(jsonO);

        return jsonO;
    }

    private boolean skipOwned(Boolean owned, JSONObject json){

        Boolean sOwned = null;
        if(json.containsKey("owned")){
            sOwned = Boolean.valueOf(json.getString("owned"));
        }

        //System.out.println("Owned => " + owned + " sOwned => " + sOwned);

        if (owned == null){
            return false;
        }else if(owned && sOwned!= null && sOwned){
            return false;
        }else if(!owned && sOwned!=null && !sOwned){
            return false;
        }else{
            return true;
        }
    }

    /**
     *
     * @param jsonA
     * @param expectedKeywords
     */
    private boolean checkKeywords(JSONArray jsonA, JSONObject json, List<String> expectedKeywords, String match){
        Boolean returnVal = false;
        if(!expectedKeywords.isEmpty()){
            try{
                if(json.containsKey("originalTokens")){
                    JSONArray words = json.getJSONArray("originalTokens");
                    Boolean ok = true;
                    if(okToAdd(expectedKeywords, words, match)){
                        jsonA.add(PortalUtils.fixKeywordsInJSON(json));
                        returnVal = true;
                    }
                }
                if(json.containsKey("artificialTokens")){
                    JSONArray words = json.getJSONArray("originalTokens");
                    Boolean ok = true;
                    if(okToAdd(expectedKeywords, words, match)){
                        jsonA.add(PortalUtils.fixKeywordsInJSON(json));
                        returnVal = true;
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
                returnVal =false;
            }
        }else{
            jsonA.add(PortalUtils.fixKeywordsInJSON(json));
            returnVal = true;
        }
        return returnVal;
    }


    private boolean okToAdd(List<String> expectedKeywords, JSONArray originalKeywords, String match ){

        System.out.println(originalKeywords);

        boolean flag = true;
        Set<String> originalKeywordList = new HashSet<String>();
        for(Object o : originalKeywords){
            originalKeywordList.add(((String)o).toLowerCase());
        }

        System.out.println("originalKeywordList=>" + originalKeywordList + " expectedKeywords=>" + expectedKeywords );

        if(match.equals("any")){
            originalKeywordList.retainAll(expectedKeywords);
            return originalKeywordList.size() > 0;
        }else{
            return originalKeywordList.containsAll(expectedKeywords);
        }
    }

    private JSONObject getTumblrPost(Result result){

        TumblrPost instagramDTO = new TumblrPost();
        instagramDTO.init(result);
        Gson gson = new Gson();
        String json = gson.toJson(instagramDTO);

        if(instagramDTO.getIsHidden()!= null && instagramDTO.getIsHidden()){
            return null;
        }else{
            return (JSONObject) JSONSerializer.toJSON(json);
        }

    }


    private JSONObject getTumblrBlog(Result result){

        TumblrBlog instagramDTO = new TumblrBlog();
        instagramDTO.init(result);
        Gson gson = new Gson();
        String json = gson.toJson(instagramDTO);

        if(instagramDTO.getIsHidden()!= null && instagramDTO.getIsHidden()){
            return null;
        }else{
            return (JSONObject) JSONSerializer.toJSON(json);
        }

    }


    private JSONObject getFeedMessage(Result result){

        FeedMessage instagramDTO = new FeedMessage();
        instagramDTO.init(result);
        Gson gson = new Gson();
        String json = gson.toJson(instagramDTO);

        if(instagramDTO.getIsHidden()!= null && instagramDTO.getIsHidden()){
            return null;
        }else{
            return (JSONObject) JSONSerializer.toJSON(json);
        }

    }

    private JSONObject getPinObject(Result result) throws XmlRpcException, BoilerpipeProcessingException, IOException {

        PinObject instagramDTO = new PinObject();
        instagramDTO.init(result);
        Gson gson = new Gson();
        String json = gson.toJson(instagramDTO);

        if(instagramDTO.getIsHidden()!= null && instagramDTO.getIsHidden()){
            return null;
        }else{
            return (JSONObject) JSONSerializer.toJSON(json);
        }

    }

    private JSONObject getInstagramDTO(Result result){

        InstagramDTO instagramDTO = new InstagramDTO();
        instagramDTO.init(result);
        Gson gson = new Gson();
        String json = gson.toJson(instagramDTO);

        if(instagramDTO.getIsHidden()!= null && instagramDTO.getIsHidden()){
            return null;
        }else{
            return (JSONObject) JSONSerializer.toJSON(json);
        }
    }


    /**
     * Bing
     * @param result
     * @return
     */
    private JSONObject getBing(Result result) throws XmlRpcException, BoilerpipeProcessingException, IOException, InvocationTargetException, IllegalAccessException {

        BingData bing = new BingData();
        bing.init(result);
        Gson gson = new Gson();
        String json = gson.toJson(bing);

        if(bing.getIsHidden()!= null && bing.getIsHidden()){
            return null;
        }else{
            return (JSONObject) JSONSerializer.toJSON(json);
        }
    }


    private JSONObject getTweet(Result result){
        Tweet tweet = new Tweet();
        tweet.init(result);
        Gson gson = new Gson();
        String json = gson.toJson(tweet);
        if(tweet.getIsHidden()!= null && tweet.getIsHidden()){
            return null;
        }else{
            return (JSONObject) JSONSerializer.toJSON(json);
        }
    }

    /**
     * Facebook....
     * @param result
     * @param owned
     * @return
     */
    private JSONObject getFB(Result result, Boolean owned){
        Trend   trend = new Trend();
        trend.init(result);

        JSONObject json = JSONObject.fromObject(trend);
        if(json.containsKey("keyValMap")){
            json.remove("keyValMap");
        }

        if(trend.getIsHidden()!= null && trend.getIsHidden()){
            return null;
        }

        if((owned && trend.getOwned().equals("1")) || (!owned && trend.getOwned().equals("0"))){
            return json;
        }else{
            return null;
        }
    }
}

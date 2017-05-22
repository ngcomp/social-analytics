package com.ngcomp.analytics.engine.service.impl;

import com.google.common.base.Strings;
import com.ngcomp.analytics.engine.conn.HBaseProxy;
import com.ngcomp.analytics.engine.service.KeyWordsManager;
import com.ngcomp.analytics.engine.util.PortalUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.util.*;

/**
 * User: Ram Parashar
 * Date: 9/5/13
 * Time: 8:58 AM
 */

@Service("keyWordsManager")
public class KeyWordManagerImpl implements KeyWordsManager {


    /**
     *
     * @param sourceId
     * @param storyId
     * @param keyWord
     * @throws IOException
     */
    @Override
    public void removeStoryKeyWord(String sourceId, String storyId, String keyWord) throws IOException {

//        System.out.println("SourceId => " + sourceId + " StoryId=>" + storyId + " Keyword=>" + keyWord);

        HBaseProxy hBaseProxy = HBaseProxy.getInstance();
        HTable         hTable = hBaseProxy.getHTable(sourceId);
        try{
            Get               get = new Get(Bytes.toBytes(storyId));

            Result result = hTable.get(get);

            JSONObject jsonO = PortalUtils.getJSONObject(result.getFamilyMap(Bytes.toBytes("cf")));

            JSONArray jsonA = jsonO.getJSONArray("artificialTokens");
            JSONArray newJsonA = new JSONArray();
            for(Object j : jsonA){
                String s = (String)j;
                if(s.equalsIgnoreCase(keyWord)){

                }else {
                    newJsonA.add(s);
                }
            }

            jsonO.put      ("artificialTokens", newJsonA);

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
     * @param keyWord
     * @throws IOException
     */
    @Override
    public void addStoryKeyWord(String sourceId, String storyId, String keyWord) throws IOException {


//        System.out.println("SourceId => " + sourceId + " StoryId=>" + storyId + " Keyword=>" + keyWord);

        HBaseProxy hBaseProxy = HBaseProxy.getInstance();
        HTable         hTable = hBaseProxy.getHTable(sourceId);
        try{
            Get               get = new Get(Bytes.toBytes(storyId));

            Result result = hTable.get(get);

            JSONObject jsonO = PortalUtils.getJSONObject(result.getFamilyMap(Bytes.toBytes("cf")));

            JSONArray jsonA = new JSONArray();
            if(!jsonO.containsKey("artificialTokens")){
                jsonA.addAll(Arrays.asList(keyWord.split(",")));
            }else{
                jsonA = jsonO.getJSONArray("artificialTokens");
                jsonA.addAll(Arrays.asList(keyWord.split(",")));
            }
            jsonO.put("artificialTokens", jsonA);

            Map<String, String[]> map = PortalUtils.getHBaseKeyValMap(jsonO);

            hBaseProxy.put(hTable, storyId,  sourceId, map.get("quals"), map.get("vals"));

        }finally{
            hTable.close();
        }
    }


    /**
     *
     *
     * @param sourceId
     * @param marketId
     * @param brandId
     * @param keyWord
     * @param results
     * @param startKey
     * @return
     * @throws DatatypeConfigurationException
     */
    //keyWords => POPULAR or RELATED
    @Override
    public JSONObject getPopularKeywords(String sourceId, String marketId, String brandId, String keyWord, Long results, String startKey) throws DatatypeConfigurationException, IOException {
        JSONObject jsonO = new JSONObject();
        //JSONArray  jsonA = new JSONArray();
        HTable    hTable = null;
        ResultScanner scanner = null;
//        Gson        gson = new Gson();
        int      counter = 0;

        List<String> keyWordList = new ArrayList<String>();
        if(!Strings.isNullOrEmpty(keyWord)){
            keyWordList.add(keyWord.toLowerCase());
        }

        Set keywordSet = new HashSet();

        if(!Strings.isNullOrEmpty(sourceId)){ //Fetch Sources for a particular Source.
            try{
                HBaseProxy hBaseProxy = HBaseProxy.getInstance();
                hTable = hBaseProxy.getHTable(sourceId);
                scanner = hBaseProxy.getResultScanner(hTable, startKey, null, 100, 100);
                for (Result result : scanner) {

                    JSONObject json = PortalUtils.getJSONObject(result.getFamilyMap(Bytes.toBytes("cf")));
                    //json.put("key", Bytes.toString(result.getRow()));

                    //MarketId not matching.
                    if(!Strings.isNullOrEmpty(marketId) && json.containsKey("marketId") && !marketId.equals(json.getString("marketId"))){
                        continue;
                    }

                    //BrandId not matching.
                    if(!Strings.isNullOrEmpty(brandId)  && json.containsKey("brandId") && !brandId.equals(json.getString("brandId"))){
                        continue;
                    }

                    if(this.checkKeywords(json, keyWordList, "any")) {
                        json = PortalUtils.fixKeywordsInJSON(json);
                        if(json.containsKey("keywords")){
                            keywordSet.addAll(json.getJSONArray("keywords"));
                        }

                        if(counter == 0){
                            jsonO.put("startKey"  , Bytes.toString(result.getRow()));
                        }
                        counter++;
                    }

                    jsonO.put("endKey"    , Bytes.toString(result.getRow()));

                    if(counter >= results){
                        break;
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }finally {
                if(scanner!= null){
                    scanner.close();
                }
                hTable.close();
            }

            //jsonO.put("data"    , jsonA);
        }else{ //Fetch Sources from Big Table.
            try{
                HBaseProxy hBaseProxy = HBaseProxy.getInstance();

                hTable = hBaseProxy.getHTable(PortalUtils.getTableName());
                scanner = hBaseProxy.getResultScanner(hTable, null, null, 100, 100);
                for (Result result : scanner) {

                    JSONObject json = PortalUtils.getJSONObject(result.getFamilyMap(Bytes.toBytes("cf")));
                    json.put("key", Bytes.toString(result.getRow()));

                    //MarketId not matching.
                    if(!Strings.isNullOrEmpty(marketId) && json.containsKey("marketId") && !marketId.equals(json.getString("marketId"))){
                        continue;
                    }

                    //BrandId not matching.
                    if(!Strings.isNullOrEmpty(brandId)  && json.containsKey("brandId") && !brandId.equals(json.getString("brandId"))){
                        continue;
                    }

                    if(this.checkKeywords(json, keyWordList, "any")) {
                        json = PortalUtils.fixKeywordsInJSON(json);
                        if(json.containsKey("keywords")){
                            keywordSet.addAll(json.getJSONArray("keywords"));
                        }
                        //jsonA.add(PortalUtils.fixKeywordsInJSON(json));

                        if(counter == 0){
                            jsonO.put("startKey"  , Bytes.toString(result.getRow()));
                        }
                        counter++;
                    }

                    jsonO.put("endKey"    , Bytes.toString(result.getRow()));

                    if(counter >= results){
                        break;
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }finally {
                if(scanner!= null){
                    scanner.close();
                }
                hTable.close();
            }
        }

        JSONArray jsonA = new JSONArray();
        for(Object keyword : keywordSet){
            jsonA.add(((String)keyword).toLowerCase());
        }
        jsonO.put("data"    , jsonA);

        return jsonO;
    }

    private boolean checkKeywords(JSONObject json, List<String> expectedKeywords, String match){
        Boolean returnVal = false;
        if(!expectedKeywords.isEmpty()){
            try{
                if(json.containsKey("originalTokens")){
                    JSONArray words = json.getJSONArray("originalTokens");
                    Boolean ok = true;
                    if(PortalUtils.okToAdd(expectedKeywords, words, match)){
                        returnVal = true;
                    }
                }
                if(json.containsKey("artificialTokens")){
                    JSONArray words = json.getJSONArray("artificialTokens");
                    Boolean ok = true;
                    if(PortalUtils.okToAdd(expectedKeywords, words, match)){
                        returnVal = true;
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
                returnVal =false;
            }
        }else{
            returnVal = true;
        }
        return returnVal;
    }
}



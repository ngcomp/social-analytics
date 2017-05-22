package com.ngcomp.analytics.engine.service.impl;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.ngcomp.analytics.engine.conn.HBaseProxy;
import com.ngcomp.analytics.engine.service.ImagesManager;
import com.ngcomp.analytics.engine.util.PortalUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Controller;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Ram Parashar
 * Date: 9/5/13
 * Time: 8:15 AM
 */
@Controller("imagesManager")
public class ImagesManagerImpl implements ImagesManager {

    /**
     *
     * @param sourceId
     * @param marketId
     * @param brandId
     * @param keywords
     * @param results
     * @param startKey
     * @return
     * @throws DatatypeConfigurationException
     * @throws IOException
     */
    @Override
    public JSONObject relatedImages(String sourceId, String marketId, String brandId, String keywords, Integer results, String startKey) throws DatatypeConfigurationException, IOException {

        JSONObject jsonO = new JSONObject();
        JSONArray  jsonA = new JSONArray();
        HTable    hTable = null;
        ResultScanner scanner = null;
        Gson        gson = new Gson();
        int      counter = 0;

        List<String> keyWordList = new ArrayList<String>();
        if(!Strings.isNullOrEmpty(keywords)){
            for(String k : keywords.split(","))
            keyWordList.add(k.toLowerCase());
        }

        if(!Strings.isNullOrEmpty(sourceId)){ //Fetch Sources for a particular Source.
            try{
                HBaseProxy hBaseProxy = HBaseProxy.getInstance();
                               hTable = hBaseProxy.getHTable(sourceId);
                              scanner = hBaseProxy.getResultScanner(hTable, startKey, null, 100, 100);
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
                    //JSONObject json, List<String> expectedKeywords, String match
                    if(this.checkKeywords(json, keyWordList, "any")) {
                        jsonA.add(PortalUtils.fixKeywordsInJSON(json));
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
            jsonO.put("data"    , jsonA);
        }else{ //Fetch Sources from Big Table.
            try{
                HBaseProxy hBaseProxy = HBaseProxy.getInstance();
                String tName = String.valueOf(PortalUtils.getPastHourRoundedOff() - 3600);
                               hTable = hBaseProxy.getHTable(tName + "_sbi");
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
                        jsonA.add(PortalUtils.fixKeywordsInJSON(json));
                        counter++;
                        if(counter == 0){
                            jsonO.put("startKey"  , Bytes.toString(result.getRow()));
                        }
                        jsonO.put("endKey"    , Bytes.toString(result.getRow()));
                    }

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
            jsonO.put("data"    , jsonA);
        }

        return jsonO;
    }


    /**
     *
     * @param json
     * @param expectedKeywords
     * @param match
     * @return
     */
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

package com.ngcomp.analytics.engine.util;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.ngcomp.analytics.engine.conn.HBaseProxy;
import com.ngcomp.analytics.engine.conn.JedisConnectionPool;
import com.ngcomp.analytics.engine.connector.common.model.Data;
import com.ngcomp.analytics.engine.domain.FBPost;
import com.ngcomp.analytics.engine.domain.RunLog;
import com.ngcomp.analytics.engine.model.FBFeed;
import com.ngcomp.analytics.engine.web.hb.HibernateConfig;
import com.ngcomp.analytics.engine.web.hb.JndiConfig;
import com.ngcomp.analytics.engine.web.hb.LogManagerDAO;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import redis.clients.jedis.Jedis;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.ngcomp.analytics.engine.util.Constants.CF;


/**
 * User: Ram Parashar
 * Date: 9/11/13
 * Time: 9:57 PM
 */
public class PortalUtils {

    private static final Logger logger = Logger.getLogger(PortalUtils.class);

	public static String SUCCESS_MSG = "SUCCESS";

    /**
     *
     * @return
     * @throws IOException
     */
    public static String getTableName() throws IOException {

        Long pastHour = PortalUtils.getPastHourRoundedOff();

        for(int counter = 0; counter < 48; counter ++ ){
            String tName = (pastHour - (counter * 3600)) + "_sbi";
            if(HBaseProxy.getInstance().getAdmin().tableExists(tName)){
                return tName;
            }else{
                continue;
            }
        }

        throw new InternalError("No Indexed data available, Please try later");
    }


    /**
     *
     * @param json
     * @return
     */
    public static JSONObject fixKeywordsInJSON(JSONObject json){
        JSONArray jsonArray = new JSONArray();
        Set keywords = new HashSet();
        if(json.containsKey("artificialTokens")){
            keywords.addAll(json.getJSONArray("artificialTokens"));
            json.remove("artificialTokens");
        }
        if(json.containsKey("originalTokens")){
            keywords.addAll(json.getJSONArray("originalTokens"));
            json.remove("originalTokens");
        }

        if(json.containsKey("fbStat")){
            if(json.containsKey("stats")){
                JSONObject k = (JSONObject) json.get("stats");
                k.put("fbStat", json.get("fbStat"));
                json.put("stats", k);
            }else{
                JSONObject k = new JSONObject();
                k.put("fbStat", json.get("fbStat"));
                json.put("stats", k);
            }
            json.remove("fbStat");
        }

        if(json.containsKey("bitlyInfo")){
            if(json.containsKey("stats")){
                JSONObject k = (JSONObject) json.get("stats");
                k.put("bitlyInfo", json.get("bitlyInfo"));
                json.put("stats", k);
            }else{
                JSONObject k = new JSONObject();
                k.put("bitlyInfo", json.get("bitlyInfo"));
                json.put("stats", k);
            }
            json.remove("bitlyInfo");
        }


        jsonArray.addAll(keywords);
        json.put("keywords", jsonArray);
        return json;
    }

    /**
     *
     * @param sourceId
     * @param result
     * @param message
     */
    public static void logMessage(LogManagerDAO logManager, String sourceId, String result, String message){

        RunLog log = new RunLog();
        log.setStartTime(System.currentTimeMillis());
        log.setEndTime(System.currentTimeMillis());
        log.setSourceID(sourceId);
        log.setResult(result);
        log.setMessage(message);
        log.setCreatedAt(System.currentTimeMillis());
        logManager.writeLog(log);
    }


    public static JSONObject exceptionAsJson(Exception e) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", e.getMessage());
        jsonObject.put("class", e.getClass().toString());

        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        jsonObject.put("cause", errors.toString());


//        if (e.getCause() != null) {
//
//            jsonObject.put("cause", e.getCause().toString());
//        }
        return jsonObject;
    }

    private static boolean refreshed = false;


    /**
     *
     */
    public static final synchronized void setRefreshed(){
        if(!refreshed){
            context.refresh();
            refreshed = true;
        }
    }


    static AnnotationConfigApplicationContext context = null;

	public static void api_logs(String sourceId, String message, long startTime, long endTime){

        try{
            if(!Strings.isNullOrEmpty(sourceId)){
                if(context == null)
                {
                    context = new AnnotationConfigApplicationContext();
                    context.register(JndiConfig.class, HibernateConfig.class);
                    setRefreshed();
                }

                LogManagerDAO logManager = context.getBean(LogManagerDAO.class);
                RunLog log = new RunLog();
                log.setStartTime(startTime);
                log.setEndTime(endTime);
                log.setSourceID(sourceId);
                log.setResult(message);
                log.setCreatedAt(System.currentTimeMillis());
                logManager.writeLog(log);
            }
        }catch (Exception ex){
            logger.info(exceptionAsJson(ex));
        }
	}
	
    public static String getUrl(String urlS){

        String[] tokens = urlS.split(" ");

        for(String s : tokens){
            if(s.startsWith("http")){
                return cleanUrl(s);
            }else if(s.startsWith("href")){
                String str = s.replaceAll("=","").replaceAll("\\\\", "").replaceAll("\"", "").replaceAll("href","");
                if(str.contains(">")){
                    str = str.substring(0, str.indexOf(">"));
                }
                return cleanUrl(str);

            }
        }
        return null;
    }

    public static String cleanUrl(String urlS){
        urlS = urlS.replaceAll("=","").replaceAll("\\\\", "").replaceAll("\"", "").replaceAll("href","");
        if(urlS.contains(">")){
            urlS = urlS.substring(0, urlS.indexOf(">"));
        }
        return  urlS;
    }


    public static String getMainImage(String url) throws IOException {
        String mainImageUrl = null;

        try{
            Document document = Jsoup.connect(url).get();

            Elements elements =document.getElementsByTag("IMG");


            int area = 0;

            for(int i=0; i<elements.size(); i++)
            {
                try{
                    String imgUrl = elements.get(i).attr("src");
                    InputStream inputStream = new URL(imgUrl).openStream();
                    ImageInfo s = new ImageInfo(inputStream);
                    if(s.getWidth() * s.getHeight() > area){
                        area = s.getWidth() * s.getHeight();
                        mainImageUrl = elements.get(i).attr("src");

                    }
                }catch(Exception ex){
                    logger.debug(PortalUtils.exceptionAsJson(ex));
                }
            }
        }catch(Exception ex){
            logger.debug(PortalUtils.exceptionAsJson(ex));
        }
        return mainImageUrl;
    }



    public static String getKeyNew(String id, Double relevanceScore, Long time, String sourceUrl){

        String key = null;

        if(relevanceScore == null ){
            key = String.valueOf(Long.MAX_VALUE);
        }else{
            Long score = (long)(relevanceScore.longValue()) * 1000000000;
            key = String.valueOf(Long.MAX_VALUE - score);
        }

        if(time != null){
            key += "##"  + time;
        }else if(id!=null && !Strings.isNullOrEmpty(id) && !"null".equals(id)){
            key += "##"  + id;
        }else{
            key += "##"  + sourceUrl;
        }

        System.out.println(key + " Score=>" + relevanceScore);
        return key;
    }


    /**
     *
     * @param id
     * @param relevanceScore
     * @return
     */
    public static String getKey(String id, Double relevanceScore, Long time){
        String key = null;

        if(relevanceScore == null ){
            key = String.valueOf(Long.MAX_VALUE);
        }else{
            Long score = (long)(relevanceScore.longValue()) * 1000000000;
            key = String.valueOf(Long.MAX_VALUE - score);
        }

        if(id == null){
            key = key + "##"  + time;
        }else{
            key = key + "##"  + id;
        }

        System.out.println(key + " " + relevanceScore);
        return key;
    }

    public static Map<String, String[]> getHBaseKeyValMap(JSONObject data){

        String[] keys = new String[data.keySet().size()];
        String[] vals = new String[data.keySet().size()];
        //System.out.println(data.toString());
        int counter = 0;
        for(Object keyO : data.keySet()){
            String key = (String)keyO;
            keys[counter] = key;
            //System.out.println(key);
            vals[counter] = data.get(key).toString();
        }

        Map<String, String[]> map = new HashMap<String, String[]>();
        map.put("quals", keys);
        map.put("vals" , vals);
        return map;
    }

    public static Map<String, String[]> getHBaseKeyValMap(FBFeed data){
        Gson gson = new Gson();

        JSONObject jsonO = JSONObject.fromObject(gson.toJson(data));

        System.out.println(jsonO.toString());

        gson = null;

        String[] quals = new String[jsonO.keySet().size()];
        String[] vals = new String[jsonO.keySet().size()];

        int counter = 0;
        for(Object obj : jsonO.keySet()){
            quals[counter] = (String)obj;
            vals[counter] = String.valueOf(jsonO.get(obj));
            counter++;
        }

        Map<String, String[]> map = new HashMap<String, String[]>();
        map.put("quals", quals);
        map.put("vals" , vals);

        return map;
    }

    public static Map<String, String[]> getHBaseKeyValMap(FBPost data){
        Gson gson = new Gson();



        JSONObject jsonO = JSONObject.fromObject(gson.toJson(data));
//        System.out.println(jsonO.toString());
        gson = null;

        String[] quals = new String[jsonO.keySet().size()];
        String[] vals = new String[jsonO.keySet().size()];

        int counter = 0;
        for(Object obj : jsonO.keySet()){
            quals[counter] = (String)obj;
            vals[counter] = String.valueOf(jsonO.get(obj));
            counter++;
        }

        Map<String, String[]> map = new HashMap<String, String[]>();
        map.put("quals", quals);
        map.put("vals" , vals);

        return map;
    }


    public static Map<String, String[]> getHBaseKeyValMap(Data data){
        Gson gson = new Gson();

        JSONObject jsonO = JSONObject.fromObject(gson.toJson(data));

//        System.out.println(jsonO.toString());

        gson = null;

        String[] quals = new String[jsonO.keySet().size()];
        String[] vals  = new String[jsonO.keySet().size()];


        int counter = 0;
        for(Object obj : jsonO.keySet()){
            quals[counter] = (String)obj;
            vals[counter] = String.valueOf(jsonO.get(obj));
            counter++;
        }

        Map<String, String[]> map = new HashMap<String, String[]>();
        map.put("quals", quals);
        map.put("vals" , vals);

        return map;
    }


    public static NavigableMap<byte[], byte[]> getKeyValMap(Result result){
        NavigableMap<byte[], byte[]> map = result.getFamilyMap(Bytes.toBytes("cf"));
        return map;
    }



    public static JSONArray getJSONArray(NavigableMap<byte[], byte[]> map){

        String data = Bytes.toString(map.get(Bytes.toBytes("data")));

        return JSONArray.fromObject(data);
    }


    /**
     *
     * @param result
     * @param json
     * @return
     */
    public static JSONArray getJSONArray(Result result, JSONObject json){

        String data = Bytes.toString(result.getFamilyMap(Bytes.toBytes("cf")).get(Bytes.toBytes("data")));

        JSONArray jsonA = JSONArray.fromObject(data);
        jsonA.add(json);
        return jsonA;
    }


    /**
     *
     * @param map
     * @return
     */
    public static JSONObject getJSONObject(NavigableMap<byte[], byte[]> map){

        JSONObject json = new JSONObject();
        for(byte[] key : map.keySet()){
            json.put(Bytes.toString(key), Bytes.toString(map.get(key)));
        }
        return json;
    }


    public static String getStory(String urlS){
        URL url;

        StringBuffer data = new StringBuffer() ;
        try {
            url = new URL(urlS);
            URLConnection yc = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                data.append(inputLine + "\n") ;
            }
            in.close();
        }catch(Exception ex){

        }
        return data.toString();
    }

    private static final Map<String, HTable> hBaseTableMap = new HashMap<String, HTable>();

    /**
     *
     * @param tableName
     * @return
     * @throws IOException
     */
    public static HTable fixHBaseFBTables(String tableName) throws IOException {
        HBaseProxy hBaseProxy = HBaseProxy.getInstance();
        if(!hBaseProxy.existsTable(tableName)){
            String[] colFamilies = {CF};
            hBaseProxy.createTable(tableName, colFamilies);
        }
        if(!hBaseTableMap.containsKey(tableName)){
            HTable hTable = new HTable(hBaseProxy.getConf(), tableName);
            hTable.setAutoFlush(false);
            hBaseTableMap.put(tableName, hTable);
        }
        return  hBaseTableMap.get(tableName);
    }


    public static Long getPastHourRoundedOff(){
        Long current = System.currentTimeMillis();
        current/= 1000;
        current -= current % (60*60);
        return current;
    }

    /**
     *
     */
    private static HTable historyTable = null;


    /**
     *
     * @return
     * @throws IOException
     */
    public static synchronized HTable fixHistoryTable() throws IOException {
        String tableName = "HISTORY";
        HBaseProxy hBaseProxy = HBaseProxy.getInstance();
        if(!hBaseProxy.existsTable(tableName)){
            String[] colFamilies = new String[2000];
            for(int count = 0; count < 2000; count ++){
                colFamilies[count] = String.valueOf(count);
            }
            hBaseProxy.createTable(tableName, colFamilies);
        }

        if(historyTable == null){
            historyTable = new HTable(hBaseProxy.getConf(), tableName);
            historyTable.setAutoFlush(Boolean.FALSE);
        }

        return historyTable;
    }


    /**
     *
     * @param tableName
     * @return
     * @throws IOException
     */
    public static HTable fixHBaseBingTables(String tableName) throws IOException {
        HBaseProxy hBaseProxy = HBaseProxy.getInstance();
        if(!hBaseProxy.existsTable(tableName)){
            String[] colFamilies = {CF};
            hBaseProxy.createTable(tableName, colFamilies);
        }
        return  new HTable(hBaseProxy.getConf(), tableName);
    }


    /**
     *
     * @param sourceId
     */
    public static void setConstantsInRedis(String sourceId){

        Jedis jedis = JedisConnectionPool.getResource();

        Map<String, String> map = new HashMap<String, String>();

        map.put(Constants.Z_SCORE          , "10");
        map.put(Constants.AMPLIFIER_CHANNEL, "10");
        map.put(Constants.DECAY            , "0.02");
        map.put(Constants.AVG_STORY_DECAY  , "0.02");
        map.put(Constants.W1               , "1");
        map.put(Constants.W2               , "1");
        map.put(Constants.W3               , "1");
        jedis.hmset( sourceId +  "_SCORES", map);

        JedisConnectionPool.returnResource(jedis);

    }


    /**
     *
     * @param message
     * @return
     */
    public static String getExtension(String message){
        message = message.toLowerCase();
        if(message.contains("http")){
            if(message.contains("png") || message.contains("gif") || message.contains("bmp") || message.contains("tiff") ||message.contains("jpeg")){
                return "image";
            }else{
                return "story";
            }
        }
        return null;
    }


    /**
     *
     * @param jsonArray
     * @return
     */
    public static Set<String> getTokenSet(org.json.simple.JSONArray jsonArray){
        Set<String> set = new HashSet<String>();
        if(jsonArray!= null){
            for(int count = 0; count < jsonArray.size(); count ++){
                set.add((String)jsonArray.get(count));
            }
        }
        return set;
    }


    /**
     *
     * @param text
     * @return
     * @throws IOException
     * @throws XmlRpcException
     * @throws BoilerpipeProcessingException
     */
    public static List<String> getKeywords(String text) throws IOException, XmlRpcException, BoilerpipeProcessingException {

        List<String> keyWords = new LinkedList<String>();

        try{
            if(!Strings.isNullOrEmpty(text)){
                if(text.contains("<html>")){
                    text = ArticleExtractor.INSTANCE.getText(text);
                }

                //Get Keywords using KEA algorithm
                String keaServer =  PropUtils.getVal("kea.server");
                XmlRpcClient server = new XmlRpcClient(keaServer);
                Vector params = new Vector();
                params.addElement(text);
                Object result = server.execute("kea.extractKeyphrasesFromString", params);

                //System.out.println(result);

                keyWords =  (List<String>)result;
            }
        }catch(ClassCastException | XmlRpcException | NullPointerException ex){
            logger.debug(PortalUtils.exceptionAsJson(ex));
        }
        return keyWords;
    }




    public static Map<String, Float> getFloatMap(Map<String, String> map){
        Map<String, Float> floatMap = new HashMap<String, Float>();

        if(map == null || map.isEmpty()){
            floatMap.put(Constants.Z_SCORE          , 10.0F);
            floatMap.put(Constants.AMPLIFIER_CHANNEL, 10.0F);
            floatMap.put(Constants.DECAY            , 0.02F);
            floatMap.put(Constants.AVG_STORY_DECAY  , 0.02F);
            floatMap.put(Constants.W1               , 1F);
            floatMap.put(Constants.W2               , 1F);
            floatMap.put(Constants.W3               , 1F);
        }else{
            for(String key : map.keySet()){
                floatMap.put(key, Float.valueOf(map.get(key)));
            }
        }
        return floatMap;
    }

    public static String encrypt(String enterpriseId) throws XmlException, DecoderException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException {

        byte[] keyValue = key.getBytes();
        Key secretKey = generateKey(keyValue);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(enterpriseId.getBytes());
        encryptedBytes = Base64.encodeBase64(encryptedBytes);
        String encryptedString = new String(encryptedBytes);

        return URLEncoder.encode(encryptedString, "UTF-8");
    }

    private static Key generateKey(byte[] keyValue){
        Key key = new SecretKeySpec(keyValue, "AES");
        return key;
    }

//    public static String decrypt(String userKey) throws XmlException, NoSuchPaddingException, NoSuchAlgorithmException, DecoderException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
//
//        //url decode and base64 decode
//        userKey = URLDecoder.decode(userKey, "UTF-8");
//        byte[] base64DecodedBytes = Base64.decodeBase64(userKey);
//
//        // get the cipher
//        byte[] keyValue = key.getBytes();
//        Key secretKey = generateKey(keyValue);
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.DECRYPT_MODE, secretKey);
//
//        byte[] encryptedBytes = cipher.doFinal(base64DecodedBytes);
//        String decryptString = new String(encryptedBytes);
//
//        return decryptString;
//    }

    public static boolean checkKeywords(JSONArray jsonA, JSONObject json, String keyWord){
        Boolean returnVal = false;
        try{
            if(json.containsKey("originalTokens")){
                JSONArray words = json.getJSONArray("originalTokens");
                List list = new LinkedList();
                list.add(keyWord);
                if(PortalUtils.okToAdd(list, words, "any")){
                    returnVal = true;
                }
            }
            if(json.containsKey("artificialTokens")){
                JSONArray words = json.getJSONArray("originalTokens");

                if(words.contains(keyWord)){
                    returnVal = true;
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            returnVal =false;
        }
        return returnVal;
    }


    public static boolean checkKeywords(JSONObject json, String keyWord){
        Boolean returnVal = false;
        try{
            if(json.containsKey("originalTokens")){
                JSONArray words = json.getJSONArray("originalTokens");
                if(words.contains(keyWord)){
                    //jsonA.add(PortalUtils.fixKeywordsInJSON(json));
                    returnVal = true;
                }
            }
            if(json.containsKey("artificialTokens")){
                JSONArray words = json.getJSONArray("originalTokens");
                if(words.contains(keyWord)){
                    //jsonA.add(PortalUtils.fixKeywordsInJSON(json));
                    returnVal = true;
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            returnVal =false;
        }
        return returnVal;
    }






    public static boolean okToAdd(List<String> expectedKeywords, JSONArray originalKeywords, String match ){

        //System.out.println(originalKeywords);

        boolean flag = true;
        Set<String> originalKeywordList = new HashSet<String>();
        for(Object o : originalKeywords){
            if(o instanceof JSONObject){
                originalKeywordList.add(((JSONObject)o).toString().toLowerCase());
            }else{
                originalKeywordList.add(((String)o).toLowerCase());
            }
        }

        System.out.println("originalKeywordList=>" + originalKeywordList + " expectedKeywords=>" + expectedKeywords );

        if(match.equals("any")){
            originalKeywordList.retainAll(expectedKeywords);
            return originalKeywordList.size() > 0;
        }else{
            return originalKeywordList.containsAll(expectedKeywords);
        }
    }


    private static final String key = "TheBestSecretKey";
}

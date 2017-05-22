package com.ngcomp.analytics.engine.conn;

/**
 * User: rparashar
 * Date: 9/1/13
 * Time: 7:52 AM
 */

import com.google.common.base.Strings;
import com.ngcomp.analytics.engine.util.Constants;
import com.ngcomp.analytics.engine.util.PortalUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;

import static com.ngcomp.analytics.engine.util.Constants.CF;

public class HBaseProxy {

    private static final Logger logger = Logger.getLogger(HBaseProxy.class);

    private static HBaseProxy instance;
    private static Configuration  conf;
    private static HTable         allKeysTable;

    private static HBaseAdmin    admin;

    static
    {
        conf         = HBaseConfiguration.create();
        try {
            PortalUtils.fixHBaseFBTables(Constants.ALL_KEYS);
            allKeysTable = new HTable   (conf, Constants.ALL_KEYS);
        } catch (IOException ex) {
            logger.debug(PortalUtils.exceptionAsJson(ex));
        }
    }

    public static String key(String key) throws IOException {
        Result result =  HBaseProxy.getInstance().getRow(allKeysTable, key);
        if(result.isEmpty()){
            return null;
        }else{
            return Bytes.toString(result.getValue(Bytes.toBytes(CF), Bytes.toBytes("val")));
        }
    }

    private HBaseProxy() throws IOException {
        admin  = new HBaseAdmin(conf);
    }


    public HTable getHTable(String tableName) throws IOException {
        return new HTable(conf, tableName);
    }


    public Configuration getConf() {
        return conf;
    }


    public HBaseAdmin getAdmin() {
        return admin;
    }

    public static HBaseProxy getInstance() throws IOException
    {
        if(instance == null ) {
            instance = new HBaseProxy();
        }
        return instance;
    }

    public void put(HTable hBaseTable, String row, Map<byte[], byte[]> map) throws IOException {

        Put putO = new Put(Bytes.toBytes(row));
        for (byte[] key : map.keySet()) {
            if(map.get(key) != null){
                putO.add(Bytes.toBytes(Constants.CF), key, map.get(key));
            }
        }
        if(!putO.isEmpty() && putO.size() > 0){
            hBaseTable.put( putO);

        }
    }

    public void putNew(String row) throws IOException {
    }


    public static void delRecord(HTable hTable, String rowKey) throws IOException {
        if(rowKey!=null){
            Delete del = new Delete(rowKey.getBytes());
            hTable.delete (del);
        }
    }

    public synchronized static void updateKeyInBKS(String keyInBKS, String valInBKS) throws IOException {

        try{
            if(!Strings.isNullOrEmpty(keyInBKS) && !Strings.isNullOrEmpty(valInBKS)){
                Put putO = new Put(Bytes.toBytes(keyInBKS));
                putO.add(Bytes.toBytes(Constants.CF), Bytes.toBytes("val"), Bytes.toBytes(valInBKS));
                if(putO != null && !putO.isEmpty()){
                    allKeysTable.put(putO);
//                    allKeysTable.flushCommits();
                }
            }
        }catch (Exception ex){
            logger.error(PortalUtils.exceptionAsJson(ex));
        }
    }

    /**
     *
     * @param hBaseTable
     * @param keyInBKS
     * @param valInBKS
     * @param newKey
     * @param quals
     * @param vals
     * @throws IOException
     */
    public synchronized void put(HTable hBaseTable, String keyInBKS, String valInBKS, String newKey, String[] quals, String[] vals) throws IOException {
        this.put(hBaseTable, keyInBKS, valInBKS, newKey, quals, vals, false);
    }

    /**
     *
     * @param hBaseTable
     * @param keyInBKS
     * @param valInBKS
     * @param newKey
     * @param quals
     * @param vals
     * @param commit
     * @throws IOException
     */
    public synchronized void put(HTable hBaseTable, String keyInBKS, String valInBKS, String newKey, String[] quals, String[] vals, Boolean commit) throws IOException {

        //System.out.println("keyInBKS=>" + keyInBKS  + " valInBKS=>" + valInBKS + " newKey=>" + valInBKS );

        if(valInBKS!=null && !newKey.equalsIgnoreCase(valInBKS)){
            HBaseProxy.getInstance().delRecord (hBaseTable, valInBKS);
        }
        HBaseProxy.getInstance().updateKeyInBKS(keyInBKS  , newKey);

        Put putO = new Put(Bytes.toBytes(newKey));
        int v = 0;
        for (String qual : quals) {
            if(vals[v] != null){
                putO.add(Bytes.toBytes(Constants.CF), Bytes.toBytes(qual), Bytes.toBytes(vals[v]));
            }
            v++;
        }
        if(!putO.isEmpty() && putO.size() > 0){
            hBaseTable.put(putO);
            if(commit){
                hBaseTable.flushCommits();
            }
        }
    }


    /**
     *
     * @param hBaseTable
     * @param row
     * @param sourceId
     * @param quals
     * @param vals
     * @throws IOException
     */
    public synchronized void put(HTable hBaseTable, String row, String sourceId, String[] quals, String[] vals) throws IOException {

        Put putO = new Put(Bytes.toBytes(row));
        int v = 0;
        for (String qual : quals) {
            if(vals[v] != null){
                putO.add(Bytes.toBytes(Constants.CF), Bytes.toBytes(qual), Bytes.toBytes(vals[v]));
            }
            v++;
        }
        if(!putO.isEmpty() && putO.size() > 0){
            hBaseTable.put(putO);
        }
    }


    /**
     *
     * @param hBaseTable
     * @param row
     * @param sourceId
     * @param quals
     * @param vals
     * @param commit
     * @throws IOException
     */
    public synchronized void put(HTable hBaseTable, String row, String sourceId, String[] quals, String[] vals, boolean commit) throws IOException {

        Put putO = new Put(Bytes.toBytes(row));
        int v = 0;
        for (String qual : quals) {
            if(vals[v] != null){
                putO.add(Bytes.toBytes(sourceId), Bytes.toBytes(qual), Bytes.toBytes(vals[v]));
            }
            v++;
        }
        if(!putO.isEmpty() && putO.size() > 0){
            hBaseTable.put(putO);
            if(commit){
                hBaseTable.flushCommits();
            }
        }
    }

    /**
     *
     * @param hBaseTable
     * @param row
     * @param quals
     * @param vals
     * @throws IOException
     */
    public synchronized void put(HTable hBaseTable, String row, Object[] quals, Object[] vals) throws IOException {

        Put putO = new Put(Bytes.toBytes(row));
        int v = 0;
        for (Object qual : quals) {
            if(vals[v] != null){
                putO.add(Bytes.toBytes(Constants.CF), Bytes.toBytes((String)qual), Bytes.toBytes((String)vals[v]));
            }
            v++;
        }
        if(!putO.isEmpty() && putO.size() > 0){
            hBaseTable.put(putO);
//            hBaseTable.flushCommits();
        }
    }



    public Result getRow(HTable hTable, String row) throws IOException {
        Get get = new Get(Bytes.toBytes(row));
        return hTable.get(get);
    }



    public boolean existsTable(String table) throws IOException {
		return admin.tableExists(table);
	}
//
//
    public void createTable(String table, String...colfams) throws IOException {
        createTable(table, null, colfams);
    }


    public void createTable(String table, byte[][] splitKeys, String...colfams) throws IOException {

        HTableDescriptor desc = new HTableDescriptor(table);
        for (String cf : colfams)
        {
            HColumnDescriptor coldef = new HColumnDescriptor(cf);
            desc.addFamily(coldef);
        }
        if (splitKeys != null)
        {
            admin.createTable(desc, splitKeys);
        }
        else
        {
            admin.createTable(desc);
        }
    }



    public ResultScanner getResultScanner(HTable hTable, String startRow, String endRow, Integer caching, Integer batch) throws IOException {

        System.out.println("Start=> " + startRow);

        Scan scan = new Scan();

        //Cache
        if(caching != null){
            scan.setCaching(caching);
        }

        //Batch Count
        if(batch !=null){
            scan.setBatch(batch);
        }

//        //Start Row
        if(!Strings.isNullOrEmpty(startRow)){
            scan.setStartRow(Bytes.toBytes(startRow));
        }

//        //End Row
        if(!Strings.isNullOrEmpty(endRow)){
            scan.setStopRow (Bytes.toBytes(endRow));
        }

        return hTable.getScanner(scan);
    }





    /**
     *
     * @param table
     * @param rows
     * @param fams
     * @param quals
     * @throws IOException
     */

}
package com.ngcomp.analytics.engine.thread;

import com.ngcomp.analytics.engine.conn.HBaseProxy;
import com.ngcomp.analytics.engine.util.PortalUtils;
import net.sf.json.JSONObject;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;


/**
 * User: Ram Parashar
 * Date: 10/1/13
 * Time: 9:07 PM
 */
public class MRAllThread implements Runnable {

    @Override
    public void run() {

        HTable sbiTable = null;

        try {
            //Score Based Index...
            String tablaName = String.valueOf(PortalUtils.getPastHourRoundedOff());
            sbiTable = PortalUtils.fixHBaseFBTables(tablaName + "_sbi");

            HBaseProxy hBaseProxy = HBaseProxy.getInstance();
            HTableDescriptor[] tables = hBaseProxy.getAdmin().listTables();

            int counter = 0;


            for(HTableDescriptor table : tables){

                String tName = Bytes.toString(table.getName());
                if("sbi".equals(tName) || "tbi".equals(tName) || "HISTORY".equals(tName) || "ALL_KEYS".equals(tName) || tName.endsWith("sbi")){
                    continue;
                }
                System.out.println("Table========================>" + Bytes.toString(table.getName()));
                HTable hTable = hBaseProxy.getHTable(Bytes.toString(table.getName()));

                ResultScanner scanner = hBaseProxy.getResultScanner(hTable, null, null, 100, 100);
                for (Result result : scanner) {
                    JSONObject      jsonO = PortalUtils.getJSONObject(result.getFamilyMap(Bytes.toBytes("cf")));
                    if(jsonO.containsKey("relevanceScore")){
                        Long score = (long)(Double.valueOf(jsonO.getString("relevanceScore")) * 1000000000);
                        String key = String.valueOf(Long.MAX_VALUE - score);
                               key = key + "##"  + Bytes.toString(result.getRow());
                        HBaseProxy.getInstance().put(sbiTable, key, PortalUtils.getKeyValMap(result));
                    }else{
                        String key = String.valueOf(Long.MAX_VALUE);
                        key = key + "##"  + Bytes.toString(result.getRow());
                        System.out.println(key);
                        HBaseProxy.getInstance().put(sbiTable, key, PortalUtils.getKeyValMap(result));
                    }

                }
                scanner.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
           try {
               if(sbiTable != null){
                   sbiTable.close();
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
        }
    }


    public static void main(String...strings){
        //System.out.println(PortalUtils.getPastHourRoundedOff());
        MRAllThread m = new MRAllThread();
        m.run();
    }

}

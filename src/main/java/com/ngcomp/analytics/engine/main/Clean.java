package com.ngcomp.analytics.engine.main;

import com.ngcomp.analytics.engine.conn.HBaseProxy;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;

import java.io.IOException;

/**
 * User: Ram Parashar
 * Date: 10/22/13
 * Time: 7:46 PM
 */
public class Clean {
    public static void main(String...strings) throws IOException {
        HBaseProxy hBaseProxy = HBaseProxy.getInstance();
        HBaseAdmin hBaseAdmin  = hBaseProxy.getAdmin();
        for(HTableDescriptor hTableDescriptor :  hBaseAdmin.listTables()){
            hBaseAdmin.disableTable(hTableDescriptor.getName());
            hBaseAdmin.deleteTable(hTableDescriptor.getName());
        }
    }
}

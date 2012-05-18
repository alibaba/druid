package com.alibaba.druid.hbase;

import junit.framework.TestCase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseConnectTest extends TestCase {

    public void test_connection() throws Exception {
        Configuration config = new Configuration();//HBaseConfiguration.create();
//        config.set("dfs.support.append", "true");
//        config.set("hbase.rootdir", "hdfs://10.20.153.63:9000/hbase");
//        config.set("hbase.cluster.distributed", "true");
//        config.set("hbase.master", "10.20.153.63:60000");
        config.set("hbase.zookeeper.quorum", "10.20.153.63");
//        config.set("zookeeper.session.timeout", "30000");
//        config.set("hbase.regionserver.handler.count", "100");
//        config.set("hbase.client.keyvalue.maxsize", "67108864");
        
//        config = HBaseConfiguration.create();
        
        HTable table = new HTable(config, "test");
        
        Put put = new Put(Bytes.toBytes("1001"));
        put.add(Bytes.toBytes("d"), Bytes.toBytes("id"), Bytes.toBytes("x1001"));
        
        table.put(put);
        
        table.close();
    }
}

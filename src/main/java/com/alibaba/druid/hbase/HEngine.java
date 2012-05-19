package com.alibaba.druid.hbase;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;

public class HEngine {

    private static ConcurrentMap<String, HEngine> engines = new ConcurrentHashMap<String, HEngine>();

    public static HEngine getHEngine(String url, Properties connectProperties) {
        HEngine engine = engines.get(url);
        if (engine == null) {
            engine = new HEngine(url, connectProperties);
            engines.putIfAbsent(url, engine);
            engine = engines.get(url);
        }
        return engine;
    }

    // =============================

    private final String        url;

    private HTablePool          tablePool;

    private final Configuration config;

    public HEngine(String url, Properties connectProperties){
        super();
        this.url = url;

        config = new Configuration(false);
    }

    public String getUrl() {
        return url;
    }

    public HTablePool getTablePool() {
        return tablePool;
    }

    public Configuration getConfig() {
        return config;
    }
    
    public HTableInterface getHTable(String tableName) {
        return tablePool.getTable(tableName);
    }

}

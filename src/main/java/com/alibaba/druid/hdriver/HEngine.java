package com.alibaba.druid.hdriver;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;

import com.alibaba.druid.hdriver.mapping.HMappingTable;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

public class HEngine {

    private final static Log                      LOG     = LogFactory.getLog(HEngine.class);

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

    private final String                    url;

    private HTablePool                      tablePool;
    private int                             htablePoolMaxSize = 256;
    private Configuration                   config;
    private ConcurrentMap<String, HMappingTable> mappings          = new ConcurrentHashMap<String, HMappingTable>();

    public HEngine(String url, Properties connectProperties){
        super();
        this.url = url;

        config = new Configuration(false);

        if (connectProperties != null) {
            String propValue = connectProperties.getProperty("htable.pool.size");
            if (propValue != null) {
                try {
                    htablePoolMaxSize = Integer.parseInt(propValue);
                } catch (NumberFormatException ex) {
                    LOG.error("parse property 'htable.pool.size' error", ex);
                }
            }
        }
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

    public ConcurrentMap<String, HMappingTable> getMappings() {
        return mappings;
    }

    public synchronized HTableInterface getHTable(String tableName) {
        if (tablePool == null) {
            tablePool = new HTablePool(config, htablePoolMaxSize);
        }
        return tablePool.getTable(tableName);
    }
    
    public HBaseAdmin getHBaseAdmin() throws IOException {
        return new HBaseAdmin(this.config);
    }

}

package com.alibaba.druid.bvt.pool;

import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.Constants;
import com.alibaba.druid.stat.JdbcDataSourceStat;

public class JdbcDataSourceStatTest extends TestCase {

    public void test_max() throws Exception {
        JdbcDataSourceStat stat = new JdbcDataSourceStat("", "");

        for (int i = 0; i < 1000 * 10; ++i) {
            stat.createSqlStat("select " + i);
        }

        Assert.assertEquals(1000, stat.getSqlStatMap().size());
    }

    public void test_max_10() throws Exception {
        Properties connectProperties = new Properties();
        connectProperties.put(Constants.DRUID_STAT_SQL_MAX_SIZE, 10);
        JdbcDataSourceStat stat = new JdbcDataSourceStat("", "", "mysql", connectProperties);

        for (int i = 0; i < 1000 * 1; ++i) {
            stat.createSqlStat("select " + i);
        }

        Assert.assertEquals(10, stat.getSqlStatMap().size());
    }
    
    public void test_max_10_str() throws Exception {
        Properties connectProperties = new Properties();
        connectProperties.put(Constants.DRUID_STAT_SQL_MAX_SIZE, "10");
        JdbcDataSourceStat stat = new JdbcDataSourceStat("", "", "mysql", connectProperties);
        
        for (int i = 0; i < 1000 * 1; ++i) {
            stat.createSqlStat("select " + i);
        }
        
        Assert.assertEquals(10, stat.getSqlStatMap().size());
    }
}

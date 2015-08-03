package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsAlterTableDropPartitionTest extends TestCase {
    
    public void test_if() throws Exception {
        String sql = "alter table sale_detail drop if not exists partition (sale_date='201312', region='hangzhou');";
        Assert.assertEquals("ALTER TABLE sale_detail" //
                + "\n\tDROP IF NOT EXISTS PARTITION (sale_date = '201312', region = 'hangzhou');", SQLUtils.formatOdps(sql));
    }
    
    public void test_purge() throws Exception {
        String sql = "alter table my_log drop partition (ds='20150618') purge;";
        Assert.assertEquals("ALTER TABLE my_log"
                + "\n\tDROP PARTITION (ds = '20150618') PURGE;", SQLUtils.formatOdps(sql));
    }
}

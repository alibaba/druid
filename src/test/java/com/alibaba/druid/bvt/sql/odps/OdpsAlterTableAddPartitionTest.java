package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsAlterTableAddPartitionTest extends TestCase {
    
    public void test_if() throws Exception {
        String sql = "alter table sale_detail add if not exists partition (sale_date='201312', region='hangzhou');";
        Assert.assertEquals("ALTER TABLE sale_detail" //
                + "\n\tADD IF NOT EXISTS PARTITION (sale_date = '201312', region = 'hangzhou');", SQLUtils.formatOdps(sql));
    }
}

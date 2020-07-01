package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsAlterTableRenamePartitionTest extends TestCase {
    
    public void test_if() throws Exception {
        String sql = "alter table sale_detail partition (sale_date='201312', region='hangzhou')"
                + "\nrename to partition(sale_date='201313', region='hangzhou');";
        Assert.assertEquals("ALTER TABLE sale_detail"
                + "\n\tPARTITION (sale_date = '201312', region = 'hangzhou') RENAME TO PARTITION(sale_date = '201313', region = 'hangzhou');", SQLUtils.formatOdps(sql));
    }
}

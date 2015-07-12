package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsReadTest extends TestCase {
    
    public void test_read() throws Exception {
        String sql = "read sale_detail";
        Assert.assertEquals("READ sale_detail", SQLUtils.formatOdps(sql));
    }
    
    
    public void test_read_column() throws Exception {
        String sql = "read sale_detail(f0, f1, f2) 10";
        Assert.assertEquals("READ sale_detail (f0, f1, f2) 10", SQLUtils.formatOdps(sql));
    }
    
    public void test_read_limit() throws Exception {
        String sql = "read sale_detail 10";
        Assert.assertEquals("READ sale_detail 10", SQLUtils.formatOdps(sql));
    }
    
    public void test_read_partition() throws Exception {
        String sql = "read sale_detail partition(ds='20150701')";
        Assert.assertEquals("READ sale_detail PARTITION (ds = '20150701')", SQLUtils.formatOdps(sql));
    }
    
    public void test_read_partition_limit() throws Exception {
        String sql = "read sale_detail partition(ds='20150701') 10";
        Assert.assertEquals("READ sale_detail PARTITION (ds = '20150701') 10", SQLUtils.formatOdps(sql));
    }

}

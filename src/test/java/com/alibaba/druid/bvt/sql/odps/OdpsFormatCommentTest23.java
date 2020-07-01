package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest23 extends TestCase {
    public void test_drop_table() throws Exception {
        String sql = "-- xxx"
                + "\n -- yyy"
                + "\ndrop table if exists mytables;";
        Assert.assertEquals("-- xxx"
                + "\n-- yyy"
                + "\nDROP TABLE IF EXISTS mytables;", SQLUtils.formatOdps(sql));
    }   
}

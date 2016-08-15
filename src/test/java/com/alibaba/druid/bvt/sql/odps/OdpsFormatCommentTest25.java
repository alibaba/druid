package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest25 extends TestCase {
    public void test_drop_function() throws Exception {
        String sql = "-- xxx"
                + "\n -- yyy"
                + "\ndrop function if exists mytables;";
        Assert.assertEquals("-- xxx"
                + "\n-- yyy"
                + "\nDROP FUNCTION IF EXISTS mytables;", SQLUtils.formatOdps(sql));
    }   
}

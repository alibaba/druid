package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest24 extends TestCase {
    public void test_drop_view() throws Exception {
        String sql = "-- xxx"
                + "\n -- yyy"
                + "\ndrop view if exists mytables;";
        Assert.assertEquals("-- xxx"
                + "\n-- yyy"
                + "\nDROP VIEW IF EXISTS mytables;", SQLUtils.formatOdps(sql));
    }   
}

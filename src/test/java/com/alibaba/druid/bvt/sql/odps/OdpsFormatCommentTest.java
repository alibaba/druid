package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "select f1 -- aaa"
                + "\n from t1";
        Assert.assertEquals("SELECT f1 -- aaa" //
                + "\nFROM t1", SQLUtils.formatOdps(sql));
    }

    public void test_column_2_comment() throws Exception {
        String sql = "select f1 -- aaa"
                + "\n, f2 -- bbb"
                + "\n from t1";
        Assert.assertEquals("SELECT f1 -- aaa" //
                + "\n\t, f2 -- bbb" //
                + "\nFROM t1", SQLUtils.formatOdps(sql));
    }
    
    public void test_column_2_multi_comment() throws Exception {
        String sql = "select f1 /*aa*/"
                + "\n, f2 -- bbb"
                + "\n from t1";
        Assert.assertEquals("SELECT f1 /*aa*/" //
                + "\n\t, f2 -- bbb" //
                + "\nFROM t1", SQLUtils.formatOdps(sql));
    }
}

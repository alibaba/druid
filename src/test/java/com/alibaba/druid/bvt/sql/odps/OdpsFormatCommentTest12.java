package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest12 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "select * from t --abc";
        Assert.assertEquals("SELECT *" //
                + "\nFROM t --abc", SQLUtils.formatOdps(sql));
    }

   
}

package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest11 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "select * from t where -- comment_xx"
                + "\n f1 > 0";
        Assert.assertEquals("SELECT *" //
                + "\nFROM t" //
                + "\nWHERE -- comment_xx"
                + "\nf1 > 0", SQLUtils.formatOdps(sql));
    }

   
}

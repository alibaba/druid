package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest21 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "create table sales (f1 bigint)";
        Assert.assertEquals("CREATE TABLE sales ("
                + "\n\tf1 BIGINT"
                + "\n)", SQLUtils.formatOdps(sql));
    }

   
}

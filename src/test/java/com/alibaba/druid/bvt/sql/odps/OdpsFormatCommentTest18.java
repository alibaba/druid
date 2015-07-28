package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest18 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "create table t (f1 string comment \"xxx\")";
        Assert.assertEquals("CREATE TABLE t ("
                + "\n\tf1 STRING COMMENT 'xxx'"
                + "\n)", SQLUtils.formatOdps(sql));
    }

   
    public void test_column_comment_2() throws Exception {
        String sql = "create table t (f1 string comment \"xxx's\")";
        Assert.assertEquals("CREATE TABLE t ("
                + "\n\tf1 STRING COMMENT 'xxx''s'"
                + "\n)", SQLUtils.formatOdps(sql));
    }
}

package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest16 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "create table t1 (f0 bigint) partitioned by ("
                + "ds string, -- c_1"
                + "\nhh string -- c_2"
                + "\n);";
        Assert.assertEquals("CREATE TABLE t1t1 ("
                + "\n\tf0 bigint"
                + "\n)"
                + "\nPARTITIONED BY ("
                + "\n\tds string, -- c_1"
                + "\n\thh string -- c_2"
                + "\n)"
                + "\n;", SQLUtils.formatOdps(sql));
    }

   
}

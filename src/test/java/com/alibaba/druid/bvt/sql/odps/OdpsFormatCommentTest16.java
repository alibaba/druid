package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest16 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "create table t1 ("
                + "\nf0 bigint, -- fc_0"
                + "\nf1 string, -- fc_1"
                + "\nf2 string -- fc_2"
                + "\n) partitioned by ("
                + "ds string, -- c_1"
                + "\nhh string -- c_2"
                + "\n);";
        Assert.assertEquals("CREATE TABLE t1 ("
                + "\n\tf0 BIGINT, -- fc_0"
                + "\n\tf1 STRING, -- fc_1"
                + "\n\tf2 STRING -- fc_2"
                + "\n)"
                + "\nPARTITIONED BY ("
                + "\n\tds STRING, -- c_1"
                + "\n\thh STRING -- c_2"
                + "\n);", SQLUtils.formatOdps(sql));
    }

   
}

package com.alibaba.druid.bvt.sql.odps;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest15 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "create table t1 (f0 bigint) partitioned by (ds string, hh string);";
        assertEquals("CREATE TABLE t1 ("
                + "\n\tf0 BIGINT"
                + "\n)"
                + "\nPARTITIONED BY ("
                + "\n\tds STRING,"
                + "\n\thh STRING"
                + "\n);", SQLUtils.formatOdps(sql));
    }

}

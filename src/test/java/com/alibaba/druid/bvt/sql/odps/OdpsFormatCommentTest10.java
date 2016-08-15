package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest10 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "insert overwrite table ttt partition (ds='20150710',hh='07') select tt_split(content, 60) as (f0, f1, f2, f3,f4,f5,f6) from xxx;";
        Assert.assertEquals("INSERT OVERWRITE TABLE ttt PARTITION (ds='20150710', hh='07')"
                + "\nSELECT tt_split(content, 60)"
                + "\n\tAS ("
                + "\n\t\tf0,"
                + "\n\t\tf1,"
                + "\n\t\tf2,"
                + "\n\t\tf3,"
                + "\n\t\tf4,"
                + "\n\t\tf5,"
                + "\n\t\tf6"
                + "\n\t)"
                + "\nFROM xxx;", SQLUtils.formatOdps(sql));
    }

   
}

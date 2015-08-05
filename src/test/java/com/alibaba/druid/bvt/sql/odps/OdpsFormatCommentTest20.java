package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest20 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "select * from a full outer join b on a.id=b.id";
        Assert.assertEquals("SELECT *"
                + "\nFROM a"
                + "\nFULL OUTER JOIN b"
                + "\nON a.id = b.id", SQLUtils.formatOdps(sql));
    }

   
}

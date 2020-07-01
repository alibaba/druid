package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest6 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "select *" //
                + "\nfrom t "//
                + "\nwhere status = '20' -- comment xxx"
                + "\nand flag & 127 > 0 -- comment kkkkk"
                + "\n;"
                ;//
        Assert.assertEquals("SELECT *"
                + "\nFROM t"
                + "\nWHERE status = '20' -- comment xxx"
                + "\n\tAND flag & 127 > 0 -- comment kkkkk;", SQLUtils.formatOdps(sql));
    }

   
}

package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest8 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "select * from table1;--comment1 goes here" //
                + "\n" //
                + "\nselect * from table2;;select * from table3;" //
                + "\n--comment2 goes here"
                ;//
        Assert.assertEquals("SELECT *"
                + "\nFROM table1;"
                + "\n--comment1 goes here"
                + "\nSELECT *"
                + "\nFROM table2;"
                + "\nSELECT *"
                + "\nFROM table3;--comment2 goes here", SQLUtils.formatOdps(sql));
    }

   
}

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
                + "\n"
                + "\n-- comment1 goes here"
                + "\nSELECT *"
                + "\nFROM table2;"
                + "\n"
                + "\nSELECT *"
                + "\nFROM table3;-- comment2 goes here", SQLUtils.formatOdps(sql));
        
        Assert.assertEquals("select *"
                + "\nfrom table1;"
                + "\n"
                + "\n-- comment1 goes here"
                + "\nselect *"
                + "\nfrom table2;"
                + "\n"
                + "\nselect *"
                + "\nfrom table3;-- comment2 goes here", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }

   
}

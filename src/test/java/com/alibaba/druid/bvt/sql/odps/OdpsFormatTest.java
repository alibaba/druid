package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatTest extends TestCase {
    public void test_format() throws Exception {
        String sql = "select * from t1; ;select * from t2;";
        Assert.assertEquals("SELECT *" //
                + "\nFROM t1;" //
                + "\n"
                + "\nSELECT *" //
                + "\nFROM t2;", SQLUtils.formatOdps(sql));
    }
    
    public void test_no_semi() throws Exception {
        String sql = "select * from t1; ;select * from t2";
        Assert.assertEquals("SELECT *" //
                + "\nFROM t1;" //
                + "\n"
                + "\nSELECT *" //
                + "\nFROM t2", SQLUtils.formatOdps(sql));
    }
}

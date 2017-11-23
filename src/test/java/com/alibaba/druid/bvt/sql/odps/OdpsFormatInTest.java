package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatInTest extends TestCase {
    public void test_drop_function() throws Exception {
        String sql = "select * from t1 where f1 in ('1', '2', '3', '4', '5', '6', '7', '8', '9', '10')";
        Assert.assertEquals("SELECT *"
                + "\nFROM t1"
                + "\nWHERE f1 IN ("
                + "\n\t'1', "
                + "\n\t'2', "
                + "\n\t'3', "
                + "\n\t'4', "
                + "\n\t'5', "
                + "\n\t'6', "
                + "\n\t'7', "
                + "\n\t'8', "
                + "\n\t'9', "
                + "\n\t'10'"
                + "\n)"
                + "", SQLUtils.formatOdps(sql));
    }   
}

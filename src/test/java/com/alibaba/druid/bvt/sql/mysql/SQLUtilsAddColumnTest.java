package com.alibaba.druid.bvt.sql.mysql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

public class SQLUtilsAddColumnTest extends TestCase {

    public void test_select() throws Exception {
        Assert.assertEquals("SELECT id, name" //
                            + "\nFROM t", SQLUtils.addSelectItem("select id from t", "name", null, null));
    }

    public void test_select_1() throws Exception {
        Assert.assertEquals("SELECT id, name AS XX" //
                            + "\nFROM t", SQLUtils.addSelectItem("select id from t", "name", "XX", null));
    }
    
    public void test_select_2() throws Exception {
        Assert.assertEquals("SELECT id, name AS \"XX W\"" //
                            + "\nFROM t", SQLUtils.addSelectItem("select id from t", "name", "XX W", null));
    }

}

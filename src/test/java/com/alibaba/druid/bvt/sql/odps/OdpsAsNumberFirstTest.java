package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsAsNumberFirstTest extends TestCase {
    public void test_0() throws Exception {
        String sql = "select id as 39dd"
                + "\n from t1";
        Assert.assertEquals("SELECT id AS 39dd" //
                + "\nFROM t1", SQLUtils.formatOdps(sql));
    }

}

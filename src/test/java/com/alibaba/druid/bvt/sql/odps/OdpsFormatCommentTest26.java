package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest26 extends TestCase {
    public void test_drop_function() throws Exception {
        String sql = "create table t as select * from dual;";
        Assert.assertEquals("CREATE TABLE t"
                + "\nAS"
                + "\nSELECT *"
                + "\nFROM dual;", SQLUtils.formatOdps(sql));
    }   
}

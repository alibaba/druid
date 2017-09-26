package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest30 extends TestCase {
    public void test_drop_function() throws Exception {
        String sql = "--啊实打实大啊实打实大"
                + "\nCREATE TABLE xxx ("
                + "\n  aa STRING,"
                + "\n  asdasd STRING,"
                + "\n  asasd STRING"
                + "\n);";
        Assert.assertEquals("-- 啊实打实大啊实打实大"
                + "\nCREATE TABLE xxx ("
                + "\n\taa STRING,"
                + "\n\tasdasd STRING,"
                + "\n\tasasd STRING"
                + "\n);", SQLUtils.formatOdps(sql));
    }   
}

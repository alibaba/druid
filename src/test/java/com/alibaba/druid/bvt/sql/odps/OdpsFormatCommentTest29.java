package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest29 extends TestCase {
    public void test_drop_function() throws Exception {
        String sql = "create table xxxx001(   --测试"
                + "\ncol string,  --测试2"
                + "\ncol2 string  --测试3"
                + "\n)";
        Assert.assertEquals("CREATE TABLE xxxx001 ( -- 测试"
                + "\n\tcol STRING, -- 测试2"
                + "\n\tcol2 STRING -- 测试3"
                + "\n)", SQLUtils.formatOdps(sql));
    }   
}

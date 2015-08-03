package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest17 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "set xxx=aaa;--ssss";
        Assert.assertEquals("SET xxx = aaa;--ssss", SQLUtils.formatOdps(sql));
    }

   
}

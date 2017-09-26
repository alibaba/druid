package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest19 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "set xxx=aaa;--ssss"
                + "\nset yyy=123;";
        Assert.assertEquals("SET xxx = aaa;-- ssss"
                + "\nSET yyy = 123;", SQLUtils.formatOdps(sql));
    }

   
}

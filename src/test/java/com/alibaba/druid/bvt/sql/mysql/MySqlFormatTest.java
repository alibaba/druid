package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MySqlFormatTest extends TestCase {

    public void test_0() throws Exception {
        String text = "CREATE TABLE customer (a INT, b CHAR (20), INDEX (a));";
        Assert.assertEquals("CREATE TABLE customer (\n" + //
                            "\ta INT, \n" + //
                            "\tb CHAR(20), \n" + //
                            "\tINDEX(a)\n" + //
                            ")", SQLUtils.format(text, JdbcUtils.MYSQL));
    }
}

package com.alibaba.druid.bvt.sql.postgresql;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcUtils;

public class PGFormatTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE foo (fooid int, foosubid int, fooname text);";
        String formatedSql = SQLUtils.format(sql, JdbcUtils.POSTGRESQL);
        Assert.assertEquals("CREATE TABLE foo (\n" + //
                            "\tfooid int, \n" + //
                            "\tfoosubid int, \n" + //
                            "\tfooname text\n" + //
                            ")", formatedSql);
    }
}

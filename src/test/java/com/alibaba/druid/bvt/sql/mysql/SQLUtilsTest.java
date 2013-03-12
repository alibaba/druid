package com.alibaba.druid.bvt.sql.mysql;

import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;

public class SQLUtilsTest extends TestCase {

    public void test_format() throws Exception {
        String formattedSql = SQLUtils.format("select * from t where id = ?", JdbcConstants.MYSQL,
                                              Arrays.<Object> asList("abc"));
        Assert.assertEquals("SELECT *" + //
                            "\nFROM t" + //
                            "\nWHERE id = 'abc'", formattedSql);
    }
}

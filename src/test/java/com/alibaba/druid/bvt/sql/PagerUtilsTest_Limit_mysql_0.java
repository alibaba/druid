package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcConstants;

public class PagerUtilsTest_Limit_mysql_0 extends TestCase {

    public void test_mysql_0() throws Exception {
        String sql = "select * from t";
        String result = PagerUtils.limit(sql, JdbcConstants.MYSQL, 0, 10);
        Assert.assertEquals("SELECT *" + //
                            "\nFROM t" + //
                            "\nLIMIT 10", result);
    }

    public void test_mysql_1() throws Exception {
        String sql = "select * from t";
        String result = PagerUtils.limit(sql, JdbcConstants.MYSQL, 10, 10);
        Assert.assertEquals("SELECT *" + //
                            "\nFROM t" + //
                            "\nLIMIT 10, 10", result);
    }

    public void test_mysql_2() throws Exception {
        String sql = "select * from t";
        String result = PagerUtils.limit(sql, JdbcConstants.MYSQL, 20, 10);
        Assert.assertEquals("SELECT *" + //
                            "\nFROM t" + //
                            "\nLIMIT 20, 10", result);
    }

    public void test_mysql_3() throws Exception {
        String sql = "select id, name, salary from t order by id, name";
        String result = PagerUtils.limit(sql, JdbcConstants.MYSQL, 20, 10);
        Assert.assertEquals("SELECT id, name, salary" + //
                            "\nFROM t" + //
                            "\nORDER BY id, name" + //
                            "\nLIMIT 20, 10", result);
    }
}

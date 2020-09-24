package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcConstants;

public class PagerUtilsTest_Count_Oracle_0 extends TestCase {

    public void test_oracle_0() throws Exception {
        String sql = "select * from t";
        String result = PagerUtils.count(sql, JdbcConstants.ORACLE);
        Assert.assertEquals("SELECT COUNT(*)\n" + //
                            "FROM t", result);
        result = PagerUtils.count(sql, JdbcConstants.OCEANBASE_ORACLE);
        Assert.assertEquals("SELECT COUNT(*)\n" + //
                "FROM t", result);
    }

    public void test_oracle_1() throws Exception {
        String sql = "select id, name from t";
        String result = PagerUtils.count(sql, JdbcConstants.ORACLE);
        Assert.assertEquals("SELECT COUNT(*)\n" + //
                            "FROM t", result);
        result = PagerUtils.count(sql, JdbcConstants.OCEANBASE_ORACLE);
        Assert.assertEquals("SELECT COUNT(*)\n" + //
                "FROM t", result);
    }

    public void test_oracle_2() throws Exception {
        String sql = "select id, name from t order by id";
        String result = PagerUtils.count(sql, JdbcConstants.ORACLE);
        Assert.assertEquals("SELECT COUNT(*)\n" + //
                            "FROM t", result);
        result = PagerUtils.count(sql, JdbcConstants.OCEANBASE_ORACLE);
        Assert.assertEquals("SELECT COUNT(*)\n" + //
                "FROM t", result);
    }

    public void test_oracle_group_0() throws Exception {
        String sql = "select type, count(*) from t group by type";
        String result = PagerUtils.count(sql, JdbcConstants.ORACLE);
        Assert.assertEquals("SELECT COUNT(*)\n" +
                "FROM (\n" +
                "\tSELECT type, count(*)\n" +
                "\tFROM t\n" +
                "\tGROUP BY type\n" +
                ") ALIAS_COUNT", result);

        result = PagerUtils.count(sql, JdbcConstants.OCEANBASE_ORACLE);
        Assert.assertEquals("SELECT COUNT(*)\n" +
                "FROM (\n" +
                "\tSELECT type, count(*)\n" +
                "\tFROM t\n" +
                "\tGROUP BY type\n" +
                ") ALIAS_COUNT", result);
    }

    public void test_oracle_union_0() throws Exception {
        String sql = "select id, name from t1 union select id, name from t2 order by id";
        String result = PagerUtils.count(sql, JdbcConstants.ORACLE);
        Assert.assertEquals("SELECT COUNT(*)\n" +
                "FROM (\n" +
                "\tSELECT id, name\n" +
                "\tFROM t1\n" +
                "\tUNION\n" +
                "\tSELECT id, name\n" +
                "\tFROM t2\n" +
                ") ALIAS_COUNT", result);

        result = PagerUtils.count(sql, JdbcConstants.OCEANBASE_ORACLE);
        Assert.assertEquals("SELECT COUNT(*)\n" +
                "FROM (\n" +
                "\tSELECT id, name\n" +
                "\tFROM t1\n" +
                "\tUNION\n" +
                "\tSELECT id, name\n" +
                "\tFROM t2\n" +
                ") ALIAS_COUNT", result);
    }
}

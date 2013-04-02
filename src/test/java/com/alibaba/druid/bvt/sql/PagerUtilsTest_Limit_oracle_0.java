package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcConstants;

public class PagerUtilsTest_Limit_oracle_0 extends TestCase {

    public void test_oracle_oderby_0() throws Exception {
        String sql = "select * from t order by id";
        String result = PagerUtils.limit(sql, JdbcConstants.ORACLE, 0, 10);
        Assert.assertEquals("SELECT XX.*, ROWNUM AS RN" + //
                            "\nFROM (SELECT *" + //
                            "\n\tFROM t" + //
                            "\n\tORDER BY id" + //
                            "\n\t) XX" + //
                            "\nWHERE ROWNUM <= 10", result);
    }

    public void test_oracle_0() throws Exception {
        String sql = "select * from t";
        String result = PagerUtils.limit(sql, JdbcConstants.ORACLE, 0, 10);
        Assert.assertEquals("SELECT *" + //
                            "\nFROM t" + //
                            "\nWHERE ROWNUM <= 10", result);
    }

    public void test_oracle_1() throws Exception {
        String sql = "select * from t";
        String result = PagerUtils.limit(sql, JdbcConstants.ORACLE, 10, 10);
        Assert.assertEquals("SELECT *" + //
                            "\nFROM (SELECT XX.*, ROWNUM AS RN" + //
                            "\n\tFROM (SELECT *" + //
                            "\n\t\tFROM t" + //
                            "\n\t\t) XX" + //
                            "\n\tWHERE ROWNUM <= 20" + //
                            "\n\t) XXX" + //
                            "\nWHERE RN > 10", result);
    }

    public void test_oracle_2() throws Exception {
        String sql = "select * from t";
        String result = PagerUtils.limit(sql, JdbcConstants.ORACLE, 20, 10);
        Assert.assertEquals("SELECT *" + //
                            "\nFROM (SELECT XX.*, ROWNUM AS RN" + //
                            "\n\tFROM (SELECT *" + //
                            "\n\t\tFROM t" + //
                            "\n\t\t) XX" + //
                            "\n\tWHERE ROWNUM <= 30" + //
                            "\n\t) XXX" + //
                            "\nWHERE RN > 20", result);
    }

    public void test_oracle_3() throws Exception {
        String sql = "select id, name, salary from t order by id, name";
        String result = PagerUtils.limit(sql, JdbcConstants.ORACLE, 20, 10);
        Assert.assertEquals("SELECT *" + //
                            "\nFROM (SELECT XX.*, ROWNUM AS RN" + //
                            "\n\tFROM (SELECT id, name, salary" + //
                            "\n\t\tFROM t" + //
                            "\n\t\tORDER BY id, name" + //
                            "\n\t\t) XX" + //
                            "\n\tWHERE ROWNUM <= 30" + //
                            "\n\t) XXX" + //
                            "\nWHERE RN > 20", result);
    }
}

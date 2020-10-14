package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

public class PagerUtilsTest_Limit_mysql_0 extends TestCase {

    public void test_mysql_0() throws Exception {
        String sql = "select * from t";
        String result = PagerUtils.limit(sql, JdbcConstants.MYSQL, 0, 10);
        assertEquals("SELECT *" + //
                "\nFROM t" + //
                "\nLIMIT 10", result);
    }

    public void test_mysql_1() throws Exception {
        String sql = "select * from t";
        String result = PagerUtils.limit(sql, JdbcConstants.MYSQL, 10, 10);
        assertEquals("SELECT *" + //
                "\nFROM t" + //
                "\nLIMIT 10, 10", result);
    }

    public void test_mysql_2() throws Exception {
        String sql = "select * from t";
        String result = PagerUtils.limit(sql, JdbcConstants.MYSQL, 20, 10);
        assertEquals("SELECT *" + //
                "\nFROM t" + //
                "\nLIMIT 20, 10", result);
    }

    public void test_mysql_3() throws Exception {
        String sql = "select id, name, salary from t order by id, name";
        String result = PagerUtils.limit(sql, JdbcConstants.MYSQL, 20, 10);
        assertEquals("SELECT id, name, salary" + //
                "\nFROM t" + //
                "\nORDER BY id, name" + //
                "\nLIMIT 20, 10", result);
    }

    public void test_mysql_4() throws Exception {
        String sql = "SELECT *\n" +
                "FROM test.a\n" +
                "WHERE a.field0 = 0\n" +
                "\tAND (a.field1 LIKE BINARY '1'\n" +
                "\tOR a.field2 = 2)\n";
        String result = PagerUtils.limit(sql, JdbcConstants.MYSQL, 0, 10);
        assertEquals("SELECT *\n" +
                "FROM test.a\n" +
                "WHERE a.field0 = 0\n" +
                "\tAND (a.field1 LIKE BINARY '1'\n" +
                "\t\tOR a.field2 = 2)\n" +
                "LIMIT 10", result);
    }

    public void test_mysql_5() throws Exception {
        String sql = " SELECT * FROM order_biz GROUP BY product_id";
        String result = PagerUtils.limit(sql, JdbcConstants.MYSQL, 0, 10);
        assertEquals("SELECT *\n" +
                "FROM order_biz\n" +
                "GROUP BY product_id\n" +
                "LIMIT 10", result);
    }

    public void test_mysql_6() throws Exception {
        String sql = "select * from t limit 10";
        String result = PagerUtils.limit(sql, JdbcConstants.MYSQL, 0, 100, true);
        assertEquals("SELECT *\n" +
                "FROM t\n" +
                "LIMIT 10", result);
    }

    public void test_mysql_7() throws Exception {
        String sql = "select * from t limit 1000, 1000";
        String result = PagerUtils.limit(sql, JdbcConstants.MYSQL, 0, 100, true);
        assertEquals("SELECT *\n" +
                "FROM t\n" +
                "LIMIT 1000, 100", result);
    }
}

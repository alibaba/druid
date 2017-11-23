package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class Issue1695 extends TestCase {
    public void test_for_mysql() throws Exception {
        String sql = "select ht.* from t_books ht";
        String result = PagerUtils.count(sql, JdbcConstants.MYSQL);
        assertEquals("SELECT COUNT(*)\n" +
                "FROM t_books ht", result);
    }

    public void test_for_pg() throws Exception {
        String sql = "select ht.* from t_books ht";
        String result = PagerUtils.count(sql, JdbcConstants.POSTGRESQL);
        assertEquals("SELECT COUNT(*)\n" +
                "FROM t_books ht", result);
    }

    public void test_for_oracle() throws Exception {
        String sql = "select ht.* from t_books ht";
        String result = PagerUtils.count(sql, JdbcConstants.ORACLE);
        assertEquals("SELECT COUNT(*)\n" +
                "FROM t_books ht", result);
    }

    public void test_for_sqlserver() throws Exception {
        String sql = "select ht.* from t_books ht";
        String result = PagerUtils.count(sql, JdbcConstants.SQL_SERVER);
        assertEquals("SELECT COUNT(*)\n" +
                "FROM t_books ht", result);
    }
}

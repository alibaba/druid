package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

public class PagerUtilsTest_hasUnorderedLimit extends TestCase {

    public void test_false() throws Exception {
        String sql = " select * from test t order by id limit 3";
        assertFalse(PagerUtils.hasUnorderedLimit(sql, JdbcConstants.MYSQL));
    }

    public void test_false_1() throws Exception {
        String sql = " select * from test t";
        assertFalse(PagerUtils.hasUnorderedLimit(sql, JdbcConstants.MYSQL));
    }

    public void test_true() throws Exception {
        String sql = " select * from test t limit 3";
        assertTrue(PagerUtils.hasUnorderedLimit(sql, JdbcConstants.MYSQL));
    }

    public void test_true_subquery() throws Exception {
        String sql = "select * from(select * from test t limit 3) x";
        assertTrue(PagerUtils.hasUnorderedLimit(sql, JdbcConstants.MYSQL));
    }

    public void test_true_subquery_2() throws Exception {
        String sql = "select * from (select * from test t order by id desc) z limit 100";
        assertFalse(PagerUtils.hasUnorderedLimit(sql, JdbcConstants.MYSQL));
    }
}

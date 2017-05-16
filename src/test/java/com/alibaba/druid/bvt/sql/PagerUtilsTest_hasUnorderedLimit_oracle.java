package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class PagerUtilsTest_hasUnorderedLimit_oracle extends TestCase {

    public void test_false() throws Exception {
        String sql = "SELECT *" + //
                "\nFROM (SELECT XX.*, ROWNUM AS RN" + //
                "\n\tFROM (SELECT id, name, salary" + //
                "\n\t\tFROM t" + //
                "\n\t\tORDER BY id, name" + //
                "\n\t\t) XX" + //
                "\n\tWHERE ROWNUM <= 30" + //
                "\n\t) XXX" + //
                "\nWHERE RN > 20";
        assertFalse(PagerUtils.hasUnorderedLimit(sql, JdbcConstants.ORACLE));
    }

    public void test_false_1() throws Exception {
        String sql = " select * from test t";
        assertFalse(PagerUtils.hasUnorderedLimit(sql, JdbcConstants.ORACLE));
    }

    public void test_true() throws Exception {
        String sql = "SELECT *" + //
                "\nFROM t" + //
                "\nWHERE ROWNUM <= 10";
        assertTrue(PagerUtils.hasUnorderedLimit(sql, JdbcConstants.ORACLE));
    }

    public void test_true_1() throws Exception {
        String sql = "select * from (select * from t where id > 1 ) where rownum < 1000";
        assertTrue(PagerUtils.hasUnorderedLimit(sql, JdbcConstants.ORACLE));
    }
}

package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PagerUtilsTest_YashanDb {

    @Test
    public void test_count() throws Exception {
        String sql = "select * from t";
        String result = PagerUtils.count(sql, JdbcConstants.YASHANDB);
        assertEquals("SELECT COUNT(*)\n" +
                "FROM t", result);
    }

    @Test
    public void test_count_with_columns() throws Exception {
        String sql = "select id, name from t";
        String result = PagerUtils.count(sql, JdbcConstants.YASHANDB);
        assertEquals("SELECT COUNT(*)\n" +
                "FROM t", result);
    }

    @Test
    public void test_count_group_by() throws Exception {
        String sql = "select type, count(*) from t group by type";
        String result = PagerUtils.count(sql, JdbcConstants.YASHANDB);
        assertEquals("SELECT COUNT(*)\n" +
                "FROM (\n" +
                "\tSELECT type, count(*)\n" +
                "\tFROM t\n" +
                "\tGROUP BY type\n" +
                ") ALIAS_COUNT", result);
    }

    @Test
    public void test_limit_offset_zero() throws Exception {
        String sql = "select * from t";
        String result = PagerUtils.limit(sql, JdbcConstants.YASHANDB, 0, 10);
        assertEquals("SELECT *\n" +
                "FROM t\n" +
                "WHERE ROWNUM <= 10", result);
    }

    @Test
    public void test_limit_with_offset() throws Exception {
        String sql = "select * from t";
        String result = PagerUtils.limit(sql, JdbcConstants.YASHANDB, 10, 10);
        assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT XX.*, ROWNUM AS RN\n" +
                "\tFROM (\n" +
                "\t\tSELECT *\n" +
                "\t\tFROM t\n" +
                "\t) XX\n" +
                "\tWHERE ROWNUM <= 20\n" +
                ") XXX\n" +
                "WHERE RN > 10", result);
    }

    @Test
    public void test_limit_with_order_by() throws Exception {
        String sql = "select * from t order by id";
        String result = PagerUtils.limit(sql, JdbcConstants.YASHANDB, 0, 10);
        assertEquals("SELECT XX.*, ROWNUM AS RN\n" +
                "FROM (\n" +
                "\tSELECT *\n" +
                "\tFROM t\n" +
                "\tORDER BY id\n" +
                ") XX\n" +
                "WHERE ROWNUM <= 10", result);
    }
}

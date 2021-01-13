package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcConstants;

public class PagerUtilsTest_Count_PG_0 extends TestCase {

    public void test_pg_0() throws Exception {
        String sql = "select * from t";
        String result = PagerUtils.count(sql, JdbcConstants.POSTGRESQL);
        Assert.assertEquals("SELECT COUNT(*)\n" + //
                            "FROM t", result);
    }

    public void test_pg_1() throws Exception {
        String sql = "select id, name from t";
        String result = PagerUtils.count(sql, JdbcConstants.POSTGRESQL);
        Assert.assertEquals("SELECT COUNT(*)\n" + //
                            "FROM t", result);
    }

    public void test_pg_2() throws Exception {
        String sql = "select id, name from t order by id";
        String result = PagerUtils.count(sql, JdbcConstants.POSTGRESQL);
        Assert.assertEquals("SELECT COUNT(*)\n" + //
                            "FROM t", result);
    }

    public void test_pg_3() throws Exception {
        String sql = "select * from test where shape.STIntersects(geometry::STGeomFromText('POLYGON ((86610.054 86610.054,112372.95799999963 88785.5940000005,112372.91199999955 88675.996999999508,86610.054 86610.054))',0))=1;";
        String countSql = PagerUtils.count(sql, JdbcConstants.POSTGRESQL);
        assertEquals("SELECT COUNT(*)\n" +
                "FROM test\n" +
                "WHERE shape.STIntersects(geometry::STGeomFromText('POLYGON ((86610.054 86610.054,112372.95799999963 88785.5940000005,112372.91199999955 88675.996999999508,86610.054 86610.054))', 0)) = 1", countSql);

        String limitSql = PagerUtils.limit(sql, JdbcConstants.POSTGRESQL, 100, 10);
        assertEquals("SELECT *\n" +
                "FROM test\n" +
                "WHERE shape.STIntersects(geometry::STGeomFromText('POLYGON ((86610.054 86610.054,112372.95799999963 88785.5940000005,112372.91199999955 88675.996999999508,86610.054 86610.054))', 0)) = 1\n" +
                "LIMIT 10 OFFSET 100", limitSql);
    }

    public void test_pg_group_0() throws Exception {
        String sql = "select type, count(*) from t group by type";
        String result = PagerUtils.count(sql, JdbcConstants.POSTGRESQL);
        Assert.assertEquals("SELECT COUNT(*)\n" +
                "FROM (\n" +
                "\tSELECT type, count(*)\n" +
                "\tFROM t\n" +
                "\tGROUP BY type\n" +
                ") ALIAS_COUNT", result);
    }

    public void test_pg_union_0() throws Exception {
        String sql = "select id, name from t1 union select id, name from t2 order by id";
        String result = PagerUtils.count(sql, JdbcConstants.POSTGRESQL);
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

package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcConstants;

public class PagerUtilsTest_Limit_db2_0 extends TestCase {

    public void test_mysql_0() throws Exception {
        String sql = "select * from t";
        String result = PagerUtils.limit(sql, JdbcConstants.DB2, 0, 10);
        Assert.assertEquals("SELECT *" + //
                            "\nFROM t" + //
                            "\nFETCH FIRST 10 ROWS ONLY", result);
    }

    public void test_mysql_1() throws Exception {
        String sql = "select * from t";
        String result = PagerUtils.limit(sql, JdbcConstants.DB2, 10, 10);
        Assert.assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT *, ROW_NUMBER() OVER () AS ROWNUM\n" +
                "\tFROM t\n" +
                ") XX\n" +
                "WHERE ROWNUM > 10\n" +
                "\tAND ROWNUM <= 20", result);
    }

    public void test_mysql_2() throws Exception {
        String sql = "select * from t where age > 100";
        String result = PagerUtils.limit(sql, JdbcConstants.DB2, 20, 10);
        Assert.assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT *, ROW_NUMBER() OVER () AS ROWNUM\n" +
                "\tFROM t\n" +
                "\tWHERE age > 100\n" +
                ") XX\n" +
                "WHERE ROWNUM > 20\n" +
                "\tAND ROWNUM <= 30", result);
    }

    public void test_mysql_3() throws Exception {
        String sql = "select id, name, salary from t order by id, name";
        String result = PagerUtils.limit(sql, JdbcConstants.DB2, 20, 10);
        Assert.assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT id, name, salary, ROW_NUMBER() OVER (ORDER BY id, name) AS ROWNUM\n" +
                "\tFROM t\n" +
                ") XX\n" +
                "WHERE ROWNUM > 20\n" +
                "\tAND ROWNUM <= 30", result);
    }

    public void test_fetch_order() {
        String  sql="SELECT t.CUSTNAME AS custname\n" +
                "FROM CPS_LOAN_INFO t WHERE t.DEL_FLAG = 0\n" +
                "ORDER BY t.CREATE_TIME DESC";

        String limitSql=PagerUtils.limit(sql, JdbcConstants.DB2,0,20)  ;
        int fetchRowIndex=limitSql.indexOf("FETCH FIRST");
        int orderIndex=limitSql.indexOf("ORDER BY") ;
        Assert.assertTrue(fetchRowIndex>orderIndex);
    }
}

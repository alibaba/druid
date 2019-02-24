package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcConstants;

public class PagerUtilsTest_Limit_oracle_0 extends TestCase {

    public void test_oracle_oderby_0() throws Exception {
        String sql = "select * from t order by id";
        String result = PagerUtils.limit(sql, JdbcConstants.ORACLE, 0, 10);
        Assert.assertEquals("SELECT XX.*, ROWNUM AS RN\n" +
                "FROM (\n" +
                "\tSELECT *\n" +
                "\tFROM t\n" +
                "\tORDER BY id\n" +
                ") XX\n" +
                "WHERE ROWNUM <= 10", result);
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
        Assert.assertEquals("SELECT *\n" +
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

    public void test_oracle_2() throws Exception {
        String sql = "select * from t";
        String result = PagerUtils.limit(sql, JdbcConstants.ORACLE, 20, 10);
        Assert.assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT XX.*, ROWNUM AS RN\n" +
                "\tFROM (\n" +
                "\t\tSELECT *\n" +
                "\t\tFROM t\n" +
                "\t) XX\n" +
                "\tWHERE ROWNUM <= 30\n" +
                ") XXX\n" +
                "WHERE RN > 20", result);
    }

    public void test_oracle_3() throws Exception {
        String sql = "select id, name, salary from t order by id, name";
        String result = PagerUtils.limit(sql, JdbcConstants.ORACLE, 20, 10);
        Assert.assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT XX.*, ROWNUM AS RN\n" +
                "\tFROM (\n" +
                "\t\tSELECT id, name, salary\n" +
                "\t\tFROM t\n" +
                "\t\tORDER BY id, name\n" +
                "\t) XX\n" +
                "\tWHERE ROWNUM <= 30\n" +
                ") XXX\n" +
                "WHERE RN > 20", result);
    }

    public void test_oracle_4() throws Exception {
        String sql = "SELECT TO_CHAR(ADD_MONTHS(TO_DATE('2014', 'yyyy'), (ROWNUM) * 12), 'yyyy') as YEAR\n" +
                "FROM DUAL\n" +
                "CONNECT BY ROWNUM <=\n" +
                "months_between(to_date(to_char(sysdate,'yyyy'),'yyyy') ,\n" +
                "to_date('2014', 'yyyy')) / 12";

        String result = PagerUtils.limit(sql, JdbcConstants.ORACLE, 20, 10);
        Assert.assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT XX.*, ROWNUM AS RN\n" +
                "\tFROM (\n" +
                "\t\tSELECT TO_CHAR(ADD_MONTHS(TO_DATE('2014', 'yyyy'), ROWNUM * 12), 'yyyy') AS YEAR\n" +
                "\t\tFROM DUAL\n" +
                "\t\tCONNECT BY ROWNUM <= months_between(to_date(to_char(SYSDATE, 'yyyy'), 'yyyy'), to_date('2014', 'yyyy')) / 12\n" +
                "\t) XX\n" +
                "\tWHERE ROWNUM <= 30\n" +
                ") XXX\n" +
                "WHERE RN > 20", result);
    }
}

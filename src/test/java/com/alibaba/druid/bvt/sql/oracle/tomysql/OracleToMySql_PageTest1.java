package com.alibaba.druid.bvt.sql.oracle.tomysql;

import org.junit.Assert;

import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;

public class OracleToMySql_PageTest1 extends TestCase {

    public void test_page() throws Exception {
        String sql = "SELECT *" + //
                     "\nFROM (SELECT XX.*, ROWNUM AS RN" + //
                     "\n\tFROM (SELECT *" + //
                     "\n\t\tFROM t" + //
                     "\n\t\tORDER BY id" + //
                     "\n\t\t) XX" + //
                     "\n\tWHERE ROWNUM <= 20" + //
                     "\n\t) XXX" + //
                     "\nWHERE RN > 10";

        String mysqlSql = SQLUtils.translateOracleToMySql(sql);
        Assert.assertEquals("SELECT *"//
                            + "\nFROM t"//
                            + "\nORDER BY id"//
                            + "\nLIMIT 10, 10", mysqlSql);
        System.out.println(mysqlSql);
    }
}

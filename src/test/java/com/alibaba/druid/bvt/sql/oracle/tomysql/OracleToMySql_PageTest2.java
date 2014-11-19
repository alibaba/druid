package com.alibaba.druid.bvt.sql.oracle.tomysql;

import org.junit.Assert;

import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;

public class OracleToMySql_PageTest2 extends TestCase {

    public void test_page() throws Exception {
        String sql = "SELECT XX.*, ROWNUM AS RN" + //
                     "\nFROM (SELECT *" + //
                     "\n\tFROM t" + //
                     "\n\tORDER BY id" + //
                     "\n\t) XX" + //
                     "\nWHERE ROWNUM < 10";

        String mysqlSql = SQLUtils.translateOracleToMySql(sql);
        Assert.assertEquals("SELECT *"//
                            + "\nFROM t"//
                            + "\nORDER BY id"//
                            + "\nLIMIT 9", mysqlSql);
        System.out.println(mysqlSql);
    }
}

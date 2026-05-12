package com.alibaba.druid.bvt.sql.oracle.tomysql;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OracleToMySql_PageTest2 {
    @Test
    public void test_page() throws Exception {
        String sql = "SELECT XX.*, ROWNUM AS RN" +
                "\nFROM (SELECT *" +
                "\n\tFROM t" +
                "\n\tORDER BY id" +
                "\n\t) XX" +
                "\nWHERE ROWNUM < 10";

        String mysqlSql = SQLUtils.translateOracleToMySql(sql);
        assertEquals("SELECT *"//
                + "\nFROM t"//
                + "\nORDER BY id"//
                + "\nLIMIT 9", mysqlSql);
        System.out.println(mysqlSql);
    }
}

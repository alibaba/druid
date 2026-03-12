package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PagerUtilsTest_Limit_SQLServer_2 {
    @Test
    public void test_db2_union() throws Exception {
        String sql = "select * from t1 union select * from t2";
        String result = PagerUtils.limit(sql, JdbcConstants.SQL_SERVER, 0, 10);
        assertEquals("SELECT TOP 10 XX.*\n" +
                "FROM (\n" +
                "\tSELECT *\n" +
                "\tFROM t1\n" +
                "\tUNION\n" +
                "\tSELECT *\n" +
                "\tFROM t2\n" +
                ") XX", result);
    }
}

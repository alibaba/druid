package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PagerUtilsTest_Limit_db2_1 {
    @Test
    public void test_db2_union() throws Exception {
        String sql = "select * from t1 union select * from t2";
        String result = PagerUtils.limit(sql, JdbcConstants.DB2, 20, 10);
        assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT XX.*, ROW_NUMBER() OVER () AS ROWNUM\n" +
                "\tFROM (\n" +
                "\t\tSELECT *\n" +
                "\t\tFROM t1\n" +
                "\t\tUNION\n" +
                "\t\tSELECT *\n" +
                "\t\tFROM t2\n" +
                "\t) XX\n" +
                ") XXX\n" +
                "WHERE ROWNUM > 20\n" +
                "\tAND ROWNUM <= 30", result);
    }
}

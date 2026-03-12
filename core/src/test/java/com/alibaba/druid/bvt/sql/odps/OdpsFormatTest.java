package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsFormatTest {
    @Test
    public void test_format() throws Exception {
        String sql = "select * from t1; ;select * from t2;";
        assertEquals("SELECT *"
                + "\nFROM t1;"
                + "\n"
                + "\nSELECT *"
                + "\nFROM t2;", SQLUtils.formatOdps(sql));
    }

    @Test
    public void test_no_semi() throws Exception {
        String sql = "select * from t1; ;select * from t2";
        assertEquals("SELECT *"
                + "\nFROM t1;"
                + "\n"
                + "\nSELECT *"
                + "\nFROM t2", SQLUtils.formatOdps(sql));
    }
}

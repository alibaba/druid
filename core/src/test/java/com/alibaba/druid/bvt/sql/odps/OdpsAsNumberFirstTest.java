package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsAsNumberFirstTest {
    @Test
    public void test_0() throws Exception {
        String sql = "select id as 39dd"
                + "\n from t1";
        assertEquals("SELECT id AS 39dd"
                + "\nFROM t1", SQLUtils.formatOdps(sql));
    }
}

package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsIfTest {
    @Test
    public void test_if() throws Exception {
        String sql = "select sum(if(a > 0, 1, 0)) from t1";
        assertEquals("SELECT sum(IF(a > 0, 1, 0))"
                + "\nFROM t1", SQLUtils.formatOdps(sql));
    }
}

package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsUDFTest {
    @Test
    public void test_if() throws Exception {
        String sql = "select secods:ip_region('192.168.1.1', 'city') from dual";
        assertEquals("SELECT secods:ip_region('192.168.1.1', 'city')"
                + "\nFROM dual", SQLUtils.formatOdps(sql));
    }
}

package com.alibaba.druid.bvt.utils;

import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcUtilsTest_for_odps {
    @Test
    public void test_odps() throws Exception {
        assertEquals(JdbcConstants.ODPS_DRIVER, JdbcUtils.getDriverClassName("jdbc:odps:"));
    }

    @Test
    public void test_odps_dbtype() throws Exception {
        assertEquals(JdbcConstants.ODPS, JdbcUtils.getDbTypeRaw("jdbc:odps:", null));
    }
}

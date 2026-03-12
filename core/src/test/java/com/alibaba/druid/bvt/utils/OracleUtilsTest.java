package com.alibaba.druid.bvt.utils;

import com.alibaba.druid.util.OracleUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by wenshao on 19/06/2017.
 */
public class OracleUtilsTest {
    @Test
    public void test_builtin() throws Exception {
        assertTrue(OracleUtils.isBuiltinFunction("nvl"));
        assertTrue(OracleUtils.isBuiltinFunction("NVL"));
        assertFalse(OracleUtils.isBuiltinFunction("xxx_nvl"));

        assertTrue(OracleUtils.isBuiltinTable("user_ts_quotas"));
        assertTrue(OracleUtils.isBuiltinTable("user_TS_quotas"));
        assertFalse(OracleUtils.isBuiltinTable("user_TS_quotas_xxxxxxxxxxxx"));
    }
}

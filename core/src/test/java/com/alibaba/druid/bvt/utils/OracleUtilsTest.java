package com.alibaba.druid.bvt.utils;

import com.alibaba.druid.util.OracleUtils;
import junit.framework.TestCase;

/**
 * Created by wenshao on 19/06/2017.
 */
public class OracleUtilsTest extends TestCase {
    public void test_builtin() throws Exception {
        assertTrue(OracleUtils.isBuiltinFunction("nvl"));
        assertTrue(OracleUtils.isBuiltinFunction("NVL"));
        assertFalse(OracleUtils.isBuiltinFunction("xxx_nvl"));

        assertTrue(OracleUtils.isBuiltinTable("user_ts_quotas"));
        assertTrue(OracleUtils.isBuiltinTable("user_TS_quotas"));
        assertFalse(OracleUtils.isBuiltinTable("user_TS_quotas_xxxxxxxxxxxx"));
    }
}

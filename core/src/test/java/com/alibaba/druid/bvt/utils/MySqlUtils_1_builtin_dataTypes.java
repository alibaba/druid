package com.alibaba.druid.bvt.utils;

import com.alibaba.druid.util.MySqlUtils;
import junit.framework.TestCase;

public class MySqlUtils_1_builtin_dataTypes extends TestCase {
    public void test_builtin_dataTypes() throws Exception {
        assertTrue(MySqlUtils.isBuiltinDataType("decimal"));
        assertTrue(MySqlUtils.isBuiltinDataType("INT"));
        assertFalse(MySqlUtils.isBuiltinDataType("decimalx"));
    }
}

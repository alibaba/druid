package com.alibaba.druid.bvt.utils;

import com.alibaba.druid.util.MySqlUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlUtils_1_builtin_dataTypes {
    @Test
    public void test_builtin_dataTypes() throws Exception {
        assertTrue(MySqlUtils.isBuiltinDataType("decimal"));
        assertTrue(MySqlUtils.isBuiltinDataType("INT"));
        assertFalse(MySqlUtils.isBuiltinDataType("decimalx"));
    }
}

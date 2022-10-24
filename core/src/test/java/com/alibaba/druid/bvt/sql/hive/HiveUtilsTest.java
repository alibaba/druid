package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.util.HiveUtils;
import junit.framework.TestCase;

public class HiveUtilsTest extends TestCase {
    public void test_for_hive() {
        assertTrue(HiveUtils.isBuiltinDataType("STRING"));
        assertTrue(HiveUtils.isBuiltinDataType("VARCHAR"));
        assertTrue(HiveUtils.isBuiltinDataType("CHAR"));
        assertTrue(HiveUtils.isBuiltinDataType("DECIMAL"));
        assertTrue(HiveUtils.isBuiltinDataType("NUMERIC"));
        assertTrue(HiveUtils.isBuiltinDataType("TINYINT"));
        assertTrue(HiveUtils.isBuiltinDataType("BIGINT"));
        assertTrue(HiveUtils.isBuiltinDataType("BOOLEAN"));
        assertTrue(HiveUtils.isBuiltinDataType("INTERVAL"));
    }
}

package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.PagerUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PagerUtilsTest_Limit_hive_0 {
    @Test
    public void test_hive_0() throws Exception {
        String result = PagerUtils.limit("SELECT * FROM test", DbType.hive, 0, 10);
        System.out.println(result);
        assertEquals("SELECT *\n" +
                "FROM test\n" +
                "LIMIT 10", result);
    }

    @Test
    public void test_odps_0() throws Exception {
        String result = PagerUtils.limit("SELECT * FROM test", DbType.odps, 0, 10);
        System.out.println(result);
        assertEquals("SELECT *\n" +
                "FROM test\n" +
                "LIMIT 10", result);
    }
}

package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsAnalyzeTableTest {
    @Test
    public void test_0() throws Exception {
        String sql = "analyze table t partition(pt='1') compute statistics";
        assertEquals("ANALYZE TABLE t PARTITION (pt = '1') COMPUTE STATISTICS", SQLUtils.formatOdps(sql));
    }

    @Test
    public void test_no_partition() throws Exception {
        String sql = "analyze table t compute statistics";
        assertEquals("ANALYZE TABLE t COMPUTE STATISTICS", SQLUtils.formatOdps(sql));
    }
}

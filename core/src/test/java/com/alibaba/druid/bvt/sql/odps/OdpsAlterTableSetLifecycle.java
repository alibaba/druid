package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsAlterTableSetLifecycle {
    @Test
    public void test_if() throws Exception {
        String sql = "alter table test_lifecycle set lifecycle 50;";
        assertEquals("ALTER TABLE test_lifecycle"
                + "\n\tSET LIFECYCLE 50;", SQLUtils.formatOdps(sql));
    }
}

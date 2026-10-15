package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsAlterTableMergeSmallFilesTest {
    @Test
    public void test_touch() throws Exception {
        String sql = "ALTER TABLE abc_dev.tdl_mytable_xx MERGE SMALLFILES;";
        assertEquals("ALTER TABLE abc_dev.tdl_mytable_xx MERGE SMALLFILES;", SQLUtils.formatOdps(sql));
    }
}

package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsAlterTableTouchTest {
    @Test
    public void test_touch() throws Exception {
        String sql = "alter table test_lifecycle touch;";
        assertEquals("ALTER TABLE test_lifecycle"
                + "\n\tTOUCH;", SQLUtils.formatOdps(sql));
    }

    @Test
    public void test_touch_partition() throws Exception {
        String sql = "alter table test_lifecycle touch PARTITION (dt='20141111');";
        assertEquals("ALTER TABLE test_lifecycle"
                + "\n\tTOUCH PARTITION (dt = '20141111');", SQLUtils.formatOdps(sql));
    }
}

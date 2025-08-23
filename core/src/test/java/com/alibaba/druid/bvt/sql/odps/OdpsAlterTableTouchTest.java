package com.alibaba.druid.bvt.sql.odps;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsAlterTableTouchTest extends TestCase {
    public void test_touch() throws Exception {
        String sql = "alter table test_lifecycle touch;";
        assertEquals("ALTER TABLE test_lifecycle" //
                + "\n\tTOUCH;", SQLUtils.formatOdps(sql));
    }

    public void test_touch_partition() throws Exception {
        String sql = "alter table test_lifecycle touch PARTITION (dt='20141111');";
        assertEquals("ALTER TABLE test_lifecycle" //
                + "\n\tTOUCH PARTITION (dt = '20141111');", SQLUtils.formatOdps(sql));
    }
}

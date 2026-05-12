package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsAlterTableEnableLifecycle {
    @Test
    public void test_no_partition() throws Exception {
        String sql = "ALTER TABLE trans  ENABLE LIFECYCLE;";
        assertEquals("ALTER TABLE trans"
                + "\n\tENABLE LIFECYCLE;", SQLUtils.formatOdps(sql));

        assertEquals("alter table trans"
                + "\n\tenable lifecycle;", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }

    @Test
    public void test_has_partition() throws Exception {
        String sql = "ALTER TABLE trans PARTITION(dt='20141111') ENABLE LIFECYCLE;";
        assertEquals("ALTER TABLE trans"
                + "\n\tPARTITION (dt = '20141111') ENABLE LIFECYCLE;", SQLUtils.formatOdps(sql));

        assertEquals("alter table trans"
                + "\n\tpartition (dt = '20141111') enable lifecycle;", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}

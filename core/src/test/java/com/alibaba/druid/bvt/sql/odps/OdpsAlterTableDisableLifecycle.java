package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsAlterTableDisableLifecycle {
    @Test
    public void test_no_partition() throws Exception {
        String sql = "ALTER TABLE trans  DISABLE LIFECYCLE;";
        assertEquals("ALTER TABLE trans"
                + "\n\tDISABLE LIFECYCLE;", SQLUtils.formatOdps(sql));

        assertEquals("alter table trans"
                + "\n\tdisable lifecycle;", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }

    @Test
    public void test_has_partition() throws Exception {
        String sql = "ALTER TABLE trans PARTITION(dt='20141111') DISABLE LIFECYCLE;";
        assertEquals("ALTER TABLE trans"
                + "\n\tPARTITION (dt = '20141111') DISABLE LIFECYCLE;", SQLUtils.formatOdps(sql));

        assertEquals("alter table trans"
                + "\n\tpartition (dt = '20141111') disable lifecycle;", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}

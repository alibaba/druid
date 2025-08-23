package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;
import static org.junit.Assert.*;

public class OdpsAlterTableDropPartitionTest2 extends TestCase {
    public void test_if() throws Exception {
        String sql = "alter table myp.table2 drop if exists  partition(ds=20161209) \n";
        assertEquals("ALTER TABLE myp.table2\n" +
                "\tDROP IF EXISTS PARTITION (ds = 20161209)", SQLUtils.formatOdps(sql));
        assertEquals("alter table myp.table2\n" +
                "\tdrop if exists partition (ds = 20161209)", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}

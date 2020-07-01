package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsAlterTableEnableLifecycle extends TestCase {

    public void test_no_partition() throws Exception {
        String sql = "ALTER TABLE trans  ENABLE LIFECYCLE;";
        Assert.assertEquals("ALTER TABLE trans" //
                + "\n\tENABLE LIFECYCLE;", SQLUtils.formatOdps(sql));
        
        Assert.assertEquals("alter table trans" //
                            + "\n\tenable lifecycle;", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
    

    public void test_has_partition() throws Exception {
        String sql = "ALTER TABLE trans PARTITION(dt='20141111') ENABLE LIFECYCLE;";
        Assert.assertEquals("ALTER TABLE trans"
                + "\n\tPARTITION (dt = '20141111') ENABLE LIFECYCLE;", SQLUtils.formatOdps(sql));
        
        Assert.assertEquals("alter table trans"
                + "\n\tpartition (dt = '20141111') enable lifecycle;", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}

package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsAlterTableDisableLifecycle extends TestCase {

    public void test_no_partition() throws Exception {
        String sql = "ALTER TABLE trans  DISABLE LIFECYCLE;";
        Assert.assertEquals("ALTER TABLE trans" //
                + "\n\tDISABLE LIFECYCLE;", SQLUtils.formatOdps(sql));
    }
    

    public void test_has_partition() throws Exception {
        String sql = "ALTER TABLE trans PARTITION(dt='20141111') DISABLE LIFECYCLE;";
        Assert.assertEquals("ALTER TABLE trans"
                + "\n\tPARTITION (dt = '20141111') DISABLE LIFECYCLE;", SQLUtils.formatOdps(sql));
    }
}

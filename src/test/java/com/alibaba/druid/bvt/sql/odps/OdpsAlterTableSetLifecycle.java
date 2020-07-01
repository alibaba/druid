package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsAlterTableSetLifecycle extends TestCase {

    public void test_if() throws Exception {
        String sql = "alter table test_lifecycle set lifecycle 50;";
        Assert.assertEquals("ALTER TABLE test_lifecycle" //
                + "\n\tSET LIFECYCLE 50;", SQLUtils.formatOdps(sql));
    }
}

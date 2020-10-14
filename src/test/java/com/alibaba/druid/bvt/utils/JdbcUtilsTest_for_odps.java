package com.alibaba.druid.bvt.utils;

import org.junit.Assert;

import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

import junit.framework.TestCase;

public class JdbcUtilsTest_for_odps extends TestCase {
    public void test_odps() throws Exception {
        Assert.assertEquals(JdbcConstants.ODPS_DRIVER, JdbcUtils.getDriverClassName("jdbc:odps:"));
    }
    public void test_odps_dbtype() throws Exception {
        Assert.assertEquals(JdbcConstants.ODPS, JdbcUtils.getDbTypeRaw("jdbc:odps:", null));
    }
}

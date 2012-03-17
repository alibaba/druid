package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

/**
 * 这个场景测试访问SQLServer系统表
 *
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class SQLServerWallPermitTableTest extends TestCase {
    public void test_permitTable() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("select * from sysObjects"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("select * from sysdatabases"));
    }
}

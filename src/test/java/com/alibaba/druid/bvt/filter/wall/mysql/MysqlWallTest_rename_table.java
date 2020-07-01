package com.alibaba.druid.bvt.filter.wall.mysql;

import org.junit.Assert;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

import junit.framework.TestCase;

public class MysqlWallTest_rename_table extends TestCase {

    public void test_true() throws Exception {
        WallConfig config = new WallConfig();
        config.setRenameTableAllow(true);
        Assert.assertTrue(WallUtils.isValidateMySql("RENAME TABLE t1 TO t2", config));
    }
    
    public void test_false() throws Exception {
        WallConfig config = new WallConfig();
        config.setRenameTableAllow(false);
        Assert.assertFalse(WallUtils.isValidateMySql("RENAME TABLE t1 TO t2", config));
    }
}

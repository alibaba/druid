package com.alibaba.druid.bvt.filter.wall.mysql;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MysqlWallTest_rename_table {
    @Test
    public void test_true() throws Exception {
        WallConfig config = new WallConfig();
        config.setRenameTableAllow(true);
        assertTrue(WallUtils.isValidateMySql("RENAME TABLE t1 TO t2", config));
    }

    @Test
    public void test_false() throws Exception {
        WallConfig config = new WallConfig();
        config.setRenameTableAllow(false);
        assertFalse(WallUtils.isValidateMySql("RENAME TABLE t1 TO t2", config));
    }
}

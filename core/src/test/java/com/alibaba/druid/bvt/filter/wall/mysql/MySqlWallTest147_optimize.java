package com.alibaba.druid.bvt.filter.wall.mysql;

import static org.junit.Assert.assertTrue;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import junit.framework.TestCase;

public class MySqlWallTest147_optimize extends TestCase {
    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        String sql = "optimize table table1";
        assertTrue(
                provider.checkValid(sql)
        );
    }
}

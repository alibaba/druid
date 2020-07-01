package com.alibaba.druid.bvt.filter.wall.mysql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

public class MySqlWallTest145 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        String sql = "SHOW FULL TABLES WHERE Table_type != 'VIEW'";
        Assert.assertTrue(
                provider.checkValid(sql)
        );
    }
}

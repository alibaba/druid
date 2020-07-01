package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

public class WallStatTest_blacklist extends TestCase {

    private String sql = "select * from t where id = ? and 1 = 1";

    protected void setUp() throws Exception {
        WallContext.clearContext();
    }

    protected void tearDown() throws Exception {
        WallContext.clearContext();
    }

    public void testMySql() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        for (int i = 0; i < 10; ++i) {
            Assert.assertFalse(provider.checkValid(sql));
        }

        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(10, tableStat.getSelectCount());
        Assert.assertEquals(0, provider.getWhiteListHitCount());
        Assert.assertEquals(9, provider.getBlackListHitCount());
    }

}

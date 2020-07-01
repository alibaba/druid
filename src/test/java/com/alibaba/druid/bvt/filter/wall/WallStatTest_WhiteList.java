package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

public class WallStatTest_WhiteList extends TestCase {

    protected void setUp() throws Exception {
        WallContext.clearContext();
    }

    protected void tearDown() throws Exception {
        WallContext.clearContext();
    }

    public void testMySql() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        for (int i = 0; i < 3001; ++i) {
            String sql = "select * from t where id = " + i;
            Assert.assertTrue(provider.checkValid(sql));
        }

        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(3001, tableStat.getSelectCount());
        Assert.assertEquals(0, provider.getBlackListHitCount());
        Assert.assertEquals(3000, provider.getWhiteListHitCount());
        Assert.assertEquals(1, provider.getWhiteList().size());
        Assert.assertEquals(3001, provider.getCheckCount());
    }

}

package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

public class WallStatTest_blackList_1 extends TestCase {

    protected void setUp() throws Exception {
        WallContext.clearContext();
    }

    protected void tearDown() throws Exception {
        WallContext.clearContext();
    }

    public void testMySql() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        for (int i = 0; i < 1001; ++i) {
            String sql = "select * from t where id = " + i + " OR 1 = 1";
            Assert.assertFalse(provider.checkValid(sql));
        }

        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(1001, tableStat.getSelectCount());
        Assert.assertEquals(0, provider.getBlackListHitCount());
        Assert.assertEquals(0, provider.getWhiteListHitCount());
        Assert.assertEquals(0, provider.getWhiteList().size());
        Assert.assertEquals(200, provider.getBlackList().size());
        Assert.assertEquals(1001, provider.getCheckCount());
    }

}

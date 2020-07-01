package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

public class WallStatTest_whiteList_syntaxError extends TestCase {

    protected void setUp() throws Exception {
        WallContext.clearContext();
    }

    protected void tearDown() throws Exception {
        WallContext.clearContext();
    }

    public void testMySql() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        provider.getConfig().setStrictSyntaxCheck(false);

        String sql = "select xx * x *";
        Assert.assertTrue(provider.checkValid(sql));
        {
            WallTableStat tableStat = provider.getTableStat("t");
            Assert.assertEquals(0, tableStat.getSelectCount());
            Assert.assertEquals(0, provider.getBlackListHitCount());
            Assert.assertEquals(0, provider.getWhiteListHitCount());
            Assert.assertEquals(0, provider.getWhiteList().size());
            Assert.assertEquals(0, provider.getBlackList().size());
            Assert.assertEquals(1, provider.getCheckCount());
            Assert.assertEquals(1, provider.getSyntaxErrorCount());
            Assert.assertEquals(1, provider.getHardCheckCount());
        }
        
        Assert.assertTrue(provider.checkValid(sql));
        {
            WallTableStat tableStat = provider.getTableStat("t");
            Assert.assertEquals(0, tableStat.getSelectCount());
            Assert.assertEquals(0, provider.getBlackListHitCount());
            Assert.assertEquals(0, provider.getWhiteListHitCount());
            Assert.assertEquals(0, provider.getWhiteList().size());
            Assert.assertEquals(0, provider.getBlackList().size());
            Assert.assertEquals(2, provider.getCheckCount());
            Assert.assertEquals(2, provider.getSyntaxErrorCount());
            Assert.assertEquals(2, provider.getHardCheckCount());
        }
    }

}

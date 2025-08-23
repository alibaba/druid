package com.alibaba.druid.bvt.filter.wall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import junit.framework.TestCase;


import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

public class WallStatTest_blackList_syntaxError extends TestCase {
    protected void setUp() throws Exception {
        WallContext.clearContext();
    }

    protected void tearDown() throws Exception {
        WallContext.clearContext();
    }

    public void testMySql() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        String sql = "select xx * x *";
        assertFalse(provider.checkValid(sql));
        {
            WallTableStat tableStat = provider.getTableStat("t");
            assertEquals(0, tableStat.getSelectCount());
            assertEquals(0, provider.getBlackListHitCount());
            assertEquals(0, provider.getWhiteListHitCount());
            assertEquals(0, provider.getWhiteList().size());
            assertEquals(1, provider.getBlackList().size());
            assertEquals(1, provider.getCheckCount());
            assertEquals(1, provider.getSyntaxErrorCount());
            assertEquals(1, provider.getHardCheckCount());
        }

        assertFalse(provider.checkValid(sql));
        {
            WallTableStat tableStat = provider.getTableStat("t");
            assertEquals(0, tableStat.getSelectCount());
            assertEquals(1, provider.getBlackListHitCount());
            assertEquals(0, provider.getWhiteListHitCount());
            assertEquals(0, provider.getWhiteList().size());
            assertEquals(1, provider.getBlackList().size());
            assertEquals(2, provider.getCheckCount());
            assertEquals(2, provider.getSyntaxErrorCount());
            assertEquals(1, provider.getHardCheckCount());
        }
    }

}

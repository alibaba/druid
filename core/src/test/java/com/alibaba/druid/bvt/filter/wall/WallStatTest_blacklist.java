package com.alibaba.druid.bvt.filter.wall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import junit.framework.TestCase;


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
            assertTrue(provider.checkValid(sql));
        }

        WallTableStat tableStat = provider.getTableStat("t");
        assertEquals(10, tableStat.getSelectCount());
        assertEquals(9, provider.getWhiteListHitCount());
        assertEquals(0, provider.getBlackListHitCount());

        provider.reset();;
        provider.getConfig().setConditionAndAlwayTrueAllow(false);
        for (int i = 0; i < 10; ++i) {
            assertFalse(provider.checkValid(sql));
        }
        tableStat = provider.getTableStat("t");
        assertEquals(10, tableStat.getSelectCount());
        assertEquals(0, provider.getWhiteListHitCount());
        assertEquals(9, provider.getBlackListHitCount());

    }

}

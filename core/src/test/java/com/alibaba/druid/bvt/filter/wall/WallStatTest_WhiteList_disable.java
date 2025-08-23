package com.alibaba.druid.bvt.filter.wall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import junit.framework.TestCase;


import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

public class WallStatTest_WhiteList_disable extends TestCase {
    protected void setUp() throws Exception {
        WallContext.clearContext();
    }

    protected void tearDown() throws Exception {
        WallContext.clearContext();
    }

    public void testMySql() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        provider.setBlackListEnable(false);
        provider.setWhiteListEnable(false);

        for (int i = 0; i < 301; ++i) {
            String sql = "select * from t where id = " + i;
            assertTrue(provider.checkValid(sql));
        }

        for (int i = 0; i < 301; ++i) {
            String sql = "select * from t where id = " + i + " OR 1 = 1";
            assertFalse(provider.checkValid(sql));
        }

        WallTableStat tableStat = provider.getTableStat("t");
        assertEquals(602, tableStat.getSelectCount());
        assertEquals(0, provider.getBlackListHitCount());
        assertEquals(0, provider.getWhiteListHitCount());
        assertEquals(0, provider.getWhiteList().size());
        assertEquals(602, provider.getCheckCount());
    }

}

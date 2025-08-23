package com.alibaba.druid.bvt.filter.wall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import junit.framework.TestCase;


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
            assertTrue(provider.checkValid(sql));
        }

        WallTableStat tableStat = provider.getTableStat("t");
        assertEquals(3001, tableStat.getSelectCount());
        assertEquals(0, provider.getBlackListHitCount());
        assertEquals(3000, provider.getWhiteListHitCount());
        assertEquals(1, provider.getWhiteList().size());
        assertEquals(3001, provider.getCheckCount());
    }

}

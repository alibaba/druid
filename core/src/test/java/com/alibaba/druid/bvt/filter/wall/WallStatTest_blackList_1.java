package com.alibaba.druid.bvt.filter.wall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import junit.framework.TestCase;


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
            assertFalse(provider.checkValid(sql));
        }

        WallTableStat tableStat = provider.getTableStat("t");
        assertEquals(1001, tableStat.getSelectCount());
        assertEquals(1000, provider.getBlackListHitCount());
        assertEquals(0, provider.getWhiteListHitCount());
        assertEquals(0, provider.getWhiteList().size());
        assertEquals(1, provider.getBlackList().size());
        assertEquals(1001, provider.getCheckCount());
    }


    public void testMysql2() {
        WallProvider provider = new MySqlWallProvider();

        for (int i = 0; i < 1001; ++i) {
            String sql = "select * from t where field_" + i + " = " + i + " OR 1 = 1";
            assertFalse(provider.checkValid(sql));
        }

        WallTableStat tableStat = provider.getTableStat("t");
        assertEquals(1001, tableStat.getSelectCount());
        assertEquals(0, provider.getBlackListHitCount());
        assertEquals(0, provider.getWhiteListHitCount());
        assertEquals(0, provider.getWhiteList().size());
        assertEquals(WallProvider.BLACK_SQL_MAX_SIZE, provider.getBlackList().size());
        assertEquals(1001, provider.getCheckCount());
    }
}

package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WallStatTest_blackList_1 {
    @BeforeEach
    protected void setUp() throws Exception {
        WallContext.clearContext();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        WallContext.clearContext();
    }

    @Test
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

    @Test
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

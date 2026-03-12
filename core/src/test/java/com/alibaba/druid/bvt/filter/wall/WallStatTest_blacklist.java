package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WallStatTest_blacklist {
    private String sql = "select * from t where id = ? and 1 = 1";

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
        for (int i = 0; i < 10; ++i) {
            assertTrue(provider.checkValid(sql));
        }

        WallTableStat tableStat = provider.getTableStat("t");
        assertEquals(10, tableStat.getSelectCount());
        assertEquals(9, provider.getWhiteListHitCount());
        assertEquals(0, provider.getBlackListHitCount());

        provider.reset();
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

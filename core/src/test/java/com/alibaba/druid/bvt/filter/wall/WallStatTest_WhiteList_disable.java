package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WallStatTest_WhiteList_disable {
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

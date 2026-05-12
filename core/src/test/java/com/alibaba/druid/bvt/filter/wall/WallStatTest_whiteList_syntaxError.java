package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WallStatTest_whiteList_syntaxError {
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
        provider.getConfig().setStrictSyntaxCheck(false);

        String sql = "select xx * x *";
        assertTrue(provider.checkValid(sql));
        {
            WallTableStat tableStat = provider.getTableStat("t");
            assertEquals(0, tableStat.getSelectCount());
            assertEquals(0, provider.getBlackListHitCount());
            assertEquals(0, provider.getWhiteListHitCount());
            assertEquals(0, provider.getWhiteList().size());
            assertEquals(0, provider.getBlackList().size());
            assertEquals(1, provider.getCheckCount());
            assertEquals(1, provider.getSyntaxErrorCount());
            assertEquals(1, provider.getHardCheckCount());
        }

        assertTrue(provider.checkValid(sql));
        {
            WallTableStat tableStat = provider.getTableStat("t");
            assertEquals(0, tableStat.getSelectCount());
            assertEquals(0, provider.getBlackListHitCount());
            assertEquals(0, provider.getWhiteListHitCount());
            assertEquals(0, provider.getWhiteList().size());
            assertEquals(0, provider.getBlackList().size());
            assertEquals(2, provider.getCheckCount());
            assertEquals(2, provider.getSyntaxErrorCount());
            assertEquals(2, provider.getHardCheckCount());
        }
    }
}

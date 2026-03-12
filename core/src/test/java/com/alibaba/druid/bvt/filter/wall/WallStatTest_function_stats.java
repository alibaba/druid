package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallFunctionStat;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WallStatTest_function_stats {
    private String sql = "select len(fname), len(fdesc) from t";

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
        assertTrue(provider.checkValid(sql));
        {
            WallTableStat tableStat = provider.getTableStat("t");
            assertEquals(1, tableStat.getSelectCount());
        }
        {
            WallFunctionStat functionStat = provider.getFunctionStat("len");
            assertEquals(2, functionStat.getInvokeCount());
        }
        assertTrue(provider.checkValid(sql));
        {
            WallTableStat tableStat = provider.getTableStat("t");
            assertEquals(2, tableStat.getSelectCount());
        }
        {
            WallFunctionStat functionStat = provider.getFunctionStat("len");
            assertEquals(4, functionStat.getInvokeCount());
        }
    }
}

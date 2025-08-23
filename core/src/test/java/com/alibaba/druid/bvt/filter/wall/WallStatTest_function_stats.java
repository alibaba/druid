package com.alibaba.druid.bvt.filter.wall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import junit.framework.TestCase;


import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallFunctionStat;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

public class WallStatTest_function_stats extends TestCase {
    private String sql = "select len(fname), len(fdesc) from t";

    protected void setUp() throws Exception {
        WallContext.clearContext();
    }

    protected void tearDown() throws Exception {
        WallContext.clearContext();
    }

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

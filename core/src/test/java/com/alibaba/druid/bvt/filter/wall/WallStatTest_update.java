package com.alibaba.druid.bvt.filter.wall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import junit.framework.TestCase;


import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.alibaba.druid.wall.spi.OracleWallProvider;
import com.alibaba.druid.wall.spi.PGWallProvider;
import com.alibaba.druid.wall.spi.SQLServerWallProvider;


public class WallStatTest_update extends TestCase {
    private String sql = "update t set fname = ? where fid = ?";

    protected void setUp() throws Exception {
        WallContext.clearContext();
    }

    protected void tearDown() throws Exception {
        WallContext.clearContext();
    }

    public void testMySql() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        assertEquals(1, tableStat.getUpdateCount());
    }

    public void testOracle() throws Exception {
        WallProvider provider = new OracleWallProvider();
        assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        assertEquals(1, tableStat.getUpdateCount());
    }

    public void testPG() throws Exception {
        WallProvider provider = new PGWallProvider();
        assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        assertEquals(1, tableStat.getUpdateCount());
    }

    public void testSQLServer() throws Exception {
        WallProvider provider = new SQLServerWallProvider();
        assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        assertEquals(1, tableStat.getUpdateCount());
    }

}

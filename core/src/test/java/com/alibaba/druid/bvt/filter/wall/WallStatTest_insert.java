package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.alibaba.druid.wall.spi.OracleWallProvider;
import com.alibaba.druid.wall.spi.PGWallProvider;
import com.alibaba.druid.wall.spi.SQLServerWallProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WallStatTest_insert {
    private String sql = "insert into t (fid, fname) values (?, ?)";

    @Test
    public void testMySql() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        assertEquals(1, tableStat.getInsertCount());
    }

    @Test
    public void testOracle() throws Exception {
        WallProvider provider = new OracleWallProvider();
        assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        assertEquals(1, tableStat.getInsertCount());
    }

    @Test
    public void testPG() throws Exception {
        WallProvider provider = new PGWallProvider();
        assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        assertEquals(1, tableStat.getInsertCount());
    }

    @Test
    public void testSQLServer() throws Exception {
        WallProvider provider = new SQLServerWallProvider();
        assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        assertEquals(1, tableStat.getInsertCount());
    }
}

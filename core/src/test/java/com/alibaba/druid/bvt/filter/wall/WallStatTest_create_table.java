package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.alibaba.druid.wall.spi.OracleWallProvider;
import com.alibaba.druid.wall.spi.PGWallProvider;
import com.alibaba.druid.wall.spi.SQLServerWallProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WallStatTest_create_table {
    private String sql = "create table t (fid int, fname varchar(50))";

    @Test
    public void testMySql() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        provider.getConfig().setCreateTableAllow(true);

        assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        assertEquals(1, tableStat.getCreateCount());
    }

    @Test
    public void testOracle() throws Exception {
        WallProvider provider = new OracleWallProvider();
        provider.getConfig().setCreateTableAllow(true);

        assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        assertEquals(1, tableStat.getCreateCount());
    }

    @Test
    public void testPG() throws Exception {
        WallProvider provider = new PGWallProvider();
        provider.getConfig().setCreateTableAllow(true);

        assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        assertEquals(1, tableStat.getCreateCount());
    }

    @Test
    public void testSQLServer() throws Exception {
        WallProvider provider = new SQLServerWallProvider();
        provider.getConfig().setCreateTableAllow(true);

        assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        assertEquals(1, tableStat.getCreateCount());
    }
}

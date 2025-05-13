package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.spi.*;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;


public class WallStatTest_insert extends TestCase {
    private String sql = "insert into t (fid, fname) values (?, ?)";

    public void testMySql() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getInsertCount());
    }

    public void testOracle() throws Exception {
        WallProvider provider = new OracleWallProvider();
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getInsertCount());
    }

    public void testPG() throws Exception {
        WallProvider provider = new PGWallProvider();
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getInsertCount());
    }

    public void testGaussDB() throws Exception {
        WallProvider provider = new GaussDBWallProvider();
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getInsertCount());
    }

    public void testSQLServer() throws Exception {
        WallProvider provider = new SQLServerWallProvider();
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getInsertCount());
    }

}

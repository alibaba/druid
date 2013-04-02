package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.alibaba.druid.wall.spi.OracleWallProvider;
import com.alibaba.druid.wall.spi.PGWallProvider;
import com.alibaba.druid.wall.spi.SQLServerWallProvider;

public class WallStatTest_select_1 extends TestCase {

    private String sql = "SELECT b.* FROM lhwbbs_posts_reply a LEFT JOIN lhwbbs_posts b ON a.pid=b.pid WHERE a.rpid=? AND b.disabled=? ORDER BY a.pid DESC";

    protected void setUp() throws Exception {
        WallContext.clearContext();
    }

    protected void tearDown() throws Exception {
        WallContext.clearContext();
    }

    public void testMySql() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        Assert.assertTrue(provider.checkValid(sql));
        {
            WallTableStat tableStat = provider.getTableStat("lhwbbs_posts_reply");
            Assert.assertEquals(1, tableStat.getSelectCount());
        }
        {
            WallTableStat tableStat = provider.getTableStat("lhwbbs_posts");
            Assert.assertEquals(1, tableStat.getSelectCount());
        }
    }

    public void testOracle() throws Exception {
        WallProvider provider = new OracleWallProvider();
        Assert.assertTrue(provider.checkValid(sql));
        {
            WallTableStat tableStat = provider.getTableStat("lhwbbs_posts_reply");
            Assert.assertEquals(1, tableStat.getSelectCount());
        }
        {
            WallTableStat tableStat = provider.getTableStat("lhwbbs_posts");
            Assert.assertEquals(1, tableStat.getSelectCount());
        }
    }

    public void testPG() throws Exception {
        WallProvider provider = new PGWallProvider();
        Assert.assertTrue(provider.checkValid(sql));
        {
            WallTableStat tableStat = provider.getTableStat("lhwbbs_posts_reply");
            Assert.assertEquals(1, tableStat.getSelectCount());
        }
        {
            WallTableStat tableStat = provider.getTableStat("lhwbbs_posts");
            Assert.assertEquals(1, tableStat.getSelectCount());
        }
    }

    public void testSQLServer() throws Exception {
        WallProvider provider = new SQLServerWallProvider();
        Assert.assertTrue(provider.checkValid(sql));
        {
            WallTableStat tableStat = provider.getTableStat("lhwbbs_posts_reply");
            Assert.assertEquals(1, tableStat.getSelectCount());
        }
        {
            WallTableStat tableStat = provider.getTableStat("lhwbbs_posts");
            Assert.assertEquals(1, tableStat.getSelectCount());
        }
    }

}

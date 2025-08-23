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
        assertTrue(provider.checkValid(sql));
        {
            WallTableStat tableStat = provider.getTableStat("lhwbbs_posts_reply");
            assertEquals(1, tableStat.getSelectCount());
        }
        {
            WallTableStat tableStat = provider.getTableStat("lhwbbs_posts");
            assertEquals(1, tableStat.getSelectCount());
        }
    }

    public void testOracle() throws Exception {
        WallProvider provider = new OracleWallProvider();
        assertTrue(provider.checkValid(sql));
        {
            WallTableStat tableStat = provider.getTableStat("lhwbbs_posts_reply");
            assertEquals(1, tableStat.getSelectCount());
        }
        {
            WallTableStat tableStat = provider.getTableStat("lhwbbs_posts");
            assertEquals(1, tableStat.getSelectCount());
        }
    }

    public void testPG() throws Exception {
        WallProvider provider = new PGWallProvider();
        assertTrue(provider.checkValid(sql));
        {
            WallTableStat tableStat = provider.getTableStat("lhwbbs_posts_reply");
            assertEquals(1, tableStat.getSelectCount());
        }
        {
            WallTableStat tableStat = provider.getTableStat("lhwbbs_posts");
            assertEquals(1, tableStat.getSelectCount());
        }
    }

    public void testSQLServer() throws Exception {
        WallProvider provider = new SQLServerWallProvider();
        assertTrue(provider.checkValid(sql));
        {
            WallTableStat tableStat = provider.getTableStat("lhwbbs_posts_reply");
            assertEquals(1, tableStat.getSelectCount());
        }
        {
            WallTableStat tableStat = provider.getTableStat("lhwbbs_posts");
            assertEquals(1, tableStat.getSelectCount());
        }
    }

}

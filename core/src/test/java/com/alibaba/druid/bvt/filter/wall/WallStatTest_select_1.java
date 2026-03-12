package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.alibaba.druid.wall.spi.OracleWallProvider;
import com.alibaba.druid.wall.spi.PGWallProvider;
import com.alibaba.druid.wall.spi.SQLServerWallProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WallStatTest_select_1 {
    private String sql = "SELECT b.* FROM lhwbbs_posts_reply a LEFT JOIN lhwbbs_posts b ON a.pid=b.pid WHERE a.rpid=? AND b.disabled=? ORDER BY a.pid DESC";

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
            WallTableStat tableStat = provider.getTableStat("lhwbbs_posts_reply");
            assertEquals(1, tableStat.getSelectCount());
        }
        {
            WallTableStat tableStat = provider.getTableStat("lhwbbs_posts");
            assertEquals(1, tableStat.getSelectCount());
        }
    }

    @Test
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

    @Test
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

    @Test
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

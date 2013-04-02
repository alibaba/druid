package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.alibaba.druid.wall.spi.OracleWallProvider;
import com.alibaba.druid.wall.spi.PGWallProvider;
import com.alibaba.druid.wall.spi.SQLServerWallProvider;


public class WallStatTest_alter_table extends TestCase {
    public void testMySql() throws Exception {
        String sql = "alter table t add column fname varchar(50)";
        WallProvider provider = new MySqlWallProvider();
        provider.getConfig().setAlterTableAllow(true);
        
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat =  provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getAlterCount());
    }
    
    public void testOracle() throws Exception {
        String sql = "alter table t add (fname varchar(50))";
        WallProvider provider = new OracleWallProvider();
        provider.getConfig().setAlterTableAllow(true);
        
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat =  provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getAlterCount());
    }
    
//    public void testPG() throws Exception {
//        WallProvider provider = new PGWallProvider();
//        provider.getConfig().setAlterTableAllow(true);
//        
//        Assert.assertTrue(provider.checkValid(sql));
//        WallTableStat tableStat =  provider.getTableStat("t");
//        Assert.assertEquals(1, tableStat.getAlterCount());
//    }
//    
//    public void testSQLServer() throws Exception {
//        WallProvider provider = new SQLServerWallProvider();
//        provider.getConfig().setAlterTableAllow(true);
//        
//        Assert.assertTrue(provider.checkValid(sql));
//        WallTableStat tableStat =  provider.getTableStat("t");
//        Assert.assertEquals(1, tableStat.getAlterCount());
//    }

}

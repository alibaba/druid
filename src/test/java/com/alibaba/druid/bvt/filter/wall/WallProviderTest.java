package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallProviderStatValue;
import com.alibaba.druid.wall.WallSqlStat;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

public class WallProviderTest extends TestCase {

    public void test_getSqlStat() throws Exception {
        String whiteSql_1 = "select * from t1 where fid = 1";
        String whiteSql_2 = "select * from t1 where fid = 2";
        String blackSql_1 = "select * from t2 where len(fid) = 1 OR 1 = 1";
        String blackSql_2 = "select * from t2 where len(fid) = 2 OR 1 = 1";

        MySqlWallProvider provider = new MySqlWallProvider();

        Assert.assertTrue(provider.checkValid(whiteSql_1));
        Assert.assertTrue(provider.checkValid(whiteSql_2));
        Assert.assertEquals(1, provider.getSqlList().size());

        Assert.assertFalse(provider.checkValid(blackSql_1));
        Assert.assertFalse(provider.checkValid(blackSql_2));
        Assert.assertEquals(2, provider.getSqlList().size());

        Assert.assertNotNull(provider.getSqlStat(whiteSql_1));
        Assert.assertNotNull(provider.getSqlStat(blackSql_1));

        Assert.assertSame(provider.getSqlStat(blackSql_1), provider.getSqlStat(blackSql_2));

        WallSqlStat whiteStat_1 = provider.getSqlStat(whiteSql_1);
        WallSqlStat whiteStat_2 = provider.getSqlStat(whiteSql_2);
        Assert.assertSame(whiteStat_1, whiteStat_2);
        provider.addFetchRowCount(whiteStat_1, 3);
        WallTableStat wallTableStat = provider.getTableStat("t1");
        Assert.assertNotNull(wallTableStat);
        Assert.assertEquals(3, wallTableStat.getFetchRowCount());

        Assert.assertTrue(provider.checkValid(whiteSql_1));
        Assert.assertTrue(provider.checkValid(whiteSql_2));
        
        Assert.assertFalse(provider.checkValid(blackSql_1));
        Assert.assertFalse(provider.checkValid(blackSql_2));
        
        for (int i = 1000; i < 1000 * 2; ++i) {
            String sql_x = "select * from t1 where fid = " + i;
            Assert.assertTrue(provider.checkValid(sql_x));
            Assert.assertSame(whiteStat_1, provider.getSqlStat(sql_x));
        }

        Assert.assertEquals(1, provider.getWhiteList().size());
        Assert.assertEquals(2, provider.getSqlList().size());

        Assert.assertFalse(provider.checkValid("slelc"));
        
        {
            WallProviderStatValue statValue = provider.getStatValue(true);
            Assert.assertNotNull(statValue);
            
            Assert.assertEquals(2, statValue.getTables().size());
            Assert.assertEquals(1, statValue.getFunctions().size());
            
            Assert.assertEquals(1009, statValue.getCheckCount());
            Assert.assertEquals(2, statValue.getBlackListHitCount());
            Assert.assertEquals(4, statValue.getHardCheckCount());
            Assert.assertEquals(1, statValue.getSyntaxErrorCount());
            Assert.assertEquals(5, statValue.getViolationCount());
            Assert.assertEquals(1003, statValue.getWhiteListHitCount());
            
            Assert.assertEquals(1, statValue.getWhiteList().size());
            Assert.assertEquals(2, statValue.getBlackList().size());
        }
        
        {
            WallProviderStatValue statValue = provider.getStatValue(true);
            Assert.assertNotNull(statValue);
            
            Assert.assertEquals(0, statValue.getTables().size());
            Assert.assertEquals(0, statValue.getFunctions().size());
            
            Assert.assertEquals(0, statValue.getCheckCount());
            Assert.assertEquals(0, statValue.getBlackListHitCount());
            Assert.assertEquals(0, statValue.getHardCheckCount());
            Assert.assertEquals(0, statValue.getSyntaxErrorCount());
            Assert.assertEquals(0, statValue.getViolationCount());
            Assert.assertEquals(0, statValue.getWhiteListHitCount());
            
            Assert.assertEquals(0, statValue.getWhiteList().size());
            Assert.assertEquals(0, statValue.getBlackList().size());
        }
    }
}

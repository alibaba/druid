package com.alibaba.druid.bvt.filter.wall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import junit.framework.TestCase;


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

        assertTrue(provider.checkValid(whiteSql_1));
        assertTrue(provider.checkValid(whiteSql_2));
        assertEquals(1, provider.getSqlList().size());

        assertFalse(provider.checkValid(blackSql_1));
        assertFalse(provider.checkValid(blackSql_2));
        assertEquals(2, provider.getSqlList().size());

        assertNotNull(provider.getSqlStat(whiteSql_1));
        assertNotNull(provider.getSqlStat(blackSql_1));

        assertSame(provider.getSqlStat(blackSql_1), provider.getSqlStat(blackSql_2));

        WallSqlStat whiteStat_1 = provider.getSqlStat(whiteSql_1);
        WallSqlStat whiteStat_2 = provider.getSqlStat(whiteSql_2);
        assertSame(whiteStat_1, whiteStat_2);
        provider.addFetchRowCount(whiteStat_1, 3);
        WallTableStat wallTableStat = provider.getTableStat("t1");
        assertNotNull(wallTableStat);
        assertEquals(3, wallTableStat.getFetchRowCount());

        assertTrue(provider.checkValid(whiteSql_1));
        assertTrue(provider.checkValid(whiteSql_2));

        assertFalse(provider.checkValid(blackSql_1));
        assertFalse(provider.checkValid(blackSql_2));

        for (int i = 1000; i < 1000 * 2; ++i) {
            String sql_x = "select * from t1 where fid = " + i;
            assertTrue(provider.checkValid(sql_x));
            assertSame(whiteStat_1, provider.getSqlStat(sql_x));
        }

        assertEquals(1, provider.getWhiteList().size());
        assertEquals(2, provider.getSqlList().size());

        assertFalse(provider.checkValid("slelc"));

        {
            WallProviderStatValue statValue = provider.getStatValue(true);
            assertNotNull(statValue);

            assertEquals(2, statValue.getTables().size());
            assertEquals(1, statValue.getFunctions().size());

            assertEquals(1009, statValue.getCheckCount());
            assertEquals(3, statValue.getBlackListHitCount());
            assertEquals(3, statValue.getHardCheckCount());
            assertEquals(1, statValue.getSyntaxErrorCount());
            assertEquals(5, statValue.getViolationCount());
            assertEquals(1003, statValue.getWhiteListHitCount());

            assertEquals(1, statValue.getWhiteList().size());
            assertEquals(2, statValue.getBlackList().size());
        }

        {
            WallProviderStatValue statValue = provider.getStatValue(true);
            assertNotNull(statValue);

            assertEquals(0, statValue.getTables().size());
            assertEquals(0, statValue.getFunctions().size());

            assertEquals(0, statValue.getCheckCount());
            assertEquals(0, statValue.getBlackListHitCount());
            assertEquals(0, statValue.getHardCheckCount());
            assertEquals(0, statValue.getSyntaxErrorCount());
            assertEquals(0, statValue.getViolationCount());
            assertEquals(0, statValue.getWhiteListHitCount());

            assertEquals(0, statValue.getWhiteList().size());
            assertEquals(0, statValue.getBlackList().size());
        }
    }
}

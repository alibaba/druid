package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.*;
import junit.framework.TestCase;
import org.junit.Assert;

public class WallStatTestWhereAlwaysTrue extends TestCase {
    private String[] sqls = new String[]{
            "select * from T where a=1 or 1=1",
            "update T set name='N' where a=1 or 1=1",
            "delete from T where a=1 or 1=1",
            "update T set name='N' where 1=1",
            "delete from T where 1=1",
            "select * from T where 1=1",
            "update T set name='N' where 1=1 ",
            "delete from T where 1=1 ",
            "select * from T where 1=1 ",
            "update T set name='N' where 0=1 or 2=2",
            "delete from T  where 0=1 or 2=2",
            "select * from T where 0=1 or 2=2",
    };

    private String[] okSqls = new String[]{
            "delete from T where a=1",
            "delete from T where a=1 ",
            "update T set name='N' where a=1",
            "update T set name='N' where a=1 ",
            "select * from T where a=1",
            "select * from T where a=1 ",
    };


    protected void setUp() throws Exception {
        WallContext.clearContext();
    }

    protected void tearDown() throws Exception {
        WallContext.clearContext();
    }


    protected void doTest(final WallProvider provider) {
        final WallConfig config = provider.getConfig();
        config.setDeleteWhereAlwayTrueCheck(true);
        //config.setUpdateWhereAlwayTrueCheck(true);
        config.setSelectWhereAlwayTrueCheck(true);
        for (final String sql : sqls) {
            Assert.assertFalse(sql, provider.checkValid(sql));
            final WallTableStat tableStat = provider.getTableStat("t");
            if (sql.startsWith("delete")) {
                Assert.assertTrue(tableStat.getDeleteCount() > 0);
            }
        }
        for (final String sql : okSqls) {
            Assert.assertTrue(sql, provider.checkValid(sql));
        }
    }

    public void testMySql() throws Exception {
        final WallProvider provider = new MySqlWallProvider();
        doTest(provider);
    }

    public void testOracle() throws Exception {
        final WallProvider provider = new OracleWallProvider();
        doTest(provider);
    }

    public void testPG() throws Exception {
        final WallProvider provider = new PGWallProvider();
        doTest(provider);
    }

    public void testDB2Server() throws Exception {
        final WallProvider provider = new DB2WallProvider();
        doTest(provider);
    }

    public void testSQLServer() throws Exception {
        final WallProvider provider = new SQLServerWallProvider();
        doTest(provider);
    }
}

/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.GaussDBWallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.alibaba.druid.wall.spi.OracleWallProvider;
import com.alibaba.druid.wall.spi.PGWallProvider;
import com.alibaba.druid.wall.spi.SQLServerWallProvider;

public class WallStatTest_delete extends TestCase {
    private String sql = "delete from T where fid = ?";

    protected void setUp() throws Exception {
        WallContext.clearContext();
    }

    protected void tearDown() throws Exception {
        WallContext.clearContext();
    }

    public void testMySql() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getDeleteCount());
    }

    public void testOracle() throws Exception {
        WallProvider provider = new OracleWallProvider();
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getDeleteCount());
    }

    public void testPG() throws Exception {
        WallProvider provider = new PGWallProvider();
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getDeleteCount());
    }

    public void testGaussDB() throws Exception {
        WallProvider provider = new GaussDBWallProvider();
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getDeleteCount());
    }

    public void testSQLServer() throws Exception {
        WallProvider provider = new SQLServerWallProvider();
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getDeleteCount());
    }

}

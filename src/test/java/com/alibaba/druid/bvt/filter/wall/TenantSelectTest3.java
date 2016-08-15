/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

public class TenantSelectTest3 extends TestCase {

    private String sql        = "SELECT ID, NAME " + //
                                "FROM orders o inner join users u ON o.userid = u.id " + //
                                "WHERE FID = ? OR FID = ?";
    private String expect_sql = "SELECT ID, NAME, u.tenant, o.tenant" + //
                                "\nFROM orders o" + //
                                "\n\tINNER JOIN users u ON o.userid = u.id" + //
                                "\nWHERE FID = ?" + //
                                "\n\tOR FID = ?";

    protected void setUp() throws Exception {

    }

    public void testMySql() throws Exception {
        WallConfig config = new WallConfig();
        WallConfig config_callback = new WallConfig();
        config.setTenantTablePattern("*");
        config.setTenantColumn("tenant");

        config_callback.setTenantCallBack(new TenantTestCallBack());
        
        WallProvider.setTenantValue(123);
        MySqlWallProvider provider = new MySqlWallProvider(config);
        WallCheckResult checkResult = provider.check(sql);
        Assert.assertEquals(0, checkResult.getViolations().size());

        String resultSql = SQLUtils.toSQLString(checkResult.getStatementList(), JdbcConstants.MYSQL);
        Assert.assertEquals(expect_sql, resultSql);
    }

    public void testMySql2() throws Exception {
        WallConfig config = new WallConfig();
        WallConfig config_callback = new WallConfig();
        config.setTenantTablePattern("*");
        config.setTenantColumn("tenant");

        config_callback.setTenantCallBack(new TenantTestCallBack());

        MySqlWallProvider provider = new MySqlWallProvider(config_callback);
        WallCheckResult checkResult = provider.check(sql);
        Assert.assertEquals(0, checkResult.getViolations().size());

        String resultSql = SQLUtils.toSQLString(checkResult.getStatementList(), JdbcConstants.MYSQL);
        Assert.assertEquals(expect_sql, resultSql);
    }
}

/*
 * Copyright 2013 Alibaba Group Holding Ltd.
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
// Created on 2013-10-17
// $Id$

package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

/**
 * @author kiki
 */
public class TenantInsertTest extends TestCase {

    private WallConfig config          = new WallConfig();
    private WallConfig config_callback = new WallConfig();

    protected void setUp() throws Exception {
        config.setTenantTablePattern("*");
        config.setTenantColumn("tenant");

        config_callback.setTenantCallBack(new TenantTestCallBack());
    }

    public void testMySql3() throws Exception {
        String insert_sql = "INSERT INTO orders (ID, NAME) VALUES (1, \"KIKI\")";
        String expect_sql = "INSERT INTO orders (ID, NAME, tenant)\n" +
                "VALUES (1, 'KIKI', 123)";
        {
            MySqlWallProvider provider = new MySqlWallProvider(config_callback);
            WallCheckResult checkResult = provider.check(insert_sql);
            Assert.assertEquals(0, checkResult.getViolations().size());

            String resultSql = SQLUtils.toSQLString(checkResult.getStatementList(), JdbcConstants.MYSQL);
            Assert.assertEquals(expect_sql, resultSql);
        }

        {
            WallProvider.setTenantValue(123);
            MySqlWallProvider provider = new MySqlWallProvider(config);
            WallCheckResult checkResult = provider.check(insert_sql);
            Assert.assertEquals(0, checkResult.getViolations().size());

            String resultSql = SQLUtils.toSQLString(checkResult.getStatementList(), JdbcConstants.MYSQL);
            Assert.assertEquals(expect_sql, resultSql);
        }

    }

    public void testMySql4() throws Exception {
        String insert_sql = "INSERT INTO orders (ID, NAME) VALUES (1, \"KIKI\"), (1, \"CICI\")";
        String expect_sql = "INSERT INTO orders (ID, NAME, tenant)\n" +
                "VALUES (1, 'KIKI', 123),\n" +
                "\t(1, 'CICI', 123)";

        {
            MySqlWallProvider provider = new MySqlWallProvider(config_callback);
            WallCheckResult checkResult = provider.check(insert_sql);
            Assert.assertEquals(0, checkResult.getViolations().size());

            String resultSql = SQLUtils.toSQLString(checkResult.getStatementList(), JdbcConstants.MYSQL);
            Assert.assertEquals(expect_sql, resultSql);
        }

        {
            WallProvider.setTenantValue(123);
            MySqlWallProvider provider = new MySqlWallProvider(config);
            WallCheckResult checkResult = provider.check(insert_sql);
            Assert.assertEquals(0, checkResult.getViolations().size());

            String resultSql = SQLUtils.toSQLString(checkResult.getStatementList(), JdbcConstants.MYSQL);
            Assert.assertEquals(expect_sql, resultSql);
        }

    }

    public void testMySql5() throws Exception {
        String insert_sql = "INSERT INTO orders (ID, NAME) SELECT ID, NAME FROM temp WHERE age = 18";
        String expect_sql = "INSERT INTO orders (ID, NAME, tenant)" + //
                            "\nSELECT ID, NAME, 123" + //
                            "\nFROM temp" + //
                            "\nWHERE age = 18";

        {
            MySqlWallProvider provider = new MySqlWallProvider(config_callback);
            WallCheckResult checkResult = provider.check(insert_sql);
            Assert.assertEquals(0, checkResult.getViolations().size());

            String resultSql = SQLUtils.toSQLString(checkResult.getStatementList(), JdbcConstants.MYSQL);
            Assert.assertEquals(expect_sql, resultSql);
        }

        {
            WallProvider.setTenantValue(123);
            MySqlWallProvider provider = new MySqlWallProvider(config);
            WallCheckResult checkResult = provider.check(insert_sql);
            Assert.assertEquals(0, checkResult.getViolations().size());

            String resultSql = SQLUtils.toSQLString(checkResult.getStatementList(), JdbcConstants.MYSQL);
            Assert.assertEquals(expect_sql, resultSql);
        }
    }

    public void testMySql6() throws Exception {
        String insert_sql = "INSERT INTO orders (ID, NAME) SELECT ID, NAME FROM temp1 WHERE age = 18 UNION SELECT ID, NAME FROM temp2 UNION ALL SELECT ID, NAME FROM temp3";
        String expect_sql = "INSERT INTO orders (ID, NAME, tenant)\n" +
                "SELECT ID, NAME, 123\n" +
                "FROM temp1\n" +
                "WHERE age = 18\n" +
                "UNION\n" +
                "SELECT ID, NAME, 123\n" +
                "FROM temp2\n" +
                "UNION ALL\n" +
                "SELECT ID, NAME, 123\n" +
                "FROM temp3";

        {
            MySqlWallProvider provider = new MySqlWallProvider(config_callback);
            WallCheckResult checkResult = provider.check(insert_sql);
            Assert.assertEquals(0, checkResult.getViolations().size());

            String resultSql = SQLUtils.toSQLString(checkResult.getStatementList(), JdbcConstants.MYSQL);
            Assert.assertEquals(expect_sql, resultSql);
        }

        {
            WallProvider.setTenantValue(123);
            MySqlWallProvider provider = new MySqlWallProvider(config);
            WallCheckResult checkResult = provider.check(insert_sql);
            Assert.assertEquals(0, checkResult.getViolations().size());

            String resultSql = SQLUtils.toSQLString(checkResult.getStatementList(), JdbcConstants.MYSQL);
            Assert.assertEquals(expect_sql, resultSql);
        }
    }

}

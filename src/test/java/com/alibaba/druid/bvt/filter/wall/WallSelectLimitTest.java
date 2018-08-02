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

import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallUtils;
import com.alibaba.druid.wall.spi.*;
import junit.framework.TestCase;
import org.junit.Assert;

public class WallSelectLimitTest extends TestCase {
    private String sql = "select * from t";
    
    private WallConfig config = new WallConfig();
    
    protected void setUp() throws Exception {
        config.setSelectLimit(1000);
    }

    public void testMySql() throws Exception {
        WallProvider provider = new MySqlWallProvider(config);
        WallCheckResult checkResult = provider.check(sql);
        String resultSql = checkResult.getSql();
        System.out.println(resultSql);
        assertEquals("SELECT *\n" +
                "FROM t\n" +
                "LIMIT 1000", resultSql);
    }

    public void testDB2() throws Exception {
        WallProvider provider = new DB2WallProvider(config);
        WallCheckResult checkResult = provider.check(sql);
        String resultSql = checkResult.getSql();
        System.out.println(resultSql);
        assertEquals("SELECT *\n" +
                "FROM t\n" +
                "FETCH FIRST 1000 ROWS ONLY", resultSql);
    }

    public void testSQLServer() throws Exception {
        WallProvider provider = new SQLServerWallProvider(config);
        WallCheckResult checkResult = provider.check(sql);
        String resultSql = checkResult.getSql();
        System.out.println(resultSql);
        assertEquals("SELECT TOP 1000 *\n" +
                "FROM t", resultSql);
    }

    public void testOracle() throws Exception {
        WallProvider provider = new OracleWallProvider(config);
        WallCheckResult checkResult = provider.check(sql);
        String resultSql = checkResult.getSql();
        System.out.println(resultSql);
        assertEquals("SELECT *\n" +
                "FROM t\n" +
                "WHERE ROWNUM <= 1000", resultSql);
    }

    public void testPG() throws Exception {
        WallProvider provider = new PGWallProvider(config);
        WallCheckResult checkResult = provider.check(sql);
        String resultSql = checkResult.getSql();
        System.out.println(resultSql);
        assertEquals("SELECT *\n" +
                "FROM t\n" +
                "LIMIT 1000", resultSql);
    }
}

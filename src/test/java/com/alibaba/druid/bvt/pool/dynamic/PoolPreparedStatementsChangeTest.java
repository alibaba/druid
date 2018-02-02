/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.pool.dynamic;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.alibaba.druid.PoolTestCase;
import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;

public class PoolPreparedStatementsChangeTest extends PoolTestCase {

    private DruidDataSource dataSource;

    private Log             dataSourceLog;

    protected void setUp() throws Exception {
        super.setUp();

        Field logField = DruidDataSource.class.getDeclaredField("LOG");
        logField.setAccessible(true);
        dataSourceLog = (Log) logField.get(null);

        dataSourceLog.resetStat();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.init();

        Assert.assertEquals(1, dataSourceLog.getInfoCount());
    }

    protected void tearDown() throws Exception {
        dataSource.close();

        super.tearDown();
    }

    public void test_connectPropertiesChange() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            conn.close();
        }

        Assert.assertEquals(false, dataSource.isPoolPreparedStatements());

        dataSource.setPoolPreparedStatements(true);

        Assert.assertEquals(true, dataSource.isPoolPreparedStatements());

        String sql = "select ?";

        Assert.assertEquals(0, dataSource.getCachedPreparedStatementCount());
        {
            Connection conn = dataSource.getConnection();

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "xxx");
            ResultSet rs = stmt.executeQuery();

            rs.close();

            conn.close();
        }
        Assert.assertEquals(1, dataSource.getCachedPreparedStatementCount());
        Assert.assertEquals(1, dataSource.getCachedPreparedStatementAccessCount());
        
        {
            Connection conn = dataSource.getConnection();
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "xxx");
            ResultSet rs = stmt.executeQuery();
            
            rs.close();
            
            conn.close();
        }
        Assert.assertEquals(1, dataSource.getCachedPreparedStatementCount());
        Assert.assertEquals(2, dataSource.getCachedPreparedStatementAccessCount());
        
        dataSource.setPoolPreparedStatements(false);
        Assert.assertEquals(0, dataSource.getCachedPreparedStatementCount());
        Assert.assertEquals(2, dataSource.getCachedPreparedStatementAccessCount());
        
        {
            Connection conn = dataSource.getConnection();
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "xxx");
            ResultSet rs = stmt.executeQuery();
            
            rs.close();
            
            conn.close();
        }
        Assert.assertEquals(0, dataSource.getCachedPreparedStatementCount());
        Assert.assertEquals(2, dataSource.getCachedPreparedStatementAccessCount());
    }
}

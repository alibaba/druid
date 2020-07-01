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
package com.alibaba.druid.bvt.filter;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;
import org.nutz.lang.util.ByteInputStream;

import com.alibaba.druid.mock.MockBlob;
import com.alibaba.druid.mock.MockClob;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockNClob;
import com.alibaba.druid.mock.MockRowId;
import com.alibaba.druid.mock.MockStatementBase;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.JdbcUtils;

public class StatFilterBuildSlowParameterTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");
        dataSource.setTestOnBorrow(false);
        dataSource.setConnectionProperties("druid.stat.slowSqlMillis=1");

        MockDriver driver = new MockDriver() {

            public ResultSet executeQuery(MockStatementBase stmt, String sql) throws SQLException {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return super.executeQuery(stmt, sql);
            }
        };

        dataSource.setDriver(driver);

        dataSource.init();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_buildSlowSql() throws Exception {
        long currentMillis = System.currentTimeMillis();
        String sql = "select ?, ?, ?, ?, ?";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String dateText = dateFormat.format(date);

        {
            Connection conn = dataSource.getConnection();

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setBoolean(1, true);
            stmt.setInt(2, 123);
            stmt.setLong(3, 10001);
            stmt.setTimestamp(4, new java.sql.Timestamp(currentMillis));
            stmt.setDate(5, new java.sql.Date(currentMillis));

            ResultSet rs = stmt.executeQuery();
            rs.close();

            stmt.close();

            conn.close();

            // //////

            JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);
            Assert.assertNotNull(sqlStat);

            String slowParameters = sqlStat.getLastSlowParameters();
            Assert.assertNotNull(slowParameters);

            List<Object> parameters = (List<Object>) JSONUtils.parse(slowParameters);
            Assert.assertEquals(5, parameters.size());

            Assert.assertEquals(true, parameters.get(0));
            Assert.assertEquals(123, parameters.get(1));
            Assert.assertEquals(10001, parameters.get(2));
            Assert.assertEquals(dateText, parameters.get(3));
            Assert.assertEquals(dateText, parameters.get(4));
        }

        currentMillis = System.currentTimeMillis();
        date = new Date(System.currentTimeMillis());
        dateText = dateFormat.format(date);
        {
            Connection conn = dataSource.getConnection();

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setBoolean(1, false);
            stmt.setInt(2, 234);
            stmt.setLong(3, 10002);
            stmt.setTimestamp(4, new java.sql.Timestamp(currentMillis));
            stmt.setDate(5, new java.sql.Date(currentMillis));

            ResultSet rs = stmt.executeQuery();
            rs.close();

            stmt.close();

            conn.close();

            // //////

            JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);
            Assert.assertNotNull(sqlStat);

            String slowParameters = sqlStat.getLastSlowParameters();
            Assert.assertNotNull(slowParameters);

            List<Object> parameters = (List<Object>) JSONUtils.parse(slowParameters);
            Assert.assertEquals(5, parameters.size());

            Assert.assertEquals(false, parameters.get(0));
            Assert.assertEquals(234, parameters.get(1));
            Assert.assertEquals(10002, parameters.get(2));
            Assert.assertEquals(dateText, parameters.get(3));
            Assert.assertEquals(dateText, parameters.get(4));
        }

        {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < 10; ++i) {
                buf.append("abcdefghijklmnABCDEFGHIJKLMN1234567890!@#$%^&*(");
            }

            Connection conn = dataSource.getConnection();

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setNull(1, Types.VARCHAR);
            stmt.setString(2, buf.toString());
            stmt.setClob(3, new MockClob());
            stmt.setNClob(4, new MockNClob());
            stmt.setBlob(5, new MockBlob());

            ResultSet rs = stmt.executeQuery();
            rs.close();

            stmt.close();

            conn.close();

            // //////

            JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);
            Assert.assertNotNull(sqlStat);

            String slowParameters = sqlStat.getLastSlowParameters();
            Assert.assertNotNull(slowParameters);

            List<Object> parameters = (List<Object>) JSONUtils.parse(slowParameters);
            Assert.assertEquals(5, parameters.size());

            Assert.assertEquals(null, parameters.get(0));
            Assert.assertEquals(buf.substring(0, 97) + "...", parameters.get(1));
            Assert.assertEquals("<Clob>", parameters.get(2));
            Assert.assertEquals("<NClob>", parameters.get(3));
            Assert.assertEquals("<Blob>", parameters.get(4));
        }
        {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < 10; ++i) {
                buf.append("中国abcdefghijklmnABCDEFGHIJKLMN1234567890!@#$%^&*(");
            }
            
            Connection conn = dataSource.getConnection();
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setBinaryStream(1, new ByteInputStream(new byte[0]));
            stmt.setString(2, buf.toString());
            stmt.setTime(3, new Time(currentMillis));
            stmt.setBigDecimal(4, new BigDecimal("56789.123"));
            stmt.setRowId(5, new MockRowId());
            
            ResultSet rs = stmt.executeQuery();
            rs.close();
            
            stmt.close();
            
            conn.close();
            
            // //////
            
            JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);
            Assert.assertNotNull(sqlStat);
            
            String slowParameters = sqlStat.getLastSlowParameters();
            Assert.assertNotNull(slowParameters);
            
            List<Object> parameters = (List<Object>) JSONUtils.parse(slowParameters);
            Assert.assertEquals(5, parameters.size());
            
            Assert.assertEquals("<InputStream>", parameters.get(0));
            Assert.assertEquals(buf.substring(0, 97) + "...", parameters.get(1));
            Assert.assertEquals(dateText, parameters.get(2));
            Assert.assertEquals(56789.123, parameters.get(3));
            Assert.assertEquals("<com.alibaba.druid.mock.MockRowId>", parameters.get(4));
        }
    }
}

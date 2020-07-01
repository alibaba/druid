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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.encoding.EncodingConvertFilter;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.mock.MockStatementBase;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.util.JdbcUtils;

public class EncodingConvertFilterTest extends TestCase {

    private DruidDataSource dataSource;

    private static String   CLIENT_ENCODING = "UTF-8";
    private static String   SERVER_ENCODING = "ISO-8859-1";

    private static String   text            = "中华人民共和国";

    protected void setUp() throws Exception {

        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("encoding");

        dataSource.setDriver(new MockDriver() {
            
            public ResultSet createResultSet(MockPreparedStatement stmt) {
                return new MyResultSet(stmt);
            }

            public ResultSet executeQuery(MockStatementBase stmt, String sql) throws SQLException {
                return new MyResultSet(stmt);
            }
        });

        dataSource.getConnectProperties().put("clientEncoding", CLIENT_ENCODING);
        dataSource.getConnectProperties().put("serverEncoding", SERVER_ENCODING);

        dataSource.init();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_stat() throws Exception {

        Assert.assertTrue(dataSource.isInited());
        
        EncodingConvertFilter filter = (EncodingConvertFilter) dataSource.getProxyFilters().get(0);

        DruidPooledConnection conn = dataSource.getConnection();

        final String PARAM_VALUE = "中国";
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setString(1, PARAM_VALUE);

        MockPreparedStatement raw = stmt.unwrap(MockPreparedStatement.class);

        String param1 = (String) raw.getParameters().get(0);

        Assert.assertEquals(PARAM_VALUE, new String(param1.getBytes(SERVER_ENCODING), CLIENT_ENCODING));
        Assert.assertFalse(param1.equals(PARAM_VALUE));

        ResultSet rs = stmt.executeQuery();
        
        MyResultSet rawRs = rs.unwrap(MyResultSet.class);
        rawRs.setValue(filter.encode((ConnectionProxy) conn.getConnection(), text));
        
        rs.next();

         Assert.assertEquals(text, rs.getString(1));

        rs.close();
        stmt.close();

        conn.close();

    }

    public static class MyResultSet extends MockResultSet {

        private String value;

        public MyResultSet(Statement statement){
            super(statement);
        }

        public String getObject(int index) throws SQLException {
            return getString(index);
        }

        public String getString(int columnIndex) throws SQLException {
            return value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }
}

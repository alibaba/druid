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

import java.io.Reader;
import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collections;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.encoding.EncodingConvertFilter;
import com.alibaba.druid.mock.MockCallableStatement;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.mock.MockResultSetMetaData;
import com.alibaba.druid.mock.MockStatementBase;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.util.Utils;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.jdbc.ResultSetMetaDataBase.ColumnMetaData;

public class EncodingConvertFilterTest2 extends TestCase {

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
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.setString(1, PARAM_VALUE);

        MockCallableStatement raw = stmt.unwrap(MockCallableStatement.class);

        stmt.execute();
        String param1 = (String) raw.getParameters().get(0);

        String C_TEXT = new String(param1.getBytes(SERVER_ENCODING), CLIENT_ENCODING);
        Assert.assertEquals(PARAM_VALUE, C_TEXT);
        Assert.assertFalse(param1.equals(PARAM_VALUE));

        MyResultSet rawRs = new MyResultSet(raw);

        rawRs.setValue(filter.encode((ConnectionProxy) conn.getConnection(), text));

        raw.getOutParameters().add(rawRs);

        ResultSet rs = (ResultSet) stmt.getObject(1);

        rs.next();

        Assert.assertEquals(text, rs.getString(1));
        Assert.assertEquals(text, rs.getString("1"));
        Assert.assertEquals(text, rs.getObject(1));
        Assert.assertEquals(text, rs.getObject("1"));
        Assert.assertEquals(text, rs.getObject(1, Collections.<String,Class<?>>emptyMap()));
        Assert.assertEquals(text, rs.getObject("1", Collections.<String,Class<?>>emptyMap()));
        
        Assert.assertEquals(text, rs.getString(2));
        Assert.assertEquals(text, rs.getString("2"));
        Assert.assertEquals(text, rs.getObject(2));
        Assert.assertEquals(text, rs.getObject("2"));
        Assert.assertEquals(text, rs.getObject(2, Collections.<String,Class<?>>emptyMap()));
        Assert.assertEquals(text, rs.getObject("2", Collections.<String,Class<?>>emptyMap()));
        
        Assert.assertEquals(text, rs.getString(3));
        Assert.assertEquals(text, rs.getString("3"));
        Assert.assertEquals(text, rs.getObject(3));
        Assert.assertEquals(text, rs.getObject("3"));
        Assert.assertEquals(text, rs.getObject(3, Collections.<String,Class<?>>emptyMap()));
        Assert.assertEquals(text, rs.getObject("3", Collections.<String,Class<?>>emptyMap()));
        
        Assert.assertEquals(text, rs.getString(4));
        Assert.assertEquals(text, rs.getString("4"));
        Assert.assertEquals(text, rs.getObject(4));
        Assert.assertEquals(text, rs.getObject("4"));
        Assert.assertEquals(text, rs.getObject(4, Collections.<String,Class<?>>emptyMap()));
        Assert.assertEquals(text, rs.getObject("4", Collections.<String,Class<?>>emptyMap()));
        
        stmt.registerOutParameter(2, Types.VARCHAR);
        stmt.registerOutParameter(3, Types.CLOB);
        raw.getOutParameters().add(param1);
        raw.getOutParameters().add(param1);
        
        
        Assert.assertEquals(C_TEXT, stmt.getString(4));
        Assert.assertEquals(C_TEXT, stmt.getString("4"));
        Assert.assertEquals(C_TEXT, stmt.getObject(4));
        Assert.assertEquals(C_TEXT, stmt.getObject("4"));
        Assert.assertEquals(C_TEXT, stmt.getObject(4, Collections.<String,Class<?>>emptyMap()));
        Assert.assertEquals(C_TEXT, stmt.getObject("4", Collections.<String,Class<?>>emptyMap()));
        
        Assert.assertEquals(C_TEXT, stmt.getString(5));
        Assert.assertEquals(C_TEXT, stmt.getString("5"));
        Assert.assertEquals(C_TEXT, stmt.getObject(5));
        Assert.assertEquals(C_TEXT, stmt.getObject("5"));
        Assert.assertEquals(C_TEXT, stmt.getObject(5, Collections.<String,Class<?>>emptyMap()));
        Assert.assertEquals(C_TEXT, stmt.getObject("5", Collections.<String,Class<?>>emptyMap()));
        
        stmt.setObject(1, C_TEXT);
        Assert.assertEquals(param1, raw.getParameters().get(0));
        
        stmt.setObject(2, new StringReader(C_TEXT));
        Assert.assertEquals(param1, Utils.read((Reader) raw.getParameters().get(1)));
        
        stmt.setCharacterStream(3, new StringReader(C_TEXT));
        Assert.assertEquals(param1, Utils.read((Reader) raw.getParameters().get(2)));
       
        stmt.setCharacterStream(4, new StringReader(C_TEXT), C_TEXT.length());
        Assert.assertEquals(param1, Utils.read((Reader) raw.getParameters().get(3)));
        
        stmt.setCharacterStream(5, new StringReader(C_TEXT), (long) C_TEXT.length());
        Assert.assertEquals(param1, Utils.read((Reader) raw.getParameters().get(4)));
        
        stmt.setObject(6, C_TEXT, Types.VARCHAR);
        Assert.assertEquals(param1, raw.getParameters().get(5));
        stmt.setObject(7, new StringReader(C_TEXT), Types.VARCHAR);
        Assert.assertEquals(param1, Utils.read((Reader) raw.getParameters().get(6)));
        
        stmt.setObject(8, C_TEXT, Types.VARCHAR, 0);
        Assert.assertEquals(param1, raw.getParameters().get(7));
        stmt.setObject(9, new StringReader(C_TEXT), Types.VARCHAR, 0);
        Assert.assertEquals(param1, Utils.read((Reader) raw.getParameters().get(8)));
        
        stmt.setObject(10, 1, Types.INTEGER);
        Assert.assertEquals(1, raw.getParameters().get(9));
        
        stmt.setObject(11, 2, Types.INTEGER, 0);
        Assert.assertEquals(2, raw.getParameters().get(10));
        
        stmt.setObject(12, 3);
        Assert.assertEquals(3, raw.getParameters().get(11));
        
        stmt.setObject("13", C_TEXT, Types.VARCHAR);
        Assert.assertEquals(param1, raw.getParameters().get(12));
        stmt.setObject("14", new StringReader(C_TEXT), Types.VARCHAR);
        Assert.assertEquals(param1, Utils.read((Reader) raw.getParameters().get(13)));
        
        stmt.setObject("15", C_TEXT, Types.VARCHAR, 0);
        Assert.assertEquals(param1, raw.getParameters().get(14));
        stmt.setObject("16", new StringReader(C_TEXT), Types.VARCHAR, 0);
        Assert.assertEquals(param1, Utils.read((Reader) raw.getParameters().get(15)));
        
        stmt.setObject("17", 1, Types.INTEGER);
        Assert.assertEquals(1, raw.getParameters().get(16));
        
        stmt.setObject("18", 2, Types.INTEGER, 0);
        Assert.assertEquals(2, raw.getParameters().get(17));
        
        stmt.setObject("19", 3);
        Assert.assertEquals(3, raw.getParameters().get(18));
        
        stmt.setCharacterStream("20", new StringReader(C_TEXT));
        Assert.assertEquals(param1, Utils.read((Reader) raw.getParameters().get(19)));
       
        stmt.setCharacterStream("21", new StringReader(C_TEXT), C_TEXT.length());
        Assert.assertEquals(param1, Utils.read((Reader) raw.getParameters().get(20)));
        
        stmt.setCharacterStream("22", new StringReader(C_TEXT), (long) C_TEXT.length());
        Assert.assertEquals(param1, Utils.read((Reader) raw.getParameters().get(21)));
        
        stmt.setObject("23", C_TEXT);
        Assert.assertEquals(param1, raw.getParameters().get(22));
        
        stmt.setObject("24", new StringReader(C_TEXT));
        Assert.assertEquals(param1, Utils.read((Reader) raw.getParameters().get(23)));
        
        stmt.setObject("25", 1, Types.INTEGER);
        Assert.assertEquals(1, raw.getParameters().get(24));
        
        stmt.setObject("26", 2, Types.INTEGER, 0);
        Assert.assertEquals(2, raw.getParameters().get(25));
        
        stmt.setObject("27", 3);
        Assert.assertEquals(3, raw.getParameters().get(26));
        
        rs.close();
        stmt.close();

        conn.close();

    }

    public static class MyResultSet extends MockResultSet {

        private String                value;
        private MockResultSetMetaData meta = new MockResultSetMetaData();

        public MyResultSet(Statement statement){
            super(statement);
            {
                ColumnMetaData column = new ColumnMetaData();
                column.setColumnType(Types.VARCHAR);
                meta.getColumns().add(column);
            }
            {
                ColumnMetaData column = new ColumnMetaData();
                column.setColumnType(Types.LONGVARCHAR);
                meta.getColumns().add(column);
            }
            {
                ColumnMetaData column = new ColumnMetaData();
                column.setColumnType(Types.CHAR);
                meta.getColumns().add(column);
            }
            {
                ColumnMetaData column = new ColumnMetaData();
                column.setColumnType(Types.CLOB);
                meta.getColumns().add(column);
            }
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

        @Override
        public ResultSetMetaData getMetaData() {
            return meta;
        }
    }
}

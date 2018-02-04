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
package com.alibaba.druid.bvt.proxy;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Types;
import java.util.Calendar;
import java.util.Properties;

import org.junit.Assert;

import junit.framework.TestCase;

import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxyImpl;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyConfig;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyImpl;
import com.alibaba.druid.stat.JdbcStatManager;

public class CallableStatementProxyImplTest extends TestCase {
    protected void tearDown() throws Exception {
        DruidDriver.getProxyDataSources().clear();
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }
    
    public void test_call() throws Exception {
        DataSourceProxyConfig config = new DataSourceProxyConfig();
        DataSourceProxy dataSource = new DataSourceProxyImpl(null, config);

        FilterEventAdapter filter = new FilterEventAdapter() {
        };
        filter.init(dataSource);

        config.getFilters().add(filter);

        String sql = "CALL P_0(?, ?)";
        CallableStatementProxyImpl rawCallStatement = new FakeCallableStatement(new ConnectionProxyImpl(null, null, null, 0), null, sql, 1001);

        ConnectionProxy connection = new ConnectionProxyImpl(dataSource, null, new Properties(), 1001);
        CallableStatementProxyImpl cstmt = new CallableStatementProxyImpl(connection, rawCallStatement, sql, 2001);

        cstmt.registerOutParameter(1, Types.VARCHAR);
        cstmt.registerOutParameter(1, Types.VARCHAR, "VARCHAR");
        cstmt.registerOutParameter(1, Types.VARCHAR, 3);

        cstmt.registerOutParameter("1", Types.VARCHAR);
        cstmt.registerOutParameter("1", Types.VARCHAR, "VARCHAR");
        cstmt.registerOutParameter("1", Types.VARCHAR, 3);

        cstmt.setBoolean("1", true);
        cstmt.setByte("1", (byte) 0);
        cstmt.setShort("1", (short) 0);
        cstmt.setInt("1", 0);
        cstmt.setLong("1", 0);
        cstmt.setFloat("1", 0);
        cstmt.setDouble("1", 0);
        cstmt.setBigDecimal("1", new BigDecimal("111"));
        cstmt.setString("1", "X");
        cstmt.setURL("1", null);
        cstmt.setSQLXML("1", null);
        cstmt.setBytes("1", null);
        cstmt.setDate("1", null);
        cstmt.setDate("1", null, Calendar.getInstance());
        cstmt.setTime("1", null);
        cstmt.setTime("1", null, Calendar.getInstance());
        cstmt.setTimestamp("1", null);
        cstmt.setTimestamp("1", null, Calendar.getInstance());
        cstmt.setAsciiStream("1", null);
        cstmt.setAsciiStream("1", null, 0);
        cstmt.setAsciiStream("1", null, 0L);
        cstmt.setBinaryStream("1", null);
        cstmt.setBinaryStream("1", null, 0);
        cstmt.setBinaryStream("1", null, 0L);
        cstmt.setObject("1", null);
        cstmt.setObject("1", null, Types.VARCHAR);
        cstmt.setObject("1", null, Types.VARCHAR, 3);
        cstmt.setCharacterStream("1", null);
        cstmt.setCharacterStream("1", null, 0);
        cstmt.setCharacterStream("1", null, 0L);
        cstmt.setNull("1", Types.VARCHAR);
        cstmt.setNull("1", Types.VARCHAR, "VARCHAR");
        cstmt.setRowId("1", null);
        cstmt.setNString("1", null);
        cstmt.setNCharacterStream("1", null);
        cstmt.setNCharacterStream("1", null, 0);
        cstmt.setNClob("1", (NClob) null);
        cstmt.setNClob("1", (Reader) null);
        cstmt.setNClob("1", (Reader) null, 0);
        cstmt.setClob("1", (Clob) null);
        cstmt.setClob("1", (Reader) null);
        cstmt.setClob("1", (Reader) null, 0);
        cstmt.setBlob("1", (Blob) null);
        cstmt.setBlob("1", (InputStream) null);
        cstmt.setBlob("1", (InputStream) null, 0);

        cstmt.setURL(1, null);
        cstmt.setSQLXML(1, null);

        cstmt.setArray(1, null);
        cstmt.setNCharacterStream(1, null);
        cstmt.setNCharacterStream(1, null, 0);
        cstmt.setNClob(1, (NClob) null);
        cstmt.setNClob(1, (Reader) null);
        cstmt.setNClob(1, (Reader) null, 0);
        cstmt.setNString(1, null);
        cstmt.setObject(1, null);
        cstmt.setRef(1, null);
        cstmt.setRowId(1, null);
        cstmt.setUnicodeStream(1, null, 0);

        cstmt.getClob(1);
        cstmt.getClob("1");
        cstmt.cancel();

        cstmt.getResultSet();
    }

    private static final class FakeCallableStatement extends CallableStatementProxyImpl {

        private FakeCallableStatement(ConnectionProxy connection, CallableStatement statement, String sql, long id){
            super(connection, statement, sql, id);
        }

        @Override
        public ResultSet getResultSet() throws SQLException {
            return null;
        }

        @Override
        public void cancel() throws SQLException {
        }

        @Override
        public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        }

        @Override
        public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {

        }

        @Override
        public void registerOutParameter(String parameterIndex, int sqlType, String typeName) throws SQLException {

        }

        @Override
        public void registerOutParameter(String parameterIndex, int sqlType) throws SQLException {
        }

        @Override
        public void registerOutParameter(String parameterIndex, int sqlType, int scale) throws SQLException {

        }

        @Override
        public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {

        }

        @Override
        public Clob getClob(int parameterIndex) throws SQLException {
            return null;
        }

        @Override
        public void setRowId(int parameterIndex, RowId x) throws SQLException {
        }

        @Override
        public void setRef(int parameterIndex, Ref x) throws SQLException {
        }

        @Override
        public void setObject(int parameterIndex, Object x) throws SQLException {
        }

        @Override
        public void setNString(int parameterIndex, String x) throws SQLException {
        }

        @Override
        public void setNCharacterStream(int parameterIndex, Reader x) throws SQLException {
        }

        @Override
        public void setNCharacterStream(int parameterIndex, Reader x, long length) throws SQLException {
        }

        @Override
        public void setNClob(int parameterIndex, NClob x) throws SQLException {
        }

        @Override
        public void setNClob(int parameterIndex, Reader x) throws SQLException {
        }

        @Override
        public void setNClob(int parameterIndex, Reader x, long length) throws SQLException {
        }

        @Override
        public void setArray(int parameterIndex, Array x) throws SQLException {
        }

        @Override
        public void setURL(int parameterIndex, URL x) throws SQLException {
        }

        @Override
        public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        }

        @Override
        public java.net.URL getURL(int parameterIndex) throws SQLException {
            return null;
        }

        @Override
        public void setSQLXML(int parameterIndex, SQLXML x) throws SQLException {
        }

        @Override
        public void setURL(String parameterName, java.net.URL val) throws SQLException {
        }

        @Override
        public void setNull(String parameterName, int sqlType) throws SQLException {
        }

        @Override
        public void setBoolean(String parameterName, boolean x) throws SQLException {
        }

        @Override
        public void setByte(String parameterName, byte x) throws SQLException {
        }

        @Override
        public void setShort(String parameterName, short x) throws SQLException {
        }

        @Override
        public void setInt(String parameterName, int x) throws SQLException {
        }

        @Override
        public void setLong(String parameterName, long x) throws SQLException {
        }

        @Override
        public void setFloat(String parameterName, float x) throws SQLException {
        }

        @Override
        public void setDouble(String parameterName, double x) throws SQLException {
        }

        @Override
        public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        }

        @Override
        public void setString(String parameterName, String x) throws SQLException {
        }

        @Override
        public void setBytes(String parameterName, byte[] x) throws SQLException {
        }

        @Override
        public void setDate(String parameterName, java.sql.Date x) throws SQLException {
        }

        @Override
        public void setTime(String parameterName, java.sql.Time x) throws SQLException {
        }

        @Override
        public void setTimestamp(String parameterName, java.sql.Timestamp x) throws SQLException {
        }

        @Override
        public void setAsciiStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
        }

        @Override
        public void setBinaryStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
        }

        @Override
        public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        }

        @Override
        public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        }

        @Override
        public void setObject(String parameterName, Object x) throws SQLException {
        }

        @Override
        public void setCharacterStream(String parameterName, java.io.Reader reader, int length) throws SQLException {
        }

        @Override
        public void setDate(String parameterName, java.sql.Date x, Calendar cal) throws SQLException {
        }

        @Override
        public void setTime(String parameterName, java.sql.Time x, Calendar cal) throws SQLException {
        }

        @Override
        public void setTimestamp(String parameterName, java.sql.Timestamp x, Calendar cal) throws SQLException {
        }

        @Override
        public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        }

        @Override
        public String getString(String parameterName) throws SQLException {
            return null;
        }

        @Override
        public boolean getBoolean(String parameterName) throws SQLException {
            return true;
        }

        @Override
        public byte getByte(String parameterName) throws SQLException {
            return 0;
        }

        @Override
        public short getShort(String parameterName) throws SQLException {
            return 0;
        }

        @Override
        public int getInt(String parameterName) throws SQLException {
            return 0;
        }

        @Override
        public long getLong(String parameterName) throws SQLException {
            return 0;
        }

        @Override
        public float getFloat(String parameterName) throws SQLException {
            return 0;
        }

        @Override
        public double getDouble(String parameterName) throws SQLException {
            return 0;
        }

        @Override
        public byte[] getBytes(String parameterName) throws SQLException {
            return null;
        }

        @Override
        public java.sql.Date getDate(String parameterName) throws SQLException {
            return null;
        }

        @Override
        public java.sql.Time getTime(String parameterName) throws SQLException {
            return null;
        }

        @Override
        public java.sql.Timestamp getTimestamp(String parameterName) throws SQLException {
            return null;
        }

        @Override
        public Object getObject(String parameterName) throws SQLException {
            return null;
        }

        @Override
        public BigDecimal getBigDecimal(String parameterName) throws SQLException {
            return null;
        }

        @Override
        public Object getObject(String parameterName, java.util.Map<String, Class<?>> map) throws SQLException {
            return null;
        }

        @Override
        public Ref getRef(String parameterName) throws SQLException {
            return null;
        }

        @Override
        public Blob getBlob(String parameterName) throws SQLException {
            return null;
        }

        @Override
        public Clob getClob(String parameterName) throws SQLException {
            return null;
        }

        @Override
        public Array getArray(String parameterName) throws SQLException {
            return null;
        }

        @Override
        public java.sql.Date getDate(String parameterName, Calendar cal) throws SQLException {
            return null;
        }

        @Override
        public java.sql.Time getTime(String parameterName, Calendar cal) throws SQLException {
            return null;
        }

        @Override
        public java.sql.Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
            return null;
        }

        @Override
        public java.net.URL getURL(String parameterName) throws SQLException {
            return null;
        }

        @Override
        public RowId getRowId(int parameterIndex) throws SQLException {
            return null;
        }

        @Override
        public RowId getRowId(String parameterName) throws SQLException {
            return null;
        }

        @Override
        public void setRowId(String parameterName, RowId x) throws SQLException {
        }

        @Override
        public void setNString(String parameterName, String value) throws SQLException {
        }

        @Override
        public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
        }

        @Override
        public void setNClob(String parameterName, NClob value) throws SQLException {
        }

        @Override
        public void setClob(String parameterName, Reader reader, long length) throws SQLException {
        }

        @Override
        public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        }

        @Override
        public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        }

        @Override
        public NClob getNClob(int parameterIndex) throws SQLException {
            return null;
        }

        @Override
        public NClob getNClob(String parameterName) throws SQLException {
            return null;
        }

        @Override
        public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
        }

        @Override
        public SQLXML getSQLXML(int parameterIndex) throws SQLException {
            return null;
        }

        @Override
        public SQLXML getSQLXML(String parameterName) throws SQLException {
            return null;
        }

        @Override
        public String getNString(int parameterIndex) throws SQLException {
            return null;
        }

        @Override
        public String getNString(String parameterName) throws SQLException {
            return null;
        }

        @Override
        public java.io.Reader getNCharacterStream(int parameterIndex) throws SQLException {
            return null;
        }

        @Override
        public java.io.Reader getNCharacterStream(String parameterName) throws SQLException {
            return null;
        }

        @Override
        public java.io.Reader getCharacterStream(int parameterIndex) throws SQLException {
            return null;
        }

        @Override
        public java.io.Reader getCharacterStream(String parameterName) throws SQLException {
            return null;
        }

        @Override
        public void setBlob(String parameterName, Blob x) throws SQLException {
        }

        @Override
        public void setClob(String parameterName, Clob x) throws SQLException {
        }

        @Override
        public void setAsciiStream(String parameterName, java.io.InputStream x, long length) throws SQLException {
        }

        @Override
        public void setBinaryStream(String parameterName, java.io.InputStream x, long length) throws SQLException {
        }

        @Override
        public void setCharacterStream(String parameterName, java.io.Reader reader, long length) throws SQLException {
        }

        @Override
        public void setAsciiStream(String parameterName, java.io.InputStream x) throws SQLException {
        }

        @Override
        public void setBinaryStream(String parameterName, java.io.InputStream x) throws SQLException {
        }

        @Override
        public void setCharacterStream(String parameterName, java.io.Reader reader) throws SQLException {
        }

        @Override
        public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
        }

        @Override
        public void setClob(String parameterName, Reader reader) throws SQLException {
        }

        @Override
        public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
        }

        @Override
        public void setNClob(String parameterName, Reader reader) throws SQLException {
        }
    }

}

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
package com.alibaba.druid.bvt.pool.basic;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockCallableStatement;
import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidConnectionHolder;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledCallableStatement;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.DruidPooledPreparedStatement.PreparedStatementKey;
import com.alibaba.druid.pool.PreparedStatementHolder;

public class PoolableCallableStatementTest extends TestCase {

    private DruidDataSource                dataSource = new DruidDataSource();
    protected DruidPooledConnection        conn;
    protected MockCallableStatement        raw;
    protected DruidPooledCallableStatement stmt;

    protected void setUp() throws Exception {
        MockConnection mockConn = new MockConnection();
        DruidConnectionHolder connHolder = new DruidConnectionHolder(dataSource, mockConn);
        conn = new DruidPooledConnection(connHolder);
        raw = new MockCallableStatement(null, null);
        stmt = new DruidPooledCallableStatement(conn, new PreparedStatementHolder(new PreparedStatementKey("", null,
                                                                                                           null, 0, 0,
                                                                                                           0), raw)) {

            protected SQLException checkException(Throwable error) throws SQLException {
                if (error instanceof SQLException) {
                    return (SQLException) error;
                }

                return new SQLException(error);
            }
        };

        Assert.assertEquals(0, raw.getOutParameters().size());
        stmt.registerOutParameter(1, Types.INTEGER);
        Assert.assertEquals(1, raw.getOutParameters().size());

        stmt.registerOutParameter(2, Types.DECIMAL, 10);
        Assert.assertEquals(2, raw.getOutParameters().size());
    }

    public void test_basic() throws Exception {
        Assert.assertEquals(raw, stmt.getCallableStatementRaw());
    }

    @SuppressWarnings("deprecation")
    public void test_callableStmt() throws Exception {

        Assert.assertTrue(stmt.wasNull() == false);
        stmt.getString(1);
        Assert.assertTrue(stmt.wasNull());
        stmt.getBoolean(1);
        stmt.getByte(1);
        stmt.getShort(1);
        stmt.getInt(1);
        stmt.getLong(1);
        stmt.getFloat(1);
        stmt.getDouble(1);
        stmt.getBigDecimal(1);
        stmt.getBigDecimal(1, 1);
        stmt.getBytes(1);
        stmt.getDate(1);
        stmt.getTime(1);
        stmt.getTimestamp(1);
        stmt.getObject(1);
        stmt.getRef(1);
        stmt.getBlob(1);

        stmt.getString("1");
        stmt.getBoolean("1");
        stmt.getByte("1");
        stmt.getShort("1");
        stmt.getInt("1");
        stmt.getLong("1");
        stmt.getFloat("1");
        stmt.getDouble("1");
        stmt.getBigDecimal("1");
        stmt.getBytes("1");
        stmt.getDate("1");
        stmt.getTime("1");
        stmt.getTimestamp("1");
        stmt.getObject("1");
        stmt.getRef("1");
        stmt.getBlob("1");
    }

    public void test_getByLabel_error() {
        {
            SQLException error = null;
            try {
                stmt.getTimestamp(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        // ////////////////

        {
            SQLException error = null;
            try {
                stmt.getString("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getBoolean("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getByte("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getShort("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getInt("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getLong("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getFloat("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getDouble("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getBigDecimal("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getBytes("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getDate("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getTime("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getTimestamp("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getObject("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getRef("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getBlob("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    @SuppressWarnings("deprecation")
    public void test_get_error() {
        {
            SQLException error = null;
            try {
                stmt.getString(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getBoolean(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getByte(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getShort(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getInt(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getLong(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getFloat(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getDouble(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getBigDecimal(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getBigDecimal(0, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getBytes(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getDate(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getTime(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getObject(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getRef(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getBlob(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

    }

    public void test_getNClob() throws Exception {

        stmt.getNClob(1);
        stmt.getNClob("1");

        {
            SQLException error = null;
            try {
                stmt.getNClob(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getNClob("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getNString() throws Exception {

        stmt.getNString(1);
        stmt.getNString("1");

        {
            SQLException error = null;
            try {
                stmt.getNString(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getNString("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getNCharacterStream() throws Exception {

        stmt.getNCharacterStream(1);
        stmt.getNCharacterStream("1");

        {
            SQLException error = null;
            try {
                stmt.getNCharacterStream(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getNCharacterStream("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getCharacterStream() throws Exception {

        stmt.getCharacterStream(1);
        stmt.getCharacterStream("1");

        {
            SQLException error = null;
            try {
                stmt.getCharacterStream(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getCharacterStream("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateCharacterStream_2() throws Exception {

        stmt.setCharacterStream(1, (Reader) null, 1L);
        stmt.setCharacterStream("1", (Reader) null, 1L);

        {
            SQLException error = null;
            try {
                stmt.setCharacterStream("0", (Reader) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setCharacterStream(0, (Reader) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setNClob() throws Exception {

        stmt.setNClob(1, (Reader) null);
        stmt.setNClob("1", (Reader) null);

        {
            SQLException error = null;
            try {
                stmt.setNClob("0", (Reader) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setNClob(0, (Reader) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setClob() throws Exception {

        stmt.setClob(1, (Reader) null);
        stmt.setClob("1", (Reader) null);

        {
            SQLException error = null;
            try {
                stmt.setClob("0", (Reader) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setClob(0, (Reader) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setNCharacterStream() throws Exception {

        stmt.setNCharacterStream(1, (Reader) null);
        stmt.setNCharacterStream("1", (Reader) null);

        {
            SQLException error = null;
            try {
                stmt.setNCharacterStream("0", (Reader) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setNCharacterStream(0, (Reader) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setCharacterStream() throws Exception {

        stmt.setCharacterStream(1, (Reader) null);
        stmt.setCharacterStream("1", (Reader) null);

        {
            SQLException error = null;
            try {
                stmt.setCharacterStream("0", (Reader) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setCharacterStream(0, (Reader) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setBinaryStream() throws Exception {

        stmt.setBinaryStream(1, (InputStream) null);
        stmt.setBinaryStream("1", (InputStream) null);

        {
            SQLException error = null;
            try {
                stmt.setBinaryStream("0", (InputStream) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setBinaryStream(0, (InputStream) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setBinaryStream_1() throws Exception {

        stmt.setBinaryStream(1, (InputStream) null, 1);
        stmt.setBinaryStream("1", (InputStream) null, 1);

        {
            SQLException error = null;
            try {
                stmt.setBinaryStream("0", (InputStream) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setBinaryStream(0, (InputStream) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setBinaryStream_2() throws Exception {

        stmt.setBinaryStream(1, (InputStream) null, 1L);
        stmt.setBinaryStream("1", (InputStream) null, 1L);

        {
            SQLException error = null;
            try {
                stmt.setBinaryStream("0", (InputStream) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setBinaryStream(0, (InputStream) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setAsciiStream() throws Exception {

        stmt.setAsciiStream(1, (InputStream) null);
        stmt.setAsciiStream("1", (InputStream) null);

        {
            SQLException error = null;
            try {
                stmt.setAsciiStream("0", (InputStream) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setAsciiStream(0, (InputStream) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setBlob() throws Exception {

        stmt.setBlob(1, (InputStream) null);
        stmt.setBlob("1", (InputStream) null);

        {
            SQLException error = null;
            try {
                stmt.setBlob("0", (InputStream) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setBlob(0, (InputStream) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setClob_1() throws Exception {

        stmt.setClob(1, (Clob) null);
        stmt.setClob("1", (Clob) null);

        {
            SQLException error = null;
            try {
                stmt.setClob("0", (Clob) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setClob(0, (Clob) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setClob_2() throws Exception {

        stmt.setClob(1, (Reader) null, 1L);
        stmt.setClob("1", (Reader) null, 1L);

        {
            SQLException error = null;
            try {
                stmt.setClob("0", (Reader) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setClob(0, (Reader) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setAsciiStream_1() throws Exception {

        stmt.setAsciiStream(1, (InputStream) null, 1L);
        stmt.setAsciiStream("1", (InputStream) null, 1L);

        {
            SQLException error = null;
            try {
                stmt.setAsciiStream("0", (InputStream) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setAsciiStream(0, (InputStream) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setNull() throws Exception {

        stmt.setNull(1, Types.INTEGER, "Int");
        stmt.setNull("1", Types.INTEGER, "Int");

        {
            SQLException error = null;
            try {
                stmt.setNull("0", Types.INTEGER, "Int");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setNull(0, Types.INTEGER, "Int");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setTimestamp() throws Exception {

        stmt.setTimestamp(1, (Timestamp) null);
        stmt.setTimestamp("1", (Timestamp) null);

        {
            SQLException error = null;
            try {
                stmt.setTimestamp("0", (Timestamp) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setTimestamp(0, (Timestamp) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setTimestamp_1() throws Exception {

        stmt.setTimestamp(1, (Timestamp) null, null);
        stmt.setTimestamp("1", (Timestamp) null, null);

        {
            SQLException error = null;
            try {
                stmt.setTimestamp("0", (Timestamp) null, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setTimestamp(0, (Timestamp) null, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setTime() throws Exception {

        stmt.setTime(1, (Time) null);
        stmt.setTime("1", (Time) null);

        {
            SQLException error = null;
            try {
                stmt.setTime("0", (Time) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setTime(0, (Time) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setTime_1() throws Exception {

        stmt.setTime(1, (Time) null, null);
        stmt.setTime("1", (Time) null, null);

        {
            SQLException error = null;
            try {
                stmt.setTime("0", (Time) null, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setTime(0, (Time) null, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setDate() throws Exception {

        stmt.setDate(1, (Date) null);
        stmt.setDate("1", (Date) null);

        {
            SQLException error = null;
            try {
                stmt.setDate("0", (Date) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setDate(0, (Date) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setDate_1() throws Exception {

        stmt.setDate(1, (Date) null, null);
        stmt.setDate("1", (Date) null, null);

        {
            SQLException error = null;
            try {
                stmt.setDate("0", (Date) null, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setDate(0, (Date) null, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setCharacterStream_1() throws Exception {

        stmt.setCharacterStream(1, (Reader) null, 1);
        stmt.setCharacterStream("1", (Reader) null, 1);

        {
            SQLException error = null;
            try {
                stmt.setCharacterStream("0", (Reader) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setCharacterStream(0, (Reader) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setObject() throws Exception {

        stmt.setObject(1, null);
        stmt.setObject("1", null);

        {
            SQLException error = null;
            try {
                stmt.setObject("0", null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setObject(0, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setObject_1() throws Exception {

        stmt.setObject(1, null, Types.INTEGER);
        stmt.setObject("1", null, Types.INTEGER);

        {
            SQLException error = null;
            try {
                stmt.setObject("0", null, Types.INTEGER);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setObject(0, null, Types.INTEGER);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setObject_2() throws Exception {

        stmt.setObject(1, null, Types.INTEGER, 2);
        stmt.setObject("1", null, Types.INTEGER, 2);

        {
            SQLException error = null;
            try {
                stmt.setObject("0", null, Types.INTEGER, 2);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setObject(0, null, Types.INTEGER, 2);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setAsciiStream_2() throws Exception {

        stmt.setAsciiStream(1, (InputStream) null, 1);
        stmt.setAsciiStream("1", (InputStream) null, 1);

        {
            SQLException error = null;
            try {
                stmt.setAsciiStream("0", (InputStream) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setAsciiStream(0, (InputStream) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setBytes() throws Exception {

        stmt.setBytes(1, null);
        stmt.setBytes("1", null);

        {
            SQLException error = null;
            try {
                stmt.setBytes("0", null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setBytes(0, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setString() throws Exception {

        stmt.setString(1, null);
        stmt.setString("1", null);

        {
            SQLException error = null;
            try {
                stmt.setString("0", null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setString(0, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setBigDecimal() throws Exception {

        stmt.setBigDecimal(1, null);
        stmt.setBigDecimal("1", null);

        {
            SQLException error = null;
            try {
                stmt.setBigDecimal("0", null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setBigDecimal(0, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setDouble() throws Exception {

        stmt.setDouble(1, 1.0D);
        stmt.setDouble("1", 1.0D);

        {
            SQLException error = null;
            try {
                stmt.setDouble("0", 1.0D);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setDouble(0, 1.0D);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setFloat() throws Exception {

        stmt.setFloat(1, 1.0F);
        stmt.setFloat("1", 1.0F);

        {
            SQLException error = null;
            try {
                stmt.setFloat("0", 1.0F);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setFloat(0, 1.0F);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setLong() throws Exception {

        stmt.setLong(1, 2);
        stmt.setLong("1", 2);

        {
            SQLException error = null;
            try {
                stmt.setLong("0", 2);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setLong(0, 2);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setInt() throws Exception {

        stmt.setInt(1, 2);
        stmt.setInt("1", 2);

        {
            SQLException error = null;
            try {
                stmt.setInt("0", 2);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setInt(0, 2);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setShort() throws Exception {

        stmt.setShort(1, Short.MAX_VALUE);
        stmt.setShort("1", Short.MAX_VALUE);

        {
            SQLException error = null;
            try {
                stmt.setShort("0", Short.MAX_VALUE);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setShort(0, Short.MAX_VALUE);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setByte() throws Exception {

        stmt.setByte(1, Byte.MAX_VALUE);
        stmt.setByte("1", Byte.MAX_VALUE);

        {
            SQLException error = null;
            try {
                stmt.setByte("0", Byte.MAX_VALUE);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setByte(0, Byte.MAX_VALUE);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getClob() throws Exception {

        stmt.getClob(1);
        stmt.getClob("1");

        {
            SQLException error = null;
            try {
                stmt.getClob(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getClob("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getArray() throws Exception {

        stmt.getArray(1);
        stmt.getArray("1");

        {
            SQLException error = null;
            try {
                stmt.getArray(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getArray("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getDate() throws Exception {

        stmt.getDate(1);
        stmt.getDate("1");

        {
            SQLException error = null;
            try {
                stmt.getDate(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getDate("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getDate_1() throws Exception {

        stmt.getDate(1, null);
        stmt.getDate("1", null);

        {
            SQLException error = null;
            try {
                stmt.getDate(0, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getDate("0", null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getTime() throws Exception {

        stmt.getTime(1);
        stmt.getTime("1");

        {
            SQLException error = null;
            try {
                stmt.getTime(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getTime("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getTime_1() throws Exception {

        stmt.getTime(1, null);
        stmt.getTime("1", null);

        {
            SQLException error = null;
            try {
                stmt.getTime(0, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getTime("0", null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getTimestamp() throws Exception {

        stmt.getTimestamp(1);
        stmt.getTimestamp("1");

        {
            SQLException error = null;
            try {
                stmt.getTimestamp(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getTimestamp("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getTimestamp_1() throws Exception {

        stmt.getTimestamp(1, null);
        stmt.getTimestamp("1", null);

        {
            SQLException error = null;
            try {
                stmt.getTimestamp(0, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getTimestamp("0", null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getURL() throws Exception {

        stmt.getURL(1);
        stmt.getURL("1");

        {
            SQLException error = null;
            try {
                stmt.getURL(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getURL("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setBlob_1() throws Exception {

        stmt.setBlob(1, (Blob) null);
        stmt.setBlob("1", (Blob) null);

        {
            SQLException error = null;
            try {
                stmt.setBlob("0", (Blob) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setBlob(0, (Blob) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setSQLXML() throws Exception {

        stmt.setSQLXML(1, (SQLXML) null);
        stmt.setSQLXML("1", (SQLXML) null);

        {
            SQLException error = null;
            try {
                stmt.setSQLXML("0", (SQLXML) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setSQLXML(0, (SQLXML) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getSQLXML() throws Exception {

        stmt.getSQLXML(1);
        stmt.getSQLXML("1");

        {
            SQLException error = null;
            try {
                stmt.getSQLXML(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getSQLXML("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setBlob_2() throws Exception {

        stmt.setBlob(1, (InputStream) null, 1L);
        stmt.setBlob("1", (InputStream) null, 1L);

        {
            SQLException error = null;
            try {
                stmt.setBlob("0", (InputStream) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setBlob(0, (InputStream) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setNClob_1() throws Exception {

        stmt.setNClob(1, (Reader) null, 1L);
        stmt.setNClob("1", (Reader) null, 1L);

        {
            SQLException error = null;
            try {
                stmt.setNClob("0", (Reader) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setNClob(0, (Reader) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setNCharacterStream_1() throws Exception {

        stmt.setNCharacterStream(1, (Reader) null, 1L);
        stmt.setNCharacterStream("1", (Reader) null, 1L);

        {
            SQLException error = null;
            try {
                stmt.setNCharacterStream("0", (Reader) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setNCharacterStream(0, (Reader) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setRowId() throws Exception {

        stmt.setRowId(1, (RowId) null);
        stmt.setRowId("1", (RowId) null);

        {
            SQLException error = null;
            try {
                stmt.setRowId("0", (RowId) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setRowId(0, (RowId) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getRowId() throws Exception {

        stmt.getRowId(1);
        stmt.getRowId("1");

        {
            SQLException error = null;
            try {
                stmt.getRowId(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getRowId("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setNString() throws Exception {

        stmt.setNString(1, (String) null);
        stmt.setNString("1", (String) null);

        {
            SQLException error = null;
            try {
                stmt.setNString("0", (String) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setNString(0, (String) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getObject() throws Exception {

        stmt.getObject(1, (java.util.Map) null);
        stmt.getObject("1", (java.util.Map) null);

        {
            SQLException error = null;
            try {
                stmt.getObject(0, (java.util.Map) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.getObject("0", (java.util.Map) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setBoolean() throws Exception {

        stmt.setBoolean(1, true);
        stmt.setBoolean("1", true);

        {
            SQLException error = null;
            try {
                stmt.setBoolean("0", true);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setBoolean(0, true);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setURL() throws Exception {

        stmt.setURL(1, null);
        stmt.setURL("1", null);

        {
            SQLException error = null;
            try {
                stmt.setURL("0", null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setURL(0, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setNClob_2() throws Exception {

        stmt.setNClob(1, (NClob) null);
        stmt.setNClob("1", (NClob) null);

        {
            SQLException error = null;
            try {
                stmt.setNClob("0", (NClob) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setNClob(0, (NClob) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setNull_1() throws Exception {

        stmt.setNull(1, Types.INTEGER);
        stmt.setNull("1", Types.INTEGER);

        {
            SQLException error = null;
            try {
                stmt.setNull("0", Types.INTEGER);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setNull(0, Types.INTEGER);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_registerOutParameter() throws Exception {

        stmt.registerOutParameter(1, Types.INTEGER, "Int");
        stmt.registerOutParameter("1", Types.INTEGER, "Int");

        {
            SQLException error = null;
            try {
                stmt.registerOutParameter("0", Types.INTEGER, "Int");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.registerOutParameter(0, Types.INTEGER, "Int");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_registerOutParameter_1() throws Exception {

        stmt.registerOutParameter(1, Types.INTEGER, 2);
        stmt.registerOutParameter("1", Types.INTEGER, 2);

        {
            SQLException error = null;
            try {
                stmt.registerOutParameter("0", Types.INTEGER, 2);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.registerOutParameter(0, Types.INTEGER, 2);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_registerOutParameter_2() throws Exception {

        stmt.registerOutParameter(1, Types.INTEGER);
        stmt.registerOutParameter("1", Types.INTEGER);

        {
            SQLException error = null;
            try {
                stmt.registerOutParameter("0", Types.INTEGER);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.registerOutParameter(0, Types.INTEGER);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }
}

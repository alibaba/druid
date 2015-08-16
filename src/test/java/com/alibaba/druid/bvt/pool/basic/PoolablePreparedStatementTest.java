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
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.pool.DruidConnectionHolder;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.alibaba.druid.pool.DruidPooledPreparedStatement.PreparedStatementKey;
import com.alibaba.druid.pool.PreparedStatementHolder;

public class PoolablePreparedStatementTest extends TestCase {

    protected MockPreparedStatement        raw;
    protected DruidPooledPreparedStatement stmt;

    protected void setUp() throws Exception {
        DruidDataSource                dataSource = new DruidDataSource();
        MockConnection mockConn = new MockConnection();
        DruidConnectionHolder connHolder = new DruidConnectionHolder(dataSource, mockConn);
        DruidPooledConnection conn = new DruidPooledConnection(connHolder);

        raw = new MockPreparedStatement(null, null);
        stmt = new DruidPooledPreparedStatement(conn, new PreparedStatementHolder(new PreparedStatementKey("", null,
                                                                                                           null, 0, 0,
                                                                                                           0), raw)) {

            protected SQLException checkException(Throwable error) throws SQLException {
                if (error instanceof SQLException) {
                    return (SQLException) error;
                }

                return new SQLException(error);
            }
        };
    }

    protected void tearDown() throws Exception {
        stmt.clearParameters();
        Assert.assertEquals(0, raw.getParameters().size());
    }

    public void test_basic() throws Exception {
        Assert.assertEquals(raw, stmt.getRawPreparedStatement());
        Assert.assertEquals(raw, stmt.getRawStatement());
    }

    public void test_setBoolean() throws Exception {
        stmt.setBoolean(1, true);

        Assert.assertEquals(Boolean.TRUE, raw.getParameters().get(0));

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

    public void test_setNull() throws Exception {
        stmt.setNull(1, Types.INTEGER);

        Assert.assertEquals(null, raw.getParameters().get(0));

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

    public void test_setByte() throws Exception {
        stmt.setByte(1, (byte) 23);

        Assert.assertEquals(new Byte((byte) 23), raw.getParameters().get(0));

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

    public void test_setShort() throws Exception {
        stmt.setShort(1, Short.MAX_VALUE);

        Assert.assertEquals(new Short(Short.MAX_VALUE), raw.getParameters().get(0));

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

    public void test_setInt() throws Exception {
        stmt.setInt(1, Integer.MAX_VALUE);

        Assert.assertEquals(new Integer(Integer.MAX_VALUE), raw.getParameters().get(0));

        {
            SQLException error = null;
            try {
                stmt.setInt(0, Integer.MAX_VALUE);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setLong() throws Exception {
        stmt.setLong(1, Long.MAX_VALUE);

        Assert.assertEquals(new Long(Long.MAX_VALUE), raw.getParameters().get(0));

        {
            SQLException error = null;
            try {
                stmt.setLong(0, Long.MAX_VALUE);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setFloat() throws Exception {
        stmt.setFloat(1, Float.MAX_VALUE);

        Assert.assertEquals(new Float(Float.MAX_VALUE), raw.getParameters().get(0));

        {
            SQLException error = null;
            try {
                stmt.setFloat(0, Float.MAX_VALUE);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setDouble() throws Exception {
        stmt.setDouble(1, Double.MAX_VALUE);

        Assert.assertEquals(new Double(Double.MAX_VALUE), raw.getParameters().get(0));

        {
            SQLException error = null;
            try {
                stmt.setDouble(0, Double.MAX_VALUE);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setBigDecimal() throws Exception {
        stmt.setBigDecimal(1, BigDecimal.TEN);

        Assert.assertEquals(BigDecimal.TEN, raw.getParameters().get(0));

        {
            SQLException error = null;
            try {
                stmt.setBigDecimal(0, BigDecimal.TEN);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setString() throws Exception {
        stmt.setString(1, "中国");

        Assert.assertEquals("中国", raw.getParameters().get(0));

        {
            SQLException error = null;
            try {
                stmt.setString(0, "中国");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setBytes() throws Exception {
        byte[] bytes = "中国".getBytes();
        stmt.setBytes(1, bytes);

        Assert.assertEquals(true, Arrays.equals(bytes, (byte[]) raw.getParameters().get(0)));

        {
            SQLException error = null;
            try {
                stmt.setBytes(0, bytes);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setDate() throws Exception {
        Date value = new Date(System.currentTimeMillis());
        stmt.setDate(1, value);

        Assert.assertEquals(value, raw.getParameters().get(0));

        {
            SQLException error = null;
            try {
                stmt.setDate(0, value);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setTimestamp() throws Exception {
        Timestamp value = new Timestamp(System.currentTimeMillis());
        stmt.setTimestamp(1, value);

        Assert.assertEquals(value, raw.getParameters().get(0));

        {
            SQLException error = null;
            try {
                stmt.setTimestamp(0, value);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setAsciiStream() throws Exception {
        InputStream value = null;
        stmt.setAsciiStream(1, value);

        Assert.assertEquals(value, raw.getParameters().get(0));

        {
            SQLException error = null;
            try {
                stmt.setAsciiStream(0, value);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    @SuppressWarnings("deprecation")
    public void test_setUnicodeStream() throws Exception {
        InputStream value = null;
        stmt.setUnicodeStream(1, value, 0);

        Assert.assertEquals(value, raw.getParameters().get(0));

        {
            SQLException error = null;
            try {
                stmt.setUnicodeStream(0, value, 0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setBinaryStream() throws Exception {
        InputStream value = null;
        stmt.setBinaryStream(1, value, 0);

        Assert.assertEquals(value, raw.getParameters().get(0));

        {
            SQLException error = null;
            try {
                stmt.setBinaryStream(0, value, 0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateCharacterStream_2() throws Exception {

        stmt.setCharacterStream(1, (Reader) null, 1L);

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

    public void test_setRef() throws Exception {

        stmt.setRef(1, null);

        {
            SQLException error = null;
            try {
                stmt.setRef(0, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setArray() throws Exception {

        stmt.setArray(1, null);

        {
            SQLException error = null;
            try {
                stmt.setArray(0, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }
}

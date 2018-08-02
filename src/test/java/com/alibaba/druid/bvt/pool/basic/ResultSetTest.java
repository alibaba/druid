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
package com.alibaba.druid.bvt.pool.basic;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.SQLXML;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.pool.DruidPooledResultSet;
import com.alibaba.druid.pool.DruidPooledStatement;

public class ResultSetTest extends TestCase {

    private DruidPooledStatement stmt;
    private MockResultSet     raw;
    private DruidPooledResultSet resultSet;

    protected void setUp() throws Exception {
        stmt = new DruidPooledStatement(null, null) {

            protected SQLException checkException(Throwable error) throws SQLException {
                if (error instanceof SQLException) {
                    return (SQLException) error;
                }

                return new SQLException(error);
            }
        };

        raw = new MockResultSet(null);
        raw.getRows().add(new Object[] { null });
        resultSet = new DruidPooledResultSet(stmt, raw);
    }

    @SuppressWarnings("deprecation")
    public void test_get() throws Exception {

        Assert.assertTrue(stmt == resultSet.getPoolableStatement());
        Assert.assertTrue(raw == resultSet.getRawResultSet());

        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.wasNull() == false);
        resultSet.getString(1);
        Assert.assertTrue(resultSet.wasNull());
        resultSet.getBoolean(1);
        resultSet.getByte(1);
        resultSet.getShort(1);
        resultSet.getInt(1);
        resultSet.getLong(1);
        resultSet.getFloat(1);
        resultSet.getDouble(1);
        resultSet.getBigDecimal(1);
        resultSet.getBigDecimal(1, 1);
        resultSet.getBytes(1);
        resultSet.getDate(1);
        resultSet.getTime(1);
        resultSet.getTimestamp(1);
        resultSet.getAsciiStream(1);
        resultSet.getUnicodeStream(1);
        resultSet.getBinaryStream(1);

        resultSet.getString("1");
        resultSet.getBoolean("1");
        resultSet.getByte("1");
        resultSet.getShort("1");
        resultSet.getInt("1");
        resultSet.getLong("1");
        resultSet.getFloat("1");
        resultSet.getDouble("1");
        resultSet.getBigDecimal("1");
        resultSet.getBigDecimal("1", 1);
        resultSet.getBytes("1");
        resultSet.getDate("1");
        resultSet.getTime("1");
        resultSet.getTimestamp("1");
        resultSet.getAsciiStream("1");
        resultSet.getUnicodeStream("1");
        resultSet.getBinaryStream("1");

    }

    public void test_set() throws Exception {
        long currentMillis = System.currentTimeMillis();

        Assert.assertTrue(resultSet.next());

        resultSet.updateNull(1);
        Assert.assertNull(resultSet.getString(1));
        resultSet.updateBoolean(1, true);
        Assert.assertEquals(true, resultSet.getBoolean(1));

        resultSet.updateByte(1, (byte) 12);
        Assert.assertEquals(12, resultSet.getByte(1));

        resultSet.updateShort(1, (short) 23);
        Assert.assertEquals(23, resultSet.getShort(1));

        resultSet.updateInt(1, 34);
        Assert.assertEquals(34, resultSet.getInt(1));

        resultSet.updateLong(1, 45);
        Assert.assertEquals(45, resultSet.getLong(1));

        resultSet.updateFloat(1, 1.0F);
        Assert.assertEquals(true, 1.0F == resultSet.getFloat(1));

        resultSet.updateDouble(1, 2.0D);
        Assert.assertEquals(true, 2.0D == resultSet.getDouble(1));

        resultSet.updateBigDecimal(1, new BigDecimal("33"));
        Assert.assertEquals(new BigDecimal("33"), resultSet.getBigDecimal(1));

        resultSet.updateString(1, "xxx");
        Assert.assertEquals("xxx", resultSet.getString(1));

        resultSet.updateBytes(1, new byte[0]);
        Assert.assertEquals(0, resultSet.getBytes(1).length);

        resultSet.updateDate(1, new java.sql.Date(currentMillis));
        Assert.assertEquals(new java.sql.Date(currentMillis), resultSet.getDate(1));

        resultSet.updateTime(1, new java.sql.Time(1000));
        Assert.assertEquals(new java.sql.Time(1000), resultSet.getTime(1));

        resultSet.updateTimestamp(1, new java.sql.Timestamp(currentMillis));
        Assert.assertEquals(new java.sql.Timestamp(currentMillis), resultSet.getTimestamp(1));
    }

    public void test_set_error() throws Exception {
        long currentMillis = System.currentTimeMillis();

        Assert.assertTrue(resultSet.next());

        {
            SQLException error = null;
            try {
                resultSet.updateNull(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateBoolean(0, true);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateByte(0, (byte) 12);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateShort(0, (short) 23);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateInt(0, 34);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateLong(0, 45);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateFloat(0, 1.0F);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateDouble(0, 2.0D);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateBigDecimal(0, new BigDecimal("33"));
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateString(0, "xxx");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateBytes(0, new byte[0]);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateDate(0, new java.sql.Date(currentMillis));
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateTime(0, new java.sql.Time(1000));
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateTimestamp(0, new java.sql.Timestamp(currentMillis));
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

    }

    public void test_setByName() throws Exception {
        long currentMillis = System.currentTimeMillis();

        Assert.assertTrue(resultSet.next());

        resultSet.updateNull("1");
        Assert.assertNull(resultSet.getString(1));
        resultSet.updateBoolean("1", true);
        Assert.assertEquals(true, resultSet.getBoolean("1"));

        resultSet.updateByte("1", (byte) 12);
        Assert.assertEquals(12, resultSet.getByte("1"));

        resultSet.updateShort("1", (short) 23);
        Assert.assertEquals(23, resultSet.getShort("1"));

        resultSet.updateInt("1", 34);
        Assert.assertEquals(34, resultSet.getInt("1"));

        resultSet.updateLong("1", 45);
        Assert.assertEquals(45, resultSet.getLong("1"));

        resultSet.updateFloat("1", 1.0F);
        Assert.assertEquals(true, 1.0F == resultSet.getFloat("1"));

        resultSet.updateDouble("1", 2.0D);
        Assert.assertEquals(true, 2.0D == resultSet.getDouble("1"));

        resultSet.updateBigDecimal("1", new BigDecimal("33"));
        Assert.assertEquals(new BigDecimal("33"), resultSet.getBigDecimal("1"));

        resultSet.updateString("1", "xxx");
        Assert.assertEquals("xxx", resultSet.getString("1"));

        resultSet.updateBytes("1", new byte[0]);
        Assert.assertEquals(0, resultSet.getBytes("1").length);

        resultSet.updateDate("1", new java.sql.Date(currentMillis));
        Assert.assertEquals(new java.sql.Date(currentMillis), resultSet.getDate("1"));

        resultSet.updateTime("1", new java.sql.Time(1000));
        Assert.assertEquals(new java.sql.Time(1000), resultSet.getTime("1"));

        resultSet.updateTimestamp("1", new java.sql.Timestamp(currentMillis));
        Assert.assertEquals(new java.sql.Timestamp(currentMillis), resultSet.getTimestamp("1"));
    }

    public void test_updateByLabel_error() throws Exception {
        long currentMillis = System.currentTimeMillis();

        Assert.assertTrue(resultSet.next());

        {
            SQLException error = null;
            try {
                resultSet.updateNull("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateBoolean("0", true);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateByte("0", (byte) 12);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateShort("0", (short) 23);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateInt("0", 34);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateLong("0", 45);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateFloat("0", 1.0F);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateDouble("0", 2.0D);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateBigDecimal("0", new BigDecimal("33"));
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateString("0", "xxx");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateBytes("0", new byte[0]);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateDate("0", new java.sql.Date(currentMillis));
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateTime("0", new java.sql.Time(1000));
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateTimestamp("0", new java.sql.Timestamp(currentMillis));
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

    }

    public void test_updateBinaryStream() throws Exception {
        resultSet.next();

        resultSet.updateBinaryStream(1, (InputStream) null);
        resultSet.updateBinaryStream("1", (InputStream) null);

        {
            SQLException error = null;
            try {
                resultSet.updateBinaryStream("0", (InputStream) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateBinaryStream(0, (InputStream) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateBinaryStream_2() throws Exception {
        resultSet.next();

        resultSet.updateBinaryStream(1, (InputStream) null, 1L);
        resultSet.updateBinaryStream("1", (InputStream) null, 1L);

        {
            SQLException error = null;
            try {
                resultSet.updateBinaryStream("0", (InputStream) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateBinaryStream(0, (InputStream) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateCharacterStream() throws Exception {
        resultSet.next();

        resultSet.updateCharacterStream(1, (Reader) null);
        resultSet.updateCharacterStream("1", (Reader) null);

        {
            SQLException error = null;
            try {
                resultSet.updateCharacterStream("0", (Reader) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateCharacterStream(0, (Reader) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_update_blob() throws Exception {
        resultSet.next();

        resultSet.updateBlob(1, (InputStream) null);
        resultSet.updateBlob("1", (InputStream) null);

        {
            SQLException error = null;
            try {
                resultSet.updateBlob("0", (InputStream) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateBlob(0, (InputStream) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_update_clob() throws Exception {
        resultSet.next();

        resultSet.updateClob(1, (Reader) null);
        resultSet.updateClob("1", (Reader) null);

        {
            SQLException error = null;
            try {
                resultSet.updateClob("0", (Reader) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateClob(0, (Reader) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_update_nclob() throws Exception {
        resultSet.next();

        resultSet.updateNClob(1, (Reader) null);
        resultSet.updateNClob("1", (Reader) null);

        {
            SQLException error = null;
            try {
                resultSet.updateNClob("0", (Reader) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateNClob(0, (Reader) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_update_nclob_1() throws Exception {
        resultSet.next();

        resultSet.updateNClob(1, (Reader) null, 1);
        resultSet.updateNClob("1", (Reader) null, 1);

        {
            SQLException error = null;
            try {
                resultSet.updateNClob("0", (Reader) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateNClob(0, (Reader) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_update_clob_1() throws Exception {
        resultSet.next();

        resultSet.updateClob(1, (Reader) null, 1);
        resultSet.updateClob("1", (Reader) null, 1);

        {
            SQLException error = null;
            try {
                resultSet.updateClob("0", (Reader) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateClob(0, (Reader) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_update_blob_1() throws Exception {
        resultSet.next();

        resultSet.updateBlob(1, (InputStream) null, 1);
        resultSet.updateBlob("1", (InputStream) null, 1);

        {
            SQLException error = null;
            try {
                resultSet.updateBlob("0", (InputStream) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateBlob(0, (InputStream) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateCharacterStream_1() throws Exception {
        resultSet.next();

        resultSet.updateCharacterStream(1, (Reader) null, 1);
        resultSet.updateCharacterStream("1", (Reader) null, 1);

        {
            SQLException error = null;
            try {
                resultSet.updateCharacterStream("0", (Reader) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateCharacterStream(0, (Reader) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateAsciiStream_1() throws Exception {
        resultSet.next();

        resultSet.updateAsciiStream(1, (InputStream) null, 1);
        resultSet.updateAsciiStream("1", (InputStream) null, 1);

        {
            SQLException error = null;
            try {
                resultSet.updateAsciiStream("0", (InputStream) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateAsciiStream(0, (InputStream) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateAsciiStream_2() throws Exception {
        resultSet.next();

        resultSet.updateAsciiStream(1, (InputStream) null, 1L);
        resultSet.updateAsciiStream("1", (InputStream) null, 1L);

        {
            SQLException error = null;
            try {
                resultSet.updateAsciiStream("0", (InputStream) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateAsciiStream(0, (InputStream) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateBinaryStream_1() throws Exception {
        resultSet.next();

        resultSet.updateBinaryStream(1, (InputStream) null, 1);
        resultSet.updateBinaryStream("1", (InputStream) null, 1);

        {
            SQLException error = null;
            try {
                resultSet.updateBinaryStream("0", (InputStream) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateBinaryStream(0, (InputStream) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateAsciiStream() throws Exception {
        resultSet.next();

        resultSet.updateAsciiStream(1, (InputStream) null);
        resultSet.updateAsciiStream("1", (InputStream) null);

        {
            SQLException error = null;
            try {
                resultSet.updateAsciiStream("0", (InputStream) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateAsciiStream(0, (InputStream) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateNCharacterStream() throws Exception {
        resultSet.next();

        resultSet.updateNCharacterStream(1, (Reader) null);
        resultSet.updateNCharacterStream("1", (Reader) null);

        {
            SQLException error = null;
            try {
                resultSet.updateNCharacterStream("0", (Reader) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateNCharacterStream(0, (Reader) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateNCharacterStream_1() throws Exception {
        resultSet.next();

        resultSet.updateNCharacterStream(1, (Reader) null, 1);
        resultSet.updateNCharacterStream("1", (Reader) null, 1);

        {
            SQLException error = null;
            try {
                resultSet.updateNCharacterStream("0", (Reader) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateNCharacterStream(0, (Reader) null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateSQLXML() throws Exception {
        resultSet.next();

        resultSet.updateSQLXML(1, (SQLXML) null);
        resultSet.updateSQLXML("1", (SQLXML) null);

        {
            SQLException error = null;
            try {
                resultSet.updateSQLXML("0", (SQLXML) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateSQLXML(0, (SQLXML) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_get_error() throws Exception {
        {
            SQLException error = null;
            try {
                resultSet.getString(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getBoolean(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getByte(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getShort(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getInt(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getLong(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getFloat(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getDouble(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getBigDecimal(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getBigDecimal(0, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getBytes(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getDate(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getTime(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getTimestamp(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getAsciiStream(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getUnicodeStream(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getBinaryStream(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        // ////////////////

        {
            SQLException error = null;
            try {
                resultSet.getString("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getBoolean("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getByte("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getShort("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getInt("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getLong("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getFloat("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getDouble("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getBigDecimal("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getBigDecimal("0", 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getBytes("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getDate("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getTime("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getTimestamp("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getAsciiStream("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getUnicodeStream("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getBinaryStream("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getSQLXML() throws Exception {
        resultSet.next();

        resultSet.getSQLXML(1);
        resultSet.getSQLXML("1");

        {
            SQLException error = null;
            try {
                resultSet.getSQLXML(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getSQLXML("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getNClob() throws Exception {
        resultSet.next();

        resultSet.getNClob(1);
        resultSet.getNClob("1");

        {
            SQLException error = null;
            try {
                resultSet.getNClob(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getNClob("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getNString() throws Exception {
        resultSet.next();

        resultSet.getNString(1);
        resultSet.getNString("1");

        {
            SQLException error = null;
            try {
                resultSet.getNString(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getNString("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getNCharacterStream() throws Exception {
        resultSet.next();

        resultSet.getNCharacterStream(1);
        resultSet.getNCharacterStream("1");

        {
            SQLException error = null;
            try {
                resultSet.getNCharacterStream(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getNCharacterStream("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getCharacterStream() throws Exception {
        resultSet.next();

        resultSet.getCharacterStream(1);
        resultSet.getCharacterStream("1");

        {
            SQLException error = null;
            try {
                resultSet.getCharacterStream(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getCharacterStream("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateCharacterStream_2() throws Exception {
        resultSet.next();

        resultSet.updateCharacterStream(1, (Reader) null, 1L);
        resultSet.updateCharacterStream("1", (Reader) null, 1L);

        {
            SQLException error = null;
            try {
                resultSet.updateCharacterStream("0", (Reader) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateCharacterStream(0, (Reader) null, 1L);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getURL() throws Exception {
        resultSet.next();

        resultSet.getURL(1);
        resultSet.getURL("1");

        {
            SQLException error = null;
            try {
                resultSet.getURL(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getURL("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getRowId() throws Exception {
        resultSet.next();

        resultSet.getRowId(1);
        resultSet.getRowId("1");

        {
            SQLException error = null;
            try {
                resultSet.getRowId(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getRowId("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getObject_1() throws Exception {
        resultSet.next();

        resultSet.getObject(1);
        resultSet.getObject("1");

        {
            SQLException error = null;
            try {
                resultSet.getObject(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getObject("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getTimestamp() throws Exception {
        resultSet.next();

        resultSet.getTimestamp(1, null);
        resultSet.getTimestamp("1", null);

        {
            SQLException error = null;
            try {
                resultSet.getTimestamp(0, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getTimestamp("0", null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getTime() throws Exception {
        resultSet.next();

        resultSet.getTime(1, null);
        resultSet.getTime("1", null);

        {
            SQLException error = null;
            try {
                resultSet.getTime(0, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getTime("0", null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getDate() throws Exception {
        resultSet.next();

        resultSet.getDate(1, null);
        resultSet.getDate("1", null);

        {
            SQLException error = null;
            try {
                resultSet.getDate(0, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getDate("0", null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getArray() throws Exception {
        resultSet.next();

        resultSet.getArray(1);
        resultSet.getArray("1");

        {
            SQLException error = null;
            try {
                resultSet.getArray(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getArray("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getClob() throws Exception {
        resultSet.next();

        resultSet.getClob(1);
        resultSet.getClob("1");

        {
            SQLException error = null;
            try {
                resultSet.getClob(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getClob("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getBlob() throws Exception {
        resultSet.next();

        resultSet.getBlob(1);
        resultSet.getBlob("1");

        {
            SQLException error = null;
            try {
                resultSet.getBlob(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getBlob("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getRef() throws Exception {
        resultSet.next();

        resultSet.getRef(1);
        resultSet.getRef("1");

        {
            SQLException error = null;
            try {
                resultSet.getRef(0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getRef("0");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getObject() throws Exception {
        resultSet.next();

        resultSet.getObject(1, (java.util.Map) null);
        resultSet.getObject("1", (java.util.Map) null);

        {
            SQLException error = null;
            try {
                resultSet.getObject(0, (java.util.Map) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.getObject("0", (java.util.Map) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateNClob() throws Exception {
        resultSet.next();

        resultSet.updateNClob(1, (NClob) null);
        resultSet.updateNClob("1", (NClob) null);

        {
            SQLException error = null;
            try {
                resultSet.updateNClob(0, (NClob) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateNClob("0", (NClob) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateNString() throws Exception {
        resultSet.next();

        resultSet.updateNString(1, null);
        resultSet.updateNString("1", null);

        {
            SQLException error = null;
            try {
                resultSet.updateNString(0, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateNString("0", null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateRowId() throws Exception {
        resultSet.next();

        resultSet.updateRowId(1, null);
        resultSet.updateRowId("1", null);

        {
            SQLException error = null;
            try {
                resultSet.updateRowId(0, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateRowId("0", null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateArray() throws Exception {
        resultSet.next();

        resultSet.updateArray(1, null);
        resultSet.updateArray("1", null);

        {
            SQLException error = null;
            try {
                resultSet.updateArray(0, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateArray("0", null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateClob() throws Exception {
        resultSet.next();

        resultSet.updateClob(1, (Clob) null);
        resultSet.updateClob("1", (Clob) null);

        {
            SQLException error = null;
            try {
                resultSet.updateClob(0, (Clob) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateClob("0", (Clob) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateBlob() throws Exception {
        resultSet.next();

        resultSet.updateBlob(1, (Blob) null);
        resultSet.updateBlob("1", (Blob) null);

        {
            SQLException error = null;
            try {
                resultSet.updateBlob(0, (Blob) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateBlob("0", (Blob) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateRef() throws Exception {
        resultSet.next();

        resultSet.updateRef(1, (Ref) null);
        resultSet.updateRef("1", (Ref) null);

        {
            SQLException error = null;
            try {
                resultSet.updateRef(0, (Ref) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateRef("0", (Ref) null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateObject() throws Exception {
        resultSet.next();

        resultSet.updateObject(1, null);
        resultSet.updateObject("1", null);

        {
            SQLException error = null;
            try {
                resultSet.updateObject(0, null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateObject("0", null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateObject_1() throws Exception {
        resultSet.next();

        resultSet.updateObject(1, null, 1);
        resultSet.updateObject("1", null, 1);

        {
            SQLException error = null;
            try {
                resultSet.updateObject(0, null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                resultSet.updateObject("0", null, 1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }
}

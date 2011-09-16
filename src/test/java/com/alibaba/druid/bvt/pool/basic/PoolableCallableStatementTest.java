package com.alibaba.druid.bvt.pool.basic;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockCallableStatement;
import com.alibaba.druid.pool.PoolableCallableStatement;

public class PoolableCallableStatementTest extends TestCase {

    protected MockCallableStatement     raw;
    protected PoolableCallableStatement stmt;

    protected void setUp() throws Exception {
        raw = new MockCallableStatement(null, null);
        stmt = new PoolableCallableStatement(null, raw, null) {

            protected SQLException checkException(Throwable error) throws SQLException {
                if (error instanceof SQLException) {
                    return (SQLException) error;
                }

                return new SQLException(error);
            }
        };

        Assert.assertEquals(0, raw.getParameters().size());
        stmt.registerOutParameter(1, Types.INTEGER);
        Assert.assertEquals(1, raw.getParameters().size());

        stmt.registerOutParameter(2, Types.DECIMAL, 10);
        Assert.assertEquals(2, raw.getParameters().size());
    }

    public void test_basic() throws Exception {
        Assert.assertEquals(raw, stmt.getCallableStatementRaw());
    }

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
}

package com.alibaba.druid.bvt.pool.basic;

import java.math.BigDecimal;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.pool.PoolableResultSet;
import com.alibaba.druid.pool.PoolableStatement;

public class ResultSetTest extends TestCase {

    private PoolableStatement stmt;
    private MockResultSet     raw;
    private PoolableResultSet resultSet;

    protected void setUp() throws Exception {
        stmt = new PoolableStatement(null, null) {

            protected SQLException checkException(Throwable error) throws SQLException {
                if (error instanceof SQLException) {
                    return (SQLException) error;
                }

                return new SQLException(error);
            }
        };

        raw = new MockResultSet(null);
        raw.getRows().add(new Object[] { null });
        resultSet = new PoolableResultSet(stmt, raw);
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
}

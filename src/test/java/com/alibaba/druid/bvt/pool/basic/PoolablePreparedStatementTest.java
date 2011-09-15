package com.alibaba.druid.bvt.pool.basic;

import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Types;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.pool.PoolablePreparedStatement;

public class PoolablePreparedStatementTest extends TestCase {

    protected MockPreparedStatement     raw;
    protected PoolablePreparedStatement stmt;

    protected void setUp() throws Exception {
        raw = new MockPreparedStatement(null, null);
        stmt = new PoolablePreparedStatement(null, raw, null) {

            protected SQLException checkException(Throwable error) throws SQLException {
                if (error instanceof SQLException) {
                    return (SQLException) error;
                }

                return new SQLException(error);
            }
        };
    }

    public void test_basic() throws Exception {
        Assert.assertEquals(raw, stmt.getRawPreparedStatement());
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
}

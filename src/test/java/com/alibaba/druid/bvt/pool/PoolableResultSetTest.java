package com.alibaba.druid.bvt.pool;

import java.sql.SQLException;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.pool.PoolableResultSet;
import com.alibaba.druid.pool.PoolableStatement;


public class PoolableResultSetTest extends TestCase {
    public void test_0() throws Exception {
        PoolableStatement stmt = new PoolableStatement(null, null) {
            protected SQLException checkException(Throwable error) throws SQLException {
                if (error instanceof SQLException) {
                    return (SQLException) error;
                }
                
                return new SQLException(error);
            }
        };
        
        MockResultSet raw = new MockResultSet(null);
        raw.getRows().add(new Object[] {null});
        PoolableResultSet resultSet = new PoolableResultSet(stmt, raw);
        
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
}

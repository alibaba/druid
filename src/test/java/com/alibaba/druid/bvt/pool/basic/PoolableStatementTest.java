package com.alibaba.druid.bvt.pool.basic;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockStatement;
import com.alibaba.druid.pool.PoolableStatement;


public class PoolableStatementTest extends TestCase {
    protected Statement     raw;
    protected PoolableStatement stmt;

    protected void setUp() throws Exception {
        raw = new MockStatement(null);
        stmt = new PoolableStatement(null, raw) {
            protected SQLException checkException(Throwable error) throws SQLException {
                if (error instanceof SQLException) {
                    return (SQLException) error;
                }

                return new SQLException(error);
            }
        };
    }
    
    protected void tearDown() throws Exception {
        
    }
    
    public void test_basic() throws Exception {
        Assert.assertEquals(raw, stmt.getStatement());
        Assert.assertEquals(null, stmt.getPoolableConnection());
        Assert.assertEquals(null, stmt.getConnection());
        Assert.assertEquals(false, stmt.isPoolable());
        stmt.toString();
    }
    
  
}

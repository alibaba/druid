package com.alibaba.druid.bvt.pool.basic;

import java.sql.SQLException;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.pool.PoolableResultSet;
import com.alibaba.druid.pool.PoolableStatement;


public class ResultSetTest2 extends TestCase {
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
    
    public void test_rowDeleted() throws Exception {
        resultSet.rowDeleted();
        raw.close();
        
        {
            SQLException error = null;
            try {
                resultSet.rowDeleted();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }
    
    public void test_rowInserted() throws Exception {
        resultSet.rowInserted();
        raw.close();
        
        {
            SQLException error = null;
            try {
                resultSet.rowInserted();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }
    
    public void test_rowUpdated() throws Exception {
        resultSet.rowInserted();
        raw.close();
        
        {
            SQLException error = null;
            try {
                resultSet.rowUpdated();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }
    
    public void test_getConcurrency() throws Exception {
        resultSet.getConcurrency();
        raw.close();
        
        {
            SQLException error = null;
            try {
                resultSet.getConcurrency();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }
    
    public void test_getType() throws Exception {
        resultSet.getType();
        raw.close();
        
        {
            SQLException error = null;
            try {
                resultSet.getType();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }
    
    public void test_FetchSize() throws Exception {
        resultSet.setFetchSize(10);
        Assert.assertEquals(10, resultSet.getFetchSize());
        raw.close();
        
        {
            SQLException error = null;
            try {
                resultSet.getFetchSize();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }
    
}

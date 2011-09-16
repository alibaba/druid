package com.alibaba.druid.bvt.pool.basic;

import java.sql.ResultSet;
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
        {
            SQLException error = null;
            try {
                resultSet.setFetchSize(10);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_FetchDirection() throws Exception {
        resultSet.setFetchDirection(ResultSet.FETCH_REVERSE);
        Assert.assertEquals(ResultSet.FETCH_REVERSE, resultSet.getFetchDirection());
        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.getFetchDirection();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
        {
            SQLException error = null;
            try {
                resultSet.setFetchDirection(ResultSet.FETCH_REVERSE);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getHoldability() throws Exception {
        resultSet.getHoldability();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.getHoldability();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getStatement() throws Exception {
        resultSet.getStatement();

        raw.close();

        {
            SQLException error = null;
            try {
                raw.getStatement();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_moveToCurrentRow() throws Exception {
        resultSet.moveToCurrentRow();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.moveToCurrentRow();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_moveToInsertRow() throws Exception {
        resultSet.moveToInsertRow();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.moveToInsertRow();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_cancelRowUpdates() throws Exception {
        resultSet.cancelRowUpdates();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.cancelRowUpdates();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_refreshRow() throws Exception {
        resultSet.refreshRow();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.refreshRow();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_deleteRow() throws Exception {
        resultSet.deleteRow();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.deleteRow();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_updateRow() throws Exception {
        resultSet.updateRow();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.updateRow();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_insertRow() throws Exception {
        resultSet.insertRow();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.insertRow();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_previous() throws Exception {
        resultSet.previous();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.previous();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_relative() throws Exception {
        resultSet.relative(1);

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.relative(1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_absolute() throws Exception {
        resultSet.absolute(1);

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.absolute(1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_next() throws Exception {
        resultSet.next();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.next();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_wasNull() throws Exception {
        resultSet.wasNull();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.wasNull();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_isBeforeFirst() throws Exception {
        resultSet.isBeforeFirst();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.isBeforeFirst();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_isAfterLast() throws Exception {
        resultSet.isAfterLast();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.isAfterLast();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_isFirst() throws Exception {
        resultSet.isFirst();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.isFirst();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_isLast() throws Exception {
        resultSet.isLast();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.isLast();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_beforeFirst() throws Exception {
        resultSet.beforeFirst();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.beforeFirst();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_first() throws Exception {
        resultSet.first();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.first();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_afterLast() throws Exception {
        resultSet.afterLast();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.afterLast();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_last() throws Exception {
        resultSet.last();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.last();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getRow() throws Exception {
        resultSet.getRow();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.getRow();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getWarnings() throws Exception {
        resultSet.getWarnings();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.getWarnings();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_clearWarnings() throws Exception {
        resultSet.clearWarnings();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.clearWarnings();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getCursorName() throws Exception {
        resultSet.getCursorName();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.getCursorName();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_getMetaData() throws Exception {
        resultSet.getMetaData();

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.getMetaData();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_findColumn() throws Exception {
        resultSet.findColumn("1");

        raw.close();

        {
            SQLException error = null;
            try {
                resultSet.findColumn("xxxx");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_closeError() throws Exception {
        MockResultSet mock = new MockResultSet(null) {

            public void close() throws SQLException {
                throw new SQLException();
            }
        };

        PoolableResultSet rs = new PoolableResultSet(stmt, mock);

        SQLException error = null;
        try {
            rs.close();
        } catch (SQLException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);

    }
}

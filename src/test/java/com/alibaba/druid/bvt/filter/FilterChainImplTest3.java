package com.alibaba.druid.bvt.filter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockStatement;
import com.alibaba.druid.mock.MockStatementBase;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class FilterChainImplTest3 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat,log4j,wall,encoding");
        dataSource.getProxyFilters().add(new FilterAdapter() {} );
        dataSource.setDbType("mysql");
        
        dataSource.setDriver(new MockDriver() {
            public ResultSet executeQuery(MockStatementBase stmt, String sql) throws SQLException {
                return null;
            }
            
            public MockStatement createMockStatement(MockConnection conn) {
                return new MockStatement(conn) {
                    public ResultSet getResultSet() throws SQLException {
                        return null;
                    }
                };
            }
        });

        dataSource.init();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_executeQuery() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        Assert.assertNull(stmt.executeQuery());
        stmt.close();
        conn.close();
    }
    
    public void test_executeQuery_2() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareCall("select ?");
        stmt.setNull(1, Types.VARCHAR);
        Assert.assertNull(stmt.executeQuery());
        stmt.close();
        conn.close();
    }
    
    public void test_executeQuery_3() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        Assert.assertNull(stmt.executeQuery("select 1"));
        stmt.close();
        conn.close();
    }
    
    public void test_execute() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        stmt.execute("select 1");
        Assert.assertNull(stmt.getResultSet());
        stmt.close();
        conn.close();
    }
}

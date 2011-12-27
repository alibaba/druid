package com.alibaba.druid.spring;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;

import com.alibaba.druid.pool.PoolableCallableStatement;
import com.alibaba.druid.pool.PoolableConnection;
import com.alibaba.druid.pool.PoolablePreparedStatement;
import com.alibaba.druid.pool.PoolableResultSet;
import com.alibaba.druid.pool.PoolableStatement;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;


public class DruidJdbcExtractor implements NativeJdbcExtractor {

    @Override
    public boolean isNativeConnectionNecessaryForNativeStatements() {
        return true;
    }

    @Override
    public boolean isNativeConnectionNecessaryForNativePreparedStatements() {
        return true;
    }

    @Override
    public boolean isNativeConnectionNecessaryForNativeCallableStatements() {
        return true;
    }

    @Override
    public Connection getNativeConnection(Connection conn) throws SQLException {
        if (conn instanceof PoolableConnection) {
            conn = ((PoolableConnection) conn).getConnection();
        }
        
        if (conn instanceof ConnectionProxy) {
            conn = ((ConnectionProxy) conn).getConnectionRaw();
        }
        
        return conn;
    }

    @Override
    public Connection getNativeConnectionFromStatement(Statement stmt) throws SQLException {
        return getNativeConnection(stmt.getConnection());
    }

    @Override
    public Statement getNativeStatement(Statement stmt) throws SQLException {
        if (stmt instanceof PoolableStatement) {
            stmt = ((PoolableStatement) stmt).getStatement();
        }
        
        if (stmt instanceof StatementProxy) {
            stmt = ((StatementProxy) stmt).getStatementRaw();
        }
        
        return stmt;
    }

    @Override
    public PreparedStatement getNativePreparedStatement(PreparedStatement stmt) throws SQLException {
        if (stmt instanceof PoolablePreparedStatement) {
            stmt = ((PoolablePreparedStatement) stmt).getRawPreparedStatement();
        }
        
        if (stmt instanceof PreparedStatementProxy) {
            stmt = ((PreparedStatementProxy) stmt).getRawPreparedStatement();
        }
        
        return stmt;
    }

    @Override
    public CallableStatement getNativeCallableStatement(CallableStatement stmt) throws SQLException {
        if (stmt instanceof PoolableCallableStatement) {
            stmt = ((PoolableCallableStatement) stmt).getCallableStatementRaw();
        }
        
        if (stmt instanceof CallableStatementProxy) {
            stmt = ((CallableStatementProxy) stmt).getRawCallableStatement();
        }
        
        return stmt;
    }

    @Override
    public ResultSet getNativeResultSet(ResultSet rs) throws SQLException {
        if (rs instanceof PoolableResultSet) {
            rs = ((PoolableResultSet) rs).getRawResultSet();
        }
        
        if (rs instanceof ResultSetProxy) {
            rs = ((ResultSetProxy) rs).getResultSetRaw();
        }
        
        return rs;
    }

}

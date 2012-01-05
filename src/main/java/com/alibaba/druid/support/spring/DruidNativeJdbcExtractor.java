package com.alibaba.druid.support.spring;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractorAdapter;

public class DruidNativeJdbcExtractor extends NativeJdbcExtractorAdapter {

    protected Connection doGetNativeConnection(Connection con) throws SQLException {
        return (Connection) con.unwrap(Connection.class);
    }

    public Statement getNativeStatement(Statement stmt) throws SQLException {
        return (Statement) stmt.unwrap(Statement.class);
    }

    public PreparedStatement getNativePreparedStatement(PreparedStatement ps) throws SQLException {
        return (PreparedStatement) ps.unwrap(PreparedStatement.class);
    }

    public CallableStatement getNativeCallableStatement(CallableStatement cs) throws SQLException {
        return (CallableStatement) cs.unwrap(CallableStatement.class);
    }

    public ResultSet getNativeResultSet(ResultSet rs) throws SQLException {
        return (ResultSet) rs.unwrap(ResultSet.class);
    }
}

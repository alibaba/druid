package com.alibaba.druid.util;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.OracleStatement;
import oracle.sql.ROWID;

import com.alibaba.druid.pool.PoolablePreparedStatement;

public class OracleUtils {

    public static void clearDefines(PoolablePreparedStatement stmt) throws SQLException {
        OracleStatement oracleStmt = stmt.unwrap(OracleStatement.class);
        oracleStmt.clearDefines();
    }

    public static int getRowPrefetch(PreparedStatement stmt) throws SQLException {
        OracleStatement oracleStmt = stmt.unwrap(OracleStatement.class);
        return oracleStmt.getRowPrefetch();
    }

    public static void setRowPrefetch(PreparedStatement stmt, int value) throws SQLException {
        OracleStatement oracleStmt = stmt.unwrap(OracleStatement.class);
        oracleStmt.setRowPrefetch(value);
    }

    public static void setDefaultRowPrefetch(Connection conn, int value) throws SQLException {
        OracleConnection oracleConn = unwrap(conn);
        oracleConn.setDefaultRowPrefetch(value);
    }

    public static int getDefaultRowPrefetch(Connection conn, int value) throws SQLException {
        OracleConnection oracleConn = unwrap(conn);
        return oracleConn.getDefaultRowPrefetch();
    }

    public static void cancel(Connection conn) throws SQLException {
        OracleConnection oracleConn = unwrap(conn);
        oracleConn.cancel();
    }

    public static int pingDatabase(Connection conn) throws SQLException {
        OracleConnection oracleConn = unwrap(conn);
        return oracleConn.pingDatabase();
    }

    public static void openProxySession(Connection conn, int type, java.util.Properties prop) throws SQLException {
        OracleConnection oracleConn = unwrap(conn);
        oracleConn.openProxySession(type, prop);
    }

    public static int getDefaultExecuteBatch(Connection conn) throws SQLException {
        OracleConnection oracleConn = unwrap(conn);
        return oracleConn.getDefaultExecuteBatch();
    }

    public static OracleConnection unwrap(Connection conn) throws SQLException {
        if (conn instanceof OracleConnection) {
            return (OracleConnection) conn;
        }

        return conn.unwrap(OracleConnection.class);
    }

    public static ROWID getROWID(ResultSet rs, int columnIndex) throws SQLException {
        OracleResultSet oracleResultSet = rs.unwrap(OracleResultSet.class);
        return oracleResultSet.getROWID(columnIndex);
    }
}

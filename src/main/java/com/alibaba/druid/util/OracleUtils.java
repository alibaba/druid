package com.alibaba.druid.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.OracleStatement;
import oracle.jdbc.internal.OraclePreparedStatement;
import oracle.sql.ROWID;

import com.alibaba.druid.pool.DruidPooledPreparedStatement;

public class OracleUtils {

    public static void clearDefines(DruidPooledPreparedStatement stmt) throws SQLException {
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
    
    public static void enterImplicitCache(PreparedStatement stmt) throws SQLException {
        oracle.jdbc.internal.OraclePreparedStatement oracleStmt = unwrapInternal(stmt);
        oracleStmt.enterImplicitCache();
    }

    public static void exitImplicitCacheToClose(PreparedStatement stmt) throws SQLException {
        oracle.jdbc.internal.OraclePreparedStatement oracleStmt = unwrapInternal(stmt);
        oracleStmt.exitImplicitCacheToClose();
    }

    public static void exitImplicitCacheToActive(PreparedStatement stmt) throws SQLException {
        oracle.jdbc.internal.OraclePreparedStatement oracleStmt = unwrapInternal(stmt);
        oracleStmt.exitImplicitCacheToActive();
    }

    public static OraclePreparedStatement unwrapInternal(PreparedStatement stmt) throws SQLException {
        if (stmt instanceof OraclePreparedStatement) {
            return (OraclePreparedStatement) stmt;
        }

        return stmt.unwrap(OraclePreparedStatement.class);
    }

    public static void setDefaultRowPrefetch(Connection conn, int value) throws SQLException {
        OracleConnection oracleConn = unwrap(conn);
        oracleConn.setDefaultRowPrefetch(value);
    }

    public static int getDefaultRowPrefetch(Connection conn, int value) throws SQLException {
        OracleConnection oracleConn = unwrap(conn);
        return oracleConn.getDefaultRowPrefetch();
    }

    public static boolean getImplicitCachingEnabled(Connection conn) throws SQLException {
        OracleConnection oracleConn = unwrap(conn);
        return oracleConn.getImplicitCachingEnabled();
    }

    public static int getStatementCacheSize(Connection conn) throws SQLException {
        OracleConnection oracleConn = unwrap(conn);
        return oracleConn.getStatementCacheSize();
    }

    public static void purgeImplicitCache(Connection conn) throws SQLException {
        OracleConnection oracleConn = unwrap(conn);
        oracleConn.purgeImplicitCache();
    }

    public static void setImplicitCachingEnabled(Connection conn, boolean cache) throws SQLException {
        OracleConnection oracleConn = unwrap(conn);
        oracleConn.setImplicitCachingEnabled(cache);
    }

    public static void setStatementCacheSize(Connection conn, int size) throws SQLException {
        OracleConnection oracleConn = unwrap(conn);
        oracleConn.setStatementCacheSize(size);
    }

    @SuppressWarnings("deprecation")
    public static int pingDatabase(Connection conn) throws SQLException {
        OracleConnection oracleConn = unwrap(conn);
        return oracleConn.pingDatabase(1000);
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

/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.util;

import java.sql.*;
import java.util.*;

import javax.sql.XAConnection;
import javax.transaction.xa.XAException;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.OracleStatement;
import oracle.jdbc.internal.OraclePreparedStatement;
import oracle.jdbc.xa.client.OracleXAConnection;
import oracle.sql.ROWID;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

public class OracleUtils {

    private final static Log LOG = LogFactory.getLog(OracleUtils.class);

    public static XAConnection OracleXAConnection(Connection oracleConnection) throws XAException {
        return new OracleXAConnection(oracleConnection);
    }

    public static int getRowPrefetch(PreparedStatement stmt) throws SQLException {
        OracleStatement oracleStmt = stmt.unwrap(OracleStatement.class);
        
        if (oracleStmt == null) {
            return -1;
        }
        
        return oracleStmt.getRowPrefetch();
    }

    public static void setRowPrefetch(PreparedStatement stmt, int value) throws SQLException {
        OracleStatement oracleStmt = stmt.unwrap(OracleStatement.class);
        if (oracleStmt != null) {
            oracleStmt.setRowPrefetch(value);
        }
    }

    public static void enterImplicitCache(PreparedStatement stmt) throws SQLException {
        oracle.jdbc.internal.OraclePreparedStatement oracleStmt = unwrapInternal(stmt);
        if (oracleStmt != null) {
            oracleStmt.enterImplicitCache();
        }
    }

    public static void exitImplicitCacheToClose(PreparedStatement stmt) throws SQLException {
        oracle.jdbc.internal.OraclePreparedStatement oracleStmt = unwrapInternal(stmt);
        if (oracleStmt != null) {
            oracleStmt.exitImplicitCacheToClose();
        }
    }

    public static void exitImplicitCacheToActive(PreparedStatement stmt) throws SQLException {
        oracle.jdbc.internal.OraclePreparedStatement oracleStmt = unwrapInternal(stmt);
        if (oracleStmt != null) {
            oracleStmt.exitImplicitCacheToActive();
        }
    }

    public static OraclePreparedStatement unwrapInternal(PreparedStatement stmt) throws SQLException {
        if (stmt instanceof OraclePreparedStatement) {
            return (OraclePreparedStatement) stmt;
        }

        OraclePreparedStatement unwrapped = stmt.unwrap(OraclePreparedStatement.class);

        if (unwrapped == null) {
            LOG.error("can not unwrap statement : " + stmt.getClass());
        }

        return unwrapped;
    }

    public static short getVersionNumber(DruidPooledConnection conn) throws SQLException {
        oracle.jdbc.internal.OracleConnection oracleConn = (oracle.jdbc.internal.OracleConnection) unwrap(conn);
        return oracleConn.getVersionNumber();
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

    private static Set<String> builtinFunctions;

    public static boolean isBuiltinFunction(String function) {
        if (function == null) {
            return false;
        }

        String function_lower = function.toLowerCase();

        Set<String> functions = builtinFunctions;

        if (functions == null) {
            functions = new HashSet<String>();
            Utils.loadFromFile("META-INF/druid/parser/oracle/builtin_functions", functions);
            builtinFunctions = functions;
        }

        return functions.contains(function_lower);
    }

    private static Set<String> builtinTables;

    public static boolean isBuiltinTable(String table) {
        if (table == null) {
            return false;
        }

        String table_lower = table.toLowerCase();

        Set<String> tables = builtinTables;

        if (tables == null) {
            tables = new HashSet<String>();
            Utils.loadFromFile("META-INF/druid/parser/oracle/builtin_tables", tables);
            builtinTables = tables;
        }

        return tables.contains(table_lower);
    }

    private static Set<String> keywords;

    public static boolean isKeyword(String name) {
        if (name == null) {
            return false;
        }

        String name_lower = name.toLowerCase();

        Set<String> words = keywords;

        if (words == null) {
            words = new HashSet<String>();
            Utils.loadFromFile("META-INF/druid/parser/oracle/keywords", words);
            keywords = words;
        }

        return words.contains(name_lower);
    }

    public static List<String> showTables(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<String>();

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select table_name from user_tables");
            while (rs.next()) {
                String tableName = rs.getString(1);
                tables.add(tableName);
            }
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }

        return tables;
    }

    public static List<String> getTableDDL(Connection conn, String... tables) throws SQLException {
        return getTableDDL(conn, Arrays.asList(tables));
    }

    public static List<String> getTableDDL(Connection conn, List<String> tables) throws SQLException {
        List<String> ddlList = new ArrayList<String>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "select DBMS_METADATA.GET_DDL('TABLE', TABLE_NAME) FROM user_tables";

            if (tables.size() > 0) {
                sql += "IN (";
                for (int i = 0; i < tables.size(); ++i) {
                    if (i != 0) {
                        sql += ", ?";
                    } else {
                        sql += "?";
                    }
                }
                sql += ")";
            }
            pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < tables.size(); ++i) {
                pstmt.setString(i + 1, tables.get(i));
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String ddl = rs.getString(1);
                ddlList.add(ddl);
            }
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(pstmt);
        }

        return ddlList;
    }

    public static String getCreateTableScript(Connection conn) throws SQLException {
        return getCreateTableScript(conn, true, true);
    }

    public static String getCreateTableScript(Connection conn, boolean sorted, boolean simplify) throws SQLException {
        List<String> ddlList = OracleUtils.getTableDDL(conn);

        StringBuilder buf = new StringBuilder();
        for (String ddl : ddlList) {
            buf.append(ddl);
            buf.append(';');
        }

        String ddlScript = buf.toString();

        if (! (sorted || simplify)) {
            return ddlScript;
        }

        List stmtList = SQLUtils.parseStatements(ddlScript, JdbcConstants.ORACLE);
        if (simplify) {
            for (Object o : stmtList) {
                if (o instanceof SQLCreateTableStatement) {
                    SQLCreateTableStatement createTableStmt = (SQLCreateTableStatement) o;
                    createTableStmt.simplify();
                }
            }
        }

        if (sorted) {
            SQLCreateTableStatement.sort(stmtList);
        }
        return SQLUtils.toSQLString(stmtList, JdbcConstants.ORACLE);
    }
}

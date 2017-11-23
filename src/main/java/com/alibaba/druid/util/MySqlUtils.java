/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.mysql.jdbc.ConnectionImpl;

import javax.sql.XAConnection;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MySqlUtils {
    static Class<?> utilClass;
    static boolean utilClassError = false;
    static boolean utilClass_isJdbc4 = false;

    static Class<?> connectionClass = null;
    static Method getPinGlobalTxToPhysicalConnectionMethod = null;

    static Class<?> suspendableXAConnectionClass = null;
    static Constructor<?> suspendableXAConnectionConstructor = null;

    static Class<?> JDBC4SuspendableXAConnectionClass = null;
    static Constructor<?> JDBC4SuspendableXAConnectionConstructor = null;

    static Class<?> MysqlXAConnectionClass = null;
    static Constructor<?> MysqlXAConnectionConstructor = null;

    public static XAConnection createXAConnection(Driver driver, Connection physicalConn) throws SQLException {
        final int major = driver.getMajorVersion();
        if (major == 5) {
            if (utilClass == null && !utilClassError) {
                try {
                    utilClass = Class.forName("com.mysql.jdbc.Util");

                    Method method = utilClass.getMethod("isJdbc4");
                    utilClass_isJdbc4 = (Boolean) method.invoke(null);

                    connectionClass = Class.forName("com.mysql.jdbc.Connection");
                    getPinGlobalTxToPhysicalConnectionMethod = connectionClass.getMethod("getPinGlobalTxToPhysicalConnection");

                    suspendableXAConnectionClass = Class.forName("com.mysql.jdbc.jdbc2.optional.SuspendableXAConnection");
                    suspendableXAConnectionConstructor = suspendableXAConnectionClass.getConstructor(connectionClass);

                    JDBC4SuspendableXAConnectionClass = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4SuspendableXAConnection");
                    JDBC4SuspendableXAConnectionConstructor = JDBC4SuspendableXAConnectionClass.getConstructor(connectionClass);

                    MysqlXAConnectionClass = Class.forName("com.mysql.jdbc.jdbc2.optional.MysqlXAConnection");
                    MysqlXAConnectionConstructor = MysqlXAConnectionClass.getConstructor(connectionClass, boolean.class);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    utilClassError = true;
                }
            }

            try {
                boolean pinGlobTx = (Boolean) getPinGlobalTxToPhysicalConnectionMethod.invoke(physicalConn);
                if (pinGlobTx) {
                    if (!utilClass_isJdbc4) {
                        return (XAConnection) suspendableXAConnectionConstructor.newInstance(physicalConn);
                    }

                    return (XAConnection) JDBC4SuspendableXAConnectionConstructor.newInstance(physicalConn);
                }

                return (XAConnection) MysqlXAConnectionConstructor.newInstance(physicalConn, Boolean.FALSE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        throw new SQLFeatureNotSupportedException();
    }

    public static String buildKillQuerySql(Connection connection, SQLException error) throws SQLException {
        try {
            ConnectionImpl connImpl = (ConnectionImpl) connection;
            long threadId = connImpl.getId();
            return  "KILL QUERY " + threadId;
        } catch (Exception e) {
            // skip
        }
        return null;
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
            Utils.loadFromFile("META-INF/druid/parser/mysql/keywords", words);
            keywords = words;
        }

        return words.contains(name_lower);
    }

    private static Set<String> builtinDataTypes;

    public static boolean isBuiltinDataType(String dataType) {
        if (dataType == null) {
            return false;
        }

        String table_lower = dataType.toLowerCase();

        Set<String> dataTypes = builtinDataTypes;

        if (dataTypes == null) {
            dataTypes = new HashSet<String>();
            Utils.loadFromFile("META-INF/druid/parser/mysql/builtin_datatypes", dataTypes);
            builtinDataTypes = dataTypes;
        }

        return dataTypes.contains(table_lower);
    }

    public static List<String> showTables(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<String>();

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("show tables");
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

    public static List<String> getTableDDL(Connection conn, List<String> tables) throws SQLException {
        List<String> ddlList = new ArrayList<String>();

        Statement stmt = null;
        try {
            for (String table : tables) {
                if (stmt == null) {
                    stmt = conn.createStatement();
                }

                if (isKeyword(table)) {
                    table = "`" + table + "`";
                }

                ResultSet rs = null;
                try {
                    rs = stmt.executeQuery("show create table " + table);
                    if (rs.next()) {
                        String ddl = rs.getString(2);
                        ddlList.add(ddl);
                    }
                } finally {
                    JdbcUtils.close(rs);
                }
            }
        } finally {
            JdbcUtils.close(stmt);
        }


        return ddlList;
    }

    public static String getCreateTableScript(Connection conn) throws SQLException {
        return getCreateTableScript(conn, true, true);
    }

    public static String getCreateTableScript(Connection conn, boolean sorted, boolean simplify) throws SQLException {
        List<String> tables = showTables(conn);
        List<String> ddlList = getTableDDL(conn, tables);
        StringBuilder buf = new StringBuilder();
        for (String ddl : ddlList) {
            buf.append(ddl);
            buf.append(';');
        }
        String ddlScript = buf.toString();

        if (! (sorted || simplify)) {
            return ddlScript;
        }

        List stmtList = SQLUtils.parseStatements(ddlScript, JdbcConstants.MYSQL);
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
        return SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);
    }

    private static Class   class_connectionImpl                     = null;
    private static boolean class_connectionImpl_Error               = false;
    private static Method  method_getIO                             = null;
    private static boolean method_getIO_error                       = false;
    private static Class   class_MysqlIO                            = null;
    private static boolean class_MysqlIO_Error                      = false;
    private static Method  method_getLastPacketReceivedTimeMs       = null;
    private static boolean method_getLastPacketReceivedTimeMs_error = false;

    public static long getLastPacketReceivedTimeMs(Connection conn) throws SQLException {
        if (class_connectionImpl == null && !class_connectionImpl_Error) {
            try {
                class_connectionImpl = Utils.loadClass("com.mysql.jdbc.ConnectionImpl");
            } catch (Throwable error){
                class_connectionImpl_Error = true;
            }
        }

        if (class_connectionImpl == null) {
            return -1;
        }

        if (method_getIO == null && !method_getIO_error) {
            try {
                method_getIO = class_connectionImpl.getMethod("getIO");
            } catch (Throwable error){
                method_getIO_error = true;
            }
        }

        if (method_getIO == null) {
            return -1;
        }

        if (class_MysqlIO == null && !class_MysqlIO_Error) {
            try {
                class_MysqlIO = Utils.loadClass("com.mysql.jdbc.MysqlIO");
            } catch (Throwable error){
                class_MysqlIO_Error = true;
            }
        }

        if (class_MysqlIO == null) {
            return -1;
        }

        if (method_getLastPacketReceivedTimeMs == null && !method_getLastPacketReceivedTimeMs_error) {
            try {
                Method method = class_MysqlIO.getDeclaredMethod("getLastPacketReceivedTimeMs");
                method.setAccessible(true);
                method_getLastPacketReceivedTimeMs = method;
            } catch (Throwable error){
                method_getLastPacketReceivedTimeMs_error = true;
            }
        }

        if (method_getLastPacketReceivedTimeMs == null) {
            return -1;
        }

        try {
            Object connImpl = conn.unwrap(class_connectionImpl);
            if (connImpl == null) {
                return -1;
            }

            Object mysqlio = method_getIO.invoke(connImpl);
            Long ms = (Long) method_getLastPacketReceivedTimeMs.invoke(mysqlio);
            return ms.longValue();
        } catch (IllegalArgumentException e) {
            throw new SQLException("getLastPacketReceivedTimeMs error", e);
        } catch (IllegalAccessException e) {
            throw new SQLException("getLastPacketReceivedTimeMs error", e);
        } catch (InvocationTargetException e) {
            throw new SQLException("getLastPacketReceivedTimeMs error", e);
        }
    }
}

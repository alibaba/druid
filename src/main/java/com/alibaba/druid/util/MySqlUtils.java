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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;

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
    static Class<?>       utilClass;
    static boolean        utilClassError = false;
    static boolean        utilClass_isJdbc4 = false;

    static Class<?>       class_5_connection= null;
    static Method         method_5_getPinGlobalTxToPhysicalConnection = null;
    static Class<?>       class_5_suspendableXAConnection = null;
    static Constructor<?> constructor_5_suspendableXAConnection = null;
    static Class<?>       class_5_JDBC4SuspendableXAConnection = null;
    static Constructor<?> constructor_5_JDBC4SuspendableXAConnection = null;
    static Class<?>       class_5_MysqlXAConnection = null;
    static Constructor<?> constructor_5_MysqlXAConnection = null;

    static Class<?>       class_5_ConnectionImpl = null;
    static Method         method_5_getId         = null;

    static Class<?>       class_6_ConnectionImpl = null;
    static Method         method_6_getId         = null;

    volatile static Class<?>       class_6_connection= null;
    volatile static Method         method_6_getPropertySet = null;
    volatile static Method         method_6_getBooleanReadableProperty = null;
    volatile static Method         method_6_getValue = null;
    volatile static boolean        method_6_getValue_error = false;

    volatile static Class<?>       class_6_suspendableXAConnection = null;
    volatile static Method         method_6_getInstance = null;
    volatile static boolean        method_6_getInstance_error = false;
    volatile static Method         method_6_getInstanceXA = null;
    volatile static boolean        method_6_getInstanceXA_error = false;
    volatile static Class<?>       class_6_JDBC4SuspendableXAConnection = null;


    public static XAConnection createXAConnection(Driver driver, Connection physicalConn) throws SQLException {
        final int major = driver.getMajorVersion();
        if (major == 5) {
            if (utilClass == null && !utilClassError) {
                try {
                    utilClass = Class.forName("com.mysql.jdbc.Util");

                    Method method = utilClass.getMethod("isJdbc4");
                    utilClass_isJdbc4 = (Boolean) method.invoke(null);

                    class_5_connection = Class.forName("com.mysql.jdbc.Connection");
                    method_5_getPinGlobalTxToPhysicalConnection = class_5_connection.getMethod("getPinGlobalTxToPhysicalConnection");

                    class_5_suspendableXAConnection = Class.forName("com.mysql.jdbc.jdbc2.optional.SuspendableXAConnection");
                    constructor_5_suspendableXAConnection = class_5_suspendableXAConnection.getConstructor(class_5_connection);

                    class_5_JDBC4SuspendableXAConnection = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4SuspendableXAConnection");
                    constructor_5_JDBC4SuspendableXAConnection = class_5_JDBC4SuspendableXAConnection.getConstructor(class_5_connection);

                    class_5_MysqlXAConnection = Class.forName("com.mysql.jdbc.jdbc2.optional.MysqlXAConnection");
                    constructor_5_MysqlXAConnection = class_5_MysqlXAConnection.getConstructor(class_5_connection, boolean.class);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    utilClassError = true;
                }
            }

            try {
                boolean pinGlobTx = (Boolean) method_5_getPinGlobalTxToPhysicalConnection.invoke(physicalConn);
                if (pinGlobTx) {
                    if (!utilClass_isJdbc4) {
                        return (XAConnection) constructor_5_suspendableXAConnection.newInstance(physicalConn);
                    }

                    return (XAConnection) constructor_5_JDBC4SuspendableXAConnection.newInstance(physicalConn);
                }

                return (XAConnection) constructor_5_MysqlXAConnection.newInstance(physicalConn, Boolean.FALSE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (major == 6 || major == 8) {
            if (method_6_getValue == null && !method_6_getValue_error) {
                try {
                    class_6_connection = Class.forName("com.mysql.cj.api.jdbc.JdbcConnection");
                } catch (Throwable t) {
                }
                
                try {
                    // maybe 8.0.11 or higher version, try again with com.mysql.cj.jdbc.JdbcConnection
                    if (class_6_connection == null) {
                        class_6_connection = Class.forName("com.mysql.cj.jdbc.JdbcConnection");
                        method_6_getPropertySet = class_6_connection.getMethod("getPropertySet");
                        method_6_getBooleanReadableProperty = Class.forName("com.mysql.cj.conf.PropertySet").getMethod("getBooleanReadableProperty", String.class);
                        method_6_getValue = Class.forName("com.mysql.cj.conf.ReadableProperty").getMethod("getValue");
                    }
                    else { 
                        method_6_getPropertySet = class_6_connection.getMethod("getPropertySet");
                        method_6_getBooleanReadableProperty = Class.forName("com.mysql.cj.api.conf.PropertySet").getMethod("getBooleanReadableProperty", String.class);
                        method_6_getValue = Class.forName("com.mysql.cj.api.conf.ReadableProperty").getMethod("getValue");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    method_6_getValue_error = true;
                }
            }

            try {
                // pinGlobalTxToPhysicalConnection
                boolean pinGlobTx = (Boolean) method_6_getValue.invoke(
                        method_6_getBooleanReadableProperty.invoke(
                                method_6_getPropertySet.invoke(physicalConn)
                                , "pinGlobalTxToPhysicalConnection"
                        )
                );

                if (pinGlobTx) {
                    try {
                        if (method_6_getInstance == null && !method_6_getInstance_error) {
                            class_6_suspendableXAConnection = Class.forName("com.mysql.cj.jdbc.SuspendableXAConnection");
                            method_6_getInstance = class_6_suspendableXAConnection.getDeclaredMethod("getInstance", class_6_connection);
                            method_6_getInstance.setAccessible(true);
                        }
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        method_6_getInstance_error = true;
                    }
                    return (XAConnection) method_6_getInstance.invoke(null, physicalConn);
                } else {
                    try {
                        if (method_6_getInstanceXA == null && !method_6_getInstanceXA_error) {
                            class_6_JDBC4SuspendableXAConnection = Class.forName("com.mysql.cj.jdbc.MysqlXAConnection");
                            method_6_getInstanceXA = class_6_JDBC4SuspendableXAConnection.getDeclaredMethod("getInstance", class_6_connection, boolean.class);
                            method_6_getInstanceXA.setAccessible(true);
                        }
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        method_6_getInstanceXA_error = true;
                    }
                    return (XAConnection) method_6_getInstanceXA.invoke(null, physicalConn, Boolean.FALSE);
                }
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
            } catch (Exception e) {
                e.printStackTrace();
                method_6_getInstance_error = true;
            }
        }

        throw new SQLFeatureNotSupportedException();
    }

    public static String buildKillQuerySql(Connection connection, SQLException error) throws SQLException {
        Long threadId = null;
        try {
            Class clazz = connection.getClass();

            if (class_5_ConnectionImpl == null) {
                if (clazz.getName().equals("com.mysql.jdbc.ConnectionImpl")) {
                    class_5_ConnectionImpl = clazz;
                }
            }

            if (class_5_ConnectionImpl == clazz) {
                if (method_5_getId == null) {
                    method_5_getId = class_5_ConnectionImpl.getMethod("getId");
                }

                threadId = (Long) method_5_getId.invoke(connection);
            }

            if (class_6_ConnectionImpl == null) {
                if (clazz.getName().equals("com.mysql.cj.jdbc.ConnectionImpl")) {
                    class_6_ConnectionImpl = clazz;
                }
            }

            if (class_6_ConnectionImpl == clazz) {
                if (method_6_getId == null) {
                    method_6_getId = class_6_ConnectionImpl.getMethod("getId");
                }

                threadId = (Long) method_6_getId.invoke(connection);
            }
        } catch (Exception e) {
            // skip
        }

        if (threadId == null) {
            return null;
        }

        return  "KILL QUERY " + threadId;

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
                class_connectionImpl = Utils.loadClass("com.mysql.jdbc.MySQLConnection");
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

    static Class<?> class_5_CommunicationsException = null;
    static Class<?> class_6_CommunicationsException = null;

    public static Class getCommunicationsExceptionClass() {
        if (class_5_CommunicationsException != null) {
            return class_5_CommunicationsException;
        }

        if (class_6_CommunicationsException != null) {
            return class_6_CommunicationsException;
        }

        class_5_CommunicationsException = Utils.loadClass("com.mysql.jdbc.CommunicationsException");
        if (class_5_CommunicationsException != null) {
            return class_5_CommunicationsException;
        }

        class_6_CommunicationsException = Utils.loadClass("com.mysql.cj.jdbc.exceptions.CommunicationsException");
        if (class_6_CommunicationsException != null) {
            return class_6_CommunicationsException;
        }

        return null;
    }
}

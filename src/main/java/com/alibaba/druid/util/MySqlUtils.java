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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;

import javax.sql.XAConnection;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.Date;

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

    static Class<?>       class_ConnectionImpl = null;
    static Method         method_getId = null;
    static boolean        method_getId_error = false;

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
                        Class<?> propertySetClass = Class.forName("com.mysql.cj.conf.PropertySet");

                        NoSuchMethodException noSuchMethodException = null;
                        try {
                            method_6_getBooleanReadableProperty = propertySetClass
                                    .getMethod("getBooleanReadableProperty", String.class);
                            method_6_getValue = Class.forName("com.mysql.cj.conf.ReadableProperty")
                                    .getMethod("getValue");
                        } catch (NoSuchMethodException error) {
                            noSuchMethodException = error;
                        }
                        if (method_6_getBooleanReadableProperty == null) {
                            method_6_getBooleanReadableProperty = propertySetClass
                                    .getMethod("getBooleanProperty", String.class);
                            method_6_getValue = Class.forName("com.mysql.cj.conf.RuntimeProperty")
                                    .getMethod("getValue");
                        }

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
                Boolean pinGlobTx = (Boolean) method_6_getValue.invoke(
                        method_6_getBooleanReadableProperty.invoke(
                                method_6_getPropertySet.invoke(physicalConn)
                                , "pinGlobalTxToPhysicalConnection"
                        )
                );

                if (pinGlobTx != null && pinGlobTx) {
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
        Long threadId = getId(connection);
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

        List stmtList = SQLUtils.parseStatements(ddlScript, DbType.mysql);
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
        return SQLUtils.toSQLString(stmtList, DbType.mysql);
    }

    private static transient Class   class_connectionImpl                     = null;
    private static transient boolean class_connectionImpl_Error               = false;
    private static transient Method  method_getIO                             = null;
    private static transient boolean method_getIO_error                       = false;
    private static transient Class   class_MysqlIO                            = null;
    private static transient boolean class_MysqlIO_Error                      = false;
    private static transient Method  method_getLastPacketReceivedTimeMs       = null;
    private static transient boolean method_getLastPacketReceivedTimeMs_error = false;

    private volatile static  boolean mysqlJdbcVersion6                        = false;
    private static transient Class   classJdbc                                = null;
    private static transient Method  getIdleFor                               = null;
    private static transient boolean getIdleForError                          = false;

    public static Long getId(Object conn) {
        if (conn == null) {
            return null;
        }

        Class<?> clazz = conn.getClass();
        if (class_ConnectionImpl == null) {
            if (clazz.getName().equals("com.mysql.jdbc.ConnectionImpl")) {
                class_ConnectionImpl = clazz;
            } else if (clazz.getName().equals("com.mysql.jdbc.Connection")) { // mysql 5.0.x
                class_ConnectionImpl = clazz;
            } else if (clazz.getName().equals("com.mysql.cj.jdbc.ConnectionImpl")) { // mysql 5.0.x
                class_ConnectionImpl = clazz;
            } else if (clazz.getSuperclass().getName().equals("com.mysql.jdbc.ConnectionImpl")) {
                class_ConnectionImpl = clazz.getSuperclass();
            }
        }

        if (class_ConnectionImpl == clazz || class_ConnectionImpl == clazz.getSuperclass()) {
            try {
                if (method_getId == null && !method_getId_error) {
                    Method method = class_ConnectionImpl.getDeclaredMethod("getId");
                    method.setAccessible(true);
                    method_getId = method;
                }

                return (Long) method_getId.invoke(conn);
            } catch (Throwable ex) {
                method_getId_error = true;
            }
        }

        return null;
    }

    public static long getLastPacketReceivedTimeMs(Connection conn) throws SQLException {
        if (class_connectionImpl == null && !class_connectionImpl_Error) {
            try {
                class_connectionImpl = Utils.loadClass("com.mysql.jdbc.MySQLConnection");
                if (class_connectionImpl == null) {
                    class_connectionImpl = Utils.loadClass("com.mysql.cj.MysqlConnection");
                    if (class_connectionImpl != null) {
                        mysqlJdbcVersion6 = true;
                    }
                }
            } catch (Throwable error) {
                class_connectionImpl_Error = true;
            }
        }

        if (class_connectionImpl == null) {
            return -1;
        }

        if(mysqlJdbcVersion6){
            if (classJdbc == null) {
                classJdbc = Utils.loadClass("com.mysql.cj.jdbc.JdbcConnection");
            }

            if (classJdbc == null) {
                return -1;
            }

            if (getIdleFor == null && !getIdleForError) {
                try {
                    getIdleFor = classJdbc.getMethod("getIdleFor");
                    getIdleFor.setAccessible(true);
                } catch (Throwable error) {
                    getIdleForError = true;
                }
            }

            if (getIdleFor == null) {
                return -1;
            }

            try {
                Object connImpl = conn.unwrap(class_connectionImpl);
                if (connImpl == null) {
                    return -1;
                }

                return System.currentTimeMillis()
                        - ((Long)
                            getIdleFor.invoke(connImpl))
                        .longValue();
            } catch (Exception e) {
                throw new SQLException("getIdleFor error", e);
            }
        } else {
            if (method_getIO == null && !method_getIO_error) {
                try {
                    method_getIO = class_connectionImpl.getMethod("getIO");
                } catch (Throwable error) {
                    method_getIO_error = true;
                }
            }

            if (method_getIO == null) {
                return -1;
            }

            if (class_MysqlIO == null && !class_MysqlIO_Error) {
                try {
                    class_MysqlIO = Utils.loadClass("com.mysql.jdbc.MysqlIO");
                } catch (Throwable error) {
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
                } catch (Throwable error) {
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
                return (Long) method_getLastPacketReceivedTimeMs.invoke(mysqlio);
            } catch (Exception e) {
                throw new SQLException("getLastPacketReceivedTimeMs error", e);
            }
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

    public final static Charset GBK                 = Charset.forName("GBK");
    public final static Charset BIG5                 = Charset.forName("BIG5");
    public final static Charset UTF8                 = Charset.forName("UTF-8");
    public final static Charset UTF16                = Charset.forName("UTF-16");
    public final static Charset UTF32                = Charset.forName("UTF-32");
    public final static Charset ASCII                = Charset.forName("ASCII");


    public static void loadDataTypes(Set<String> dataTypes) {
        Utils.loadFromFile("META-INF/druid/parser/mysql/builtin_datatypes", dataTypes);
    }

    private static BigInteger[] MAX_INT = {
            new BigInteger("9"),
            new BigInteger("99"),
            new BigInteger("999"),
            new BigInteger("9999"),
            new BigInteger("99999"),
            new BigInteger("999999"),
            new BigInteger("9999999"),
            new BigInteger("99999999"),
            new BigInteger("999999999"),
            new BigInteger("9999999999"),
            new BigInteger("99999999999"),
            new BigInteger("999999999999"),
            new BigInteger("9999999999999"),
            new BigInteger("99999999999999"),
            new BigInteger("999999999999999"),
            new BigInteger("9999999999999999"),
            new BigInteger("99999999999999999"),
            new BigInteger("999999999999999999"),
            new BigInteger("9999999999999999999"),
            new BigInteger("99999999999999999999"),
            new BigInteger("999999999999999999999"),
            new BigInteger("9999999999999999999999"),
            new BigInteger("99999999999999999999999"),
            new BigInteger("999999999999999999999999"),
            new BigInteger("9999999999999999999999999"),
            new BigInteger("99999999999999999999999999"),
            new BigInteger("999999999999999999999999999"),
            new BigInteger("9999999999999999999999999999"),
            new BigInteger("99999999999999999999999999999"),
            new BigInteger("999999999999999999999999999999"),
            new BigInteger("9999999999999999999999999999999"),
            new BigInteger("99999999999999999999999999999999"),
            new BigInteger("999999999999999999999999999999999"),
            new BigInteger("9999999999999999999999999999999999"),
            new BigInteger("99999999999999999999999999999999999"),
            new BigInteger("999999999999999999999999999999999999"),
            new BigInteger("9999999999999999999999999999999999999"),
            new BigInteger("99999999999999999999999999999999999999"),
    };

    private static BigInteger[] MIN_INT = {
            new BigInteger("-9"),
            new BigInteger("-99"),
            new BigInteger("-999"),
            new BigInteger("-9999"),
            new BigInteger("-99999"),
            new BigInteger("-999999"),
            new BigInteger("-9999999"),
            new BigInteger("-99999999"),
            new BigInteger("-999999999"),
            new BigInteger("-9999999999"),
            new BigInteger("-99999999999"),
            new BigInteger("-999999999999"),
            new BigInteger("-9999999999999"),
            new BigInteger("-99999999999999"),
            new BigInteger("-999999999999999"),
            new BigInteger("-9999999999999999"),
            new BigInteger("-99999999999999999"),
            new BigInteger("-999999999999999999"),
            new BigInteger("-9999999999999999999"),
            new BigInteger("-99999999999999999999"),
            new BigInteger("-999999999999999999999"),
            new BigInteger("-9999999999999999999999"),
            new BigInteger("-99999999999999999999999"),
            new BigInteger("-999999999999999999999999"),
            new BigInteger("-9999999999999999999999999"),
            new BigInteger("-99999999999999999999999999"),
            new BigInteger("-999999999999999999999999999"),
            new BigInteger("-9999999999999999999999999999"),
            new BigInteger("-99999999999999999999999999999"),
            new BigInteger("-999999999999999999999999999999"),
            new BigInteger("-9999999999999999999999999999999"),
            new BigInteger("-99999999999999999999999999999999"),
            new BigInteger("-999999999999999999999999999999999"),
            new BigInteger("-9999999999999999999999999999999999"),
            new BigInteger("-99999999999999999999999999999999999"),
            new BigInteger("-999999999999999999999999999999999999"),
            new BigInteger("-9999999999999999999999999999999999999"),
            new BigInteger("-99999999999999999999999999999999999999"),
    };

    private static BigDecimal[] MAX_DEC_1 = {
            new BigDecimal("0.9"),
            new BigDecimal("9.9"),
            new BigDecimal("99.9"),
            new BigDecimal("999.9"),
            new BigDecimal("9999.9"),
            new BigDecimal("99999.9"),
            new BigDecimal("999999.9"),
            new BigDecimal("9999999.9"),
            new BigDecimal("99999999.9"),
            new BigDecimal("999999999.9"),
            new BigDecimal("9999999999.9"),
            new BigDecimal("99999999999.9"),
            new BigDecimal("999999999999.9"),
            new BigDecimal("9999999999999.9"),
            new BigDecimal("99999999999999.9"),
            new BigDecimal("999999999999999.9"),
            new BigDecimal("9999999999999999.9"),
            new BigDecimal("99999999999999999.9"),
            new BigDecimal("999999999999999999.9"),
            new BigDecimal("9999999999999999999.9"),
            new BigDecimal("99999999999999999999.9"),
            new BigDecimal("999999999999999999999.9"),
            new BigDecimal("9999999999999999999999.9"),
            new BigDecimal("99999999999999999999999.9"),
            new BigDecimal("999999999999999999999999.9"),
            new BigDecimal("9999999999999999999999999.9"),
            new BigDecimal("99999999999999999999999999.9"),
            new BigDecimal("999999999999999999999999999.9"),
            new BigDecimal("9999999999999999999999999999.9"),
            new BigDecimal("99999999999999999999999999999.9"),
            new BigDecimal("999999999999999999999999999999.9"),
            new BigDecimal("9999999999999999999999999999999.9"),
            new BigDecimal("99999999999999999999999999999999.9"),
            new BigDecimal("999999999999999999999999999999999.9"),
            new BigDecimal("9999999999999999999999999999999999.9"),
            new BigDecimal("99999999999999999999999999999999999.9"),
            new BigDecimal("999999999999999999999999999999999999.9"),
            new BigDecimal("9999999999999999999999999999999999999.9"),
    };

    private static BigDecimal[] MIN_DEC_1 = {
            new BigDecimal("-0.9"),
            new BigDecimal("-9.9"),
            new BigDecimal("-99.9"),
            new BigDecimal("-999.9"),
            new BigDecimal("-9999.9"),
            new BigDecimal("-99999.9"),
            new BigDecimal("-999999.9"),
            new BigDecimal("-9999999.9"),
            new BigDecimal("-99999999.9"),
            new BigDecimal("-999999999.9"),
            new BigDecimal("-9999999999.9"),
            new BigDecimal("-99999999999.9"),
            new BigDecimal("-999999999999.9"),
            new BigDecimal("-9999999999999.9"),
            new BigDecimal("-99999999999999.9"),
            new BigDecimal("-999999999999999.9"),
            new BigDecimal("-9999999999999999.9"),
            new BigDecimal("-99999999999999999.9"),
            new BigDecimal("-999999999999999999.9"),
            new BigDecimal("-9999999999999999999.9"),
            new BigDecimal("-99999999999999999999.9"),
            new BigDecimal("-999999999999999999999.9"),
            new BigDecimal("-9999999999999999999999.9"),
            new BigDecimal("-99999999999999999999999.9"),
            new BigDecimal("-999999999999999999999999.9"),
            new BigDecimal("-9999999999999999999999999.9"),
            new BigDecimal("-99999999999999999999999999.9"),
            new BigDecimal("-999999999999999999999999999.9"),
            new BigDecimal("-9999999999999999999999999999.9"),
            new BigDecimal("-99999999999999999999999999999.9"),
            new BigDecimal("-999999999999999999999999999999.9"),
            new BigDecimal("-9999999999999999999999999999999.9"),
            new BigDecimal("-99999999999999999999999999999999.9"),
            new BigDecimal("-999999999999999999999999999999999.9"),
            new BigDecimal("-9999999999999999999999999999999999.9"),
            new BigDecimal("-99999999999999999999999999999999999.9"),
            new BigDecimal("-999999999999999999999999999999999999.9"),
            new BigDecimal("-9999999999999999999999999999999999999.9"),
    };

    private static BigDecimal[] MAX_DEC_2 = {
            new BigDecimal("0.99"),
            new BigDecimal("9.99"),
            new BigDecimal("99.99"),
            new BigDecimal("999.99"),
            new BigDecimal("9999.99"),
            new BigDecimal("99999.99"),
            new BigDecimal("999999.99"),
            new BigDecimal("9999999.99"),
            new BigDecimal("99999999.99"),
            new BigDecimal("999999999.99"),
            new BigDecimal("9999999999.99"),
            new BigDecimal("99999999999.99"),
            new BigDecimal("999999999999.99"),
            new BigDecimal("9999999999999.99"),
            new BigDecimal("99999999999999.99"),
            new BigDecimal("999999999999999.99"),
            new BigDecimal("9999999999999999.99"),
            new BigDecimal("99999999999999999.99"),
            new BigDecimal("999999999999999999.99"),
            new BigDecimal("9999999999999999999.99"),
            new BigDecimal("99999999999999999999.99"),
            new BigDecimal("999999999999999999999.99"),
            new BigDecimal("9999999999999999999999.99"),
            new BigDecimal("99999999999999999999999.99"),
            new BigDecimal("999999999999999999999999.99"),
            new BigDecimal("9999999999999999999999999.99"),
            new BigDecimal("99999999999999999999999999.99"),
            new BigDecimal("999999999999999999999999999.99"),
            new BigDecimal("9999999999999999999999999999.99"),
            new BigDecimal("99999999999999999999999999999.99"),
            new BigDecimal("999999999999999999999999999999.99"),
            new BigDecimal("9999999999999999999999999999999.99"),
            new BigDecimal("99999999999999999999999999999999.99"),
            new BigDecimal("999999999999999999999999999999999.99"),
            new BigDecimal("9999999999999999999999999999999999.99"),
            new BigDecimal("99999999999999999999999999999999999.99"),
            new BigDecimal("999999999999999999999999999999999999.99"),
            new BigDecimal("9999999999999999999999999999999999999.99"),
    };

    private static BigDecimal[] MIN_DEC_2 = {
            new BigDecimal("-0.99"),
            new BigDecimal("-9.99"),
            new BigDecimal("-99.99"),
            new BigDecimal("-999.99"),
            new BigDecimal("-9999.99"),
            new BigDecimal("-99999.99"),
            new BigDecimal("-999999.99"),
            new BigDecimal("-9999999.99"),
            new BigDecimal("-99999999.99"),
            new BigDecimal("-999999999.99"),
            new BigDecimal("-9999999999.99"),
            new BigDecimal("-99999999999.99"),
            new BigDecimal("-999999999999.99"),
            new BigDecimal("-9999999999999.99"),
            new BigDecimal("-99999999999999.99"),
            new BigDecimal("-999999999999999.99"),
            new BigDecimal("-9999999999999999.99"),
            new BigDecimal("-99999999999999999.99"),
            new BigDecimal("-999999999999999999.99"),
            new BigDecimal("-9999999999999999999.99"),
            new BigDecimal("-99999999999999999999.99"),
            new BigDecimal("-999999999999999999999.99"),
            new BigDecimal("-9999999999999999999999.99"),
            new BigDecimal("-99999999999999999999999.99"),
            new BigDecimal("-999999999999999999999999.99"),
            new BigDecimal("-9999999999999999999999999.99"),
            new BigDecimal("-99999999999999999999999999.99"),
            new BigDecimal("-999999999999999999999999999.99"),
            new BigDecimal("-9999999999999999999999999999.99"),
            new BigDecimal("-99999999999999999999999999999.99"),
            new BigDecimal("-999999999999999999999999999999.99"),
            new BigDecimal("-9999999999999999999999999999999.99"),
            new BigDecimal("-99999999999999999999999999999999.99"),
            new BigDecimal("-999999999999999999999999999999999.99"),
            new BigDecimal("-9999999999999999999999999999999999.99"),
            new BigDecimal("-99999999999999999999999999999999999.99"),
            new BigDecimal("-999999999999999999999999999999999999.99"),
            new BigDecimal("-9999999999999999999999999999999999999.99"),
    };

    public static BigDecimal decimal(BigDecimal value, int precision, int scale) {
        int v_scale = value.scale();

        int v_precision;
        if (v_scale > scale) {
            value = value.setScale(scale, BigDecimal.ROUND_HALF_UP);
            v_precision = value.precision();
        } else {
            v_precision = value.precision();
        }

        int v_ints = v_precision - v_scale;
        int ints = precision - scale;

        if (v_precision > precision || v_ints > ints) {
            boolean sign = value.signum() > 0;

            if (scale == 1) {
                return sign ? MAX_DEC_1[ints] : MIN_DEC_1[ints];
            }

            if (scale == 2) {
                return sign ? MAX_DEC_2[ints] : MIN_DEC_2[ints];
            }

            return new BigDecimal(
                    sign ? MAX_INT[precision - 1] : MIN_INT[precision - 1]
                    , scale
            );
        }

        return value;
    }

    public static boolean isNumber(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }

        char c0 = str.charAt(0);

        boolean dot = false, expr = false;

        int i = 0;
        if (c0 == '+' || c0 == '-') {
            i++;
        }

        for (; i < str.length(); ++i) {
            char ch = str.charAt(i);
            if (ch == '.') {
                if (dot || expr) {
                    return false;
                } else {
                    dot = true;
                    continue;
                }
            }

            if (ch == 'e' || ch == 'E') {
                if (expr) {
                    return false;
                } else {
                    expr = true;
                }

                if (i < str.length() - 1) {
                    char next = str.charAt(i + 1);
                    if (next == '+' || next == '-') {
                        i++;
                    }
                    continue;
                } else {
                    return false;
                }
            }

            if (ch < '0' || ch > '9') {
                return false;
            }
        }

        return true;
    }

    public static DateFormat toJavaFormat(String fmt, TimeZone timeZone) {
        DateFormat dateFormat = toJavaFormat(fmt);
        if (dateFormat == null) {
            return null;
        }

        if (timeZone != null) {
            dateFormat.setTimeZone(timeZone);
        }

        return dateFormat;
    }

    public static DateFormat toJavaFormat(String fmt) {
        if (fmt == null) {
            return null;
        }

        StringBuffer buf = new StringBuffer();

        for (int i = 0, len = fmt.length(); i < len; ++i) {
            char ch = fmt.charAt(i);
            if (ch == '%') {
                if (i + 1 == len) {
                    return null;
                }

                char next_ch = fmt.charAt(++i);
                switch (next_ch) {
                    case 'a':
                        buf.append("EEE");
                        break;
                    case 'b':
                        buf.append("MMM");
                        break;
                    case 'c':
                        buf.append("M");
                        break;
                    case 'd':
                        buf.append("dd");
                        break;
                    case 'e':
                        buf.append("d");
                        break;
                    case 'f':
                        buf.append("SSS000");
                        break;
                    case 'H':
                    case 'k':
                        buf.append("HH");
                        break;
                    case 'h':
                    case 'l':
                    case 'I':
                        buf.append("hh");
                        break;
                    case 'i':
                        buf.append("mm");
                        break;
                    case 'M':
                        buf.append("MMMMM");
                        break;
                    case 'm':
                        buf.append("MM");
                        break;
                    case 'p':
                        buf.append('a');
                        break;
                    case 'r':
                        buf.append("hh:mm:ss a");
                        break;
                    case 's':
                    case 'S':
                        buf.append("ss");
                        break;
                    case 'T':
                        buf.append("HH:mm:ss");
                        break;
                    case 'W':
                        buf.append("EEEEE");
                        break;
                    case 'w':
                        buf.append("u");
                        break;
                    case 'Y':
                        buf.append("yyyy");
                        break;
                    case 'y':
                        buf.append("yy");
                        break;
                    default:
                        return null;
                }

            } else {
                buf.append(ch);
            }
        }

        try {
            return new SimpleDateFormat(buf.toString(), Locale.ENGLISH);
        } catch (IllegalArgumentException ex) {
            // skip
            return null;
        }
    }

    public static java.util.Date parseDate(String str, TimeZone timeZone) {
        if (str == null) {
            return null;
        }

        final int length = str.length();
        if (length < 8) {
            return null;
        }

        ZoneId zoneId = timeZone == null
                ? ZoneId.systemDefault()
                : timeZone.toZoneId();

        char y0 = str.charAt(0);
        char y1 = str.charAt(1);
        char y2 = str.charAt(2);
        char y3 = str.charAt(3);

        char M0 = 0, M1 = 0, d0 = 0, d1 = 0;
        char h0 = 0, h1 = 0, m0 = 0, m1 = 0, s0 = 0, s1 = 0, S0 = '0', S1 = '0', S2 = '0';

        final char c4 = str.charAt(4);
        final char c5 = str.charAt(5);
        final char c6 = str.charAt(6);
        final char c7 = str.charAt(7);
        char c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18;

        int nanos = 0;
        switch (length) {
            case 8:
                // yyyyMMdd
                if (c4 == '-' && c6 == '-') {
                    M0 = '0';
                    M1 = c5;
                    d0 = '0';
                    d1 = c7;
                } else if (y2 == ':' && c5 == ':') {
                    h0 = y0;
                    h1 = y1;
                    m0 = y3;
                    m1 = c4;
                    s0 = c6;
                    s1 = c7;

                    y0 = '1';
                    y1 = '9';
                    y2 = '7';
                    y3 = '0';
                    M0 = '0';
                    M1 = '1';
                    d0 = '0';
                    d1 = '1';
                } else {
                    M0 = c4;
                    M1 = c5;
                    d0 = c6;
                    d1 = c7;
                }
                break;
            case 9:
                // yyyy-M-dd or yyyy-MM-d
                c8 = str.charAt(8);

                if (c4 != '-') {
                    return null;
                }

                if (c6 == '-') {
                    M0 = '0';
                    M1 = c5;
                    d0 = c7;
                    d1 = c8;
                } else if (c7 == '-') {
                    M0 = c5;
                    M1 = c6;
                    d0 = '0';
                    d1 = c8;
                } else {
                    return null;
                }
                break;
            case 10:
                c8 = str.charAt(8);
                c9 = str.charAt(9);
                // yyyy-MM-dd
                if (c4 != '-' || c7 != '-') {
                    return null;
                }

                M0 = c5;
                M1 = c6;
                d0 = c8;
                d1 = c9;
                break;
            case 14:
                c8 = str.charAt(8);
                c9 = str.charAt(9);
                c10 = str.charAt(10);
                c11 = str.charAt(11);
                c12 = str.charAt(12);
                c13 = str.charAt(13);

                if (c8 == ' ') {
                    // yyyy-M-d H:m:s
                    if (c4 == '-' && c6 == '-' & c10 == ':' && c12 == ':') {
                        M0 = '0';
                        M1 = c5;
                        d0 = '0';
                        d1 = c7;
                        h0 = '0';
                        h1 = c9;
                        m0 = '0';
                        m1 = c11;
                        s0 = '0';
                        s1 = c13;
                    } else {
                        return null;
                    }
                } else {
                    // yyyyMMddHHmmss
                    M0 = c4;
                    M1 = c5;
                    d0 = c6;
                    d1 = c7;
                    h0 = c8;
                    h1 = c9;
                    m0 = c10;
                    m1 = c11;
                    s0 = c12;
                    s1 = c13;
                }
                break;
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 29:
                if (length == 19 || length == 23 || length == 29) {
                    c8 = str.charAt(8);
                    c9 = str.charAt(9);
                    c10 = str.charAt(10);
                    c11 = str.charAt(11);
                    c12 = str.charAt(12);
                    c13 = str.charAt(13);
                    c14 = str.charAt(14);
                    c15 = str.charAt(15);
                    c16 = str.charAt(16);
                    c17 = str.charAt(17);
                    c18 = str.charAt(18);

                    // yyyy-MM-dd HH:mm:ss
                    // yyyy-MM-dd HH.mm.ss
                    if (c4 == '-' && c7 == '-'
                            && (c10 == ' ' || c10 == 'T')
                            && ((c13 == ':' && c16 == ':') || (c13 == '.' && c16 == '.')))
                    {
                        M0 = c5;
                        M1 = c6;
                        d0 = c8;
                        d1 = c9;
                        h0 = c11;
                        h1 = c12;
                        m0 = c14;
                        m1 = c15;
                        s0 = c17;
                        s1 = c18;

                        // yyyy-MM-dd HH:mm:ss.SSS
                        if (length == 23) {
                            final char c19 = str.charAt(19);
                            final char c20 = str.charAt(20);
                            final char c21 = str.charAt(21);
                            final char c22 = str.charAt(22);

                            if (c19 == '.') {
                                S0 = c20;
                                S1 = c21;
                                S2 = c22;
                            } else if (c19 == ' ' && c20 == 'U' && c21 == 'T' && c22 == 'C') {
                                // skip
                                zoneId = ZoneOffset.UTC;
                            } else {
                                return null;
                            }
                        } else if (length == 29) {
                            final char c19 = str.charAt(19);
                            final char c20 = str.charAt(20);
                            final char c21 = str.charAt(21);
                            final char c22 = str.charAt(22);


                            if (c19 == '.') {
                                S0 = c20;
                                S1 = c21;
                                S2 = c22;
                            } else {
                                return null;
                            }

                            final char c23 = str.charAt(23);
                            final char c24 = str.charAt(24);
                            final char c25 = str.charAt(25);
                            final char c26 = str.charAt(26);
                            final char c27 = str.charAt(27);
                            final char c28 = str.charAt(28);

                            if (c23 < '0' || c23 > '9'
                                    || c24 < '0' || c24 > '9'
                                    || c25 < '0' || c25 > '9'
                                    || c26 < '0' || c26 > '9'
                                    || c27 < '0' || c27 > '9'
                                    || c28 < '0' || c28 > '9') {
                                return null;
                            }

                            nanos = (c23 - '0')   * 100000
                                    + (c24 - '0') * 10000
                                    + (c25 - '0') * 1000
                                    + (c26 - '0') * 100
                                    + (c27 - '0') * 10
                                    + (c28 - '0');
                        }

                        break;
                    }
                }

                if (c4 != '-') {
                    return null;
                }

                int offset;
                if (c6 == '-') {
                    M0 = '0';
                    M1 = c5;
                    offset = 7;
                } else if (c7 == '-') {
                    M0 = c5;
                    M1 = c6;
                    offset = 8;
                } else {
                    return null;
                }

            {
                char n0 = str.charAt(offset);
                char n1, n2;

                if ((n1 = str.charAt(offset + 1)) == ' ' || n1 == 'T') {
                    d0 = '0';
                    d1 = n0;
                    offset += 2;
                } else if ((n2 = str.charAt(offset + 2)) == ' ' || n2 == 'T') {
                    d0 = n0;
                    d1 = n1;
                    offset += 3;
                } else {
                    return null;
                }
            }

            {
                char n0 = str.charAt(offset);
                char n1, n2;

                if ((n1 = str.charAt(offset + 1)) == ':') {
                    h0 = '0';
                    h1 = n0;
                    offset += 2;
                } else if ((n2 = str.charAt(offset + 2)) == ':') {
                    h0 = n0;
                    h1 = n1;
                    offset += 3;
                } else {
                    return null;
                }
            }

            {
                char n0 = str.charAt(offset);
                char n1, n2;

                if ((n1 = str.charAt(offset + 1)) == ':') {
                    m0 = '0';
                    m1 = n0;
                    offset += 2;
                } else if (offset + 2 < length && (n2 = str.charAt(offset + 2)) == ':') {
                    m0 = n0;
                    m1 = n1;
                    offset += 3;
                } else {
                    return null;
                }
            }

            if (offset == length - 1) {
                s0 = '0';
                s1 = str.charAt(offset);
            } else if (offset == length - 2) {
                char n0 = str.charAt(offset);
                char n1 = str.charAt(offset + 1);
                if (n1 == '.') {
                    s0 = '0';
                    s1 = n0;
                } else {
                    s0 = n0;
                    s1 = n1;
                }
            } else {
                char x0 = str.charAt(length - 1);
                char x1 = str.charAt(length - 2);
                char x2 = str.charAt(length - 3);
                char x3 = str.charAt(length - 4);

                int lastOff;
                if (x0 == '.') {
                    // skip
                    lastOff = length - 2;
                } else if (x1 == '.') {
                    S2 = x0;
                    lastOff = length - 3;
                } else if (x2 == '.') {
                    S1 = x1;
                    S2 = x0;
                    lastOff = length - 4;
                } else if (x3 == '.') {
                    S0 = x2;
                    S1 = x1;
                    S2 = x0;
                    lastOff = length - 5;
                } else if ((x2 == '+' || x2 == '-') && length == offset + 5) {
                    String zoneIdStr = new String(new char[] {x2, x1, x0});
                    zoneId = ZoneId.of(zoneIdStr);
                    lastOff = length - 4;
                } else {
                    return null;
                }

                char k0 = str.charAt(lastOff);
                char k1 = str.charAt(lastOff - 1);
                char k2 = str.charAt(lastOff - 2);
                if (k1 == ':') {
                    s0 = '0';
                    s1 = k0;
                } else if (k2 == ':') {
                    s1 = k0;
                    s0 = k1;
                } else {
                    return null;
                }
            }
            break;
            default:
                return null;
        }

        if (y0 < '0' || y0 > '9'
                || y1 < '0' || y1 > '9'
                || y2 < '0' || y2 > '9'
                || y3 < '0' || y3 > '9') {
            return null;
        }
        int year = (y0 - '0') * 1000
                + (y1 - '0') * 100
                + (y2 - '0') * 10
                + (y3 - '0');
        if (year < 1970) {
            return null;
        }

        if (M0 < '0' || M0 > '1') {
            return null;
        }
        if (M1 < '0' || M1 > '9') {
            return null;
        }
        int month = (M0 - '0') * 10 + (M1 - '0');
        if (month < 1 || month > 12) {
            return null;
        }

        if (d0 < '0' || d0 > '9') {
            return null;
        }
        if (d1 < '0' || d1 > '9') {
            return null;
        }
        int dayOfMonth = (d0 - '0') * 10 + (d1 - '0');
        if (dayOfMonth < 1) {
            return null;
        }

        final int maxDayOfMonth;
        switch (month) {
            case 2:
                maxDayOfMonth = 29;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                maxDayOfMonth = 30;
                break;
            default:
                maxDayOfMonth = 31;
                break;
        }
        if (dayOfMonth > maxDayOfMonth) {
            return null;
        }

        ZonedDateTime zdt = null;
        if (h0 == 0) {
            zdt = LocalDate
                    .of(year, month, dayOfMonth)
                    .atStartOfDay(zoneId);
        } else {
            int hour = (h0 - '0') * 10 + (h1 - '0');
            int minute = (m0 - '0') * 10 + (m1 - '0');
            int second = (s0 - '0') * 10 + (s1 - '0');
            int nanoSecond = ((S0 - '0') * 100 + (S1 - '0') * 10 + (S2 - '0')) * 1000000 + nanos;

            if (hour >= 24 || minute > 60 || second > 61) {
                return null;
            }

            zdt = LocalDateTime
                    .of(year, month, dayOfMonth, hour, minute, second, nanoSecond)
                    .atZone(zoneId);
        }

        return Date.from(
                zdt.toInstant()
        );
    }

    public static long parseMillis(byte[] str, TimeZone timeZone) {
        if (str == null) {
            throw new IllegalArgumentException(new String(str, UTF8));
        }


        return parseMillis(str, 0, str.length, timeZone);
    }

    public static long parseMillis(final byte[] str, final int off, final int len, final TimeZone timeZone) {
        ZoneId zoneId = timeZone == null
                ? ZoneId.systemDefault()
                : timeZone.toZoneId();

        return parseDateTime(str, off, len, zoneId)
                .toInstant()
                .toEpochMilli();
    }

    public static ZonedDateTime parseDateTime(final byte[] str, final int off, final int len, ZoneId zoneId) {
        if (str == null) {
            throw new IllegalArgumentException(new String(str, UTF8));
        }

        if (len < 8) {
            throw new IllegalArgumentException(new String(str, UTF8));
        }

        byte y0 = str[off];
        byte y1 = str[off + 1];
        byte y2 = str[off + 2];
        byte y3 = str[off + 3];

        byte M0 = 0, M1 = 0, d0 = 0, d1 = 0;
        byte h0 = 0, h1 = 0, m0 = 0, m1 = 0, s0 = 0, s1 = 0, S0 = '0', S1 = '0', S2 = '0';

        final byte c4 = str[off + 4];
        final byte c5 = str[off + 5];
        final byte c6 = str[off + 6];
        final byte c7 = str[off + 7];
        byte c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18;

        int nanos = 0;
        switch (len) {
            case 8:
                // yyyyMMdd
                if (c4 == '-' && c6 == '-') {
                    M0 = '0';
                    M1 = c5;
                    d0 = '0';
                    d1 = c7;
                } else if (y2 == ':' && c5 == ':') {
                    h0 = y0;
                    h1 = y1;
                    m0 = y3;
                    m1 = c4;
                    s0 = c6;
                    s1 = c7;

                    y0 = '1';
                    y1 = '9';
                    y2 = '7';
                    y3 = '0';
                    M0 = '0';
                    M1 = '1';
                    d0 = '0';
                    d1 = '1';
                } else {
                    M0 = c4;
                    M1 = c5;
                    d0 = c6;
                    d1 = c7;
                }
                break;
            case 9:
                // yyyy-M-dd or yyyy-MM-d
                c8 = str[off + 8];

                if (c4 != '-') {
                    throw new IllegalArgumentException(new String(str, UTF8));
                }

                if (c6 == '-') {
                    M0 = '0';
                    M1 = c5;
                    d0 = c7;
                    d1 = c8;
                } else if (c7 == '-') {
                    M0 = c5;
                    M1 = c6;
                    d0 = '0';
                    d1 = c8;
                } else {
                    throw new IllegalArgumentException(new String(str, UTF8));
                }
                break;
            case 10:
                c8 = str[off + 8];
                c9 = str[off + 9];
                // yyyy-MM-dd
                if (c4 != '-' || c7 != '-') {
                    throw new IllegalArgumentException(new String(str, UTF8));
                }

                M0 = c5;
                M1 = c6;
                d0 = c8;
                d1 = c9;
                break;
            case 14:
                c8 = str[off + 8];
                c9 = str[off + 9];
                c10 = str[off + 10];
                c11 = str[off + 11];
                c12 = str[off + 12];
                c13 = str[off + 13];

                if (c8 == ' ') {
                    // yyyy-M-d H:m:s
                    if (c4 == '-' && c6 == '-' & c10 == ':' && c12 == ':') {
                        M0 = '0';
                        M1 = c5;
                        d0 = '0';
                        d1 = c7;
                        h0 = '0';
                        h1 = c9;
                        m0 = '0';
                        m1 = c11;
                        s0 = '0';
                        s1 = c13;
                    } else {
                        throw new IllegalArgumentException(new String(str, UTF8));
                    }
                } else {
                    // yyyyMMddHHmmss
                    M0 = c4;
                    M1 = c5;
                    d0 = c6;
                    d1 = c7;
                    h0 = c8;
                    h1 = c9;
                    m0 = c10;
                    m1 = c11;
                    s0 = c12;
                    s1 = c13;
                }
                break;
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 26:
            case 27:
            case 28:
            case 29:
                if (len == 19 || len >= 23) {
                    c8 = str[off + 8];
                    c9 = str[off + 9];
                    c10 = str[off + 10];
                    c11 = str[off + 11];
                    c12 = str[off + 12];
                    c13 = str[off + 13];
                    c14 = str[off + 14];
                    c15 = str[off + 15];
                    c16 = str[off + 16];
                    c17 = str[off + 17];
                    c18 = str[off + 18];

                    // yyyy-MM-dd HH:mm:ss
                    if (c4 == '-' && c7 == '-'
                            && (c10 == ' ' || c10 == 'T')
                            && c13 == ':' && c16 == ':')
                    {
                        M0 = c5;
                        M1 = c6;
                        d0 = c8;
                        d1 = c9;
                        h0 = c11;
                        h1 = c12;
                        m0 = c14;
                        m1 = c15;
                        s0 = c17;
                        s1 = c18;

                        if (len == 19) {
                            break;
                        }

                        // yyyy-MM-dd HH:mm:ss.SSS
                        final byte c19 = str[off + 19];
                        final byte c20 = str[off + 20];
                        final byte c21 = str[off + 21];
                        final byte c22 = str[off + 22];

                        if (len == 23) {
                            if (c19 == '.') {
                                S0 = c20;
                                S1 = c21;
                                S2 = c22;
                            } else if (c19 == ' ' && c20 == 'U' && c21 == 'T' && c22 == 'C') {
                                // skip
                                zoneId = ZoneOffset.UTC;
                            } else {
                                throw new IllegalArgumentException(new String(str, UTF8));
                            }
                            break;
                        }

                        if (c19 == '.') {
                            S0 = c20;
                            S1 = c21;
                            S2 = c22;
                        } else {
                            throw new IllegalArgumentException(new String(str, UTF8));
                        }

                        if (len == 29) {
                            final byte c23 = str[off + 23];
                            final byte c24 = str[off + 24];
                            final byte c25 = str[off + 25];
                            final byte c26 = str[off + 26];
                            final byte c27 = str[off + 27];
                            final byte c28 = str[off + 28];

                            if (c23 < '0' || c23 > '9'
                                    || c24 < '0' || c24 > '9'
                                    || c25 < '0' || c25 > '9'
                                    || c26 < '0' || c26 > '9'
                                    || c27 < '0' || c27 > '9'
                                    || c28 < '0' || c28 > '9') {
                                throw new IllegalArgumentException(new String(str, UTF8));
                            }

                            nanos = (c23 - '0')   * 100000
                                    + (c24 - '0') * 10000
                                    + (c25 - '0') * 1000
                                    + (c26 - '0') * 100
                                    + (c27 - '0') * 10
                                    + (c28 - '0');
                        } else if (len == 28) {
                            final byte c23 = str[off + 23];
                            final byte c24 = str[off + 24];
                            final byte c25 = str[off + 25];
                            final byte c26 = str[off + 26];
                            final byte c27 = str[off + 27];

                            if (c23 < '0' || c23 > '9'
                                    || c24 < '0' || c24 > '9'
                                    || c25 < '0' || c25 > '9'
                                    || c26 < '0' || c26 > '9'
                                    || c27 < '0' || c27 > '9') {
                                throw new IllegalArgumentException(new String(str, UTF8));
                            }

                            nanos = (c23 - '0')   * 100000
                                    + (c24 - '0') * 10000
                                    + (c25 - '0') * 1000
                                    + (c26 - '0') * 100
                                    + (c27 - '0') * 10;
                        } else if (len == 27) {
                            final byte c23 = str[off + 23];
                            final byte c24 = str[off + 24];
                            final byte c25 = str[off + 25];
                            final byte c26 = str[off + 26];

                            if (c23 < '0' || c23 > '9'
                                    || c24 < '0' || c24 > '9'
                                    || c25 < '0' || c25 > '9'
                                    || c26 < '0' || c26 > '9') {
                                throw new IllegalArgumentException(new String(str, UTF8));
                            }

                            nanos = (c23 - '0')   * 100000
                                    + (c24 - '0') * 10000
                                    + (c25 - '0') * 1000
                                    + (c26 - '0') * 100;
                        } else if (len == 26) {
                            final byte c23 = str[off + 23];
                            final byte c24 = str[off + 24];
                            final byte c25 = str[off + 25];

                            if (c23 < '0' || c23 > '9'
                                    || c24 < '0' || c24 > '9'
                                    || c25 < '0' || c25 > '9') {
                                throw new IllegalArgumentException(new String(str, UTF8));
                            }

                            nanos = (c23 - '0')   * 100000
                                    + (c24 - '0') * 10000
                                    + (c25 - '0') * 1000;
                        }

                        break;
                    }
                }

                if (c4 != '-') {
                    throw new IllegalArgumentException(new String(str, UTF8));
                }

                int off2;
                if (c6 == '-') {
                    M0 = '0';
                    M1 = c5;
                    off2 = off + 7;
                } else if (c7 == '-') {
                    M0 = c5;
                    M1 = c6;
                    off2 = off + 8;
                } else {
                    throw new IllegalArgumentException(new String(str, UTF8));
                }

            {
                byte n0 = str[off2];
                byte n1, n2;

                if ((n1 = str[off2 + 1]) == ' ' || n1 == 'T') {
                    d0 = '0';
                    d1 = n0;
                    off2 += 2;
                } else if ((n2 = str[off2 + 2]) == ' ' || n2 == 'T') {
                    d0 = n0;
                    d1 = n1;
                    off2 += 3;
                } else {
                    throw new IllegalArgumentException(new String(str, UTF8));
                }
            }

            {
                byte n0 = str[off2];
                byte n1, n2;

                if ((n1 = str[off2 + 1]) == ':') {
                    h0 = '0';
                    h1 = n0;
                    off2 += 2;
                } else if ((n2 = str[off2 + 2]) == ':') {
                    h0 = n0;
                    h1 = n1;
                    off2 += 3;
                } else {
                    throw new IllegalArgumentException(new String(str, UTF8));
                }
            }

            {
                byte n0 = str[off2];
                byte n1, n2;

                if ((n1 = str[off2 + 1]) == ':') {
                    m0 = '0';
                    m1 = n0;
                    off2 += 2;
                } else if (off2 + 2 < off + len && (n2 = str[off2 + 2]) == ':') {
                    m0 = n0;
                    m1 = n1;
                    off2 += 3;
                } else {
                    throw new IllegalArgumentException(new String(str, UTF8));
                }
            }

            if (off2 == off + len - 1) {
                s0 = '0';
                s1 = str[off2];
            } else if (off2 == off + len - 2) {
                byte n0 = str[off2];
                byte n1 = str[off2 + 1];
                if (n1 == '.') {
                    s0 = '0';
                    s1 = n0;
                } else {
                    s0 = n0;
                    s1 = n1;
                }
            } else {
                byte x0 = str[off + len - 1];
                byte x1 = str[off + len - 2];
                byte x2 = str[off + len - 3];
                byte x3 = str[off + len - 4];

                int lastOff;
                if (x0 == '.') {
                    // skip
                    lastOff = off + len - 2;
                } else if (x1 == '.') {
                    S0 = x0;
                    lastOff = off + len - 3;
                } else if (x2 == '.') {
                    S0 = x1;
                    S1 = x0;
                    lastOff = off + len - 4;
                } else if (x3 == '.') {
                    S0 = x2;
                    S1 = x1;
                    S2 = x0;
                    lastOff = off + len - 5;
                } else {
                    throw new IllegalArgumentException(new String(str, UTF8));
                }

                byte k0 = str[lastOff];
                byte k1 = str[lastOff - 1];
                byte k2 = str[lastOff - 2];
                if (k1 == ':') {
                    s0 = '0';
                    s1 = k0;
                } else if (k2 == ':') {
                    s1 = k0;
                    s0 = k1;
                } else {
                    throw new IllegalArgumentException(new String(str, UTF8));
                }
            }
            break;
            default:
                throw new IllegalArgumentException(new String(str, UTF8));
        }

        if (y0 < '0' || y0 > '9'
                || y1 < '0' || y1 > '9'
                || y2 < '0' || y2 > '9'
                || y3 < '0' || y3 > '9') {
            throw new IllegalArgumentException(new String(str, UTF8));
        }
        int year = (y0 - '0') * 1000
                + (y1 - '0') * 100
                + (y2 - '0') * 10
                + (y3 - '0');

        if (M0 < '0' || M0 > '1') {
            throw new IllegalArgumentException(new String(str, UTF8));
        }
        if (M1 < '0' || M1 > '9') {
            throw new IllegalArgumentException(new String(str, UTF8));
        }
        int month = (M0 - '0') * 10 + (M1 - '0');
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException(new String(str, UTF8));
        }

        if (d0 < '0' || d0 > '9') {
            throw new IllegalArgumentException(new String(str, UTF8));
        }
        if (d1 < '0' || d1 > '9') {
            throw new IllegalArgumentException(new String(str, UTF8));
        }
        int dayOfMonth = (d0 - '0') * 10 + (d1 - '0');
        if (dayOfMonth < 1) {
            throw new IllegalArgumentException(new String(str, UTF8));
        }

        final int maxDayOfMonth;
        switch (month) {
            case 2:
                maxDayOfMonth = 29;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                maxDayOfMonth = 30;
                break;
            default:
                maxDayOfMonth = 31;
                break;
        }
        if (dayOfMonth > maxDayOfMonth) {
            throw new IllegalArgumentException(new String(str, UTF8));
        }

        ZonedDateTime zdt;
        if (h0 == 0) {
            zdt = LocalDate
                    .of(year, month, dayOfMonth)
                    .atStartOfDay(zoneId);
        } else {
            int hour = (h0 - '0') * 10 + (h1 - '0');
            int minute = (m0 - '0') * 10 + (m1 - '0');
            int second = (s0 - '0') * 10 + (s1 - '0');
            int nanoSecond = ((S0 - '0') * 100 + (S1 - '0') * 10 + (S2 - '0')) * 1000000 + nanos;

            if (hour > 24 || minute > 60 || second > 61) {
                throw new IllegalArgumentException(new String(str, UTF8));
            }

            zdt = LocalDateTime
                    .of(year, month, dayOfMonth, hour, minute, second, nanoSecond)
                    .atZone(zoneId);
        }

        return zdt;
    }

    private final static String[] parseFormats = new String[] {
            "HH:mm:ss",
            "yyyyMMdd",
            "yyyyMMddHHmmss",
            "yyyy-M-d",
            "yyyy-M-d H:m:s",
            "yyyy-M-d H:m:s.S",
            "yyyy-M-d'T'H:m:s",
            "yyyy-M-d'T'H:m:s.S",
            "yyyy-MM-d",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
    };
    private final static long[] parseFormatCodes;
    static  {
        long[] codes = new long[parseFormats.length];
        for (int i = 0; i < parseFormats.length; i++) {
            codes[i] = FnvHash.fnv1a_64(parseFormats[i]);
        }
        Arrays.sort(codes);
        parseFormatCodes = codes;
    }

    public static boolean isSupportParseDateformat(String str) {
        if (str == null) {
            return false;
        }
        return Arrays.binarySearch(parseFormatCodes, FnvHash.fnv1a_64(str)) >= 0;
    }

    public static TimeZone parseTimeZone(String str) {
        if ("SYSTEM".equalsIgnoreCase(str)) {
            return TimeZone.getDefault();
        }

        return TimeZone.getTimeZone(str);
    }

    public static String utf32(String hex) {
        byte[] bytes = HexBin.decode(hex);
        if (bytes.length == 2) {
            return new String(bytes, UTF16);
        }
        return new String(bytes, UTF32);
    }

    public static String utf16(String hex) {
        if (hex.length() % 2 == 1) {
            char[] chars = new char[hex.length() + 1];
            chars[0] = '0';
            hex.getChars(0, hex.length(), chars, 1);
            hex = new String(chars);
        }

        byte[] bytes = HexBin.decode(hex);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, UTF16);
    }

    public static String utf8(String hex) {
        byte[] bytes = HexBin.decode(hex);
        return new String(bytes, UTF8);
    }

    public static String gbk(String hex) {
        byte[] bytes = HexBin.decode(hex);
        return new String(bytes, GBK);
    }

    public static String big5(String hex) {
        byte[] bytes = HexBin.decode(hex);
        return new String(bytes, BIG5);
    }
}

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

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public final class JdbcUtils implements JdbcConstants {

    private final static Log        LOG                = LogFactory.getLog(JdbcUtils.class);

    private static final Properties DRIVER_URL_MAPPING = new Properties();

    private static Boolean mysql_driver_version_6      = null;

    static {
        try {
            ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
            if (ctxClassLoader != null) {
                for (Enumeration<URL> e = ctxClassLoader.getResources("META-INF/druid-driver.properties"); e.hasMoreElements();) {
                    URL url = e.nextElement();

                    Properties property = new Properties();

                    InputStream is = null;
                    try {
                        is = url.openStream();
                        property.load(is);
                    } finally {
                        JdbcUtils.close(is);
                    }

                    DRIVER_URL_MAPPING.putAll(property);
                }
            }
        } catch (Exception e) {
            LOG.error("load druid-driver.properties error", e);
        }
    }

    public static void close(Connection x) {
        if (x == null) {
            return;
        }
        try {
            x.close();
        } catch (Exception e) {
            LOG.debug("close connection error", e);
        }
    }

    public static void close(Statement x) {
        if (x == null) {
            return;
        }
        try {
            x.close();
        } catch (Exception e) {
            LOG.debug("close statement error", e);
        }
    }

    public static void close(ResultSet x) {
        if (x == null) {
            return;
        }
        try {
            x.close();
        } catch (Exception e) {
            LOG.debug("close result set error", e);
        }
    }

    public static void close(Closeable x) {
        if (x == null) {
            return;
        }

        try {
            x.close();
        } catch (Exception e) {
            LOG.debug("close error", e);
        }
    }

    public static void close(Blob x) {
        if (x == null) {
            return;
        }

        try {
            x.free();
        } catch (Exception e) {
            LOG.debug("close error", e);
        }
    }

    public static void close(Clob x) {
        if (x == null) {
            return;
        }

        try {
            x.free();
        } catch (Exception e) {
            LOG.debug("close error", e);
        }
    }

    public static void printResultSet(ResultSet rs) throws SQLException {
        printResultSet(rs, System.out);
    }
    
    public static void printResultSet(ResultSet rs, PrintStream out) throws SQLException {
        printResultSet(rs, out, true, "\t");
    }

    public static void printResultSet(ResultSet rs, PrintStream out, boolean printHeader, String seperator) throws SQLException {
        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();
        if (printHeader) {
            for (int columnIndex = 1; columnIndex <= columnCount; ++columnIndex) {
                if (columnIndex != 1) {
                    out.print(seperator);
                }
                out.print(metadata.getColumnName(columnIndex));
            }
        }

        out.println();

        while (rs.next()) {

            for (int columnIndex = 1; columnIndex <= columnCount; ++columnIndex) {
                if (columnIndex != 1) {
                    out.print(seperator);
                }

                int type = metadata.getColumnType(columnIndex);

                if (type == Types.VARCHAR || type == Types.CHAR || type == Types.NVARCHAR || type == Types.NCHAR) {
                    out.print(rs.getString(columnIndex));
                } else if (type == Types.DATE) {
                    Date date = rs.getDate(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(date.toString());
                    }
                } else if (type == Types.BIT) {
                    boolean value = rs.getBoolean(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Boolean.toString(value));
                    }
                } else if (type == Types.BOOLEAN) {
                    boolean value = rs.getBoolean(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Boolean.toString(value));
                    }
                } else if (type == Types.TINYINT) {
                    byte value = rs.getByte(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Byte.toString(value));
                    }
                } else if (type == Types.SMALLINT) {
                    short value = rs.getShort(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Short.toString(value));
                    }
                } else if (type == Types.INTEGER) {
                    int value = rs.getInt(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Integer.toString(value));
                    }
                } else if (type == Types.BIGINT) {
                    long value = rs.getLong(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Long.toString(value));
                    }
                } else if (type == Types.TIMESTAMP) {
                    out.print(String.valueOf(rs.getTimestamp(columnIndex)));
                } else if (type == Types.DECIMAL) {
                    out.print(String.valueOf(rs.getBigDecimal(columnIndex)));
                } else if (type == Types.CLOB) {
                    out.print(String.valueOf(rs.getString(columnIndex)));
                } else if (type == Types.JAVA_OBJECT) {
                    Object object = rs.getObject(columnIndex);

                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(String.valueOf(object));
                    }
                } else if (type == Types.LONGVARCHAR) {
                    Object object = rs.getString(columnIndex);

                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(String.valueOf(object));
                    }
                } else if (type == Types.NULL) {
                    out.print("null");
                } else {
                    Object object = rs.getObject(columnIndex);

                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        if (object instanceof byte[]) {
                            byte[] bytes = (byte[]) object;
                            String text = HexBin.encode(bytes);
                            out.print(text);
                        } else {
                            out.print(String.valueOf(object));
                        }
                    }
                }
            }
            out.println();
        }
    }

    public static String getTypeName(int sqlType) {
        switch (sqlType) {
            case Types.ARRAY:
                return "ARRAY";

            case Types.BIGINT:
                return "BIGINT";

            case Types.BINARY:
                return "BINARY";

            case Types.BIT:
                return "BIT";

            case Types.BLOB:
                return "BLOB";

            case Types.BOOLEAN:
                return "BOOLEAN";

            case Types.CHAR:
                return "CHAR";

            case Types.CLOB:
                return "CLOB";

            case Types.DATALINK:
                return "DATALINK";

            case Types.DATE:
                return "DATE";

            case Types.DECIMAL:
                return "DECIMAL";

            case Types.DISTINCT:
                return "DISTINCT";

            case Types.DOUBLE:
                return "DOUBLE";

            case Types.FLOAT:
                return "FLOAT";

            case Types.INTEGER:
                return "INTEGER";

            case Types.JAVA_OBJECT:
                return "JAVA_OBJECT";

            case Types.LONGNVARCHAR:
                return "LONGNVARCHAR";

            case Types.LONGVARBINARY:
                return "LONGVARBINARY";

            case Types.NCHAR:
                return "NCHAR";

            case Types.NCLOB:
                return "NCLOB";

            case Types.NULL:
                return "NULL";

            case Types.NUMERIC:
                return "NUMERIC";

            case Types.NVARCHAR:
                return "NVARCHAR";

            case Types.REAL:
                return "REAL";

            case Types.REF:
                return "REF";

            case Types.ROWID:
                return "ROWID";

            case Types.SMALLINT:
                return "SMALLINT";

            case Types.SQLXML:
                return "SQLXML";

            case Types.STRUCT:
                return "STRUCT";

            case Types.TIME:
                return "TIME";

            case Types.TIMESTAMP:
                return "TIMESTAMP";

            case Types.TINYINT:
                return "TINYINT";

            case Types.VARBINARY:
                return "VARBINARY";

            case Types.VARCHAR:
                return "VARCHAR";

            default:
                return "OTHER";

        }
    }

    public static String getDriverClassName(String rawUrl) throws SQLException {
        if (rawUrl == null) {
            return null;
        }
        
        if (rawUrl.startsWith("jdbc:derby:")) {
            return "org.apache.derby.jdbc.EmbeddedDriver";
        } else if (rawUrl.startsWith("jdbc:mysql:")) {
            if (mysql_driver_version_6 == null) {
                mysql_driver_version_6 = Utils.loadClass("com.mysql.cj.jdbc.Driver") != null;
            }

            if (mysql_driver_version_6) {
                return MYSQL_DRIVER_6;
            } else {
                return MYSQL_DRIVER;
            }
        } else if (rawUrl.startsWith("jdbc:log4jdbc:")) {
            return LOG4JDBC_DRIVER;
        } else if (rawUrl.startsWith("jdbc:mariadb:")) {
            return MARIADB_DRIVER;
        } else if (rawUrl.startsWith("jdbc:oracle:") //
                   || rawUrl.startsWith("JDBC:oracle:")) {
            return ORACLE_DRIVER;
        } else if (rawUrl.startsWith("jdbc:alibaba:oracle:")) {
            return ALI_ORACLE_DRIVER;
        } else if (rawUrl.startsWith("jdbc:microsoft:")) {
            return "com.microsoft.jdbc.sqlserver.SQLServerDriver";
        } else if (rawUrl.startsWith("jdbc:sqlserver:")) {
            return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        } else if (rawUrl.startsWith("jdbc:sybase:Tds:")) {
            return "com.sybase.jdbc2.jdbc.SybDriver";
        } else if (rawUrl.startsWith("jdbc:jtds:")) {
            return "net.sourceforge.jtds.jdbc.Driver";
        } else if (rawUrl.startsWith("jdbc:fake:") || rawUrl.startsWith("jdbc:mock:")) {
            return "com.alibaba.druid.mock.MockDriver";
        } else if (rawUrl.startsWith("jdbc:postgresql:")) {
            return POSTGRESQL_DRIVER;
        } else if (rawUrl.startsWith("jdbc:edb:")) {
            return ENTERPRISEDB_DRIVER;
        } else if (rawUrl.startsWith("jdbc:odps:")) {
            return ODPS_DRIVER;
        } else if (rawUrl.startsWith("jdbc:hsqldb:")) {
            return "org.hsqldb.jdbcDriver";
        } else if (rawUrl.startsWith("jdbc:db2:")) {
            return DB2_DRIVER;
        } else if (rawUrl.startsWith("jdbc:sqlite:")) {
            return SQLITE_DRIVER;
        } else if (rawUrl.startsWith("jdbc:ingres:")) {
            return "com.ingres.jdbc.IngresDriver";
        } else if (rawUrl.startsWith("jdbc:h2:")) {
            return H2_DRIVER;
        } else if (rawUrl.startsWith("jdbc:mckoi:")) {
            return "com.mckoi.JDBCDriver";
        } else if (rawUrl.startsWith("jdbc:cloudscape:")) {
            return "COM.cloudscape.core.JDBCDriver";
        } else if (rawUrl.startsWith("jdbc:informix-sqli:")) {
            return "com.informix.jdbc.IfxDriver";
        } else if (rawUrl.startsWith("jdbc:timesten:")) {
            return "com.timesten.jdbc.TimesTenDriver";
        } else if (rawUrl.startsWith("jdbc:as400:")) {
            return "com.ibm.as400.access.AS400JDBCDriver";
        } else if (rawUrl.startsWith("jdbc:sapdb:")) {
            return "com.sap.dbtech.jdbc.DriverSapDB";
        } else if (rawUrl.startsWith("jdbc:JSQLConnect:")) {
            return "com.jnetdirect.jsql.JSQLDriver";
        } else if (rawUrl.startsWith("jdbc:JTurbo:")) {
            return "com.newatlanta.jturbo.driver.Driver";
        } else if (rawUrl.startsWith("jdbc:firebirdsql:")) {
            return "org.firebirdsql.jdbc.FBDriver";
        } else if (rawUrl.startsWith("jdbc:interbase:")) {
            return "interbase.interclient.Driver";
        } else if (rawUrl.startsWith("jdbc:pointbase:")) {
            return "com.pointbase.jdbc.jdbcUniversalDriver";
        } else if (rawUrl.startsWith("jdbc:edbc:")) {
            return "ca.edbc.jdbc.EdbcDriver";
        } else if (rawUrl.startsWith("jdbc:mimer:multi1:")) {
            return "com.mimer.jdbc.Driver";
        } else if (rawUrl.startsWith("jdbc:dm:")) {
            return JdbcConstants.DM_DRIVER;
        } else if (rawUrl.startsWith("jdbc:kingbase:")) {
            return JdbcConstants.KINGBASE_DRIVER;
        } else if (rawUrl.startsWith("jdbc:gbase:")) {
            return JdbcConstants.GBASE_DRIVER;
        } else if (rawUrl.startsWith("jdbc:hive:")) {
            return JdbcConstants.HIVE_DRIVER;
        } else if (rawUrl.startsWith("jdbc:hive2:")) {
            return JdbcConstants.HIVE_DRIVER;
        } else if (rawUrl.startsWith("jdbc:phoenix:thin:")) {
            return "org.apache.phoenix.queryserver.client.Driver";
        } else if (rawUrl.startsWith("jdbc:phoenix://")) {
            return JdbcConstants.PHOENIX_DRIVER;
        } else if (rawUrl.startsWith("jdbc:kylin:")) {
            return JdbcConstants.KYLIN_DRIVER;
        } else if (rawUrl.startsWith("jdbc:elastic:")) {
            return JdbcConstants.ELASTIC_SEARCH_DRIVER;
        } else {
            throw new SQLException("unkow jdbc driver : " + rawUrl);
        }
    }

    public static String getDbType(String rawUrl, String driverClassName) {
        if (rawUrl == null) {
            return null;
        }

        if (rawUrl.startsWith("jdbc:derby:") || rawUrl.startsWith("jdbc:log4jdbc:derby:")) {
            return DERBY;
        } else if (rawUrl.startsWith("jdbc:mysql:") || rawUrl.startsWith("jdbc:cobar:")
                   || rawUrl.startsWith("jdbc:log4jdbc:mysql:")) {
            return MYSQL;
        } else if (rawUrl.startsWith("jdbc:mariadb:")) {
            return MARIADB;
        } else if (rawUrl.startsWith("jdbc:oracle:") || rawUrl.startsWith("jdbc:log4jdbc:oracle:")) {
            return ORACLE;
        } else if (rawUrl.startsWith("jdbc:alibaba:oracle:")) {
            return ALI_ORACLE;
        } else if (rawUrl.startsWith("jdbc:microsoft:") || rawUrl.startsWith("jdbc:log4jdbc:microsoft:")) {
            return SQL_SERVER;
        } else if (rawUrl.startsWith("jdbc:sqlserver:") || rawUrl.startsWith("jdbc:log4jdbc:sqlserver:")) {
            return SQL_SERVER;
        } else if (rawUrl.startsWith("jdbc:sybase:Tds:") || rawUrl.startsWith("jdbc:log4jdbc:sybase:")) {
            return SYBASE;
        } else if (rawUrl.startsWith("jdbc:jtds:") || rawUrl.startsWith("jdbc:log4jdbc:jtds:")) {
            return JTDS;
        } else if (rawUrl.startsWith("jdbc:fake:") || rawUrl.startsWith("jdbc:mock:")) {
            return MOCK;
        } else if (rawUrl.startsWith("jdbc:postgresql:") || rawUrl.startsWith("jdbc:log4jdbc:postgresql:")) {
            return POSTGRESQL;
        } else if (rawUrl.startsWith("jdbc:edb:")) {
            return ENTERPRISEDB;
        } else if (rawUrl.startsWith("jdbc:hsqldb:") || rawUrl.startsWith("jdbc:log4jdbc:hsqldb:")) {
            return HSQL;
        } else if (rawUrl.startsWith("jdbc:odps:")) {
            return ODPS;
        } else if (rawUrl.startsWith("jdbc:db2:")) {
            return DB2;
        } else if (rawUrl.startsWith("jdbc:sqlite:")) {
            return SQLITE;
        } else if (rawUrl.startsWith("jdbc:ingres:")) {
            return "ingres";
        } else if (rawUrl.startsWith("jdbc:h2:") || rawUrl.startsWith("jdbc:log4jdbc:h2:")) {
            return H2;
        } else if (rawUrl.startsWith("jdbc:mckoi:")) {
            return "mckoi";
        } else if (rawUrl.startsWith("jdbc:cloudscape:")) {
            return "cloudscape";
        } else if (rawUrl.startsWith("jdbc:informix-sqli:") || rawUrl.startsWith("jdbc:log4jdbc:informix-sqli:")) {
            return "informix";
        } else if (rawUrl.startsWith("jdbc:timesten:")) {
            return "timesten";
        } else if (rawUrl.startsWith("jdbc:as400:")) {
            return "as400";
        } else if (rawUrl.startsWith("jdbc:sapdb:")) {
            return "sapdb";
        } else if (rawUrl.startsWith("jdbc:JSQLConnect:")) {
            return "JSQLConnect";
        } else if (rawUrl.startsWith("jdbc:JTurbo:")) {
            return "JTurbo";
        } else if (rawUrl.startsWith("jdbc:firebirdsql:")) {
            return "firebirdsql";
        } else if (rawUrl.startsWith("jdbc:interbase:")) {
            return "interbase";
        } else if (rawUrl.startsWith("jdbc:pointbase:")) {
            return "pointbase";
        } else if (rawUrl.startsWith("jdbc:edbc:")) {
            return "edbc";
        } else if (rawUrl.startsWith("jdbc:mimer:multi1:")) {
            return "mimer";
        } else if (rawUrl.startsWith("jdbc:dm:")) {
            return JdbcConstants.DM;
        } else if (rawUrl.startsWith("jdbc:kingbase:")) {
            return JdbcConstants.KINGBASE;
        } else if (rawUrl.startsWith("jdbc:gbase:")) {
            return JdbcConstants.GBASE;
        } else if (rawUrl.startsWith("jdbc:log4jdbc:")) {
            return LOG4JDBC;
        } else if (rawUrl.startsWith("jdbc:hive:")) {
            return HIVE;
        } else if (rawUrl.startsWith("jdbc:hive2:")) {
            return HIVE;
        } else if (rawUrl.startsWith("jdbc:phoenix:")) {
            return PHOENIX;
        } else if (rawUrl.startsWith("jdbc:elastic:")) {
            return ELASTIC_SEARCH;
        } else {
            return null;
        }
    }

    public static Driver createDriver(String driverClassName) throws SQLException {
        return createDriver(null, driverClassName);
    }

    public static Driver createDriver(ClassLoader classLoader, String driverClassName) throws SQLException {
        Class<?> clazz = null;
        if (classLoader != null) {
            try {
                clazz = classLoader.loadClass(driverClassName);
            } catch (ClassNotFoundException e) {
                // skip
            }
        }

        if (clazz == null) {
            try {
                ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
                if (contextLoader != null) {
                    clazz = contextLoader.loadClass(driverClassName);
                }
            } catch (ClassNotFoundException e) {
                // skip
            }
        }

        if (clazz == null) {
            try {
                clazz = Class.forName(driverClassName);
            } catch (ClassNotFoundException e) {
                throw new SQLException(e.getMessage(), e);
            }
        }

        try {
            return (Driver) clazz.newInstance();
        } catch (IllegalAccessException e) {
            throw new SQLException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }

    public static int executeUpdate(DataSource dataSource, String sql, Object... parameters) throws SQLException {
        return executeUpdate(dataSource, sql, Arrays.asList(parameters));
    }

    public static int executeUpdate(DataSource dataSource, String sql, List<Object> parameters) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            return executeUpdate(conn, sql, parameters);
        } finally {
            close(conn);
        }
    }

    public static int executeUpdate(Connection conn, String sql, List<Object> parameters) throws SQLException {
        PreparedStatement stmt = null;

        int updateCount;
        try {
            stmt = conn.prepareStatement(sql);

            setParameters(stmt, parameters);

            updateCount = stmt.executeUpdate();
        } finally {
            JdbcUtils.close(stmt);
        }

        return updateCount;
    }

    public static void execute(DataSource dataSource, String sql, Object... parameters) throws SQLException {
        execute(dataSource, sql, Arrays.asList(parameters));
    }

    public static void execute(DataSource dataSource, String sql, List<Object> parameters) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            execute(conn, sql, parameters);
        } finally {
            close(conn);
        }
    }

    public static void execute(Connection conn, String sql) throws SQLException {
        execute(conn, sql, Collections.emptyList());
    }

    public static void execute(Connection conn, String sql, List<Object> parameters) throws SQLException {
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            setParameters(stmt, parameters);

            stmt.executeUpdate();
        } finally {
            JdbcUtils.close(stmt);
        }
    }

    public static List<Map<String, Object>> executeQuery(DataSource dataSource, String sql, Object... parameters)
                                                                                                                 throws SQLException {
        return executeQuery(dataSource, sql, Arrays.asList(parameters));
    }

    public static List<Map<String, Object>> executeQuery(DataSource dataSource, String sql, List<Object> parameters)
                                                                                                                    throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            return executeQuery(conn, sql, parameters);
        } finally {
            close(conn);
        }
    }

    public static List<Map<String, Object>> executeQuery(Connection conn, String sql, List<Object> parameters)
                                                                                                              throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);

            setParameters(stmt, parameters);

            rs = stmt.executeQuery();

            ResultSetMetaData rsMeta = rs.getMetaData();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<String, Object>();

                for (int i = 0, size = rsMeta.getColumnCount(); i < size; ++i) {
                    String columName = rsMeta.getColumnLabel(i + 1);
                    Object value = rs.getObject(i + 1);
                    row.put(columName, value);
                }

                rows.add(row);
            }
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }

        return rows;
    }

    private static void setParameters(PreparedStatement stmt, List<Object> parameters) throws SQLException {
        for (int i = 0, size = parameters.size(); i < size; ++i) {
            Object param = parameters.get(i);
            stmt.setObject(i + 1, param);
        }
    }

    public static void insertToTable(DataSource dataSource, String tableName, Map<String, Object> data)
                                                                                                       throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            insertToTable(conn, tableName, data);
        } finally {
            close(conn);
        }
    }

    public static void insertToTable(Connection conn, String tableName, Map<String, Object> data) throws SQLException {
        String sql = makeInsertToTableSql(tableName, data.keySet());
        List<Object> parameters = new ArrayList<Object>(data.values());
        execute(conn, sql, parameters);
    }

    public static String makeInsertToTableSql(String tableName, Collection<String> names) {
        StringBuilder sql = new StringBuilder() //
        .append("insert into ") //
        .append(tableName) //
        .append("("); //

        int nameCount = 0;
        for (String name : names) {
            if (nameCount > 0) {
                sql.append(",");
            }
            sql.append(name);
            nameCount++;
        }
        sql.append(") values (");
        for (int i = 0; i < nameCount; ++i) {
            if (i != 0) {
                sql.append(",");
            }
            sql.append("?");
        }
        sql.append(")");

        return sql.toString();
    }

    public static <T> void executeQuery(DataSource dataSource
            , ResultSetConsumer<T> consumer
            , String sql
            , Object... parameters) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < parameters.length; ++i) {
                stmt.setObject(i + 1, parameters[i]);
            }
            rs = stmt.executeQuery();
            while (rs.next()) {
                if (consumer != null) {
                    T object = consumer.apply(rs);
                    consumer.accept(object);
                }
            }
        } finally {
            close(rs);
            close(stmt);
            close(conn);
        }
    }

    public static List<String> showTables(Connection conn, String dbType) throws SQLException {
        if (JdbcConstants.MYSQL.equals(dbType)) {
            return MySqlUtils.showTables(conn);
        }

        if (JdbcConstants.ORACLE.equals(dbType)) {
            return OracleUtils.showTables(conn);
        }

        if (JdbcConstants.POSTGRESQL.equals(dbType)) {
            return PGUtils.showTables(conn);
        }
        throw new SQLException("show tables dbType not support for " + dbType);
    }

    public static String getCreateTableScript(Connection conn, String dbType) throws SQLException {
        return getCreateTableScript(conn, dbType, true, true);
    }

    public static String getCreateTableScript(Connection conn, String dbType, boolean sorted, boolean simplify) throws SQLException {
        if (JdbcConstants.MYSQL.equals(dbType)) {
            return MySqlUtils.getCreateTableScript(conn, sorted, simplify);
        }

        if (JdbcConstants.ORACLE.equals(dbType)) {
            return OracleUtils.getCreateTableScript(conn, sorted, simplify);
        }

        throw new SQLException("getCreateTableScript dbType not support for " + dbType);
    }

    public static boolean isMySqlDriver(String driverClassName) {
        return driverClassName.equals(JdbcConstants.MYSQL_DRIVER) //
                || driverClassName.equals(JdbcConstants.MYSQL_DRIVER_6)
                || driverClassName.equals(JdbcConstants.MYSQL_DRIVER_REPLICATE);
    }
}

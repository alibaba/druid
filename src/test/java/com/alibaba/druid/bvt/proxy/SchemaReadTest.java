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
package com.alibaba.druid.bvt.proxy;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.util.JdbcUtils;

public class SchemaReadTest extends TestCase {

    private static String url = "jdbc:wrap-jdbc:filters=default,commonLogging,log4j:name=demo:jdbc:derby:classpath:petstore-db";

    protected void tearDown() throws Exception {
        DruidDriver.getProxyDataSources().clear();
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }
    
    public void test_schema() throws Exception {
        Class.forName("com.alibaba.druid.proxy.DruidDriver");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(url);

            Assert.assertTrue(conn.isReadOnly());

            // just call
            conn.getHoldability();
            conn.getTransactionIsolation();
            conn.getWarnings();
            conn.getTypeMap();
            conn.getAutoCommit();
            conn.getCatalog();
            conn.getClientInfo();
            conn.getClientInfo("xx");

            DatabaseMetaData metadata = conn.getMetaData();
            {
                ResultSet tableTypes = metadata.getTableTypes();
                JdbcUtils.printResultSet(tableTypes, System.out);
                JdbcUtils.close(tableTypes);
            }
            {
                conn.setAutoCommit(false);
                ResultSet tables = metadata.getTables(null, null, null, null);
                JdbcUtils.printResultSet(tables, System.out);
                conn.commit();
                conn.setAutoCommit(true);
                JdbcUtils.close(tables);
            }

            {
                ResultSet tables = metadata.getTables(null, null, null, null);
                while (tables.next()) {
                    String schema = tables.getString(2);
                    String tableName = tables.getString(3);
                    String sql = "SELECT * FROM " + schema + "." + tableName;

                    stmt = conn.createStatement();

                    rs = stmt.executeQuery(sql);
                    JdbcUtils.printResultSet(rs, System.out);

                    JdbcUtils.close(rs);
                    Assert.assertTrue(rs.isClosed());
                    JdbcUtils.close(stmt);
                    Assert.assertTrue(stmt.isClosed());
                }
                JdbcUtils.close(tables);
            }
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
            Assert.assertTrue(conn.isClosed());
        }
    }

    public void test_schema2() throws Exception {
        Class.forName("com.alibaba.druid.proxy.DruidDriver");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(url);

            Assert.assertTrue(conn.isReadOnly());

            // just call
            conn.getHoldability();
            conn.getTransactionIsolation();
            conn.getWarnings();
            conn.getTypeMap();
            conn.getAutoCommit();
            conn.getCatalog();
            conn.getClientInfo();
            conn.getClientInfo("xx");
            conn.isValid(10);

            DatabaseMetaData metadata = conn.getMetaData();
            {
                ResultSet tableTypes = metadata.getTableTypes();
                printResultSetUseColumnName(tableTypes, System.out);
                JdbcUtils.close(tableTypes);
            }
            {
                conn.setAutoCommit(false);
                ResultSet tables = metadata.getTables(null, null, null, null);
                printResultSetUseColumnName(tables, System.out);
                conn.commit();
                conn.setAutoCommit(true);
                JdbcUtils.close(tables);
            }

            {
                ResultSet tables = metadata.getTables(null, null, null, null);
                while (tables.next()) {
                    String schema = tables.getString(2);
                    String tableName = tables.getString(3);
                    String sql = "SELECT * FROM " + schema + "." + tableName;

                    stmt = conn.createStatement();

                    rs = stmt.executeQuery(sql);
                    printResultSetUseColumnName(rs, System.out);

                    JdbcUtils.close(rs);
                    Assert.assertTrue(rs.isClosed());
                    JdbcUtils.close(stmt);
                    Assert.assertTrue(stmt.isClosed());
                }
                JdbcUtils.close(tables);
            }
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
            Assert.assertTrue(conn.isClosed());
        }
    }

    public static void printResultSetUseColumnName(ResultSet rs, PrintStream out) throws SQLException {
        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();
        for (int columnIndex = 1; columnIndex <= columnCount; ++columnIndex) {
            if (columnIndex != 1) {
                out.print('\t');
            }
            out.print(metadata.getColumnName(columnIndex));
        }

        out.println();

        while (rs.next()) {

            for (int columnIndex = 1; columnIndex <= columnCount; ++columnIndex) {
                if (columnIndex != 1) {
                    out.print('\t');
                }

                String columnName = metadata.getColumnName(columnIndex);
                int type = metadata.getColumnType(columnIndex);

                if (type == Types.VARCHAR || type == Types.CHAR || type == Types.NVARCHAR || type == Types.NCHAR) {
                    out.print(rs.getString(columnName));
                } else if (type == Types.DATE) {
                    Date date = rs.getDate(columnName);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(date.toString());
                    }
                } else if (type == Types.BIT) {
                    boolean value = rs.getBoolean(columnName);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Boolean.toString(value));
                    }
                } else if (type == Types.BOOLEAN) {
                    boolean value = rs.getBoolean(columnName);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Boolean.toString(value));
                    }
                } else if (type == Types.TINYINT) {
                    byte value = rs.getByte(columnName);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Byte.toString(value));
                    }
                } else if (type == Types.SMALLINT) {
                    short value = rs.getShort(columnName);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Short.toString(value));
                    }
                } else if (type == Types.INTEGER) {
                    int value = rs.getInt(columnName);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Integer.toString(value));
                    }
                } else if (type == Types.BIGINT) {
                    long value = rs.getLong(columnName);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Long.toString(value));
                    }
                } else if (type == Types.TIMESTAMP) {
                    out.print(String.valueOf(rs.getTimestamp(columnName)));
                } else if (type == Types.DECIMAL) {
                    out.print(String.valueOf(rs.getBigDecimal(columnName)));
                } else if (type == Types.CLOB) {
                    out.print(String.valueOf(rs.getString(columnName)));
                } else if (type == Types.JAVA_OBJECT) {
                    Object objec = rs.getObject(columnName);

                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(String.valueOf(objec));
                    }
                } else if (type == Types.LONGVARCHAR) {
                    Object objec = rs.getString(columnName);

                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(String.valueOf(objec));
                    }
                } else {
                    Object objec = rs.getObject(columnName);

                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(String.valueOf(objec));
                    }
                }
            }
            out.println();
        }
    }
}

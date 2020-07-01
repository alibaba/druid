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

import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Calendar;

import org.junit.Assert;

import junit.framework.TestCase;

import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.util.JdbcUtils;

public class PreparedStatementTest extends TestCase {

    private static String create_url = "jdbc:wrap-jdbc:filters=default,commonLogging,log4j:name=preparedStatementTest:jdbc:derby:memory:preparedStatementTestDB;create=true";

    protected void setUp() throws Exception {
        Class.forName("com.alibaba.druid.proxy.DruidDriver");

        Connection conn = DriverManager.getConnection(create_url);

        createTable();

        conn.close();
    }

    private void createTable() throws SQLException {
        Connection conn = DriverManager.getConnection(create_url);
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE T_PRE_STMT_TEST (ID SMALLINT, NAME VARCHAR(50), BIRTHDATE TIMESTAMP)");
        stmt.close();
        conn.close();
    }

    private void dropTable() throws SQLException {
        Connection conn = DriverManager.getConnection(create_url);
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE T_PRE_STMT_TEST");
        stmt.close();
        conn.close();
    }

    protected void tearDown() throws Exception {
        dropTable();
        
        DruidDriver.getProxyDataSources().clear();
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }

    @SuppressWarnings("deprecation")
    public void test_pstmt() throws Exception {

        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(create_url);

            pstmt = conn.prepareStatement("INSERT INTO T_PRE_STMT_TEST (ID, NAME, BIRTHDATE) VALUES (?, ?, ?)",
                                          Statement.RETURN_GENERATED_KEYS);

            pstmt.setShort(1, (short) 1);
            pstmt.setString(2, "A");
            pstmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            pstmt.execute();

            pstmt.setShort(1, (short) 2);
            pstmt.setString(2, "B");
            pstmt.setNull(3, Types.TIMESTAMP);
            pstmt.execute();

            pstmt.setShort(1, (short) 3);
            pstmt.setString(2, "C");
            pstmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()), Calendar.getInstance());
            pstmt.execute();

            pstmt.setShort(1, (short) 3);
            pstmt.setString(2, "C");
            pstmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()), Calendar.getInstance());
            try {
                pstmt.setArray(2, null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                pstmt.setObject(2, null);
            } catch (SQLDataException ex) {

            }
            try {
                pstmt.setNCharacterStream(2, null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                pstmt.setNCharacterStream(2, null, 0L);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                pstmt.setNClob(2, (NClob) null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                pstmt.setNClob(2, (Reader) null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                pstmt.setNClob(2, null, 0L);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                pstmt.setNString(2, null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                pstmt.setRef(2, null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                pstmt.setRowId(2, null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                pstmt.setSQLXML(2, null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                pstmt.setURL(2, null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                pstmt.setUnicodeStream(2, null, 0);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            pstmt.execute();

            pstmt2 = conn.prepareStatement("SELECT * FROM T_PRE_STMT_TEST ORDER BY 2", ResultSet.TYPE_SCROLL_SENSITIVE,
                                           ResultSet.CONCUR_UPDATABLE);
            rs = pstmt2.executeQuery();
            rs.getCursorName();
            rs.clearWarnings();
            rs.isBeforeFirst();
            rs.isAfterLast();
            rs.isFirst();
            rs.isLast();
            rs.setFetchDirection(rs.getFetchDirection());
            rs.setFetchSize(rs.getFetchSize());
            rs.getConcurrency();
            rs.getRow();
            rs.relative(1);
            rs.absolute(1);

            rs.next();

            rs.rowDeleted();
            rs.rowInserted();
            rs.rowUpdated();

            rs.previous();
            rs.beforeFirst();
            rs.afterLast();
            rs.absolute(1);

            rs.first();
            rs.last();
            rs.close();

            pstmt2 = conn.prepareStatement("SELECT * FROM T_PRE_STMT_TEST ORDER BY 2", ResultSet.TYPE_SCROLL_SENSITIVE,
                                           ResultSet.CONCUR_UPDATABLE);
            rs = pstmt2.executeQuery();
            rs.first();
            rs.last();
            rs.close();

            pstmt2 = conn.prepareStatement("SELECT * FROM T_PRE_STMT_TEST ORDER BY 2", ResultSet.TYPE_SCROLL_SENSITIVE,
                                           ResultSet.CONCUR_UPDATABLE, ResultSet.CLOSE_CURSORS_AT_COMMIT);
            rs = pstmt2.executeQuery();
            rs.first();
            rs.last();
            rs.close();

            pstmt2 = conn.prepareStatement("SELECT * FROM T_PRE_STMT_TEST ORDER BY 2", new int[] { 1 });
            rs = pstmt2.executeQuery();
            JdbcUtils.printResultSet(rs, System.out);
            rs.close();

            pstmt2 = conn.prepareStatement("SELECT * FROM T_PRE_STMT_TEST ORDER BY 2", new String[] { "ID" });
            rs = pstmt2.executeQuery();
            JdbcUtils.printResultSet(rs, System.out);
            rs.close();

            pstmt2 = conn.prepareStatement("SELECT * FROM T_PRE_STMT_TEST ORDER BY 2", new String[] { "ID" });
            rs = pstmt2.executeQuery();
            try {
                rs.getArray(2);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.getArray("NAME");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.getNCharacterStream(2);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.getNCharacterStream("NAME");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.getNClob(2);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.getNClob("NAME");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.getNString(2);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.getNString("NAME");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.getRowId(2);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.getRowId("NAME");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.getSQLXML(2);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.getSQLXML("NAME");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            JdbcUtils.printResultSet(rs, System.out);
            rs.close();

            pstmt2 = conn.prepareStatement("SELECT * FROM T_PRE_STMT_TEST ORDER BY 2",
                                           ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = pstmt2.executeQuery();
            rs.next();
            try {
                rs.refreshRow();
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.insertRow();
            } catch (SQLException ex) {
            }
            try {
                rs.moveToCurrentRow();
            } catch (SQLException ex) {
            }
            try {
                rs.moveToInsertRow();
            } catch (SQLException ex) {
            }

            try {
                rs.getURL(2);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.getURL("NAME");
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                rs.updateArray(2, null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.updateArray("NAME", null);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                rs.updateNCharacterStream(2, null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.updateNCharacterStream("NAME", null);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                rs.updateNCharacterStream(2, null, 0);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.updateNCharacterStream("NAME", null, 0);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                rs.updateNClob(2, (NClob) null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.updateNClob("NAME", (NClob) null);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                rs.updateNClob(2, (Reader) null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.updateNClob("NAME", (Reader) null);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                rs.updateNClob(2, (Reader) null, 0);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.updateNClob("NAME", (Reader) null, 0);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                rs.updateNString(2, null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.updateNString("NAME", null);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                rs.getRef(2);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                rs.getRef("NAME");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.updateRef(2, null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.updateRef("NAME", null);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                rs.updateRowId(2, null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.updateRowId("NAME", null);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                rs.updateSQLXML(2, null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                rs.updateSQLXML("NAME", null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            JdbcUtils.printResultSet(rs, System.out);
            rs.close();
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(pstmt);
            JdbcUtils.close(pstmt2);
            JdbcUtils.close(conn);
        }
    }
}

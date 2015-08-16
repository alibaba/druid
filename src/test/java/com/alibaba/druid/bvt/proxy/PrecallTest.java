/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.sql.Types;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.util.JdbcUtils;

public class PrecallTest extends TestCase {

    private static String create_url = "jdbc:wrap-jdbc:filters=default,commonLogging,log4j:name=preCallTest:jdbc:derby:memory:preCallDB;create=true";

    protected void setUp() throws Exception {
        Class.forName("com.alibaba.druid.proxy.DruidDriver");

        Connection conn = DriverManager.getConnection(create_url);

        createTable();

        conn.close();
    }

    private void createTable() throws SQLException {
        Connection conn = DriverManager.getConnection(create_url);
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE T_USER (ID INTEGER, NAME VARCHAR(50), BIRTHDATE TIMESTAMP)");
        stmt.close();
        conn.close();
    }

    private void dropTable() throws SQLException {
        Connection conn = DriverManager.getConnection(create_url);
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE T_USER");
        stmt.close();
        conn.close();
    }

    protected void tearDown() throws Exception {
        dropTable();
        
        DruidDriver.getProxyDataSources().clear();
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }

    public void test_precall() throws Exception {

        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(create_url);

            cstmt = conn.prepareCall("CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY(?, ?)", ResultSet.FETCH_FORWARD,
                                     ResultSet.CONCUR_READ_ONLY);
            cstmt.close();

            cstmt = conn.prepareCall("CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY(?, ?)", ResultSet.FETCH_FORWARD,
                                     ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
            cstmt.close();

            cstmt = conn.prepareCall("CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY(?, ?)");

            try {
                cstmt.setObject(1, null);
            } catch (SQLDataException ex) {

            }
            try {
                cstmt.setObject(1, null, Types.VARCHAR);
            } catch (SQLDataException ex) {

            }

            try {
                cstmt.setURL(1, null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.setURL("F1", null);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.setSQLXML(1, null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.setSQLXML("F1", null);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.setRowId(1, null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.setRowId("F1", null);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.setNString(1, null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.setNString("F1", null);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.setBlob(1, (Blob) null);
            } catch (SQLDataException ex) {

            }
            try {
                cstmt.setBlob("F1", (Blob) null);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.setBlob(1, (InputStream) null);
            } catch (SQLDataException ex) {

            }
            try {
                cstmt.setBlob("F1", (InputStream) null);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.setBlob(1, (InputStream) null, 0);
            } catch (SQLDataException ex) {

            }
            try {
                cstmt.setBlob("F1", (InputStream) null, 0);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.setClob(1, (Clob) null);
            } catch (SQLDataException ex) {

            }
            try {
                cstmt.setClob("F1", (Clob) null);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.setClob(1, (Reader) null);
            } catch (SQLDataException ex) {

            }
            try {
                cstmt.setClob("F1", (Reader) null);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.setClob(1, (Reader) null, 0);
            } catch (SQLDataException ex) {

            }
            try {
                cstmt.setClob("F1", (Reader) null, 0);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.setNClob(1, (NClob) null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.setNClob("F1", (NClob) null);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.setNClob(1, (Reader) null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.setNClob("F1", (Reader) null);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.setNClob(1, (Reader) null, 0);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.setNClob("F1", (Reader) null, 0);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.setNCharacterStream(1, (Reader) null);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.setNCharacterStream("F1", (Reader) null);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.setNCharacterStream(1, (Reader) null, 0);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.setNCharacterStream("F1", (Reader) null, 0);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.setNull("F1", Types.VARCHAR);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.setNull("F1", Types.VARCHAR, "VARCHAR");
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.getRef(1);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getRef("F1");
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.getClob(1);
            } catch (SQLException ex) {

            }
            try {
                cstmt.getClob("F1");
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.getArray(1);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getArray("F1");
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.getURL(1);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getURL("F1");
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.getSQLXML(1);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getSQLXML("F1");
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.getRowId(1);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getRowId("F1");
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.getNClob(1);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getNClob("F1");
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.getNString(1);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getNString("F1");
            } catch (SQLFeatureNotSupportedException ex) {

            }

            try {
                cstmt.getNCharacterStream(1);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getNCharacterStream("F1");
            } catch (SQLFeatureNotSupportedException ex) {

            }

            cstmt.setString(1, "derby.locks.deadlockTimeout");
            cstmt.setString(2, "10");

            cstmt.execute();

        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(cstmt);
            JdbcUtils.close(conn);
        }
    }

}

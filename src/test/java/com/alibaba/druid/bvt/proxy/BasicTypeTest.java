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

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Types;
import java.util.Calendar;
import java.util.HashMap;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.util.JdbcUtils;

public class BasicTypeTest extends TestCase {

    private static String create_url = "jdbc:wrap-jdbc:filters=default,commonLogging,log4j:name=basicType:jdbc:derby:memory:basicTypeTestDB;create=true";

    protected void setUp() throws Exception {
        Class.forName("com.alibaba.druid.proxy.DruidDriver");

        Connection conn = DriverManager.getConnection(create_url);

        createDDL();

        conn.close();
    }

    private void createDDL() throws SQLException {
        Connection conn = DriverManager.getConnection(create_url);
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE T_BASIC_TYPE (F1 FLOAT, F2 DOUBLE, F3 REAL, F4 DATE, F5 TIME, F6 SMALLINT, F7 SMALLINT, F8 INTEGER, F9 BIGINT, F10 DECIMAL(9,2), F11 TIMESTAMP, F12 BLOB, F13 VARCHAR(256), F14 VARCHAR(256), F15 VARCHAR(256), F16 VARCHAR(256), F17 SMALLINT)");
        stmt.execute("CREATE PROCEDURE BASIC_CALL_0(INOUT F1 FLOAT, INOUT F2 DOUBLE, INOUT F3 REAL, INOUT F4 DATE, INOUT F5 TIME, INOUT F6 SMALLINT, INOUT F7 SMALLINT, INOUT F8 INTEGER, INOUT F9 BIGINT, INOUT F10 DECIMAL(9,2), INOUT F11 TIMESTAMP, INOUT F12 VARCHAR(128) FOR BIT DATA, INOUT F13 VARCHAR(256), INOUT F14 VARCHAR(256), INOUT F15 VARCHAR(256), INOUT F16 VARCHAR(256), INOUT F17 SMALLINT) "
                     + "LANGUAGE JAVA PARAMETER STYLE JAVA EXTERNAL NAME '"
                     + BasicTypeTest.class.getName()
                     + ".basic_process_0' " + "DYNAMIC RESULT SETS 1");
        stmt.close();
        conn.close();
    }

    private void dropTable() throws SQLException {
        Connection conn = DriverManager.getConnection(create_url);
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE T_BASIC_TYPE");
        stmt.execute("DROP PROCEDURE BASIC_CALL_0");
        stmt.close();
        conn.close();
    }

    protected void tearDown() throws Exception {
        dropTable();
        
        DruidDriver.getProxyDataSources().clear();
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }

    @SuppressWarnings("deprecation")
    public void test_basicType() throws Exception {

        Connection conn = null;
        PreparedStatement pstmt = null;
        CallableStatement cstmt = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(create_url);
            conn.rollback();

            pstmt = conn.prepareStatement("INSERT INTO T_BASIC_TYPE (F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16, F17) VALUES (?, ?, ?, ?, ?,	?, ?, ?, ?, ?,	?, ?, ?,?, ?, 	?, ?)");
            pstmt.getParameterMetaData();

            pstmt.setFloat(1, 1F);
            pstmt.setDouble(2, 2.1D);
            pstmt.setFloat(3, 2.1F);
            pstmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            pstmt.setDate(4, new java.sql.Date(System.currentTimeMillis()), Calendar.getInstance());
            pstmt.setTime(5, new java.sql.Time(System.currentTimeMillis()));
            pstmt.setTime(5, new java.sql.Time(System.currentTimeMillis()), Calendar.getInstance());
            pstmt.setByte(6, (byte) 33);
            pstmt.setShort(7, (short) 44);
            pstmt.setInt(8, 55);
            pstmt.setLong(9, 66);
            pstmt.setBigDecimal(10, new BigDecimal("77"));
            pstmt.setTimestamp(11, new java.sql.Timestamp(System.currentTimeMillis()));
            pstmt.setTimestamp(11, new java.sql.Timestamp(System.currentTimeMillis()), Calendar.getInstance());
            pstmt.setBytes(12, new byte[100]);
            pstmt.setAsciiStream(13, null);
            pstmt.setAsciiStream(14, null, 0);
            pstmt.setAsciiStream(14, null, 0L);
            pstmt.setCharacterStream(15, null);
            pstmt.setCharacterStream(16, null, 0);
            pstmt.setCharacterStream(16, null, 0L);
            pstmt.setNull(16, Types.VARCHAR);
            pstmt.setNull(16, Types.VARCHAR, "VARCHAR");
            pstmt.setObject(16, null, Types.VARCHAR);
            pstmt.setObject(16, null, Types.VARCHAR, 0);
            pstmt.setBoolean(17, true);
            pstmt.execute();
            pstmt.clearParameters();
            JdbcUtils.close(pstmt);

            stmt = conn.createStatement();
            stmt.close();

            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.close();

            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE,
                                        ResultSet.CLOSE_CURSORS_AT_COMMIT);
            stmt.setQueryTimeout(stmt.getQueryTimeout());
            stmt.setEscapeProcessing(true);
            stmt.clearBatch();
            stmt.clearWarnings();
            stmt.setCursorName("demo_cur");

            conn.setAutoCommit(false);
            Savepoint point = conn.setSavepoint();
            point = conn.setSavepoint("save_point");

            rs = stmt.executeQuery("SELECT F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16, F17 FROM T_BASIC_TYPE");
            rs.next();
            rs.getFloat(1);
            rs.getDouble(2);
            rs.getFloat(3);
            rs.getDate(4);
            rs.getDate(4, Calendar.getInstance());
            rs.getTime(5);
            rs.getTime(5, Calendar.getInstance());
            rs.getByte(6);
            rs.getShort(7);
            rs.getInt(8);
            rs.getLong(9);
            rs.getBigDecimal(10);
            rs.getBigDecimal(10, 2);
            rs.getTimestamp(11);
            rs.getTimestamp(11, Calendar.getInstance());
            rs.getBytes(12);
            rs.getBlob(12).free();
            rs.getString(13);
            rs.getAsciiStream(13);
            rs.getCharacterStream(14);
            rs.getObject(15, new HashMap<String, Class<?>>());

            rs.getHoldability();

            rs.updateFloat(1, 2F);
            rs.updateDouble(2, 2D);
            rs.updateDate(4, new java.sql.Date(System.currentTimeMillis()));
            rs.updateTime(5, new java.sql.Time(System.currentTimeMillis()));
            rs.updateByte(6, (byte) 6);
            rs.updateShort(7, (short) 77);
            rs.updateInt(8, 77);
            rs.updateLong(9, 99);
            rs.updateBigDecimal(10, new BigDecimal("10"));
            rs.updateTimestamp(11, new java.sql.Timestamp(System.currentTimeMillis()));
            rs.updateBytes(12, new byte[300]);
            rs.updateBinaryStream(12, null);
            rs.updateBinaryStream(12, null, 0);
            rs.updateBinaryStream(12, null, 0L);
            rs.updateString(13, "13x");
            rs.updateAsciiStream(13, null);
            rs.updateAsciiStream(13, null, 0);
            rs.updateAsciiStream(13, null, 0L);
            rs.updateCharacterStream(14, null);
            rs.updateCharacterStream(14, null, 0);
            rs.updateCharacterStream(14, null, 0L);
            rs.updateNull(14);
            rs.updateObject(15, "object");
            rs.updateObject(15, "object", 0);
            rs.updateBoolean(17, false);
            rs.updateRow();

            JdbcUtils.close(rs);
//            conn.rollback(point);
            conn.setAutoCommit(true);

            rs = stmt.executeQuery("SELECT F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16, F17 FROM T_BASIC_TYPE");
            rs.next();
            rs.getFloat("F1");
            rs.getDouble("F2");
            rs.getFloat("F3");
            rs.getDate("F4");
            rs.getDate("F4", Calendar.getInstance());
            rs.getTime("F5");
            rs.getTime("F5", Calendar.getInstance());
            rs.getByte("F6");
            rs.getShort("F7");
            rs.getInt("F8");
            rs.getLong("F9");
            rs.getBigDecimal("F10");
            rs.getBigDecimal("F10", 2);
            rs.getTimestamp("F11");
            rs.getTimestamp("F11", Calendar.getInstance());
            rs.getBytes("F12");
            rs.getBlob("F12");
            rs.getString("F13");
            rs.getAsciiStream("F13");
            rs.getCharacterStream("F14");
            rs.getObject("F15", new HashMap<String, Class<?>>());

            rs.updateFloat("F1", 2F);
            rs.updateDouble("F2", 2D);
            rs.updateDate("F4", new java.sql.Date(System.currentTimeMillis()));
            rs.updateTime("F5", new java.sql.Time(System.currentTimeMillis()));
            rs.updateByte("F6", (byte) 6);
            rs.updateShort("F7", (short) 77);
            rs.updateInt("F8", 77);
            rs.updateLong("F9", 99);
            rs.updateBigDecimal("F10", new BigDecimal("10"));
            rs.updateTimestamp("F11", new java.sql.Timestamp(System.currentTimeMillis()));
            rs.updateBytes("F12", new byte[300]);
            rs.updateBinaryStream("F12", null);
            rs.updateBinaryStream("F12", null, 0);
            rs.updateBinaryStream("F12", null, 0L);
            rs.updateString("F13", "13x");
            rs.updateAsciiStream("F13", null);
            rs.updateAsciiStream("F13", null, 0);
            rs.updateAsciiStream("F13", null, 0L);
            rs.updateCharacterStream("F14", null);
            rs.updateCharacterStream("F14", null, 0);
            rs.updateCharacterStream("F14", null, 0L);
            rs.updateNull("F14");
            rs.updateObject("F15", "object");
            rs.updateObject("F15", "object", 0);
            rs.updateBoolean("F17", false);
            rs.cancelRowUpdates();
            rs.deleteRow();

            Assert.assertEquals(12, rs.findColumn("F12"));
            JdbcUtils.close(rs);

            pstmt = conn.prepareStatement("SELECT F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16 FROM T_BASIC_TYPE");
            JdbcUtils.close(pstmt);

            pstmt = conn.prepareStatement("SELECT F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16 FROM T_BASIC_TYPE",
                                          new int[] { 1, 2 });
            JdbcUtils.close(pstmt);

            pstmt = conn.prepareStatement("SELECT F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16 FROM T_BASIC_TYPE",
                                          new String[] { "F1", "F2" });
            JdbcUtils.close(pstmt);

            pstmt = conn.prepareStatement("SELECT F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16 FROM T_BASIC_TYPE",
                                          Statement.RETURN_GENERATED_KEYS);
            JdbcUtils.close(pstmt);

            pstmt = conn.prepareStatement("SELECT F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16 FROM T_BASIC_TYPE",
                                          ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pstmt.executeQuery().close();
            JdbcUtils.close(pstmt);

            pstmt = conn.prepareStatement("SELECT F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16 FROM T_BASIC_TYPE",
                                          ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE,
                                          ResultSet.CLOSE_CURSORS_AT_COMMIT);
            JdbcUtils.close(pstmt);

            cstmt = conn.prepareCall("CALL BASIC_CALL_0(?, ?, ?, ?, ?,	?, ?, ?, ?, ?,	?, ?, ?,?, ?, 	?, ?)");
            cstmt.registerOutParameter(1, Types.FLOAT);
            cstmt.registerOutParameter(2, Types.DOUBLE);
            cstmt.registerOutParameter(3, Types.FLOAT);
            cstmt.registerOutParameter(4, Types.DATE);
            cstmt.registerOutParameter(5, Types.TIME);

            cstmt.registerOutParameter(6, Types.TINYINT);
            cstmt.registerOutParameter(7, Types.SMALLINT);
            cstmt.registerOutParameter(8, Types.INTEGER);
            cstmt.registerOutParameter(9, Types.BIGINT);
            cstmt.registerOutParameter(10, Types.DECIMAL);
            cstmt.registerOutParameter(10, Types.DECIMAL, 2);
            try {
                cstmt.registerOutParameter(10, Types.DECIMAL, "DECIMAL");
            } catch (SQLFeatureNotSupportedException ex) {
            }

            cstmt.registerOutParameter(11, Types.TIMESTAMP);
            cstmt.registerOutParameter(12, Types.BINARY);
            cstmt.registerOutParameter(13, Types.VARCHAR);
            cstmt.registerOutParameter(14, Types.VARCHAR);
            cstmt.registerOutParameter(15, Types.VARCHAR);

            cstmt.registerOutParameter(16, Types.VARCHAR);
            cstmt.registerOutParameter(17, Types.BOOLEAN);

            //
            try {
                cstmt.registerOutParameter("F2", Types.DECIMAL);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.registerOutParameter("F2", Types.DECIMAL, 2);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.registerOutParameter("F2", Types.DECIMAL, "DECIMAL");
            } catch (SQLFeatureNotSupportedException ex) {
            }

            // cstmt.registerOutParameter(18, Types.OTHER);

            cstmt.setFloat(1, 1F);
            cstmt.setDouble(2, 2.1D);
            cstmt.setFloat(3, 2.1F);
            cstmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            cstmt.setDate(4, new java.sql.Date(System.currentTimeMillis()), Calendar.getInstance());
            cstmt.setTime(5, new java.sql.Time(System.currentTimeMillis()));
            cstmt.setTime(5, new java.sql.Time(System.currentTimeMillis()), Calendar.getInstance());
            cstmt.setByte(6, (byte) 33);
            cstmt.setShort(7, (short) 44);
            cstmt.setInt(8, 55);
            cstmt.setLong(9, 66);
            cstmt.setBigDecimal(10, new BigDecimal("77"));
            cstmt.setTimestamp(11, new java.sql.Timestamp(System.currentTimeMillis()));
            cstmt.setTimestamp(11, new java.sql.Timestamp(System.currentTimeMillis()), Calendar.getInstance());
            cstmt.setBinaryStream(12, null);
            cstmt.setBinaryStream(12, null, 0);
            cstmt.setBinaryStream(12, null, 0L);
            cstmt.setBytes(12, new byte[100]);
            cstmt.setAsciiStream(13, null);
            cstmt.setAsciiStream(14, null, 0);
            cstmt.setAsciiStream(14, null, 0L);
            cstmt.setString(15, null);
            cstmt.setCharacterStream(15, null);
            cstmt.setCharacterStream(16, null, 0);
            cstmt.setCharacterStream(16, null, 0L);
            cstmt.setNull(16, Types.VARCHAR);
            cstmt.setObject(16, null, Types.VARCHAR);
            cstmt.setBoolean(17, true);

            try {
                cstmt.setFloat("F1", 1F);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setDouble("F2", 2.1D);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setFloat("F3", 2.1F);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setDate("F4", new java.sql.Date(System.currentTimeMillis()));
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setDate("F4", new java.sql.Date(System.currentTimeMillis()), Calendar.getInstance());
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setTime("F5", new java.sql.Time(System.currentTimeMillis()));
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setTime("F5", new java.sql.Time(System.currentTimeMillis()), Calendar.getInstance());
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setByte("F6", (byte) 33);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setShort("F7", (short) 44);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setInt("F8", 55);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setLong("F9", 66);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setBigDecimal("F10", new BigDecimal("77"));
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setTimestamp("F11", new java.sql.Timestamp(System.currentTimeMillis()));
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setTimestamp("F11", new java.sql.Timestamp(System.currentTimeMillis()), Calendar.getInstance());
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setBytes("F12", new byte[100]);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setBinaryStream("F12", null);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setBinaryStream("F12", null, 0);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setBinaryStream("F12", null, 0L);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setAsciiStream("F13", null);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setAsciiStream("F14", null, 0);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setAsciiStream("F14", null, 0L);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setCharacterStream("F15", null);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setCharacterStream("F16", null, 0);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setCharacterStream("F16", null, 0L);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setString("F16", null);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setObject("F16", null);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setObject("F16", null, Types.VARCHAR);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setNull("F16", Types.VARCHAR);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.setBoolean("F17", true);
            } catch (SQLFeatureNotSupportedException ex) {
            }

            cstmt.execute();

            try {
                cstmt.getFloat(1);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.getDouble(2);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getFloat(3);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getDate(4);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getDate(4, Calendar.getInstance());
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getTime(5);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getTime(5, Calendar.getInstance());
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getByte(6);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getShort(7);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getInt(8);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getLong(9);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getBigDecimal(10);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getBigDecimal(10, 2);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getTimestamp(11);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getTimestamp(11, Calendar.getInstance());
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getBytes(12);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getBlob(12).free();
            } catch (SQLException ex) {

            }
            try {
                cstmt.getString(13);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getCharacterStream(14);
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.getObject(15);
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getObject(15, new HashMap<String, Class<?>>());
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getBoolean(17);
            } catch (SQLFeatureNotSupportedException ex) {

            }

            // /

            try {
                cstmt.getFloat("F1");
            } catch (SQLFeatureNotSupportedException ex) {
            }
            try {
                cstmt.getDouble("F2");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getFloat("F3");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getDate("F4");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getDate("F4", Calendar.getInstance());
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getTime("F5");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getTime("F5", Calendar.getInstance());
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getByte("F6");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getShort("F7");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getInt("F8");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getLong("F9");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getBigDecimal("F10");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getTimestamp("F11");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getTimestamp("F11", Calendar.getInstance());
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getBytes("F12");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getBlob("F12").free();
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getString("F13");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getCharacterStream("F14");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getObject("F15");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getObject("F15", new HashMap<String, Class<?>>());
            } catch (SQLFeatureNotSupportedException ex) {

            }
            try {
                cstmt.getBoolean("F17");
            } catch (SQLFeatureNotSupportedException ex) {

            }
            cstmt.wasNull();

        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(pstmt);
            JdbcUtils.close(cstmt);
            JdbcUtils.close(conn);
        }
    }

    public static void basic_process_0(double[] p1, double[] p2, float[] p3, java.sql.Date[] p4, java.sql.Time[] p5,
                                       short[] p6, short[] p7, int[] p8, long[] p9, BigDecimal[] p10,
                                       java.sql.Timestamp[] p11, byte[][] p12, String[] p13, String[] p14,
                                       String[] p15, String[] p16, short[] P17, ResultSet[] p18) {
        // stmt.execute("CREATE TABLE T_BASIC_TYPE (F1 FLOAT, F2 DOUBLE, F3
        // REAL, F4 DATE,
        // F5 TIME, F6 SMALLINT, F7 SMALLINT, F8 INTEGER, F9 BIGINT, F10
        // DECIMAL(9,2),
        // F11 TIMESTAMP, F12 BLOB, F13 VARCHAR(256), F14 VARCHAR(256), F15
        // VARCHAR(256), F16 VARCHAR(256), F17 SMALLINT)");
    }

}

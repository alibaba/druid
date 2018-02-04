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

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.util.JdbcUtils;

public class BlobTest extends TestCase {

    private static String create_url = "jdbc:wrap-jdbc:filters=default,commonLogging,log4j:name=demo:jdbc:derby:memory:blobTestDB;create=true";

    protected void setUp() throws Exception {
        Class.forName("com.alibaba.druid.proxy.DruidDriver");

        Connection conn = DriverManager.getConnection(create_url);

        createTable();

        conn.close();
    }

    private void createTable() throws SQLException {
        Connection conn = DriverManager.getConnection(create_url);
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE T_BLOB (ID INTEGER, DATA BLOB)");
        stmt.close();
        conn.close();
    }

    private void dropTable() throws SQLException {
        Connection conn = DriverManager.getConnection(create_url);
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE T_BLOB");
        stmt.close();
        conn.close();
    }

    protected void tearDown() throws Exception {
        dropTable();
        
        DruidDriver.getProxyDataSources().clear();
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }

    public void test_blob() throws Exception {

        Connection conn = null;
        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(create_url);

            conn.setCatalog(conn.getCatalog());
            conn.setClientInfo(conn.getClientInfo());
            conn.setHoldability(conn.getHoldability());
            conn.setReadOnly(conn.isReadOnly());
            conn.setTransactionIsolation(conn.getTransactionIsolation());
            conn.setTypeMap(conn.getTypeMap());

            pstmt = conn.prepareStatement("INSERT INTO T_BLOB (ID, DATA) VALUES (?, ?)");

            Blob blob = conn.createBlob();

            blob.setBytes(1, new byte[100]);

            pstmt.setInt(1, 1);
            pstmt.setBlob(2, blob);

            int updateCount = pstmt.executeUpdate();
            Assert.assertEquals(1, updateCount);

            pstmt.setInt(1, 2);
            pstmt.setBlob(2, new ByteArrayInputStream("XBCSDasdfasdfasfasfF".getBytes()));
            updateCount = pstmt.executeUpdate();
            Assert.assertEquals(1, updateCount);

            pstmt.setInt(1, 2);
            pstmt.setBlob(2, new ByteArrayInputStream("XBCSDasdfasdfasfasfF".getBytes()), 20);
            updateCount = pstmt.executeUpdate();
            Assert.assertEquals(1, updateCount);

            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE,
                                        ResultSet.CLOSE_CURSORS_AT_COMMIT);

            conn.nativeSQL("SELECT ID, DATA FROM T_BLOB");
            // //////
            rs = stmt.executeQuery("SELECT ID, DATA FROM T_BLOB");
            rs.getStatement(); // just call
            while (rs.next()) {
                Blob readBlob = rs.getBlob(2);
                readBlob.length();
                readBlob.getBinaryStream(1, 10).close();
                readBlob.getBinaryStream().close();
                readBlob.free();
            }
            JdbcUtils.close(rs);

            rs = stmt.executeQuery("SELECT ID, DATA FROM T_BLOB");
            while (rs.next()) {
                rs.getBinaryStream(2).close();
            }
            JdbcUtils.close(rs);

            rs = stmt.executeQuery("SELECT ID, DATA FROM T_BLOB");
            while (rs.next()) {
                rs.getBinaryStream("DATA").close();
            }
            JdbcUtils.close(rs);

            rs = stmt.executeQuery("SELECT ID, DATA FROM T_BLOB");
            while (rs.next()) {
                rs.getBytes(2);
            }
            JdbcUtils.close(rs);

            rs = stmt.executeQuery("SELECT ID, DATA FROM T_BLOB");
            while (rs.next()) {
                Blob x = conn.createBlob();
                x.setBytes(1, new byte[100]);
                rs.updateBlob(2, x);
            }
            JdbcUtils.close(rs);

            rs = stmt.executeQuery("SELECT ID, DATA FROM T_BLOB");
            while (rs.next()) {
                Blob x = conn.createBlob();
                x.setBytes(1, new byte[100]);
                rs.updateBlob("DATA", x);
            }
            JdbcUtils.close(rs);

            rs = stmt.executeQuery("SELECT ID, DATA FROM T_BLOB");
            while (rs.next()) {
                rs.updateBlob("DATA", new ByteArrayInputStream(new byte[100]));
            }
            JdbcUtils.close(rs);

            rs = stmt.executeQuery("SELECT ID, DATA FROM T_BLOB");
            while (rs.next()) {
                rs.updateBlob(2, new ByteArrayInputStream(new byte[100]));
            }

            rs = stmt.executeQuery("SELECT ID, DATA FROM T_BLOB");
            while (rs.next()) {
                rs.updateBlob("DATA", new ByteArrayInputStream(new byte[100]), 100);
            }
            JdbcUtils.close(rs);

            rs = stmt.executeQuery("SELECT ID, DATA FROM T_BLOB");
            while (rs.next()) {
                rs.updateBlob(2, new ByteArrayInputStream(new byte[100]), 100);
            }
            JdbcUtils.close(rs);
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(pstmt);
            JdbcUtils.close(conn);
        }
    }

}

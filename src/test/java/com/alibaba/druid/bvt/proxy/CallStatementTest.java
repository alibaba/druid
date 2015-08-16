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

import java.sql.CallableStatement;
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

public class CallStatementTest extends TestCase {

    /**
     * Procedures that should be created before the tests are run and dropped when the tests have finished. First
     * element in each row is the name of the procedure, second element is SQL which creates it.
     */
    private static final String[] PROCEDURES = {

            "CREATE PROCEDURE RETRIEVE_DYNAMIC_RESULTS(number INT) "
                    + "LANGUAGE JAVA PARAMETER STYLE JAVA EXTERNAL NAME '" + CallStatementTest.class.getName()
                    + ".retrieveDynamicResults' " + "DYNAMIC RESULT SETS 4",

            "CREATE PROCEDURE RETRIEVE_CLOSED_RESULT() LANGUAGE JAVA " + "PARAMETER STYLE JAVA EXTERNAL NAME '"
                    + CallStatementTest.class.getName() + ".retrieveClosedResult' " + "DYNAMIC RESULT SETS 1",

            "CREATE PROCEDURE RETRIEVE_EXTERNAL_RESULT("
                    + "DBNAME VARCHAR(128), DBUSER VARCHAR(128), DBPWD VARCHAR(128)) LANGUAGE JAVA "
                    + "PARAMETER STYLE JAVA EXTERNAL NAME '" + CallStatementTest.class.getName()
                    + ".retrieveExternalResult' " + "DYNAMIC RESULT SETS 1",

            "CREATE PROCEDURE PROC_WITH_SIDE_EFFECTS(ret INT) LANGUAGE JAVA " + "PARAMETER STYLE JAVA EXTERNAL NAME '"
                    + CallStatementTest.class.getName() + ".procWithSideEffects' " + "DYNAMIC RESULT SETS 2",

            "CREATE PROCEDURE NESTED_RESULT_SETS(proctext VARCHAR(128)) LANGUAGE JAVA "
                    + "PARAMETER STYLE JAVA EXTERNAL NAME '" + CallStatementTest.class.getName()
                    + ".nestedDynamicResultSets' " + "DYNAMIC RESULT SETS 6"

                                             };

    private static String         create_url = "jdbc:wrap-jdbc:filters=default,commonLogging,log4j:name=demo:jdbc:derby:memory:callableStatementDB;create=true";

    protected void setUp() throws Exception {
        Class.forName("com.alibaba.druid.proxy.DruidDriver");

        Connection conn = DriverManager.getConnection(create_url);

        createTable();

        conn.close();
    }

    private void createTable() throws SQLException {
        Connection conn = DriverManager.getConnection(create_url);
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE T_CLOB (ID INTEGER, DATA CLOB)");

        for (int i = 0; i < PROCEDURES.length; i++) {
            stmt.execute(PROCEDURES[i]);
        }

        stmt.close();
        conn.close();
    }

    private void dropTable() throws SQLException {
        Connection conn = DriverManager.getConnection(create_url);
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE T_CLOB");
        stmt.close();
        conn.close();
    }

    protected void tearDown() throws Exception {
        dropTable();
        DruidDriver.getProxyDataSources().clear();
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }

    public void test_precall() throws Exception {
        f_testExecuteQueryWithNoDynamicResultSets();
        f_testExecuteQueryWithNoDynamicResultSets_callable();
    }

    public void f_testExecuteQueryWithNoDynamicResultSets() throws SQLException {
        Connection connect = DriverManager.getConnection(create_url);
        Statement stmt = connect.createStatement();
        SQLException error = null;
        try {
            stmt.executeQuery("CALL RETRIEVE_DYNAMIC_RESULTS(0)");
        } catch (SQLException sqle) {
            error = sqle;
        }

        Assert.assertNotNull(error);
        stmt.close();
        connect.close();
    }

    public void f_testExecuteQueryWithNoDynamicResultSets_callable() throws SQLException {
        Connection connect = DriverManager.getConnection(create_url);
        CallableStatement cs = connect.prepareCall("CALL RETRIEVE_DYNAMIC_RESULTS(?)");
        cs.setInt(1, 0);
        SQLException error = null;
        try {
            cs.executeQuery();
            fail("executeQuery() didn't fail.");
        } catch (SQLException sqle) {
            error = sqle;
        }
        Assert.assertNotNull(error);
        cs.close();
        connect.close();
    }

    public static void retrieveClosedResult(ResultSet[] closed) throws SQLException {
        Connection connect = DriverManager.getConnection(create_url);
        closed[0] = connect.createStatement().executeQuery("VALUES(1)");
        closed[0].close();
        connect.close();
    }

    public static void retrieveExternalResult(String dbName, String user, String password, ResultSet[] external)
                                                                                                                throws SQLException {

        Connection conn = DriverManager.getConnection(create_url);

        external[0] = conn.createStatement().executeQuery("VALUES(1)");
    }

    public static void selectRows(int p1, ResultSet[] data) throws SQLException {

        System.out.println("selectRows - 1 arg - 1 rs");

        Connection conn = DriverManager.getConnection("jdbc:default:connection");
        PreparedStatement ps = conn.prepareStatement("select * from t1 where i = ?");
        ps.setInt(1, p1);
        data[0] = ps.executeQuery();
        conn.close();
    }

    public static void selectRows(int p1, int p2, ResultSet[] data1, ResultSet[] data2) throws SQLException {

        System.out.println("selectRows - 2 arg - 2 rs");

        Connection conn = DriverManager.getConnection("jdbc:default:connection");
        PreparedStatement ps = conn.prepareStatement("select * from t1 where i = ?");
        ps.setInt(1, p1);
        data1[0] = ps.executeQuery();

        ps = conn.prepareStatement("select * from t1 where i >= ?");
        ps.setInt(1, p2);
        data2[0] = ps.executeQuery();

        if (p2 == 99) data2[0].close();

        // return no results
        if (p2 == 199) {
            data1[0].close();
            data1[0] = null;
            data2[0].close();
            data2[0] = null;
        }

        // swap results
        if (p2 == 299) {
            ResultSet rs = data1[0];
            data1[0] = data2[0];
            data2[0] = rs;
        }

        conn.close();
    }

    // select all rows from a table
    public static void selectRows(String table, ResultSet[] rs) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:default:connection");
        Statement stmt = conn.createStatement();
        rs[0] = stmt.executeQuery("SELECT * FROM " + table);
        conn.close();
    }

    public static void fivejp(ResultSet[] data1, ResultSet[] data2, ResultSet[] data3, ResultSet[] data4,
                              ResultSet[] data5) throws SQLException {

        Connection conn = DriverManager.getConnection("jdbc:default:connection");

        PreparedStatement ps1 = conn.prepareStatement("select * from MRS.FIVERS where i > ?");
        ps1.setInt(1, 1);
        data1[0] = ps1.executeQuery();

        PreparedStatement ps2 = conn.prepareStatement("select * from MRS.FIVERS  where i > ?");
        ps2.setInt(1, 2);
        data2[0] = ps2.executeQuery();

        PreparedStatement ps3 = conn.prepareStatement("select * from MRS.FIVERS  where i > ?");
        ps3.setInt(1, 3);
        data3[0] = ps3.executeQuery();

        PreparedStatement ps4 = conn.prepareStatement("select * from MRS.FIVERS  where i > ?");
        ps4.setInt(1, 4);
        data4[0] = ps4.executeQuery();

        PreparedStatement ps5 = conn.prepareStatement("select * from MRS.FIVERS  where i > ?");
        ps5.setInt(1, 5);
        data5[0] = ps5.executeQuery();

        conn.close();
    }

    public static void parameter1(int a, String b, String c, java.sql.ResultSet[] rs) throws SQLException {

        System.out.print("PT1 a=" + a);
        if (b == null) System.out.println(" b = null");
        else System.out.print(" b=<" + b + ">(" + b.length() + ")");
        if (c == null) System.out.println(" c = null");
        else System.out.print(" c=<" + c + ">(" + c.length() + ")");

        System.out.println("");

        Connection conn = DriverManager.getConnection("jdbc:default:connection");
        PreparedStatement ps = conn.prepareStatement("insert into PT1 values (?, ?, ?)");
        ps.setInt(1, a);
        ps.setString(2, b);
        ps.setString(3, c);
        ps.executeUpdate();
        ps.close();
        ps = conn.prepareStatement("select a,b, length(b), c, length(c) from PT1 where a = ?");
        ps.setInt(1, a);
        rs[0] = ps.executeQuery();
        conn.close();
    }

    public static void parameter2(int a, java.math.BigDecimal b, java.math.BigDecimal c, java.sql.ResultSet[] rs)
                                                                                                                 throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:default:connection");
        PreparedStatement ps = conn.prepareStatement("insert into PT1 values (?, ?, ?)");
        ps.setInt(1, a);
        ps.setString(2, b.toString());
        ps.setString(3, c.toString());
        ps.executeUpdate();
        ps.close();
        ps = conn.prepareStatement("select a,b,c from PT1 where a = ?");
        ps.setInt(1, a);
        rs[0] = ps.executeQuery();
        conn.close();
    }

    public static void retrieveDynamicResults(int number, ResultSet[] rs1, ResultSet[] rs2, ResultSet[] rs3,
                                              ResultSet[] rs4) throws SQLException {
        Connection c = DriverManager.getConnection("jdbc:default:connection");
        if (number > 0) {
            rs1[0] = c.createStatement().executeQuery("VALUES(1)");
        }
        if (number > 1) {
            rs2[0] = c.createStatement().executeQuery("VALUES(1)");
        }
        if (number > 2) {
            rs3[0] = c.createStatement().executeQuery("VALUES(1)");
        }
        if (number > 3) {
            rs4[0] = c.createStatement().executeQuery("VALUES(1)");
        }
        c.close();
    }

    public static void outparams1(int[] p1, int p2) {

        p1[0] = p2 * 2;
    }

}

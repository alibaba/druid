package com.alibaba.druid.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlParameterizedOutputVisitor;

public class OnlineSQLTest extends TestCase {

    private String url      = "jdbc:mysql://10.20.129.146/dragoon_v25monitordb_online";
    private String user     = "dragoon";
    private String password = "dragoon";

    public void test_list_sql() throws Exception {
        //reset();
        Connection conn = DriverManager.getConnection(url, user, password);

        int count = 0;
        String sql = "SELECT id, value FROM m_sql_const WHERE flag IS NULL LIMIT 1";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            int id = rs.getInt(1);
            String value = rs.getString(2);
            System.out.println(value);
            System.out.println();
            try {
                validate(id, value);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            count++;
        }
        rs.close();
        stmt.close();

        System.out.println("COUNT : " + count);

        conn.close();
    }
    
    void reset() throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, password);

        String sql = "UPDATE m_sql_const SET flag = NULL, value2 = NULL";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.executeUpdate();
        stmt.close();

        conn.close();
    }

    void update(int id, String value2) throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, password);

        String sql = "UPDATE m_sql_const SET flag = 1, value2 = ? WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, value2);
        stmt.setInt(2, id);
        stmt.executeUpdate();
        stmt.close();

        conn.close();
    }

    void validate(int id, String sql) throws Exception {
        sql = sql.trim();
        boolean sqlFlag = false;
        String lowerSql = sql.toLowerCase();
        if (lowerSql.startsWith("insert") || lowerSql.startsWith("select") || lowerSql.startsWith("upate") || lowerSql.startsWith("delete") || lowerSql.startsWith("create") || lowerSql.startsWith("drop")) {
            sqlFlag = true;
        }
        
        if (!sqlFlag) {
            update(id, sql);
            return;
        }

        try {
            MySqlStatementParser parser = new MySqlStatementParser(sql);
            List<SQLStatement> statementList = parser.parseStatementList();
            SQLStatement statemen = statementList.get(0);

            Assert.assertEquals(1, statementList.size());

            StringBuilder out = new StringBuilder();
            MySqlParameterizedOutputVisitor visitor = new MySqlParameterizedOutputVisitor(out);
            statemen.accept(visitor);

            update(id, out.toString());
            System.out.println(sql);
            System.out.println(out.toString());
        } catch (Exception e) {
            throw e;
        }
    }
}

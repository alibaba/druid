package com.alibaba.druid.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
        Connection conn = DriverManager.getConnection(url, user, password);

        int count = 0;
        String sql = "SELECT id, value FROM m_sql_const";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            String value = rs.getString(2);
            System.out.println(value);
            System.out.println();
            try {
                validate(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
            count++;
        }
        rs.close();
        stmt.close();

        System.out.println("COUNT : " + count);

        conn.close();
    }

    void validate(String sql) throws Exception {

        try {
            MySqlStatementParser parser = new MySqlStatementParser(sql);
            List<SQLStatement> statementList = parser.parseStatementList();
            SQLStatement statemen = statementList.get(0);

            Assert.assertEquals(1, statementList.size());

            StringBuilder out = new StringBuilder();
            MySqlParameterizedOutputVisitor visitor = new MySqlParameterizedOutputVisitor(out);
            statemen.accept(visitor);

            System.out.println(sql);
            System.out.println(out.toString());
        } catch (Exception e) {
            throw e;
        }
    }
}

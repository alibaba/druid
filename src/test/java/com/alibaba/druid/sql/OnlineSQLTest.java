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
package com.alibaba.druid.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlParameterizedOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleParameterizedOutputVisitor;

public class OnlineSQLTest extends TestCase {

    private String url      = "jdbc:mysql://a.b.c.d/dragoon_v25monitordb_online";
    private String user     = "dragoon";
    private String password = "dragoon";

    public void test_list_sql() throws Exception {
        // reset();

        // 这些中文括号
        // update(7216, "", 4);
        // update(7223, "", 4);
        // update(8387, "", 4);

        // 语法错误
        // update(17018, "", 4); //alarm_type&?
        // update(17841, "", 4); //alarm_type&?
        // update(17845, "", 4); //alarm_type&?
        // update(18247, "", 4); //alarm_type&?
        // update(19469, "", 4); //alarm_type&?
        update(19730, "", 4); // alarm_type&?
        update(20164, "", 4); // alarm_type&?
        update(20386, "", 4); // alarm_type&?
        update(20440, "", 4); // alarm_type&?
        update(21208, "", 4); // alarm_type&?

        // IBATIS NAME
        update(18035, "", 4); // alarm_type&?

        Connection conn = DriverManager.getConnection(url, user, password);

        int count = 0;
        String sql = "SELECT id, value FROM m_sql_const WHERE flag IS NULL LIMIT 100";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            int id = rs.getInt(1);
            String value = rs.getString(2);

            if (value.indexOf('（') != -1) {
                update(id, "", 4);
                continue;
            }

            System.out.println(value);
            System.out.println();
            try {
                validateOracle(id, value);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("id : " + id);
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

    void update(int id, String value2, int flag) throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, password);

        String sql = "UPDATE m_sql_const SET flag = ?, value2 = ? WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, flag);
        stmt.setString(2, value2);
        stmt.setInt(3, id);
        stmt.executeUpdate();
        stmt.close();

        conn.close();
    }

    void validate(int id, String sql) throws Exception {
        sql = sql.trim();
        boolean sqlFlag = false;
        String lowerSql = sql.toLowerCase();
        if (lowerSql.startsWith("insert") || lowerSql.startsWith("select") || lowerSql.startsWith("upate")
            || lowerSql.startsWith("delete") || lowerSql.startsWith("create") || lowerSql.startsWith("drop")) {
            sqlFlag = true;
        }

        if (!sqlFlag) {
            update(id, sql, 2);
            return;
        }

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        StringBuilder out = new StringBuilder();
        MySqlParameterizedOutputVisitor visitor = new MySqlParameterizedOutputVisitor(out);
        statemen.accept(visitor);

        update(id, out.toString(), 1);
        System.out.println(sql);
        System.out.println(out.toString());
    }

    void validateOracle(int id, String sql) throws Exception {
        sql = sql.trim();
        boolean sqlFlag = false;
        String lowerSql = sql.toLowerCase();
        if (lowerSql.startsWith("insert") || lowerSql.startsWith("select") || lowerSql.startsWith("upate")
            || lowerSql.startsWith("delete") || lowerSql.startsWith("create") || lowerSql.startsWith("drop")) {
            sqlFlag = true;
        }

        if (!sqlFlag) {
            update(id, sql, 2);
            return;
        }

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        StringBuilder out = new StringBuilder();
        OracleParameterizedOutputVisitor visitor = new OracleParameterizedOutputVisitor(out);
        statemen.accept(visitor);

        update(id, out.toString(), 1);
        System.out.println(sql);
        System.out.println(out.toString());
    }
}

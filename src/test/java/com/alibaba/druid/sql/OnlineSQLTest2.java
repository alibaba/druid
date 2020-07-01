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
package com.alibaba.druid.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

public class OnlineSQLTest2 extends TestCase {

    private String url      = "jdbc:mysql://a.b.c.d/dragoon_v25_monitordb_test";
    private String user     = "dragoon";
    private String password = "dragoon";

    public void test_list_sql() throws Exception {

        Connection conn = DriverManager.getConnection(url, user, password);

        int count = 0;
        String sql = "SELECT id, value FROM m_sql_const LIMIT 100";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            int id = rs.getInt(1);
            String value = rs.getString(2);

            if (value.indexOf('（') != -1) {
                continue;
            }

            boolean sqlFlag = false;
            String lowerSql = value.toLowerCase();
            if (lowerSql.startsWith("insert") || lowerSql.startsWith("select") || lowerSql.startsWith("upate")
                || lowerSql.startsWith("delete") || lowerSql.startsWith("create") || lowerSql.startsWith("drop")) {
                sqlFlag = true;
            }

            if (!sqlFlag) {
                continue;
            }

            System.out.println(value);
            mysqlStat(id, lowerSql);
            System.out.println();
            count++;
        }
        rs.close();
        stmt.close();

        System.out.println("COUNT : " + count);

        conn.close();
    }

    void mysqlStat(int id, String sql) throws Exception {
        sql = sql.trim();
        boolean sqlFlag = false;
        String lowerSql = sql.toLowerCase();
        if (lowerSql.startsWith("insert") || lowerSql.startsWith("select") || lowerSql.startsWith("upate")
            || lowerSql.startsWith("delete") || lowerSql.startsWith("create") || lowerSql.startsWith("drop")) {
            sqlFlag = true;
        }

        if (!sqlFlag) {
            return;
        }

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
    }
}

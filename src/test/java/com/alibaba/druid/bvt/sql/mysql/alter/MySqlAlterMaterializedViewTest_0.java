/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql.alter;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;
import junit.framework.TestCase;

public class MySqlAlterMaterializedViewTest_0 extends TestCase {

    public void test_alter_first() throws Exception {
        String sql = "ALTER MATERIALIZED VIEW view_name\n" +
                "REFRESH COMPLETE ON DEMAND\n" +
                "ENABLE QUERY REWRITE";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        
        assertEquals("ALTER MATERIALIZED VIEW view_name\n" +
                "REFRESH COMPLETE ON DEMAND\n" +
                "ENABLE QUERY REWRITE", SQLUtils.toMySqlString(stmt));
    }

    public void test_alter_0() throws Exception {
        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(
                "ALTER MATERIALIZED VIEW myview \n" +
                        "DISABLE QUERY REWRITE");

        assertEquals("ALTER MATERIALIZED VIEW myview\n" +
                "DISABLE QUERY REWRITE", SQLUtils.toMySqlString(stmt));
    }

    public void test_alter_1() throws Exception {
        String sql = "ALTER MATERIALIZED VIEW myview \n" +
                "REFRESH FAST ON DEMAND \n" +
                "DISABLE QUERY REWRITE";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        assertEquals("ALTER MATERIALIZED VIEW myview\n" +
                "REFRESH FAST ON DEMAND\n" +
                "DISABLE QUERY REWRITE", SQLUtils.toMySqlString(stmt));
    }

    public void test_alter_2() throws Exception {
        String sql = "ALTER MATERIALIZED VIEW view_name\n" +
                "REFRESH COMPLETE ON DEMAND";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        assertEquals("ALTER MATERIALIZED VIEW view_name\n" +
                "REFRESH COMPLETE ON DEMAND", SQLUtils.toMySqlString(stmt));
    }

    public void test_alter_3() throws Exception {
        String sql = "ALTER MATERIALIZED VIEW view_name\n" +
                "REFRESH COMPLETE ON DEMAND " +
                "START WITH '2020-08-20 14:50:00'\n" +
                "NEXT current_date() + INTERVAL 15 DAY";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        assertEquals("ALTER MATERIALIZED VIEW view_name\n" +
                "REFRESH COMPLETE ON DEMAND\n" +
                "START WITH '2020-08-20 14:50:00' NEXT current_date() + INTERVAL 15 DAY", SQLUtils.toMySqlString(stmt));
    }

    public void test_alter_4() throws Exception {
        String sql = "ALTER MATERIALIZED VIEW view_name\n" +
                "REFRESH COMPLETE ON DEMAND " +
                "START WITH now()\n" +
                "NEXT DATE_ADD(now(), INTERVAL 1 MINUTE)";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        assertEquals("ALTER MATERIALIZED VIEW view_name\n" +
                "REFRESH COMPLETE ON DEMAND\n" +
                "START WITH now() NEXT DATE_ADD(now(), INTERVAL 1 MINUTE)", SQLUtils.toMySqlString(stmt));
    }

    public void test_alter_5() throws Exception {
        String sql = "ALTER MATERIALIZED VIEW view_name\n" +
                "REFRESH COMPLETE ON COMMIT";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        assertEquals("ALTER MATERIALIZED VIEW view_name\n" +
                "REFRESH COMPLETE ON COMMIT", SQLUtils.toMySqlString(stmt));
    }

    public void test_alter_6() throws Exception {
        String sql = "ALTER MATERIALIZED VIEW view_name\n" +
                "REFRESH FAST ON COMMIT\n";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        assertEquals("ALTER MATERIALIZED VIEW view_name\n" +
                "REFRESH FAST ON COMMIT", SQLUtils.toMySqlString(stmt));
    }

    public void test_alter_7() throws Exception {
        String sql = "ALTER MATERIALIZED VIEW view_name\n" +
                "REFRESH COMPLETE ON OVERWRITE\n";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        assertEquals("ALTER MATERIALIZED VIEW view_name\n" +
                "REFRESH COMPLETE ON OVERWRITE", SQLUtils.toMySqlString(stmt));
    }

    public void test_alter_all() {
        ok("ALTER MATERIALIZED VIEW mymv \n" +
                "REFRESH NEXT current_date() + INTERVAL 15 DAY\n" +
                "DISABLE QUERY REWRITE;",
                "ALTER MATERIALIZED VIEW mymv\n" +
                        "REFRESH NEXT current_date() + INTERVAL 15 DAY\n" +
                        "DISABLE QUERY REWRITE;");

        ok("ALTER MATERIALIZED VIEW mymv \n" +
                        "REFRESH FAST ON DEMAND\n" +
                        "DISABLE QUERY REWRITE;",
                "ALTER MATERIALIZED VIEW mymv\n" +
                        "REFRESH FAST ON DEMAND\n" +
                        "DISABLE QUERY REWRITE;");

        ok("ALTER MATERIALIZED VIEW mymv \n" +
                        "REFRESH COMPLETE\n" +
                        "ENABLE QUERY REWRITE;",
                "ALTER MATERIALIZED VIEW mymv\n" +
                        "REFRESH COMPLETE\n" +
                        "ENABLE QUERY REWRITE;");

        ok("ALTER MATERIALIZED VIEW mymv \n" +
                        "REFRESH COMPLETE ON COMMIT\n" +
                        "ENABLE QUERY REWRITE;",
                "ALTER MATERIALIZED VIEW mymv\n" +
                        "REFRESH COMPLETE ON COMMIT\n" +
                        "ENABLE QUERY REWRITE;");

        ok("ALTER MATERIALIZED VIEW mymv \n" +
                        "REFRESH FAST ON OVERWRITE\n" +
                        "DISABLE QUERY REWRITE;",
                "ALTER MATERIALIZED VIEW mymv\n" +
                        "REFRESH FAST ON OVERWRITE\n" +
                        "DISABLE QUERY REWRITE;");

    }

    public void test_bad_case() {
        String sql = "ALTER MATERIALIZED VIEW mymv";

        failed(sql);
        String sql2 = "ALTER MATERIALIZED VIEW mymv refresh";

        failed(sql2);

        String sql3 = "ALTER MATERIALIZED VIEW mymv refresh start with";
        failed(sql3);

        String sql4 = "ALTER MATERIALIZED VIEW mymv refresh start with now () next";
        failed(sql4);

        String sql5 = "ALTER MATERIALIZED VIEW mymv " +
                " REFRESH COMPLETE" +
                " ENABLE";
        failed(sql5);

        String sql6 = "ALTER MATERIALIZED VIEW mymv " +
                " REFRESH COMPLETE" +
                " DISABLE";
        failed(sql6);

        String sql7 = "ALTER MATERIALIZED VIEW mymv " +
                " REFRESH COMPLETE " +
                "ENABLE QUERY";
        failed(sql7);

    }

    public void failed(String sql) {
        try {
            SQLUtils.parseSingleMysqlStatement(sql);
            fail();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void ok(String sql, String expectedSql) {
        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);
        System.out.println(stmt.toString());
        assertEquals(expectedSql, stmt.toString());
    }


}

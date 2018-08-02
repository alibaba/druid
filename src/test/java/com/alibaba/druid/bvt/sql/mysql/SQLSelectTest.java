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
package com.alibaba.druid.bvt.sql.mysql;

import java.util.List;

import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class SQLSelectTest extends TestCase {

    public void test_select() throws Exception {
        String sql = "SELECT ALL FID FROM T1;SELECT DISTINCT FID FROM T1;SELECT DISTINCTROW FID FROM T1;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        output(stmtList);
    }

    public void test_select_1() throws Exception {
        String sql = "SELECT HIGH_PRIORITY STRAIGHT_JOIN SQL_SMALL_RESULT SQL_BIG_RESULT SQL_BUFFER_RESULT FID FROM T1;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        output(stmtList);
    }

    public void test_select_2() throws Exception {
        String sql = "SELECT SQL_CACHE FID FROM T1;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        output(stmtList);
    }

    public void test_select_3() throws Exception {
        String sql = "SELECT SQL_NO_CACHE FID FROM T1;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        output(stmtList);
    }

    public void test_select_4() throws Exception {
        String sql = "SELECT SQL_CALC_FOUND_ROWS FID FROM T1;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        output(stmtList);
    }

    public void test_select_5() throws Exception {
        String sql = "SELECT 1 + 1;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        output(stmtList);
    }

    public void test_select_6() throws Exception {
        String sql = "SELECT CONCAT(last_name,', ',first_name) AS full_name FROM mytable ORDER BY full_name;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        output(stmtList);
    }
    
    public void test_select_7() throws Exception {
        String sql = "select * from ((select * from test1) UNION (select * from test2) UNION (select * from test3)) where t1='温高铁';";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        output(stmtList);
    }

    private String output(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();
        for (SQLStatement stmt : stmtList) {
            stmt.accept(new MySqlOutputVisitor(out));
//            System.out.println(";");
//            System.out.println();
        }
        return out.toString();
    }
}

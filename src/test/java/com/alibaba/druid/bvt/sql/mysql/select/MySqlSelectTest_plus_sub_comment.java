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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.ParserException;

import java.util.List;

public class MySqlSelectTest_plus_sub_comment extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select  1 -+2";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT 1 - +2", stmt.toString());
    }
    public void test_1() throws Exception {
        String sql = "select  1 ---2 ";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT 1 - --2", stmt.toString());
    }
    public void test_1_1() throws Exception {
        String sql = "select  1 --- 2 ";

        try {
            MySqlStatementParser parser = new MySqlStatementParser(sql);
            List<SQLStatement> statementList = parser.parseStatementList();
            SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof ParserException);
        }
    }
    public void test_3() throws Exception {
        String sql = "select  1 --+1";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT 1 - -(+1)", stmt.toString());
    }

    public void test_4() throws Exception {
        String sql = "select  1 ---++1 ";

        try {
            MySqlStatementParser parser = new MySqlStatementParser(sql);
            List<SQLStatement> statementList = parser.parseStatementList();
            SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
            assertEquals("SELECT 1 - -(-(+(+1)))", stmt.toString());
        } catch (Exception e) {
            assertTrue(e instanceof ParserException);
        }
    }

    public void test_5() throws Exception {
        String sql = "select  1 ---++--1 ";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT 1 - -(-(+(+(--1))))", stmt.toString());
    }

    public void test_6() throws Exception {
        String sql = "select  1 ---++-- 1 ";

        try {
            MySqlStatementParser parser = new MySqlStatementParser(sql);
            List<SQLStatement> statementList = parser.parseStatementList();
            SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof ParserException);
        }
    }
    public void test_7() throws Exception {
        String sql = "select  1 --+- 1";

            MySqlStatementParser parser = new MySqlStatementParser(sql);
            List<SQLStatement> statementList = parser.parseStatementList();
            SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
            assertEquals("SELECT 1 - -(+-1)", stmt.toString());
    }

    public void test_8() throws Exception {
        String sql = "select  1--1";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT 1 - -1", stmt.toString());
    }

    public void test_10() throws Exception {
        String sql = "select max(id)-- min(id) from test_tablesl";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT max(id)", stmt.toString());
    }

    public void test_11() throws Exception {
        String sql = "select max(id)--min(id) from test_tablesl";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT max(id) - -min(id)\n" + "FROM test_tablesl", stmt.toString());
    }

    public void test_12() throws Exception {
        String sql = "select max(id) -- min(id) from test_tablesl";

        MySqlStatementParser parser = new MySqlStatementParser(sql);

        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT max(id)", stmt.toString());
    }

    public void test_13() throws Exception {

        String sql = "select max(id) --- min(id) from test_tablesl";

        try {
            MySqlStatementParser parser = new MySqlStatementParser(sql);
            List<SQLStatement> statementList = parser.parseStatementList();
            SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof ParserException);
        }
    }

//    public void test_14() throws Exception {
//        String sql = "select max(id) ---min(id) from test_tablesl";
//
//        MySqlStatementParser parser = new MySqlStatementParser(sql);
//        List<SQLStatement> statementList = parser.parseStatementList();
//        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
//        assertEquals("SELECT MAX(id) - -(-MIN(id))\n" + "FROM test_tablesl", stmt.toString());
//    }

    public void test_15() throws Exception {
        String sql = "select max(id) --+-- min(id) from test_tablesl";

        try {
            MySqlStatementParser parser = new MySqlStatementParser(sql);
            List<SQLStatement> statementList = parser.parseStatementList();
            SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof ParserException);
        }
    }

    public void test_16() throws Exception {
        String sql = "select max(id)  --+--  min(id) from test_tablesl";

        try {
            MySqlStatementParser parser = new MySqlStatementParser(sql);
            List<SQLStatement> statementList = parser.parseStatementList();
            SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof ParserException);
        }
    }

    public void test_18() throws Exception {
        String sql = "SELECT * FROM mp_Sites WHERE SiteID = -1 OR -1 = -1 -- ORDER BY SiteID LIMIT 1";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT *\n" + "FROM mp_Sites\n" + "WHERE SiteID = -1\n" + "\tOR -1 = -1", stmt.toString());
    }

    public void test_19() throws Exception {
        String sql = "-- comments\n"
                     + "SELECT * FROM mp_Sites WHERE SiteID = -1 OR -1 = -1 -- ORDER BY SiteID LIMIT 1";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("-- comments\nSELECT *\n" + "FROM mp_Sites\n" + "WHERE SiteID = -1\n" + "\tOR -1 = -1", stmt.toString());
    }

    public void test_20() throws Exception {
        String sql = "-- comments";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(0, statementList.size());
    }

    public void test_21() throws Exception {
        String sql = "--";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(0, statementList.size());
    }

    public void test_22() throws Exception {
        String sql = "--\n";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(0, statementList.size());
    }
}

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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.List;


public class MySqlSelectTest_229_interval extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select DATE_ADD(utc_date(), INTERVAL -'5' DAY)";

//        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);

        assertEquals("SELECT DATE_ADD(utc_date(), INTERVAL -'5' DAY)", stmt.toString());

        assertEquals("select DATE_ADD(utc_date(), interval -'5' day)", stmt.clone().toLowerCaseString());
    }

    public void test_1() throws Exception {
        String sql = "select DATE_ADD(utc_date(), INTERVAL +'5' DAY)";

//        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);

        assertEquals("SELECT DATE_ADD(utc_date(), INTERVAL +5 DAY)", stmt.toString());

        assertEquals("select DATE_ADD(utc_date(), interval +5 day)", stmt.clone().toLowerCaseString());
    }
}
/*
 * Copyright 1999-2023 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql.show;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserFeature;

import java.util.List;

public class MySqlShowTest_33_rule_status extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "SHOW RULE STATUS";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.TDDLHint);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        // assertTrue(visitor.getTables().containsKey(new TableStat.Name("mytable")));

        assertEquals("SHOW RULE STATUS", stmt.toString());
        assertEquals("show rule status", stmt.toLowerCaseString());
    }

    public void test_1() throws Exception {
        String sql = "SHOW RULE VERSION STATUS";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.TDDLHint);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        // assertTrue(visitor.getTables().containsKey(new TableStat.Name("mytable")));

        assertEquals("SHOW RULE VERSION STATUS", stmt.toString());
        assertEquals("show rule version status", stmt.toLowerCaseString());
    }

    public void test_2() throws Exception {
        String sql = "SHOW RULE FULL STATUS";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.TDDLHint);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        // assertTrue(visitor.getTables().containsKey(new TableStat.Name("mytable")));

        assertEquals("SHOW RULE FULL STATUS", stmt.toString());
        assertEquals("show rule full status", stmt.toLowerCaseString());
    }

    public void test_3() throws Exception {
        String sql = "SHOW RULE FULL STATUS where GMT_MODIFIED_MANAGER > \"2018-03-29 13:53:18\" order by VERSION "
                + "desc limit 1,2";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.TDDLHint);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        // assertTrue(visitor.getTables().containsKey(new TableStat.Name("mytable")));

        assertEquals("SHOW RULE FULL STATUS WHERE GMT_MODIFIED_MANAGER > '2018-03-29 13:53:18' ORDER BY VERSION "
                + "DESC LIMIT 1, 2", stmt.toString());
        assertEquals("show rule full status where GMT_MODIFIED_MANAGER > '2018-03-29 13:53:18' order by VERSION "
                + "desc limit 1, 2", stmt.toLowerCaseString());
    }
}

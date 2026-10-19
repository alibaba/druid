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
package com.alibaba.druid.bvt.sql.oracle.alter;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OracleAlterSessionTest extends OracleTest {
    @Test
    public void test_0() throws Exception {
        String sql = "alter session set current_schema=retl";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());
        assertEquals("ALTER SESSION SET current_schema = retl", statemen.toString());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());

//        assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees@remote")));

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());

//        assertTrue(visitor.getColumns().contains(new TableStat.Column("bonuses", "employee_id")));
    }

    @Test
    public void test_nls_date_format() {
        assertAlterSessionOutput("NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI:SS'");
    }

    @Test
    public void test_current_schema() {
        assertAlterSessionOutput("CURRENT_SCHEMA = retl");
    }

    @Test
    public void test_multiple_items() {
        assertAlterSessionOutput("NLS_DATE_FORMAT = 'YYYY-MM-DD', CURRENT_SCHEMA = retl");
    }

    @Test
    public void test_plsql_assignment() {
        SQLStatement statement = SQLUtils.parseSingleStatement("BEGIN v_count := 1; END;", DbType.oracle);
        String expected = "BEGIN\n\tv_count := 1;\nEND;";
        assertEquals(expected, statement.toString());
        assertEquals(expected, SQLUtils.parseSingleStatement(expected, DbType.oracle).toString());
    }

    private void assertAlterSessionOutput(String items) {
        SQLStatement statement = SQLUtils.parseSingleStatement("alter session set " + items, DbType.oracle);
        String expected = "ALTER SESSION SET " + items;
        assertEquals(expected, statement.toString());
        assertEquals("alter session set " + items, SQLUtils.toSQLString(statement, DbType.oracle, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
        assertEquals(expected, SQLUtils.parseSingleStatement(expected, DbType.oracle).toString());
    }
}

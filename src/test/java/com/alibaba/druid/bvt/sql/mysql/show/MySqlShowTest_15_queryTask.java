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
package com.alibaba.druid.bvt.sql.mysql.show;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBigIntExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLShowQueryTaskStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

import java.util.List;

public class MySqlShowTest_15_queryTask extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SHOW QUERY_TASK";

        SQLStatement stmt = SQLUtils.parseStatements(sql, DbType.mysql).get(0);
        
        String result = SQLUtils.toMySqlString(stmt);
        assertEquals("SHOW QUERY_TASK", result);

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.mysql);
        stmt.accept(visitor);

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

    }

    public void test_1() throws Exception {
        String sql = "SHOW QUERY_TASK where name > 1 order by name desc limit 1";
        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.TDDLHint);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLShowQueryTaskStatement stmt = (SQLShowQueryTaskStatement)statementList.get(0);

        String result = SQLUtils.toMySqlString(stmt);
        assertEquals("SHOW QUERY_TASK\n"
                     + "WHERE name > 1\n"
                     + "ORDER BY name DESC\n"
                     + "LIMIT 1", result);

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.mysql);
        stmt.accept(visitor);
        assertTrue(statementList.size() == 1);
        assertEquals(0, visitor.getTables().size());
        assertEquals(1, visitor.getColumns().size());
        assertEquals(1, visitor.getConditions().size());

        stmt.replace(stmt.getWhere(),new SQLBinaryOpExpr(new SQLIdentifierExpr("a"),
                                                                       SQLBinaryOperator.Equality,
                                                                       new SQLBigIntExpr(1L)));

        stmt.getOrderBy().replace(stmt.getOrderBy().getItems().get(0).getExpr(), new SQLIdentifierExpr("a"));

        assertEquals("SHOW QUERY_TASK\n" + "WHERE a = BIGINT '1'\n" + "ORDER BY a DESC\n" + "LIMIT 1", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "SHOW FULL QUERY_TASK";

        SQLStatement stmt = SQLUtils.parseStatements(sql, DbType.mysql).get(0);

        String result = SQLUtils.toMySqlString(stmt);
        assertEquals("SHOW FULL QUERY_TASK", result);

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.mysql);
        stmt.accept(visitor);

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

    }

    public void test_3() throws Exception {
        String sql = "SHOW QUERY_TASK for 'userxxx' ";

        SQLStatement stmt = SQLUtils.parseStatements(sql, DbType.mysql).get(0);

        String result = SQLUtils.toMySqlString(stmt);
        assertEquals("SHOW QUERY_TASK\n" +
                "FOR 'userxxx'", result);

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.mysql);
        stmt.accept(visitor);

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

    }
}

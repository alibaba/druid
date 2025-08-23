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
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import static org.junit.Assert.*;

import java.util.List;

public class MySqlSelectTest_16 extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "select a from t where not a>1 and not b<1";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;

        SQLSelect select = selectStmt.getSelect();
        assertNotNull(select.getQuery());
        MySqlSelectQueryBlock queryBlock = (MySqlSelectQueryBlock) select.getQuery();
        assertNull(queryBlock.getOrderBy());

        {
            SQLExpr where = queryBlock.getWhere();
            assertTrue(where instanceof SQLBinaryOpExpr);
            SQLBinaryOpExpr binaryWhere = (SQLBinaryOpExpr) where;
            assertEquals(binaryWhere.getOperator(), SQLBinaryOperator.BooleanAnd);

            assertTrue(binaryWhere.getLeft() instanceof SQLNotExpr);
            assertTrue(binaryWhere.getRight() instanceof SQLNotExpr);
        }


        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(2, visitor.getColumns().size());
        assertEquals(2, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("t")));

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("SELECT a\n" +
                        "FROM t\n" +
                        "WHERE NOT a > 1\n" +
                        "\tAND NOT b < 1", //
                output);
    }
}

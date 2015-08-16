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
package com.alibaba.druid.bvt.sql.oracle;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;

public class LargeOrTest extends TestCase {

    public void test_largeOr() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("SELECT 1 FROM T WHERE ID = ?");
        for (int i = 0; i < 10000; ++i) {
            buf.append(" OR ID = ?");
        }
        String sql = buf.toString();
        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);
        SQLSelectQueryBlock select = (SQLSelectQueryBlock) stmt.getSelect().getQuery();
        SQLBinaryOpExpr where = (SQLBinaryOpExpr) select.getWhere();
        SQLBinaryOpExpr last = (SQLBinaryOpExpr) where.getRight();
        Assert.assertEquals(SQLBinaryOperator.Equality, last.getOperator());
    }

    public void test_largeAnd() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("SELECT 1 FROM T WHERE ID = ?");
        for (int i = 0; i < 10000; ++i) {
            buf.append(" AND ID = ?");
        }
        String sql = buf.toString();
        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);
        SQLSelectQueryBlock select = (SQLSelectQueryBlock) stmt.getSelect().getQuery();
        SQLBinaryOpExpr where = (SQLBinaryOpExpr) select.getWhere();
        SQLBinaryOpExpr last = (SQLBinaryOpExpr) where.getRight();
        Assert.assertEquals(SQLBinaryOperator.Equality, last.getOperator());
    }
}

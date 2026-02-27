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

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.parser.SQLExprParser;
import junit.framework.TestCase;
import org.junit.Assert;

public class SQLExprParserTest extends TestCase {
    public void test_binary() throws Exception {
        SQLExprParser exprParser = new SQLExprParser("AGE > 5");
        SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) exprParser.expr();

        Assert.assertEquals(SQLBinaryOperator.GreaterThan, binaryOpExpr.getOperator());

        SQLIdentifierExpr left = (SQLIdentifierExpr) binaryOpExpr.getLeft();
        SQLIntegerExpr right = (SQLIntegerExpr) binaryOpExpr.getRight();

        Assert.assertEquals("AGE", left.getName());
        Assert.assertEquals(5, right.getNumber().intValue());
    }

    public void test_primary_case() throws Exception {
        SQLExprParser exprParser = new SQLExprParser("CASE WHEN 1 = 1 THEN 1 ELSE 0 END");
        Assert.assertTrue(exprParser.primary() instanceof SQLCaseExpr);
    }

    public void test_primary_not_parenthesized() throws Exception {
        SQLExprParser exprParser = new SQLExprParser("NOT (1 = 1)");
        Assert.assertTrue(exprParser.primary() instanceof SQLNotExpr);
    }

    public void test_primary_negative_integer() throws Exception {
        SQLExprParser exprParser = new SQLExprParser("-1");
        SQLIntegerExpr expr = (SQLIntegerExpr) exprParser.primary();
        Assert.assertEquals(-1, expr.getNumber().intValue());
    }

    public void test_primary_positive_unary() throws Exception {
        SQLExprParser exprParser = new SQLExprParser("+1");
        SQLUnaryExpr expr = (SQLUnaryExpr) exprParser.primary();
        Assert.assertEquals(SQLUnaryOperator.Plus, expr.getOperator());
    }

    public void test_primary_variant_question() throws Exception {
        SQLExprParser exprParser = new SQLExprParser("?");
        Assert.assertTrue(exprParser.primary() instanceof SQLVariantRefExpr);
    }
}

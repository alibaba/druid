/*
 * Copyright 1999-2019 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.mysql;

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLUnaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryOperator;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlExprParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 * @author liuzonghao
 * @email ludo.arnk@gmail.com
 * @date 2019/06/04 下午7:38
 */
public class MySqlExprParserTest extends TestCase {
    public void test_likeBinaryAnd() throws Exception {
        MySqlExprParser exprParser = new MySqlExprParser("name LIKE BINARY CONCAT(CONCAT('%', 'dfsd'), '%') AND type != 40");
        SQLBinaryOpExpr expr = (SQLBinaryOpExpr)exprParser.expr();
        Assert.assertEquals(SQLBinaryOperator.BooleanAnd, expr.getOperator());

        StringBuilder out = new StringBuilder();
        MySqlOutputVisitor outputVisitor = new MySqlOutputVisitor(out);
        outputVisitor.visit(expr);
        Assert.assertEquals("name LIKE BINARY CONCAT(CONCAT('%', 'dfsd'), '%')\n" +
                "AND type != 40", out.toString());
    }

    public void test_likeBinary() throws Exception {
        MySqlExprParser exprParser = new MySqlExprParser("name LIKE BINARY CONCAT(CONCAT('%', 'dfsd'), '%')");
        SQLBinaryOpExpr expr = (SQLBinaryOpExpr)exprParser.expr();
        Assert.assertEquals(SQLBinaryOperator.Like, expr.getOperator());
        SQLUnaryExpr unaryExpr = (SQLUnaryExpr)expr.getRight();
        Assert.assertEquals(SQLUnaryOperator.BINARY, unaryExpr.getOperator());

        StringBuilder out = new StringBuilder();
        MySqlOutputVisitor outputVisitor = new MySqlOutputVisitor(out);
        outputVisitor.visit(unaryExpr);
        Assert.assertEquals("BINARY CONCAT(CONCAT('%', 'dfsd'), '%')", out.toString());
    }
}

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
package com.alibaba.druid.sql;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.parser.SQLExprParser;


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
}

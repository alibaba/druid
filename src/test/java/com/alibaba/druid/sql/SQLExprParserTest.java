package com.alibaba.druid.sql;

import junit.framework.Assert;
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

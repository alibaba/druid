package com.alibaba.druid.bvt.sql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;

public class EqualTest_not_2 extends TestCase {

    public void test_exits() throws Exception {
        String sql = "NOT A=1 AND NOT B=1";
        SQLNotExpr exprA, exprB;
        {
            OracleExprParser parser = new OracleExprParser(sql);
            SQLBinaryOpExpr binaryEpr = (SQLBinaryOpExpr) parser.expr();
            exprA = (SQLNotExpr) binaryEpr.getLeft();
            exprB = (SQLNotExpr) binaryEpr.getRight();
        }

        Assert.assertNotNull(exprA);
        Assert.assertNotNull(exprB);
    }
}

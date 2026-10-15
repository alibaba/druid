package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EqualTest_not_2 {
    @Test
    public void test_exits() throws Exception {
        String sql = "NOT A=1 AND NOT B=1";
        SQLNotExpr exprA, exprB;
        {
            OracleExprParser parser = new OracleExprParser(sql);
            SQLBinaryOpExpr binaryEpr = (SQLBinaryOpExpr) parser.expr();
            exprA = (SQLNotExpr) binaryEpr.getLeft();
            exprB = (SQLNotExpr) binaryEpr.getRight();
        }

        assertNotNull(exprA);
        assertNotNull(exprB);
    }
}

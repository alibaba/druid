package com.alibaba.druid.bvt.sql.postgresql.expr;


import org.junit.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGBoxExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGPointExpr;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;


public class BoxTest extends PGTest {
    public void test_timestamp() throws Exception {
        String sql = "box '((0,0),(1,1))' + point '(2.0,0)'";
        PGExprParser parser = new PGExprParser(sql);
        SQLBinaryOpExpr binaryExpr = (SQLBinaryOpExpr) parser.expr();
        
        PGBoxExpr box = (PGBoxExpr) binaryExpr.getLeft();
        PGPointExpr point = (PGPointExpr) binaryExpr.getRight();
        Assert.assertEquals("BOX '((0,0),(1,1))' + POINT '(2.0,0)'", binaryExpr.toString());
    }
}

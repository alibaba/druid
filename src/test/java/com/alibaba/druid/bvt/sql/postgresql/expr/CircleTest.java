package com.alibaba.druid.bvt.sql.postgresql.expr;



import org.junit.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGCircleExpr;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;


public class CircleTest extends PGTest {
    public void test_timestamp() throws Exception {
        String sql = "circle '((0,0),2)'";
        PGExprParser parser = new PGExprParser(sql);
        PGCircleExpr expr = (PGCircleExpr) parser.expr();
        Assert.assertEquals("circle '((0,0),2)'", expr.toString());

    }
}

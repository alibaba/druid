package com.alibaba.druid.bvt.sql.postgresql.expr;



import org.junit.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.expr.SQLUnaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryOperator;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;


public class PolygonTest extends PGTest {
    public void test_timestamp() throws Exception {
        String sql = "# '((1,0),(0,1),(-1,0))'";
        PGExprParser parser = new PGExprParser(sql);
        SQLUnaryExpr unaryExpr = (SQLUnaryExpr) parser.expr();
        Assert.assertEquals(SQLUnaryOperator.Pound, unaryExpr.getOperator());

    }
}

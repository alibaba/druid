package com.alibaba.druid.bvt.sql.postgresql.expr;



import org.junit.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGPolygonExpr;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;


public class PolygonTest2 extends PGTest {
    public void test_timestamp() throws Exception {
        String sql = "polygon '((0,0),(1,1))'";
        PGExprParser parser = new PGExprParser(sql);
        PGPolygonExpr expr = (PGPolygonExpr) parser.expr();
        Assert.assertEquals("polygon '((0,0),(1,1))'", expr.toString());

    }
}

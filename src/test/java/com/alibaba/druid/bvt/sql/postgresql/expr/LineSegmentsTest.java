package com.alibaba.druid.bvt.sql.postgresql.expr;


import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGLineSegmentsExpr;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;
import org.junit.Assert;


public class LineSegmentsTest extends PGTest {
    public void test_timestamp() throws Exception {
        String sql = "lseg '((-1,0),(1,0))'";
        PGExprParser parser = new PGExprParser(sql);
        PGLineSegmentsExpr expr = (PGLineSegmentsExpr) parser.expr();
        
        Assert.assertEquals("lseg '((-1,0),(1,0))'", expr.toString());
    }
}

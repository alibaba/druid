package com.alibaba.druid.bvt.sql.postgresql.expr;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGExtractExpr;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;


public class ExtractTest_Year extends PGTest {
    public void test_timestamp() throws Exception {
        String sql = "EXTRACT(YEAR FROM TIMESTAMP '2001-02-16 20:38:40')";
        PGExprParser parser = new PGExprParser(sql);
        PGExtractExpr expr = (PGExtractExpr) parser.expr();
        System.out.println(expr.toString());
    }
}

package com.alibaba.druid.bvt.sql.postgresql.datatypes;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGTypeCastExpr;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;

public class BitsTest extends PGTest {

    public void test_timestamp() throws Exception {
        String sql = "44::bit(10)";
        PGExprParser parser = new PGExprParser(sql);
        PGTypeCastExpr expr = (PGTypeCastExpr) parser.expr();
        System.out.println(expr.toString());
    }

}

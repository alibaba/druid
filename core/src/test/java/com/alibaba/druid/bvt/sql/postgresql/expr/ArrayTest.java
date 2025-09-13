package com.alibaba.druid.bvt.sql.postgresql.expr;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;

public class ArrayTest extends PGTest {
    public void test_timestamp() throws Exception {
        String sql = "cast(xx as varchar(12)[])";
        PGExprParser parser = new PGExprParser(sql);
        SQLExpr expr = parser.expr();
        assertEquals("CAST(xx AS varchar(12)[])", SQLUtils.toPGString(expr));
    }
}

package com.alibaba.druid.bvt.sql.postgresql.datatypes;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.expr.SQLTimestampExpr;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;


public class TimestampTest extends PGTest {
    public void test_timestamp() throws Exception {
        String sql = "TIMESTAMP '2004-10-19 10:23:54+02'";
        PGExprParser parser = new PGExprParser(sql);
        SQLTimestampExpr expr = (SQLTimestampExpr) parser.expr();
        System.out.println(expr.toString());
    }
    
    public void test_timestamp_with_timezone() throws Exception {
        String sql = "TIMESTAMP WITH TIME ZONE '2004-10-19 10:23:54+02'";
        PGExprParser parser = new PGExprParser(sql);
        SQLTimestampExpr expr = (SQLTimestampExpr) parser.expr();
        System.out.println(expr.toString());
    }
}

package com.alibaba.druid.bvt.sql.postgresql.expr;



import org.junit.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.expr.SQLUnaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryOperator;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGMacAddrExpr;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;


public class MacAddrTest extends PGTest {
    public void test_timestamp() throws Exception {
        String sql = "macaddr '12:34:56:78:90:ab'";
        PGExprParser parser = new PGExprParser(sql);
        PGMacAddrExpr expr = (PGMacAddrExpr) parser.expr();
        Assert.assertEquals("macaddr '12:34:56:78:90:ab'", expr.toString());

    }
}

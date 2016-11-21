package com.alibaba.druid.bvt.sql.postgresql.expr;



import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGMacAddrExpr;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;
import org.junit.Assert;


public class MacAddrTest extends PGTest {
    public void test_timestamp() throws Exception {
        String sql = "macaddr '12:34:56:78:90:ab'";
        PGExprParser parser = new PGExprParser(sql);
        PGMacAddrExpr expr = (PGMacAddrExpr) parser.expr();
        Assert.assertEquals("macaddr '12:34:56:78:90:ab'", expr.toString());

    }
}

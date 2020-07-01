package com.alibaba.druid.bvt.sql.postgresql.datatypes;

import org.junit.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.expr.SQLBinaryExpr;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;
import com.alibaba.druid.util.JdbcConstants;

public class BitStringTest extends PGTest {

    public void test_timestamp() throws Exception {
        String sql = "B'101'";
        PGExprParser parser = new PGExprParser(sql);
        SQLBinaryExpr expr = (SQLBinaryExpr) parser.expr();
        Assert.assertEquals("B'101'", SQLUtils.toSQLString(expr, JdbcConstants.POSTGRESQL));
    }

}

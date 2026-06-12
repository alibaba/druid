package com.alibaba.druid.bvt.sql.postgresql.expr;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGInetExpr;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InetTest extends PGTest {
    @Test
    public void test_timestamp() throws Exception {
        String sql = "inet '0.0.0.255'";
        PGExprParser parser = new PGExprParser(sql);
        PGInetExpr expr = (PGInetExpr) parser.expr();
        assertEquals("inet '0.0.0.255'", expr.toString());
    }
}

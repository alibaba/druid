package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.SQLUtils;
import static org.junit.*;
import org.junit.Test;

public class SQLMethodInvokeExprTest {

    @Test
    public void testMethodRemoveBrackets() {
        SQLMethodInvokeExpr methodInvokeExpr = new SQLMethodInvokeExpr("CURRENT_SCHEMA");
        assertEquals("CURRENT_SCHEMA()", SQLUtils.toSQLString(methodInvokeExpr));
        methodInvokeExpr.setRemoveBrackets(true);
        assertEquals("CURRENT_SCHEMA", SQLUtils.toSQLString(methodInvokeExpr));
    }
}

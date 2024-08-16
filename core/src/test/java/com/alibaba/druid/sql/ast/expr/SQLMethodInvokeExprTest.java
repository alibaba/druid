package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.Assert;
import org.junit.Test;

public class SQLMethodInvokeExprTest {

    @Test
    public void testMethodRemoveBrackets() {
        SQLMethodInvokeExpr methodInvokeExpr = new SQLMethodInvokeExpr("CURRENT_SCHEMA");
        Assert.assertEquals("CURRENT_SCHEMA()", SQLUtils.toSQLString(methodInvokeExpr));
        methodInvokeExpr.setRemoveBrackets(true);
        Assert.assertEquals("CURRENT_SCHEMA", SQLUtils.toSQLString(methodInvokeExpr));
    }
}

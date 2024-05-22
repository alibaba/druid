package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SQLExprStatementTest {

    @Test
    public void testDefaultConstructor() {
        SQLExpr sqlExpr = new SQLBinaryOpExpr();
        SQLExprStatement stmt = new SQLExprStatement();
        stmt.setExpr(sqlExpr);

        assertEquals(sqlExpr, stmt.getExpr());
        
        // Test parent relationship
        assertEquals(stmt, sqlExpr.getParent());
    }

    @Test
    public void testParameterizedConstructor() {
        SQLExpr sqlExpr = new SQLBinaryOpExpr();
        SQLExprStatement stmt = new SQLExprStatement(sqlExpr);
        stmt.setExpr(sqlExpr);

        assertEquals(sqlExpr, stmt.getExpr());
        
        // Test parent relationship
        assertEquals(stmt, sqlExpr.getParent());
    }
}

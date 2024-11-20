package com.alibaba.druid.bvt.sql.bigquery;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BigQueryNameTest {
    @Test
    public void name() {
        String str = "`a.b.c`";
        SQLExpr expr = SQLUtils.toSQLExpr(str, DbType.bigquery);
        assertTrue(expr instanceof SQLPropertyExpr);
        SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
        assertEquals("c", propertyExpr.getName());
    }

    @Test
    public void name1() {
        String str = "`a-x.b.c`";
        SQLExpr expr = SQLUtils.toSQLExpr(str, DbType.bigquery);
        assertTrue(expr instanceof SQLPropertyExpr);
        SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
        assertEquals("c", propertyExpr.getName());
        assertTrue(propertyExpr.getOwner() instanceof SQLPropertyExpr);
        SQLExpr ownerOwner = ((SQLPropertyExpr) propertyExpr.getOwner()).getOwner();
        assertTrue(ownerOwner instanceof SQLIdentifierExpr);
        assertEquals("a-x", ((SQLIdentifierExpr) ownerOwner).getName());
        assertEquals("`a-x`.b.c", SQLUtils.toSQLString(expr, DbType.bigquery));
    }
}

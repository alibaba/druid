package com.alibaba.druid.bvt.sql.bigquery;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLMergeStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.util.FnvHash;
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
        assertEquals(FnvHash.fnv1a_64_lower("a-x"), ((SQLIdentifierExpr) ownerOwner).hashCode64());
    }

    @Test
    public void name2() {
        String str = "`a-x`.b.c";
        SQLExpr expr = SQLUtils.toSQLExpr(str, DbType.bigquery, SQLParserFeature.IgnoreNameQuotes);
        assertTrue(expr instanceof SQLPropertyExpr);
        SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
        assertEquals("c", propertyExpr.getName());
        assertTrue(propertyExpr.getOwner() instanceof SQLPropertyExpr);
        SQLExpr ownerOwner = ((SQLPropertyExpr) propertyExpr.getOwner()).getOwner();
        assertTrue(ownerOwner instanceof SQLIdentifierExpr);
        assertEquals("a-x", ((SQLIdentifierExpr) ownerOwner).getName());
        assertEquals("`a-x`.b.c", SQLUtils.toSQLString(expr, DbType.bigquery));
        assertEquals(FnvHash.fnv1a_64_lower("a-x"), ((SQLIdentifierExpr) ownerOwner).hashCode64());
    }

    @Test
    public void name3() {
        String str = "`a-x`.b.c";
        SQLExpr expr = SQLUtils.toSQLExpr(str, DbType.bigquery, SQLParserFeature.IgnoreNameQuotes);
        assertTrue(expr instanceof SQLPropertyExpr);
        SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
        assertEquals("c", propertyExpr.getName());
        assertTrue(propertyExpr.getOwner() instanceof SQLPropertyExpr);
        SQLExpr ownerOwner = ((SQLPropertyExpr) propertyExpr.getOwner()).getOwner();
        assertTrue(ownerOwner instanceof SQLIdentifierExpr);
        assertEquals("a-x", ((SQLIdentifierExpr) ownerOwner).getName());
        assertEquals("`a-x`.b.c", SQLUtils.toSQLString(expr, DbType.bigquery));
        assertEquals(FnvHash.fnv1a_64_lower("a-x"), ((SQLIdentifierExpr) ownerOwner).hashCode64());
    }

    @Test
    public void nameView() {
        String str = "create view `a-x.b.c` as select 1;";
        SQLCreateViewStatement stmt = (SQLCreateViewStatement) SQLUtils.parseSingleStatement(str, DbType.bigquery);
        SQLExpr expr = stmt.getName();
        assertTrue(expr instanceof SQLPropertyExpr);
        SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
        assertEquals("c", propertyExpr.getName());
        assertTrue(propertyExpr.getOwner() instanceof SQLPropertyExpr);
        SQLExpr ownerOwner = ((SQLPropertyExpr) propertyExpr.getOwner()).getOwner();
        assertTrue(ownerOwner instanceof SQLIdentifierExpr);
        assertEquals("a-x", ((SQLIdentifierExpr) ownerOwner).getName());
        assertEquals("`a-x`.b.c", SQLUtils.toSQLString(expr, DbType.bigquery));
    }

    @Test
    public void nameView1() {
        String str = "create view `a-x.b.c` as select `interval` from t;";
        SQLCreateViewStatement stmt = (SQLCreateViewStatement) SQLUtils.parseSingleStatement(
                str, DbType.bigquery,
                SQLParserFeature.IgnoreNameQuotes);
        SQLExpr expr = stmt.getName();
        assertTrue(expr instanceof SQLPropertyExpr);
        SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
        assertEquals("c", propertyExpr.getName());
        assertTrue(propertyExpr.getOwner() instanceof SQLPropertyExpr);
        SQLExpr ownerOwner = ((SQLPropertyExpr) propertyExpr.getOwner()).getOwner();
        assertTrue(ownerOwner instanceof SQLIdentifierExpr);
        assertEquals("a-x", ((SQLIdentifierExpr) ownerOwner).getName());
        assertEquals("`a-x`.b.c", SQLUtils.toSQLString(expr, DbType.bigquery));
    }

    @Test
    public void select1() {
        String str = "select * from a-x.b.c";
        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleStatement(
                str, DbType.bigquery,
                SQLParserFeature.IgnoreNameQuotes);
        SQLExpr expr = ((SQLExprTableSource) stmt.getSelect().getQueryBlock().getFrom()).getExpr();
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

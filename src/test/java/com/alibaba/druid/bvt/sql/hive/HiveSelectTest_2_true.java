package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBooleanExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class HiveSelectTest_2_true extends TestCase {
    public void test_select() throws Exception {
        String sql = "SELECT * FROM customers where isvalid = true or isxx = false";//
        assertEquals("SELECT *\n" +
                "FROM customers\n" +
                "WHERE isvalid = true\n" +
                "\tOR isxx = false", SQLUtils.formatHive(sql));
        assertEquals("select *\n" +
                "from customers\n" +
                "where isvalid = true\n" +
                "\tor isxx = false", SQLUtils.formatHive(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);


//        System.out.println("Tables : " + visitor.getTables());
//      System.out.println("fields : " + visitor.getColumns());
//      System.out.println("coditions : " + visitor.getConditions());
//      System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());
        assertEquals(2, visitor.getConditions().size());

        assertTrue(visitor.containsColumn("customers", "isvalid"));
        assertTrue(visitor.containsColumn("customers", "isxx"));
        assertTrue(visitor.containsColumn("customers", "*"));

        SQLBinaryOpExpr where = (SQLBinaryOpExpr) ((SQLSelectStatement) stmt).getSelect().getQueryBlock().getWhere();
        SQLBinaryOpExpr left = (SQLBinaryOpExpr) where.getLeft();
        SQLBinaryOpExpr right = (SQLBinaryOpExpr) where.getRight();
        assertEquals(SQLBooleanExpr.class, left.getRight().getClass());
        assertEquals(SQLBooleanExpr.class, right.getRight().getClass());
    }
}

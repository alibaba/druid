package com.alibaba.druid.bvt.sql.bigquery;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStructDataType;
import com.alibaba.druid.sql.ast.expr.SQLArrayExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnnestTableSource;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UnnestTest {
    @Test
    public void test_0() throws Exception {
        String sql = "SELECT 1\n" +
                "FROM t1 pt\n" +
                "LEFT OUTER JOIN t2 cl\n" +
                "ON pt.driver_id = cl.driver_id\n" +
                "    AND pt.order_id = cl.order_no\n" +
                "LEFT OUTER JOIN UNNEST(v_list) v_type\n" +
                "LEFT OUTER JOIN UNNEST(r_list) d_id";
        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, DbType.bigquery);
        SQLTableSource from = stmt.getSelect().getQueryBlock().getFrom();
        assertTrue(from instanceof SQLJoinTableSource);
        SQLJoinTableSource join = (SQLJoinTableSource) from;
        assertTrue(join.getRight() instanceof SQLUnnestTableSource);

        SQLJoinTableSource left = (SQLJoinTableSource) join.getLeft();
        assertTrue(left.getRight() instanceof SQLUnnestTableSource);
    }

    @Test
    public void test_1() throws Exception {
        String sql = "SELECT *\n" +
                "FROM UNNEST(\n" +
                "  ARRAY<\n" +
                "    STRUCT<\n" +
                "      x INT64,\n" +
                "      y STRING,\n" +
                "      z STRUCT<a INT64, b INT64>>>[\n" +
                "        (1, 'foo', (10, 11)),\n" +
                "        (3, 'bar', (20, 21))]);";

        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, DbType.bigquery);

        SQLTableSource from = stmt.getSelect().getQueryBlock().getFrom();
        assertTrue(from instanceof SQLUnnestTableSource);

        SQLUnnestTableSource unnest = (SQLUnnestTableSource) from;
        assertEquals(1, unnest.getItems().size());

        assertTrue(unnest.getItems().get(0) instanceof SQLArrayExpr);
        SQLArrayExpr array = (SQLArrayExpr) unnest.getItems().get(0);

        assertEquals(2, array.getValues().size());
        assertTrue(array.getDataType() instanceof SQLStructDataType);
    }

    @Test
    public void test_2() throws Exception {
        String sql = "SELECT * FROM UNNEST ([10,20,30]) as numbers WITH OFFSET;";

        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, DbType.bigquery);

        SQLTableSource from = stmt.getSelect().getQueryBlock().getFrom();
        assertTrue(from instanceof SQLUnnestTableSource);

        SQLUnnestTableSource unnest = (SQLUnnestTableSource) from;
        assertTrue(unnest.isWithOffset());

        assertTrue(Objects.isNull(unnest.getOffsetAs()));
    }

    @Test
    public void test_3() throws Exception {
        String sql = "SELECT * FROM UNNEST ([10,20,30]) as numbers WITH OFFSET AS ROW_INDEX;";

        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, DbType.bigquery);

        SQLTableSource from = stmt.getSelect().getQueryBlock().getFrom();
        assertTrue(from instanceof SQLUnnestTableSource);

        SQLUnnestTableSource unnest = (SQLUnnestTableSource) from;
        assertTrue(unnest.isWithOffset());
        assertTrue(Objects.nonNull(unnest.getOffsetAs()));
        assertTrue(unnest.getOffsetAs() instanceof SQLIdentifierExpr);
        assertEquals("ROW_INDEX", unnest.getOffsetAs().toString());
    }

    @Test
    public void test_4() throws Exception {
        String sql = "SELECT *\n" +
                "FROM UNNEST([10, 20, 30]) AS numbers WITH OFFSET ";

        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, DbType.bigquery);
        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, DbType.bigquery);
        visitor.config(VisitorFeature.OutputUCase);
        stmt.accept(visitor);

        assertEquals(sql, out.toString());
    }
}

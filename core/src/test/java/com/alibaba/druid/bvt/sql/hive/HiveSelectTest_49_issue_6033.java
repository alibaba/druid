package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for issue #6033:
 * HiveSQL collect_list and collect_set functions should be recognized as aggregate functions.
 *
 * Before the fix, collect_list/collect_set were parsed as SQLMethodInvokeExpr (regular method calls)
 * instead of SQLAggregateExpr, which meant they were not tracked in schema statistics as aggregates.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6033">Issue #6033</a>
 */
public class HiveSelectTest_49_issue_6033 {
    /**
     * Verify that collect_list is parsed as an aggregate function (SQLAggregateExpr),
     * not as a regular method call (SQLMethodInvokeExpr).
     */
    @Test
    public void test_collect_list_isAggregate() {
        String sql = "SELECT collect_list(col1) FROM t";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        String normalized = stmt.toString();
        assertTrue(normalized.toUpperCase().contains("COLLECT_LIST(col1)".toUpperCase()),
                "Output should contain COLLECT_LIST(col1), but was: " + normalized);

        // Verify the expression is an SQLAggregateExpr, not SQLMethodInvokeExpr
        List<SQLAggregateExpr> aggregates = stmt.getSelect().getQueryBlock()
                .getSelectList()
                .stream()
                .filter(item -> item.getExpr() instanceof SQLAggregateExpr)
                .map(item -> (SQLAggregateExpr) item.getExpr())
                .collect(java.util.stream.Collectors.toList());

        assertEquals(1, aggregates.size(), "Expected 1 aggregate expression");
        assertEquals("COLLECT_LIST", aggregates.get(0).getMethodName().toUpperCase());
    }

    /**
     * Verify that collect_set is parsed as an aggregate function.
     */
    @Test
    public void test_collect_set_isAggregate() {
        String sql = "SELECT collect_set(col1) FROM t";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        List<SQLAggregateExpr> aggregates = stmt.getSelect().getQueryBlock()
                .getSelectList()
                .stream()
                .filter(item -> item.getExpr() instanceof SQLAggregateExpr)
                .map(item -> (SQLAggregateExpr) item.getExpr())
                .collect(java.util.stream.Collectors.toList());

        assertEquals(1, aggregates.size(), "Expected 1 aggregate expression");
        assertEquals("COLLECT_SET", aggregates.get(0).getMethodName().toUpperCase());
    }

    /**
     * Verify that the issue's original SQL (nested collect_set with concat_ws and sort_array)
     * parses correctly and round-trips.
     */
    @Test
    public void test_issue6033_originalSQL() {
        String sql = "SELECT concat_ws(',', sort_array(collect_set" +
                "(IF(a IS NOT NULL AND b IS NOT NULL, concat(a, '_', b), NULL)))) AS c FROM t";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        // The collect_set should be an aggregate expr somewhere in the expression tree.
        // Verify it round-trips without error.
        String normalized = stmt.toString();
        assertNotNull(normalized);
        assertTrue(normalized.toUpperCase().contains("COLLECT_SET"),
                "Output should contain COLLECT_SET, but was: " + normalized);
    }

    /**
     * Verify that collect_list with GROUP BY works correctly and
     * schema statistics track the aggregate function.
     */
    @Test
    public void test_collect_list_with_groupBy() {
        String sql = "SELECT dept, collect_list(name) FROM employee GROUP BY dept";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

        // collect_list should appear in the aggregate functions collected by the visitor
        boolean foundCollectList = visitor.getAggregateFunctions()
                .stream()
                .anyMatch(f -> f.getMethodName().equalsIgnoreCase("COLLECT_LIST"));
        assertTrue(foundCollectList,
                "SchemaStatVisitor should track COLLECT_LIST as an aggregate function");

        // Should have columns: dept, name (from employee table)
        assertEquals(2, visitor.getColumns().size());
    }

    /**
     * Verify that collect_set with GROUP BY works correctly and
     * schema statistics track the aggregate function.
     */
    @Test
    public void test_collect_set_with_groupBy() {
        String sql = "SELECT dept, collect_set(salary) FROM employee GROUP BY dept";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

        boolean foundCollectSet = visitor.getAggregateFunctions()
                .stream()
                .anyMatch(f -> f.getMethodName().equalsIgnoreCase("COLLECT_SET"));
        assertTrue(foundCollectSet,
                "SchemaStatVisitor should track COLLECT_SET as an aggregate function");
    }
}

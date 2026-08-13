package com.alibaba.druid.bvt.sql.odps.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ODPS 侧 {@code GROUP BY ROLLUP(...)} / {@code CUBE(...)} 后跟更多分组项的回归测试。
 *
 * <p>该能力原由 ODPS 专属特性 {@code RewriteGroupByCubeRollupToFunction} 开启，现已上移到基础
 * {@code SQLSelectParser#parseGroupBy} 对所有方言统一处理（见 issue #6662）。本测试锁定 ODPS 在
 * 尾随逗号场景下的函数式重写结果（withRollUp/withCube/paren 清零），防止该归一化回退。
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6662">issue #6662</a>
 */
public class Issue6662 {
    private static SQLSelectGroupByClause groupByOf(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.odps);
        assertEquals(1, stmts.size());
        return ((SQLSelectQueryBlock) ((SQLSelectStatement) stmts.get(0)).getSelect().getQuery()).getGroupBy();
    }

    @Test
    public void odps_rollup_followed_by_column_rewritten_to_function_form() {
        SQLSelectGroupByClause groupBy = groupByOf("SELECT count(*) FROM t GROUP BY ROLLUP(a), b");

        assertEquals(2, groupBy.getItems().size());
        SQLMethodInvokeExpr rollup = assertInstanceOf(SQLMethodInvokeExpr.class, groupBy.getItems().get(0));
        assertEquals("ROLLUP", rollup.getMethodName());

        // 重写为函数式分组项后，withRollUp/withCube/paren 必须清零，保证与标准 SQL 方言 AST 一致
        assertFalse(groupBy.isWithRollUp(), "withRollUp 应清零");
        assertFalse(groupBy.isParen(), "paren 应清零");
    }

    @Test
    public void odps_rollup_followed_by_column_round_trips() {
        String sql = "SELECT count(*) FROM t GROUP BY ROLLUP(a), b";
        String out = SQLUtils.toSQLString(SQLUtils.parseStatements(sql, DbType.odps).get(0), DbType.odps);
        assertTrue(out.contains("ROLLUP"), () -> "输出应保留 ROLLUP: " + out);
        assertTrue(out.contains("b"), () -> "输出应保留尾部列 b: " + out);
    }

    @Test
    public void odps_cube_followed_by_column_rewritten_to_function_form() {
        SQLSelectGroupByClause groupBy = groupByOf("SELECT count(*) FROM t GROUP BY CUBE(a), b");

        assertEquals(2, groupBy.getItems().size());
        SQLMethodInvokeExpr cube = assertInstanceOf(SQLMethodInvokeExpr.class, groupBy.getItems().get(0));
        assertEquals("CUBE", cube.getMethodName());
        assertFalse(groupBy.isWithCube(), "withCube 应清零");
        assertFalse(groupBy.isParen(), "paren 应清零");
    }

    @Test
    public void odps_multiple_grouping_functions_mixed() {
        SQLSelectGroupByClause groupBy = groupByOf("SELECT count(*) FROM t GROUP BY ROLLUP(a), CUBE(b), c");

        assertEquals(3, groupBy.getItems().size());
        assertEquals("ROLLUP", ((SQLMethodInvokeExpr) groupBy.getItems().get(0)).getMethodName());
        assertEquals("CUBE", ((SQLMethodInvokeExpr) groupBy.getItems().get(1)).getMethodName());
    }

    @Test
    public void odps_plain_rollup_without_trailing_items_keeps_wrap_form() {
        // GROUP BY ROLLUP(a, b)（无后续分组项）保持 wrap 形式，行为不变
        SQLSelectGroupByClause groupBy = groupByOf("SELECT count(*) FROM t GROUP BY ROLLUP(a, b)");

        assertEquals(2, groupBy.getItems().size());
        assertTrue(groupBy.isWithRollUp(), "无尾随项时保持 withRollUp");
        assertTrue(groupBy.isParen(), "无尾随项时保持 paren");
    }
}

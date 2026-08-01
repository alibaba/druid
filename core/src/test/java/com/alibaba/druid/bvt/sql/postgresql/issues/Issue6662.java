package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PostgreSQL {@code GROUP BY ROLLUP(...)} 后跟更多分组项（如 {@code GROUP BY ROLLUP(a), b}）的解析测试。
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6662">issue #6662</a>
 */
public class Issue6662 {
    private static List<SQLStatement> parsePg(String sql) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
        return parser.parseStatementList();
    }

    @Test
    public void rollup_followed_by_column_parses() {
        String sql = "SELECT count(*) FROM t GROUP BY ROLLUP(x), y";
        List<SQLStatement> stmts = parsePg(sql);
        assertEquals(1, stmts.size());

        SQLSelectGroupByClause groupBy = groupByOf(stmts.get(0));
        assertEquals(2, groupBy.getItems().size());

        SQLMethodInvokeExpr rollup = assertInstanceOf(SQLMethodInvokeExpr.class, groupBy.getItems().get(0));
        assertEquals("ROLLUP", rollup.getMethodName());
        assertEquals(1, rollup.getArguments().size());

        // ROLLUP 建模为函数式分组项，withRollUp/paren 为 false
        assertTrue(!groupBy.isWithRollUp() && !groupBy.isParen(),
                "ROLLUP 应建模为函数式分组项");

        String out = SQLUtils.toSQLString(stmts.get(0), DbType.postgresql);
        assertTrue(out.contains("ROLLUP"), () -> "输出应保留 ROLLUP: " + out);
        assertTrue(out.contains("y"), () -> "输出应保留尾部列 y: " + out);
    }

    @Test
    public void issue_minimal_repro_parses() {
        String sql = "SELECT dis_dept_code, COUNT(*)\n"
                + "FROM dwd_inp_fact_discharge_reg\n"
                + "GROUP BY rollup(dis_dept_code), dis_date";
        List<SQLStatement> stmts = parsePg(sql);
        assertEquals(1, stmts.size());

        SQLSelectGroupByClause groupBy = groupByOf(stmts.get(0));
        assertEquals(2, groupBy.getItems().size());
        assertEquals("ROLLUP",
                ((SQLMethodInvokeExpr) groupBy.getItems().get(0)).getMethodName());
    }

    @Test
    public void plain_rollup_without_trailing_items_unchanged() {
        // GROUP BY ROLLUP(a, b)（无后续分组项）解析为 withRollUp/paren 包装形式，含 a、b 两项
        String sql = "SELECT count(*) FROM t GROUP BY ROLLUP(a, b)";
        List<SQLStatement> stmts = parsePg(sql);
        assertEquals(1, stmts.size());
        SQLSelectGroupByClause groupBy = groupByOf(stmts.get(0));
        assertEquals(2, groupBy.getItems().size());
        assertTrue(groupBy.isWithRollUp(), "withRollUp 为 true");
        assertTrue(groupBy.isParen(), "paren 为 true");
    }

    @Test
    public void multiple_grouping_functions_mixed() {
        String sql = "SELECT count(*) FROM t GROUP BY ROLLUP(a), CUBE(b), c";
        List<SQLStatement> stmts = parsePg(sql);
        SQLSelectGroupByClause groupBy = groupByOf(stmts.get(0));
        assertEquals(3, groupBy.getItems().size());
        assertEquals("ROLLUP", ((SQLMethodInvokeExpr) groupBy.getItems().get(0)).getMethodName());
        assertEquals("CUBE", ((SQLMethodInvokeExpr) groupBy.getItems().get(1)).getMethodName());
    }

    @Test
    public void cube_followed_by_column_parses() {
        // CUBE 作为首个分组函数后跟逗号：覆盖 isWithCube() ? "CUBE" : "ROLLUP" 的 CUBE 分支
        String sql = "SELECT count(*) FROM t GROUP BY CUBE(a), b";
        List<SQLStatement> stmts = parsePg(sql);
        assertEquals(1, stmts.size());

        SQLSelectGroupByClause groupBy = groupByOf(stmts.get(0));
        assertEquals(2, groupBy.getItems().size());

        SQLMethodInvokeExpr cube = assertInstanceOf(SQLMethodInvokeExpr.class, groupBy.getItems().get(0));
        assertEquals("CUBE", cube.getMethodName());
        assertEquals(1, cube.getArguments().size());

        // CUBE 建模为函数式分组项后，withCube/paren 应清零，与 ROLLUP 路径保持一致
        assertTrue(!groupBy.isWithCube() && !groupBy.isParen(),
                "CUBE 应建模为函数式分组项");

        String out = SQLUtils.toSQLString(stmts.get(0), DbType.postgresql);
        assertTrue(out.contains("CUBE"), () -> "输出应保留 CUBE: " + out);
        assertTrue(out.contains("b"), () -> "输出应保留尾部列 b: " + out);
    }

    @Test
    public void missing_comma_between_items_still_rejected() {
        // 分组项之间缺少逗号的畸形 SQL 应报 ParserException
        String sql = "SELECT count(*) FROM t GROUP BY ROLLUP(a) b";
        assertThrows(ParserException.class, () -> parsePg(sql));
    }

    private static SQLSelectGroupByClause groupByOf(SQLStatement stmt) {
        SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock)
                ((SQLSelectStatement) stmt).getSelect().getQuery();
        SQLSelectGroupByClause groupBy = queryBlock.getGroupBy();
        assertTrue(groupBy != null, "应存在 GROUP BY 子句");
        return groupBy;
    }
}

package com.alibaba.druid.sql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SQLDeleteStatement.accept0 / getChildren skipped the {@code from} and
 * {@code using} children, so generic visitors that rely on the default
 * tree walk never reached identifiers inside
 * {@code DELETE ... FROM ...} or {@code DELETE ... USING ...}.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6563">#6563</a>
 */
public class Issue6563 {
    /** Collects every SQLIdentifierExpr name seen during a default tree walk. */
    private static class NameCollector extends SQLASTVisitorAdapter {
        final List<String> names = new ArrayList<>();

        @Override
        public boolean visit(SQLIdentifierExpr x) {
            names.add(x.getSimpleName());
            return true;
        }
    }

    @Test
    public void test_snowflake_delete_using_visited() {
        // Snowflake parser sets `using` on the base SQLDeleteStatement,
        // but the base accept0 used to skip it.
        // Reference table 't_using_only' ONLY from the USING clause so it
        // can only be reached if accept0 walks the using subtree.
        String sql = "DELETE FROM t1 USING t_using_only WHERE t1.id = 1";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.snowflake);
        SQLStatement stmt = parser.parseStatement();
        assertNotNull(((SQLDeleteStatement) stmt).getUsing());

        NameCollector collector = new NameCollector();
        stmt.accept(collector);
        assertTrue(collector.names.contains("t_using_only"),
                "USING-only table should be reachable via default tree walk, names=" + collector.names);
    }

    @Test
    public void test_db2_delete_from_visited() {
        // DB2 parser writes setFrom for the secondary FROM clause and uses base accept0.
        // Reference 't_from_only' ONLY in the secondary FROM so reaching it requires
        // walking the from subtree.
        String sql = "DELETE FROM t FROM t_from_only WHERE t.id = 1";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.db2);
        SQLStatement stmt = parser.parseStatement();
        assertNotNull(((SQLDeleteStatement) stmt).getFrom());

        NameCollector collector = new NameCollector();
        stmt.accept(collector);
        assertTrue(collector.names.contains("t_from_only"),
                "FROM-only table should be reachable via default tree walk, names=" + collector.names);
    }

    @Test
    public void test_getChildren_includes_from_and_using() {
        SQLDeleteStatement delete = new SQLDeleteStatement(DbType.mysql);
        delete.setTableSource(new SQLExprTableSource("t1"));
        delete.setFrom(new SQLExprTableSource("t_from"));
        delete.setUsing(new SQLExprTableSource("t_using"));

        List<SQLObject> children = delete.getChildren();
        assertTrue(children.contains(delete.getFrom()), "getChildren must include from");
        assertTrue(children.contains(delete.getUsing()), "getChildren must include using");
    }

    @Test
    public void test_setUsing_sets_parent() {
        SQLDeleteStatement delete = new SQLDeleteStatement(DbType.mysql);
        SQLExprTableSource using = new SQLExprTableSource("t_using");
        delete.setUsing(using);
        assertNotNull(using.getParent(), "setUsing must wire parent for tree walk");
        assertSame(delete, using.getParent());
    }
}

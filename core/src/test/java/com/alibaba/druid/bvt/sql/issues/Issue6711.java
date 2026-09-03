package com.alibaba.druid.bvt.sql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * A following INNER JOIN's INNER token must not be consumed as the alias of the
 * previous table source / join node. Before the fix, "X INNER JOIN Y ON ... INNER JOIN Z ON ..."
 * silently produced a corrupted AST: the first join node got alias "INNER", the second
 * join degraded to plain JOIN, and serialization emitted "... AS INNER JOIN ...".
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6711">Issue #6711</a>
 */
public class Issue6711 {

    private static SQLJoinTableSource parseJoin(DbType dbType, String sql) {
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, dbType);
        return (SQLJoinTableSource) ((SQLSelectStatement) stmt).getSelect().getQueryBlock().getFrom();
    }

    private static void assertJoinBoundary(DbType dbType, String sql) {
        SQLJoinTableSource outer = parseJoin(dbType, sql);
        assertNull(outer.getAlias(), dbType + " outer join alias should be null, sql:\n" + sql);

        if (outer.getLeft() instanceof SQLJoinTableSource) {
            SQLJoinTableSource left = (SQLJoinTableSource) outer.getLeft();
            assertNull(left.getAlias(), dbType + " left join alias should be null, sql:\n" + sql);
        }

        String output = SQLUtils.toSQLString(
                SQLUtils.parseSingleStatement(sql, dbType), dbType);
        assertFalse(output.contains("AS INNER"), dbType + " output should not contain 'AS INNER', output:\n" + output);

        // parse -> print -> parse -> print must be stable
        SQLStatement reparsed = SQLUtils.parseSingleStatement(output, dbType);
        assertEquals(output, SQLUtils.toSQLString(reparsed, dbType), dbType + " round-trip output mismatch, output:\n" + output);
    }

    @Test
    public void test_inner_join_followed_by_inner_join() {
        String sql = "SELECT c.id\n"
                + "FROM context_table c\n"
                + "INNER JOIN loan_table l ON c.id = l.id\n"
                + "INNER JOIN adjust_table a ON l.id = a.id";
        for (DbType dbType : new DbType[]{DbType.odps, DbType.mysql, DbType.postgresql}) {
            SQLJoinTableSource outer = parseJoin(dbType, sql);
            assertEquals(SQLJoinTableSource.JoinType.INNER_JOIN, outer.getJoinType(), dbType + " outer join type");
            SQLJoinTableSource left = (SQLJoinTableSource) outer.getLeft();
            assertEquals(SQLJoinTableSource.JoinType.INNER_JOIN, left.getJoinType(), dbType + " left join type");
            assertJoinBoundary(dbType, sql);
        }
    }

    @Test
    public void test_left_join_followed_by_inner_join() {
        String sql = "SELECT c.id\n"
                + "FROM context_table c\n"
                + "LEFT JOIN loan_table l ON c.id = l.id\n"
                + "INNER JOIN adjust_table a ON l.id = a.id";
        for (DbType dbType : new DbType[]{DbType.odps, DbType.mysql, DbType.postgresql}) {
            assertJoinBoundary(dbType, sql);
        }
    }

    @Test
    public void test_right_join_followed_by_inner_join() {
        String sql = "SELECT c.id\n"
                + "FROM context_table c\n"
                + "RIGHT JOIN loan_table l ON c.id = l.id\n"
                + "INNER JOIN adjust_table a ON l.id = a.id";
        for (DbType dbType : new DbType[]{DbType.odps, DbType.mysql, DbType.postgresql}) {
            assertJoinBoundary(dbType, sql);
        }
    }

    @Test
    public void test_full_join_followed_by_inner_join() {
        String sql = "SELECT c.id\n"
                + "FROM context_table c\n"
                + "FULL JOIN loan_table l ON c.id = l.id\n"
                + "INNER JOIN adjust_table a ON l.id = a.id";
        for (DbType dbType : new DbType[]{DbType.odps, DbType.postgresql}) {
            assertJoinBoundary(dbType, sql);
        }
    }

    @Test
    public void test_three_consecutive_inner_joins() {
        String sql = "SELECT a.id\n"
                + "FROM t1 a\n"
                + "INNER JOIN t2 b ON a.id = b.id\n"
                + "INNER JOIN t3 c ON b.id = c.id\n"
                + "INNER JOIN t4 d ON c.id = d.id";
        for (DbType dbType : new DbType[]{DbType.odps, DbType.mysql, DbType.postgresql}) {
            SQLJoinTableSource outer = parseJoin(dbType, sql);
            assertEquals(SQLJoinTableSource.JoinType.INNER_JOIN, outer.getJoinType(), dbType + " outer join type");
            assertNull(outer.getAlias(), dbType + " outer join alias");
            SQLJoinTableSource middle = (SQLJoinTableSource) outer.getLeft();
            assertEquals(SQLJoinTableSource.JoinType.INNER_JOIN, middle.getJoinType(), dbType + " middle join type");
            assertNull(middle.getAlias(), dbType + " middle join alias");
            assertJoinBoundary(dbType, sql);
        }
    }

    /**
     * A single INNER JOIN must keep parsing as INNER_JOIN (regression guard for the fix).
     */
    @Test
    public void test_single_inner_join() {
        String sql = "SELECT * FROM t1 INNER JOIN t2 ON t1.id = t2.id";
        SQLJoinTableSource join = parseJoin(DbType.mysql, sql);
        assertEquals(SQLJoinTableSource.JoinType.INNER_JOIN, join.getJoinType());
        assertNull(join.getLeft().getAlias());
        assertNull(join.getAlias());
    }
}

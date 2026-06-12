package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLGroupingSetExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLListExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

/**
 * Roundtrip tests for {@code GROUP BY ... [,] GROUPING SETS(...)} — the
 * presence or absence of the separator comma is semantically significant in
 * ODPS / Hive / Oracle and must be preserved through parse + reformat.
 *
 * Covers AST-level invariants ({@code equals} / {@code hashCode} / {@code clone}
 * propagating the {@code hasPrefixComma} flag) as well as roundtrip behaviour
 * across the dialects whose output visitors inherit from
 * {@link SQLASTOutputVisitor}.
 */
public class OdpsGroupingSetsCommaTest extends TestCase {
    private static final String COMMA_SQL =
            "SELECT a, b, COUNT(*) FROM data_sec.yidie_test "
                    + "GROUP BY a, GROUPING SETS((a, b), (a), (b), ())";
    private static final String NO_COMMA_SQL =
            "SELECT a, b, COUNT(*) FROM data_sec.yidie_test "
                    + "GROUP BY a GROUPING SETS((a, b), (a), (b), ())";
    private static final String GROUPING_ONLY_SQL =
            "SELECT a, b, COUNT(*) FROM t GROUP BY GROUPING SETS((a, b), (a), ())";

    private static String parseAndFormat(String sql, DbType db) {
        SQLObject stmt = new SQLStatementParser(sql, db).parseSelect();
        if (db == DbType.odps) {
            return SQLUtils.toOdpsString(stmt);
        } else if (db == DbType.hive) {
            return SQLUtils.toHiveString(stmt);
        } else if (db == DbType.oracle) {
            return SQLUtils.toOracleString(stmt);
        }
        return SQLUtils.toMySqlString(stmt);
    }

    // ---------- ODPS dialect ----------

    @Test
    public void test_odps_groupBy_comma_groupingSets() {
        String formatted = parseAndFormat(COMMA_SQL, DbType.odps);
        assertEquals(
                "SELECT a, b, COUNT(*)\n"
                        + "FROM data_sec.yidie_test\n"
                        + "GROUP BY a,\n"
                        + "\tGROUPING SETS ((a, b), (a), (b), ())",
                formatted);
    }

    @Test
    public void test_odps_groupBy_noComma_groupingSets() {
        String formatted = parseAndFormat(NO_COMMA_SQL, DbType.odps);
        assertEquals(
                "SELECT a, b, COUNT(*)\n"
                        + "FROM data_sec.yidie_test\n"
                        + "GROUP BY a\n"
                        + "\tGROUPING SETS ((a, b), (a), (b), ())",
                formatted);
    }

    @Test
    public void test_odps_groupingSets_only() {
        String formatted = parseAndFormat(GROUPING_ONLY_SQL, DbType.odps);
        assertEquals(
                "SELECT a, b, COUNT(*)\n"
                        + "FROM t\n"
                        + "GROUP BY GROUPING SETS ((a, b), (a), ())",
                formatted);
    }

    // ---------- Cross-dialect smoke roundtrip (visitor lives in base class) ----------

    @Test
    public void test_hive_groupBy_comma_groupingSets() {
        String formatted = parseAndFormat(COMMA_SQL, DbType.hive);
        assertTrue("hive expected comma before GROUPING SETS, got:\n" + formatted,
                formatted.contains("GROUP BY a,\n\tGROUPING SETS "));
    }

    @Test
    public void test_hive_groupBy_noComma_groupingSets() {
        String formatted = parseAndFormat(NO_COMMA_SQL, DbType.hive);
        assertTrue("hive expected no comma before GROUPING SETS, got:\n" + formatted,
                formatted.contains("GROUP BY a\n\tGROUPING SETS "));
        assertFalse("hive must not emit comma before GROUPING SETS, got:\n" + formatted,
                formatted.contains("a,\n\tGROUPING SETS "));
    }

    @Test
    public void test_oracle_groupBy_comma_groupingSets() {
        String formatted = parseAndFormat(COMMA_SQL, DbType.oracle);
        assertTrue("oracle expected comma before GROUPING SETS, got:\n" + formatted,
                formatted.contains("GROUP BY a,\n\tGROUPING SETS "));
    }

    @Test
    public void test_oracle_groupBy_noComma_groupingSets() {
        String formatted = parseAndFormat(NO_COMMA_SQL, DbType.oracle);
        assertTrue("oracle expected no comma before GROUPING SETS, got:\n" + formatted,
                formatted.contains("GROUP BY a\n\tGROUPING SETS "));
        assertFalse("oracle must not emit comma before GROUPING SETS, got:\n" + formatted,
                formatted.contains("a,\n\tGROUPING SETS "));
    }

    // ---------- AST invariants for hasPrefixComma ----------

    @Test
    public void test_ast_flag_default_isTrue() {
        SQLGroupingSetExpr expr = new SQLGroupingSetExpr();
        assertTrue("default hasPrefixComma should be true to preserve historical emit shape",
                expr.isHasPrefixComma());
    }

    @Test
    public void test_ast_clone_preservesFlag() {
        SQLGroupingSetExpr expr = new SQLGroupingSetExpr();
        expr.addParameter(new SQLIdentifierExpr("a"));
        expr.setHasPrefixComma(false);

        SQLGroupingSetExpr cloned = expr.clone();
        assertFalse("clone must propagate hasPrefixComma=false", cloned.isHasPrefixComma());

        expr.setHasPrefixComma(true);
        SQLGroupingSetExpr cloned2 = expr.clone();
        assertTrue("clone must propagate hasPrefixComma=true", cloned2.isHasPrefixComma());
    }

    @Test
    public void test_ast_equals_distinguishesFlag() {
        SQLGroupingSetExpr withComma = new SQLGroupingSetExpr();
        withComma.addParameter(new SQLIdentifierExpr("a"));
        SQLGroupingSetExpr withoutComma = new SQLGroupingSetExpr();
        withoutComma.addParameter(new SQLIdentifierExpr("a"));
        withoutComma.setHasPrefixComma(false);

        assertFalse("instances differing only in hasPrefixComma must not be equal",
                withComma.equals(withoutComma));
        assertFalse("hashCode should differ when hasPrefixComma differs",
                withComma.hashCode() == withoutComma.hashCode());
    }

    @Test
    public void test_ast_programmaticDefault_emitsComma() {
        // Build "GROUP BY x, GROUPING SETS((a))" by hand without setting the flag.
        SQLSelectGroupByClause clause = new SQLSelectGroupByClause();
        clause.addItem(new SQLIdentifierExpr("x"));

        SQLGroupingSetExpr gs = new SQLGroupingSetExpr();
        SQLListExpr list = new SQLListExpr();
        list.addItem(new SQLIdentifierExpr("a"));
        gs.addParameter(list);
        clause.addItem(gs);

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = new SQLASTOutputVisitor(out) {
            // anonymous; uses base behaviour
        };
        clause.accept(visitor);
        String s = out.toString();
        assertTrue("default-built AST should emit comma before GROUPING SETS, got: " + s,
                s.contains("x,\n\tGROUPING SETS ") || s.contains("x, GROUPING SETS ("));
    }

}

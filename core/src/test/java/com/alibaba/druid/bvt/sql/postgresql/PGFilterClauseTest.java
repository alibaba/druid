package com.alibaba.druid.bvt.sql.postgresql;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PGFilterClauseTest extends PGTest {
    public void test_filter_count() {
        String sql = "SELECT count(*) FILTER (WHERE x > 0) FROM t";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());

        String output = SQLUtils.toSQLString(stmtList, JdbcConstants.POSTGRESQL);
        assertTrue(output.contains("FILTER"));
        assertTrue(output.contains("WHERE"));
    }

    public void test_filter_sum() {
        String sql = "SELECT sum(amount) FILTER (WHERE status = 'active') FROM orders";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());

        String output = SQLUtils.toSQLString(stmtList, JdbcConstants.POSTGRESQL);
        assertTrue(output.contains("FILTER"));
        assertTrue(output.contains("WHERE"));
    }

    public void test_filter_multiple_aggregates() {
        String sql = "SELECT count(*) FILTER (WHERE type = 'A') AS count_a, count(*) FILTER (WHERE type = 'B') AS count_b FROM items";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());

        String output = SQLUtils.toSQLString(stmtList, JdbcConstants.POSTGRESQL);
        // Both FILTER clauses should be present
        int filterCount = output.split("FILTER").length - 1;
        assertEquals(2, filterCount);
    }

    public void test_filter_with_order_by_in_aggregate() {
        String sql = "SELECT department, array_agg(name ORDER BY name) FILTER (WHERE salary > 50000) FROM employees GROUP BY department";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());

        String output = SQLUtils.toSQLString(stmtList, JdbcConstants.POSTGRESQL);
        assertTrue(output.contains("FILTER"));
        assertTrue(output.contains("ORDER BY"));
        assertTrue(output.contains("GROUP BY"));
    }
}

package com.alibaba.druid.bvt.sql.clickhouse.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ClickHouse CREATE TABLE engine clauses (PARTITION BY / ORDER BY / PRIMARY KEY) may appear in any
 * order, and PRIMARY KEY accepts a bare column (no parentheses).
 *
 * @see <a href="https://github.com/alibaba/druid/issues/4950">Issue #4950</a>
 */
public class Issue4950 {
    private static String rt(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.clickhouse);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), DbType.clickhouse).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_primary_key_before_order_by_bare_column() {
        String out = rt("CREATE TABLE t (api_log_id String, created_date DateTime) "
                + "ENGINE = ReplacingMergeTree(created_date) PARTITION BY toYYYYMM(created_date) "
                + "PRIMARY KEY api_log_id ORDER BY (api_log_id, created_date)");
        assertTrue(out.contains("PRIMARY KEY (api_log_id)"), out);
        assertTrue(out.contains("ORDER BY (api_log_id, created_date)"), out);
    }

    @Test
    public void test_order_by_before_primary_key() {
        String out = rt("CREATE TABLE t (id String) ENGINE = MergeTree() ORDER BY (id) PRIMARY KEY (id)");
        assertTrue(out.contains("ORDER BY (id)"), out);
        assertTrue(out.contains("PRIMARY KEY (id)"), out);
    }
}

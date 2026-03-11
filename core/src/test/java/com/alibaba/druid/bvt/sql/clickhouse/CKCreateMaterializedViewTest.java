package com.alibaba.druid.bvt.sql.clickhouse;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

import java.util.List;

public class CKCreateMaterializedViewTest extends TestCase {
    private final DbType dbType = DbType.clickhouse;

    public void test_basic() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mv_test TO dest_table AS SELECT id, name FROM source_table";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        String result = SQLUtils.toSQLString(stmtList.get(0), dbType);
        assertTrue(result.contains("TO dest_table"));
    }

    public void test_if_not_exists() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW IF NOT EXISTS mv_test TO dest_table AS SELECT * FROM source_table";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        String result = SQLUtils.toSQLString(stmtList.get(0), dbType);
        assertTrue(result.contains("IF NOT EXISTS"));
        assertTrue(result.contains("TO dest_table"));
    }

    public void test_with_engine() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mv_test ENGINE = MergeTree() ORDER BY id AS SELECT id, name FROM source_table";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        String result = SQLUtils.toSQLString(stmtList.get(0), dbType);
        assertTrue(result.contains("ENGINE = MergeTree()"));
        assertTrue(result.contains("ORDER BY"));
    }

    public void test_with_engine_partition() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mv_test ENGINE = MergeTree() PARTITION BY toYYYYMM(date) ORDER BY (id, date) AS SELECT id, date, count FROM source_table";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        String result = SQLUtils.toSQLString(stmtList.get(0), dbType);
        assertTrue(result.contains("ENGINE"));
        assertTrue(result.contains("PARTITION BY"));
        assertTrue(result.contains("ORDER BY"));
    }

    public void test_on_cluster() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mv_test ON CLUSTER my_cluster TO dest_table AS SELECT id, name FROM source_table";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        String result = SQLUtils.toSQLString(stmtList.get(0), dbType);
        assertTrue(result.contains("ON CLUSTER"));
        assertTrue(result.contains("TO dest_table"));
    }

    public void test_populate() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mv_test ENGINE = MergeTree() ORDER BY id POPULATE AS SELECT id, name FROM source_table";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        String result = SQLUtils.toSQLString(stmtList.get(0), dbType);
        assertTrue(result.contains("POPULATE"));
    }

    public void test_to_qualified() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mv_test TO mydb.dest_table AS SELECT * FROM source_table";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        String result = SQLUtils.toSQLString(stmtList.get(0), dbType);
        assertTrue(result.contains("TO mydb.dest_table"));
    }

    public void test_aggregating() throws Exception {
        String sql = "CREATE MATERIALIZED VIEW mv_daily_stats\n" +
                "ENGINE = SummingMergeTree()\n" +
                "ORDER BY (date, user_id)\n" +
                "AS SELECT\n" +
                "    toDate(timestamp) AS date,\n" +
                "    user_id,\n" +
                "    count() AS cnt,\n" +
                "    sum(amount) AS total\n" +
                "FROM events\n" +
                "GROUP BY date, user_id";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        String result = SQLUtils.toSQLString(stmtList.get(0), dbType);
        assertTrue(result.contains("ENGINE = SummingMergeTree()"));
        assertTrue(result.contains("ORDER BY"));
        assertTrue(result.contains("GROUP BY"));
    }
}

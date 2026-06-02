package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateMaterializedViewStatement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StarRocksCreateMaterializedViewTest {
    @Test
    public void testSimple() {
        String sql = "CREATE MATERIALIZED VIEW mv1\nAS\nSELECT *\nFROM t1";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertInstanceOf(StarRocksCreateMaterializedViewStatement.class, stmt);

        String output = SQLUtils.toSQLString(stmt, DbType.starrocks);
        assertTrue(output.toUpperCase().contains("CREATE MATERIALIZED VIEW"));
        assertTrue(output.toUpperCase().contains("AS"));
    }

    @Test
    public void testAsyncRefreshEvery() {
        String sql = "CREATE MATERIALIZED VIEW mv_daily\n"
                + "REFRESH ASYNC EVERY(INTERVAL 1 DAY)\n"
                + "AS\nSELECT dt, sum(amount) AS total\nFROM sales\nGROUP BY dt";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        StarRocksCreateMaterializedViewStatement mv = (StarRocksCreateMaterializedViewStatement) stmt;

        assertTrue(mv.isRefreshAsync());
        assertNotNull(mv.getRefreshEvery());

        String output = SQLUtils.toSQLString(stmt, DbType.starrocks);
        assertTrue(output.toUpperCase().contains("REFRESH ASYNC EVERY("));
    }

    @Test
    public void testManualRefresh() {
        String sql = "CREATE MATERIALIZED VIEW mv_report\n"
                + "REFRESH MANUAL\n"
                + "AS\nSELECT *\nFROM orders\nWHERE status = 'done'";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        StarRocksCreateMaterializedViewStatement mv = (StarRocksCreateMaterializedViewStatement) stmt;

        assertTrue(mv.isRefreshManual());
        assertFalse(mv.isRefreshAsync());

        String output = SQLUtils.toSQLString(stmt, DbType.starrocks);
        assertTrue(output.toUpperCase().contains("REFRESH MANUAL"));
    }

    @Test
    public void testIfNotExistsWithComment() {
        String sql = "CREATE MATERIALIZED VIEW IF NOT EXISTS mv_users\n"
                + "COMMENT 'User summary'\n"
                + "REFRESH ASYNC EVERY(INTERVAL 1 HOUR)\n"
                + "AS\nSELECT user_id, count(*) AS cnt\nFROM events\nGROUP BY user_id";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        StarRocksCreateMaterializedViewStatement mv = (StarRocksCreateMaterializedViewStatement) stmt;

        assertTrue(mv.isIfNotExists());
        assertNotNull(mv.getComment());

        String output = SQLUtils.toSQLString(stmt, DbType.starrocks);
        assertTrue(output.toUpperCase().contains("IF NOT EXISTS"));
        assertTrue(output.toUpperCase().contains("COMMENT"));
    }

    @Test
    public void testDeferredAsyncWithStartAndProperties() {
        String sql = "CREATE MATERIALIZED VIEW mv_hourly\n"
                + "REFRESH DEFERRED ASYNC START('2024-01-01 00:00:00') EVERY(INTERVAL 1 HOUR)\n"
                + "PROPERTIES (\n\t\"partition_ttl\" = \"2 MONTH\"\n)\n"
                + "AS\nSELECT hour, count(*) AS cnt\nFROM logs\nGROUP BY hour";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        StarRocksCreateMaterializedViewStatement mv = (StarRocksCreateMaterializedViewStatement) stmt;

        assertTrue(mv.isRefreshDeferred());
        assertTrue(mv.isRefreshAsync());
        assertNotNull(mv.getRefreshStart());
        assertNotNull(mv.getRefreshEvery());
        assertFalse(mv.getMvProperties().isEmpty());

        String output = SQLUtils.toSQLString(stmt, DbType.starrocks);
        assertTrue(output.toUpperCase().contains("DEFERRED ASYNC"));
        assertTrue(output.toUpperCase().contains("PROPERTIES"));
    }

    @Test
    public void testDistributedByAndOrderBy() {
        String sql = "CREATE MATERIALIZED VIEW mv_dist\n"
                + "DISTRIBUTED BY HASH (user_id)\n"
                + "ORDER BY dt\n"
                + "REFRESH ASYNC EVERY(INTERVAL 1 DAY)\n"
                + "AS\nSELECT dt, user_id, count(*) AS cnt\nFROM events\nGROUP BY dt, user_id";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        StarRocksCreateMaterializedViewStatement mv = (StarRocksCreateMaterializedViewStatement) stmt;

        assertFalse(mv.getDistributedBy().isEmpty());
        assertNotNull(mv.getOrderBy());

        String output = SQLUtils.toSQLString(stmt, DbType.starrocks);
        assertTrue(output.toUpperCase().contains("DISTRIBUTED BY HASH"));
        assertTrue(output.toUpperCase().contains("ORDER BY"));
    }
}

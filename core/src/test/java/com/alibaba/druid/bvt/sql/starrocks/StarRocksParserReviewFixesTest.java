package com.alibaba.druid.bvt.sql.starrocks;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateMaterializedViewStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateRoutineLoadStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksLoadStatement;
import com.alibaba.druid.sql.parser.ParserException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression coverage for the StarRocks statement-parser review fixes on the
 * sr-compiler-optimization PR (#6647). Each test pins the behaviour requested by a reviewer
 * comment so the parser does not silently regress.
 */
public class StarRocksParserReviewFixesTest {
    private SQLStatement parse(String sql) {
        return SQLUtils.parseSingleStatement(sql, DbType.starrocks);
    }

    // #29 — INSERT INTO FILES(...) SELECT had no test coverage.
    @Test
    public void testInsertIntoFiles() {
        String sql = "INSERT INTO FILES(\"path\" = \"s3://bucket/data.parquet\", \"format\" = \"parquet\") "
                + "SELECT * FROM t1";
        SQLStatement stmt = parse(sql);
        assertNotNull(stmt);
        assertEquals("INSERT INTO FILES(\"path\" = \"s3://bucket/data.parquet\", \"format\" = \"parquet\")"
                + "\nSELECT *\nFROM t1", stmt.toString());
    }

    // #29 — INSERT INTO BLACKHOLE() SELECT had no test coverage.
    @Test
    public void testInsertIntoBlackhole() {
        String sql = "INSERT INTO BLACKHOLE() SELECT * FROM t1";
        SQLStatement stmt = parse(sql);
        assertNotNull(stmt);
        assertEquals("INSERT INTO BLACKHOLE\nSELECT *\nFROM t1", stmt.toString());
    }

    // #12 — COLUMNS TERMINATED BY <value> must not swallow the trailing column list.
    @Test
    public void testBrokerLoadColumnsTerminatedByKeepsColumnList() {
        String sql = "LOAD LABEL db1.label1 (DATA INFILE(\"hdfs://x/y\") INTO TABLE t1 "
                + "COLUMNS TERMINATED BY \",\" (c1, c2, c3))";
        SQLStatement stmt = parse(sql);
        assertTrue(stmt instanceof StarRocksLoadStatement);
        StarRocksLoadStatement load = (StarRocksLoadStatement) stmt;
        StarRocksLoadStatement.DataDescription desc = load.getDataDescriptions().get(0);
        // The delimiter is a single value; the column list survives.
        assertEquals("','", desc.getColumnTerminatedBy().toString());
        assertEquals(3, desc.getColumnList().size());
        assertEquals("c1", desc.getColumnList().get(0).toString());
        assertEquals("c2", desc.getColumnList().get(1).toString());
        assertEquals("c3", desc.getColumnList().get(2).toString());
    }

    // #12 — FORMAT AS <value> must not swallow the trailing column list.
    @Test
    public void testBrokerLoadFormatAsKeepsColumnList() {
        String sql = "LOAD LABEL db.l (DATA INFILE(\"f\") INTO TABLE t1 FORMAT AS \"parquet\" (a, b))";
        SQLStatement stmt = parse(sql);
        StarRocksLoadStatement load = (StarRocksLoadStatement) stmt;
        StarRocksLoadStatement.DataDescription desc = load.getDataDescriptions().get(0);
        assertEquals("'parquet'", desc.getFormat().toString());
        assertEquals(2, desc.getColumnList().size());
    }

    // #13 — truncated "SUBMIT TASK ... AS" must throw ParserException, not IndexOutOfBoundsException.
    @Test
    public void testSubmitTaskMissingBodyThrowsParserException() {
        assertThrows(ParserException.class, () -> parse("SUBMIT TASK t1 AS"));
    }

    // #13 — truncated "CREATE PIPE ... AS" must throw ParserException, not IndexOutOfBoundsException.
    @Test
    public void testCreatePipeMissingBodyThrowsParserException() {
        assertThrows(ParserException.class, () -> parse("CREATE PIPE p AS"));
    }

    // #17 — dictionary mapping value must keep its literal type (string stays quoted).
    @Test
    public void testCreateDictionaryPreservesStringLiteralValue() {
        String sql = "CREATE DICTIONARY dict USING tbl (k INT_VALUE, v 'hello')";
        SQLStatement stmt = parse(sql);
        assertNotNull(stmt);
        // 'hello' stays a string literal (quotes retained) rather than becoming an identifier.
        assertEquals("CREATE DICTIONARY dict USING tbl (k INT_VALUE, v 'hello')", stmt.toString());
    }

    // #20 — WITH BROKER "broker_name" (...) must parse (string-literal broker name accepted).
    @Test
    public void testLoadWithBrokerStringName() {
        String sql = "LOAD LABEL db.l (DATA INFILE(\"hdfs://x\") INTO TABLE t1) "
                + "WITH BROKER \"my_broker\" (\"key\"=\"val\")";
        SQLStatement stmt = parse(sql);
        StarRocksLoadStatement load = (StarRocksLoadStatement) stmt;
        assertEquals(1, load.getBrokerProperties().size());
    }

    // #20 — WITH BROKER (...) without a broker name still parses.
    @Test
    public void testLoadWithBrokerNoName() {
        String sql = "LOAD LABEL db.l (DATA INFILE(\"hdfs://x\") INTO TABLE t1) WITH BROKER (\"key\"=\"val\")";
        SQLStatement stmt = parse(sql);
        StarRocksLoadStatement load = (StarRocksLoadStatement) stmt;
        assertEquals(1, load.getBrokerProperties().size());
    }

    // #24 — SCHEDULE without START or EVERY must error instead of being silently dropped.
    @Test
    public void testSubmitTaskScheduleWithoutStartOrEveryThrows() {
        assertThrows(ParserException.class, () -> parse("SUBMIT TASK SCHEDULE AS SELECT 1"));
    }

    // #24 — a valid SCHEDULE EVERY(...) still parses.
    @Test
    public void testSubmitTaskScheduleEvery() {
        SQLStatement stmt = parse("SUBMIT TASK SCHEDULE EVERY (INTERVAL 1 DAY) AS SELECT 1");
        assertNotNull(stmt);
    }

    // #26 — PARTITION BY may precede DISTRIBUTED BY (canonical StarRocks DDL order).
    @Test
    public void testMvPartitionByBeforeDistributedBy() {
        String sql = "CREATE MATERIALIZED VIEW mv "
                + "PARTITION BY RANGE(dt) (PARTITION p1 VALUES LESS THAN ('2020-01-01')) "
                + "DISTRIBUTED BY HASH(col) BUCKETS 10 REFRESH ASYNC AS SELECT dt, col FROM t";
        SQLStatement stmt = parse(sql);
        assertTrue(stmt instanceof StarRocksCreateMaterializedViewStatement);
        StarRocksCreateMaterializedViewStatement mv = (StarRocksCreateMaterializedViewStatement) stmt;
        assertNotNull(mv.getPartitionBy());
        assertEquals(1, mv.getDistributedBy().size());
    }

    // #31 — async MV PARTITION BY accepts a bare expression with no definition list.
    @Test
    public void testMvPartitionByExpression() {
        String sql = "CREATE MATERIALIZED VIEW mv DISTRIBUTED BY HASH(a) "
                + "PARTITION BY date_trunc('day', dt) REFRESH ASYNC AS SELECT a, dt FROM t";
        SQLStatement stmt = parse(sql);
        StarRocksCreateMaterializedViewStatement mv = (StarRocksCreateMaterializedViewStatement) stmt;
        assertNotNull(mv.getPartitionBy());
        // Partition key expression is visible to visitors via getColumns().
        assertEquals(1, mv.getPartitionBy().getColumns().size());
        assertEquals("date_trunc('day', dt)", mv.getPartitionBy().getColumns().get(0).toString());
    }

    // #31 — async MV PARTITION BY accepts a parenthesised bare column with no definition list.
    @Test
    public void testMvPartitionByParenColumn() {
        String sql = "CREATE MATERIALIZED VIEW mv DISTRIBUTED BY HASH(a) "
                + "PARTITION BY (dt) REFRESH ASYNC AS SELECT a, dt FROM t";
        SQLStatement stmt = parse(sql);
        StarRocksCreateMaterializedViewStatement mv = (StarRocksCreateMaterializedViewStatement) stmt;
        assertNotNull(mv.getPartitionBy());
        assertEquals(1, mv.getPartitionBy().getColumns().size());
    }

    // #31 — async MV PARTITION BY accepts a bare column with no definition list.
    @Test
    public void testMvPartitionByBareColumn() {
        String sql = "CREATE MATERIALIZED VIEW mv DISTRIBUTED BY HASH(a) "
                + "PARTITION BY dt REFRESH ASYNC AS SELECT a, dt FROM t";
        SQLStatement stmt = parse(sql);
        StarRocksCreateMaterializedViewStatement mv = (StarRocksCreateMaterializedViewStatement) stmt;
        assertNotNull(mv.getPartitionBy());
        assertEquals(1, mv.getPartitionBy().getColumns().size());
    }

    // A bare MV partition key must not gain a RANGE keyword on output — it would change the
    // partition scheme and StarRocks would reject the regenerated statement.
    @Test
    public void testMvBarePartitionByRoundTrip() {
        String sql = "CREATE MATERIALIZED VIEW mv DISTRIBUTED BY HASH(a) "
                + "PARTITION BY date_trunc('day', dt) REFRESH ASYNC AS SELECT a, dt FROM t";
        String output = parse(sql).toString();
        assertTrue(output.contains("PARTITION BY date_trunc('day', dt)"), output);
        assertFalse(output.toUpperCase().contains("PARTITION BY RANGE"), output);
        // reparsing the output yields the same text
        assertEquals(output, parse(output).toString());
    }

    // Canonical StarRocks DDL emits PARTITION BY before DISTRIBUTED BY.
    @Test
    public void testMvPartitionByPrintedBeforeDistributedBy() {
        String sql = "CREATE MATERIALIZED VIEW mv DISTRIBUTED BY HASH(a) BUCKETS 10 "
                + "PARTITION BY dt REFRESH ASYNC AS SELECT a, dt FROM t";
        String output = parse(sql).toString().toUpperCase();
        assertTrue(output.indexOf("PARTITION BY") < output.indexOf("DISTRIBUTED BY"), output);
    }

    // The parent-class distributedByType field must survive cloning.
    @Test
    public void testMvCloneKeepsDistributedByType() {
        String sql = "CREATE MATERIALIZED VIEW mv DISTRIBUTED BY HASH(a) REFRESH ASYNC AS SELECT a FROM t";
        StarRocksCreateMaterializedViewStatement mv = (StarRocksCreateMaterializedViewStatement) parse(sql);
        mv.setDistributedByType(new SQLIdentifierExpr("HASH"));
        StarRocksCreateMaterializedViewStatement cloned = mv.clone();
        assertNotNull(cloned.getDistributedByType());
        assertEquals("HASH", cloned.getDistributedByType().getSimpleName());
        assertSame(cloned, cloned.getDistributedByType().getParent());
    }

    // CREATE ROUTINE LOAD accepts COLUMNS TERMINATED BY, alone and combined with a column list.
    @Test
    public void testRoutineLoadColumnsTerminatedBy() {
        String sql = "CREATE ROUTINE LOAD db.job ON tbl "
                + "COLUMNS TERMINATED BY ',', COLUMNS (k1, k2) "
                + "WHERE k1 > 0 "
                + "PROPERTIES (\"desired_concurrent_number\" = \"1\") "
                + "FROM KAFKA (\"kafka_topic\" = \"t\")";
        StarRocksCreateRoutineLoadStatement stmt = (StarRocksCreateRoutineLoadStatement) parse(sql);
        assertNotNull(stmt.getColumnTerminatedBy());
        assertEquals(2, stmt.getColumns().size());
        assertNotNull(stmt.getWhereCondition());
        assertEquals(1, stmt.getDataSourceProperties().size());

        String output = stmt.toString();
        assertTrue(output.contains("COLUMNS TERMINATED BY ','"), output);
        assertEquals(output, parse(output).toString());
    }

    @Test
    public void testRoutineLoadColumnsTerminatedByOnly() {
        String sql = "CREATE ROUTINE LOAD db.job ON tbl COLUMNS TERMINATED BY ',' "
                + "FROM KAFKA (\"kafka_topic\" = \"t\")";
        StarRocksCreateRoutineLoadStatement stmt = (StarRocksCreateRoutineLoadStatement) parse(sql);
        assertNotNull(stmt.getColumnTerminatedBy());
        assertTrue(stmt.getColumns().isEmpty());
        assertEquals("KAFKA", stmt.getDataSourceType().getSimpleName());
    }

    // Deeply self-nested SUBMIT TASK / CREATE PIPE bodies are rejected instead of overflowing the stack.
    @Test
    public void testSubmitTaskBodyDepthLimited() {
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            sql.append("SUBMIT TASK x AS ");
        }
        sql.append("SELECT 1");
        ParserException e = assertThrows(ParserException.class, () -> parse(sql.toString()));
        assertTrue(e.getMessage().contains("maximum body nesting depth"), e.getMessage());
    }

    @Test
    public void testCreatePipeBodyDepthLimited() {
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            sql.append("CREATE PIPE p AS ");
        }
        sql.append("INSERT INTO t SELECT 1");
        ParserException e = assertThrows(ParserException.class, () -> parse(sql.toString()));
        assertTrue(e.getMessage().contains("maximum body nesting depth"), e.getMessage());
    }
}

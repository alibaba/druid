package com.alibaba.druid.bvt.sql.starrocks;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateMaterializedViewStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksLoadStatement;
import com.alibaba.druid.sql.parser.ParserException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
}

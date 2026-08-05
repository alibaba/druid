package com.alibaba.druid.bvt.sql.starrocks;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateMaterializedViewStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksLoadStatement;
import com.alibaba.druid.sql.parser.ParserException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression coverage for the second review round on PR #6647 — the defects an independent review
 * of the whole branch turned up. Each test pins one reviewer finding.
 */
public class StarRocksReviewRound2Test {
    private SQLStatement parse(String sql) {
        return SQLUtils.parseSingleStatement(sql, DbType.starrocks);
    }

    private void assertRoundTripStable(String sql) {
        String out = parse(sql).toString();
        assertEquals(out, parse(out).toString(), "round trip not stable for: " + sql);
    }

    // The AS body must not swallow the ';' that terminates the enclosing statement.
    @Test
    public void testSubmitTaskDoesNotSwallowTerminator() {
        List<SQLStatement> stmts = SQLUtils.parseStatements("SUBMIT TASK t AS SELECT 1; SELECT 2", DbType.starrocks);
        assertEquals(2, stmts.size());
        assertTrue(stmts.get(0).toString().startsWith("SUBMIT TASK t"), stmts.get(0).toString());
        assertEquals("SELECT 2", stmts.get(1).toString());
    }

    @Test
    public void testCreatePipeDoesNotSwallowTerminator() {
        List<SQLStatement> stmts = SQLUtils.parseStatements(
                "CREATE PIPE p AS INSERT INTO t SELECT 1; SELECT 2", DbType.starrocks);
        assertEquals(2, stmts.size());
        assertEquals("SELECT 2", stmts.get(1).toString());
    }

    // An index COMMENT must not be shared between a statement and its clone.
    @Test
    public void testIndexCommentNotSharedWithClone() {
        String sql = "CREATE TABLE t (k INT, INDEX idx (k) USING NGRAMBF (\"gram_num\" = \"4\") COMMENT 'c') "
                + "DUPLICATE KEY(k) DISTRIBUTED BY HASH(k)";
        SQLStatement stmt = parse(sql);
        String before = stmt.toString();
        assertEquals(before, stmt.clone().toString());
        assertTrue(before.contains("COMMENT 'c'"), before);
    }

    // afterSemi must survive cloning for every StarRocks statement type.
    @Test
    public void testAfterSemiSurvivesClone() {
        String[] sqls = {
                "BACKUP SNAPSHOT db.s TO repo",
                "RESTORE SNAPSHOT db.s FROM repo",
                "CREATE CATALOG c PROPERTIES ('type' = 'hive')",
                "CREATE PIPE p AS INSERT INTO t SELECT 1",
                "SUBMIT TASK t AS SELECT 1",
                "CREATE EXTERNAL RESOURCE r PROPERTIES ('type' = 'hive')",
                "CREATE ROUTINE LOAD db.job ON tbl FROM KAFKA (\"kafka_topic\" = \"t\")",
                "LOAD LABEL db.l (DATA INFILE(\"hdfs://x\") INTO TABLE t1)",
        };
        for (String sql : sqls) {
            SQLStatement stmt = parse(sql);
            stmt.setAfterSemi(true);
            assertTrue(stmt.clone().isAfterSemi(), "afterSemi dropped by clone: " + sql);
        }
    }

    // CREATE RESOURCE with an empty PROPERTIES list must still re-parse.
    @Test
    public void testCreateResourceEmptyProperties() {
        assertRoundTripStable("CREATE EXTERNAL RESOURCE r PROPERTIES()");
        assertTrue(parse("CREATE EXTERNAL RESOURCE r PROPERTIES()").toString().contains("PROPERTIES ()"));
    }

    // WITH BROKER "<name>" must survive the round trip.
    @Test
    public void testLoadWithBrokerNamePreserved() {
        String sql = "LOAD LABEL db.l (DATA INFILE(\"hdfs://x\") INTO TABLE t1) WITH BROKER \"my_broker\" (\"k\" = \"v\")";
        StarRocksLoadStatement stmt = (StarRocksLoadStatement) parse(sql);
        assertNotNull(stmt.getBrokerName());
        String out = stmt.toString();
        assertTrue(out.contains("my_broker"), out);
        assertRoundTripStable(sql);
    }

    @Test
    public void testLoadWithBrokerNameOnly() {
        String sql = "LOAD LABEL db.l (DATA INFILE(\"hdfs://x\") INTO TABLE t1) WITH BROKER \"my_broker\"";
        String out = parse(sql).toString();
        assertTrue(out.contains("WITH BROKER"), out);
        assertTrue(out.contains("my_broker"), out);
        assertRoundTripStable(sql);
    }

    @Test
    public void testLoadBareWithBrokerPreserved() {
        String sql = "LOAD LABEL db.l (DATA INFILE(\"hdfs://x\") INTO TABLE t1) WITH BROKER";
        assertTrue(parse(sql).toString().contains("WITH BROKER"));
        assertRoundTripStable(sql);
    }

    // MV DISTRIBUTED BY must require a distribution, and must accept RANDOM.
    @Test
    public void testMvDistributedByRandom() {
        String sql = "CREATE MATERIALIZED VIEW mv DISTRIBUTED BY RANDOM REFRESH ASYNC AS SELECT a FROM t";
        StarRocksCreateMaterializedViewStatement mv = (StarRocksCreateMaterializedViewStatement) parse(sql);
        assertNotNull(mv.getDistributedByType());
        assertEquals("RANDOM", mv.getDistributedByType().getSimpleName());
        assertTrue(mv.toString().contains("DISTRIBUTED BY RANDOM"), mv.toString());
        assertRoundTripStable(sql);
    }

    @Test
    public void testMvDistributedByRequiresDistribution() {
        assertThrows(ParserException.class, () -> parse("CREATE MATERIALIZED VIEW mv DISTRIBUTED BY AS SELECT 1"));
    }

    // Canonical StarRocks INSERT clause order must parse, and output must be canonical.
    @Test
    public void testInsertCanonicalClauseOrder() {
        String sql = "INSERT INTO t PARTITION (p1) WITH LABEL lb (k1) BY NAME VALUES (1)";
        SQLStatement stmt = parse(sql);
        String out = stmt.toString();
        assertEquals("INSERT INTO t PARTITION (p1) WITH LABEL lb (k1) BY NAME\nVALUES (1)", out);
        assertRoundTripStable(sql);
    }

    // The historically-accepted order still parses and normalises to the canonical form.
    @Test
    public void testInsertLegacyClauseOrderStillParses() {
        String out = parse("INSERT INTO t WITH LABEL lb PARTITION (p1) BY NAME (k1) VALUES (1)").toString();
        assertEquals("INSERT INTO t PARTITION (p1) WITH LABEL lb (k1) BY NAME\nVALUES (1)", out);
    }

    @Test
    public void testInsertByNameAfterColumns() {
        assertEquals("INSERT INTO t (k1) BY NAME\nVALUES (1)",
                parse("INSERT INTO t (k1) by name values (1)").toString());
    }

    // The StarRocks INSERT hooks must not rewrite a Doris statement's dbType.
    @Test
    public void testDorisInsertKeepsItsDbType() {
        SQLStatement stmt = SQLUtils.parseSingleStatement("INSERT INTO t WITH LABEL lb VALUES (1)", DbType.doris);
        assertEquals(DbType.doris, stmt.getDbType());
        assertTrue(stmt.toString().contains("WITH LABEL lb"), stmt.toString());
    }

    // WITH LABEL / BY NAME must render from a statement whose dbType was never set.
    @Test
    public void testInsertClausesRenderWithoutDbType() {
        SQLInsertStatement stmt = (SQLInsertStatement) parse("INSERT INTO t WITH LABEL lb BY NAME VALUES (1)");
        stmt.setDbType(null);
        String out = stmt.toString();
        assertTrue(out.contains("WITH LABEL lb"), out);
        assertTrue(out.contains("BY NAME"), out);
    }

    // A generic (non-StarRocks) output visitor must not silently emit a different statement.
    @Test
    public void testGenericOutputVisitorDoesNotMangle() {
        String[] sqls = {
                "BACKUP SNAPSHOT db.s TO repo",
                "CREATE CATALOG c PROPERTIES ('type' = 'hive')",
                "SUBMIT TASK AS CREATE TABLE t AS SELECT 1",
                "CREATE EXTERNAL RESOURCE r PROPERTIES ('type' = 'hive')",
                "LOAD LABEL db.l (DATA INFILE(\"hdfs://x\") INTO TABLE t1)",
                "CREATE PIPE p AS INSERT INTO t SELECT 1",
        };
        for (String sql : sqls) {
            SQLStatement stmt = parse(sql);
            assertEquals(stmt.toString(), SQLUtils.toSQLString(stmt, DbType.mysql),
                    "generic visitor mangled: " + sql);
        }
    }
}

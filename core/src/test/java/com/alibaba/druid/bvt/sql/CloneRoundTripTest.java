package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Ratchet guard for clone field-completeness (deeper MID1). For every statement in the per-dialect
 * parser corpus whose clone() runs, it asserts {@code stmt.clone().toString().equals(stmt.toString())}
 * — i.e. cloning preserves everything that reaches the output. A mismatch means clone()/cloneTo()
 * dropped or mangled a field.
 *
 * <p>The currently-incomplete (statementType, dialect) combinations are grandfathered in
 * {@link #KNOWN_INCOMPLETE} — mostly dialect CREATE TABLE clones that type-slice to the base type and
 * SQLSelect clones that drop a table-source clause. Shrink this list as clones are completed; a NEW
 * combination that starts mismatching (a clone regression on a currently-clean type) fails the build.
 * clone()s that throw UnsupportedOperationException (the separate "clone not implemented" debt) are
 * skipped here.
 */
public class CloneRoundTripTest {
    private static final Set<String> KNOWN_INCOMPLETE = new HashSet<>(Arrays.asList(
            "AthenaCreateTableStatement [clone-toString-threw]",
            "BigQueryCreateTableStatement (bigquery)",
            "CKCreateTableStatement (clickhouse)",
            "CKDropTableStatement (clickhouse)",
            "GaussDbCreateTableStatement (gaussdb)",
            "GaussDbCreateTableStatement [clone-toString-threw]",
            "GaussDbInsertStatement (gaussdb)",
            "HiveCreateTableStatement (databricks)",
            "HiveCreateTableStatement (hive)",
            "ImpalaCreateTableStatement (impala)",
            "ImpalaInsertStatement (impala)",
            "MySqlCreateTableStatement (mysql)",
            "MySqlCreateTableStatement (oceanbase)",
            "PGDeleteStatement (postgresql)",
            "PGDeleteStatement [clone-toString-threw]",
            "PGInsertStatement (postgresql)",
            "PGInsertStatement [clone-toString-threw]",
            "PGSelectStatement (athena)",
            "PGSelectStatement (hologres)",
            "PGSelectStatement (postgresql)",
            "PGSelectStatement (supersql)",
            "PGSelectStatement [clone-toString-threw]",
            "PGUpdateStatement (postgresql)",
            "PrestoCreateTableStatement (trino)",
            "RedshiftCreateTableStatement (redshift)",
            "SQLAlterViewStatement (mysql)",
            "SQLBlockStatement (bigquery)",
            "SQLCommitStatement (postgresql)",
            "SQLCreateFunctionStatement (bigquery)",
            "SQLCreateFunctionStatement [clone-toString-threw]",
            "SQLCreateIndexStatement (postgresql)",
            "SQLCreateIndexStatement (sqlserver)",
            "SQLCreateTableStatement (hologres)",
            "SQLCreateTableStatement (postgresql)",
            "SQLCreateViewStatement (databricks)",
            "SQLCreateViewStatement (sqlserver)",
            "SQLDeleteStatement (bigquery)",
            "SQLDropTableStatement (bigquery)",
            "SQLDropTableStatement (doris)",
            "SQLDropTableStatement (postgresql)",
            "SQLIfStatement [clone-toString-threw]",
            "SQLSelectStatement (bigquery)",
            "SQLSelectStatement (clickhouse)",
            "SQLSelectStatement (databricks)",
            "SQLSelectStatement (doris)",
            "SQLSelectStatement (impala)",
            "SQLSelectStatement (mysql)",
            "SQLSelectStatement (odps)",
            "SQLSelectStatement (spark)",
            "SQLSelectStatement (starrocks)",
            "SQLSelectStatement (teradata)",
            "SQLServerUpdateStatement (sqlserver)",
            "SQLServerUpdateStatement (synapse)",
            "SQLSetStatement (hive)",
            "SQLSetStatement (hologres)",
            "SQLSetStatement (postgresql)",
            "SQLSetStatement (sqlserver)",
            "SQLUpdateStatement (bigquery)",
            "StarRocksCreateTableStatement (doris)",
            "StarRocksCreateTableStatement (starrocks)",
            "TDCreateTableStatement (teradata)"
    ));

    @Test
    public void cloneRoundTripsToSameSql() throws IOException {
        Path base = Paths.get("src/test/resources/bvt/parser");
        assumeTrue(Files.isDirectory(base), base.toAbsolutePath() + " not found");

        Set<String> found = new TreeSet<>();
        List<Path> dirs;
        try (Stream<Path> s = Files.list(base)) {
            dirs = s.filter(Files::isDirectory).sorted().collect(Collectors.toList());
        }
        for (Path dir : dirs) {
            DbType dbType = DbType.of(dir.getFileName().toString());
            if (dbType == null) {
                continue;
            }
            List<Path> txts;
            try (Stream<Path> s = Files.walk(dir)) {
                txts = s.filter(p -> p.toString().endsWith(".txt")).sorted().collect(Collectors.toList());
            }
            for (Path txt : txts) {
                for (String test : new String(Files.readAllBytes(txt), StandardCharsets.UTF_8).split("(?m)^-{50,}$")) {
                    String sql = test.split("(?m)^-{20}$")[0].trim();
                    if (sql.isEmpty()) {
                        continue;
                    }
                    List<SQLStatement> stmts;
                    try {
                        stmts = SQLUtils.parseStatements(sql, dbType);
                    } catch (Throwable t) {
                        continue;
                    }
                    for (SQLStatement stmt : stmts) {
                        String before;
                        try {
                            before = stmt.toString();
                        } catch (Throwable t) {
                            continue;
                        }
                        SQLStatement cloned;
                        try {
                            cloned = stmt.clone();
                        } catch (Throwable t) {
                            continue; // clone not implemented (throws) — separate debt, out of scope
                        }
                        String after;
                        try {
                            after = cloned.toString();
                        } catch (Throwable t) {
                            found.add(stmt.getClass().getSimpleName() + " [clone-toString-threw]");
                            continue;
                        }
                        if (!before.equals(after)) {
                            found.add(stmt.getClass().getSimpleName() + " (" + dbType + ")");
                        }
                    }
                }
            }
        }

        Set<String> regressions = new TreeSet<>(found);
        regressions.removeAll(KNOWN_INCOMPLETE);
        assertTrue(regressions.isEmpty(),
                "New clone field-completeness defect — clone() dropped a field that changed the SQL "
                        + "(fix the clone()/cloneTo(); do not add to KNOWN_INCOMPLETE):\n  "
                        + String.join("\n  ", regressions));
    }
}

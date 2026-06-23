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
 * <p>{@link #KNOWN_INCOMPLETE} grandfathers (statementType, dialect) combinations whose clone still
 * drops a field. The per-dialect subdirectory corpus round-trips cleanly (empty for those). The scan
 * also now covers the ~92 top-level *.txt files (oracle/db2/h2/odps/...); two deep Oracle edge cases
 * in that newly-covered set remain grandfathered below. When a clone drops a field, fix the
 * clone()/cloneTo() rather than re-adding an entry here. clone()s that throw UnsupportedOperationException
 * (the separate "clone not implemented" debt) are skipped here.
 */
public class CloneRoundTripTest {
    private static final Set<String> KNOWN_INCOMPLETE = new HashSet<>(Arrays.asList(
            // oracle-56: a statement inside a multi-statement PL/SQL block loses one inner ';' (afterSemi) on clone
            "SQLBlockStatement (oracle)",
            // oracle-61: a trailing line-comment after the SELECT is dropped on clone
            "SQLSelectStatement (oracle)"
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
                scanFile(txt, dbType, found);
            }
        }

        // Top-level *.txt files (e.g. oracle-0.txt, db2-0.txt, h2-0.txt, odps-0.txt) are NOT in a
        // dialect subdirectory; map them to a DbType by the filename prefix before the first '-'.
        List<Path> topFiles;
        try (Stream<Path> s = Files.list(base)) {
            topFiles = s.filter(p -> !Files.isDirectory(p) && p.toString().endsWith(".txt"))
                    .sorted().collect(Collectors.toList());
        }
        for (Path txt : topFiles) {
            String fn = txt.getFileName().toString();
            int dash = fn.indexOf('-');
            String prefix = dash > 0 ? fn.substring(0, dash) : fn.substring(0, fn.length() - ".txt".length());
            DbType dbType = DbType.of(prefix);
            if (dbType == null) {
                continue;
            }
            scanFile(txt, dbType, found);
        }

        Set<String> regressions = new TreeSet<>(found);
        regressions.removeAll(KNOWN_INCOMPLETE);
        assertTrue(regressions.isEmpty(),
                "New clone field-completeness defect — clone() dropped a field that changed the SQL "
                        + "(fix the clone()/cloneTo(); do not add to KNOWN_INCOMPLETE):\n  "
                        + String.join("\n  ", regressions));
    }

    private static void scanFile(Path txt, DbType dbType, Set<String> found) throws IOException {
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

package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Ratchet guard for AST parent-pointer integrity (review item C8). Parses the per-dialect parser
 * corpus and, using the accept0 traversal as the source of truth for structural parent/child links,
 * collects every (parentType -&gt; childType) pair where a structural child has the wrong parent
 * (null, or pointing outside its traversal path). The set of such pairs must stay within the
 * documented {@link #KNOWN_DEBT} allowlist: any NEW pair (a setter/parser that forgot setParent)
 * fails the build, while the remaining long-tail debt is grandfathered and shrinks over time.
 */
public class ParentIntegrityTest {
    /**
     * Residual parent-pointer debt as of the C8 paydown. Each entry is a (parentType -&gt; childType)
     * pair whose child still has a wrong parent after parsing the corpus. Shrink this list; never grow it.
     */
    private static final Set<String> KNOWN_DEBT = new HashSet<>(Arrays.asList(
            // --- null parent (attach point never called setParent) ---
            "SQLGrantStatement -> SQLIdentifierExpr",
            "Entry -> SQLCharExpr",
            "Entry -> SQLAggregateExpr",
            "Entry -> SQLArrayExpr",
            "Entry -> SQLMethodInvokeExpr",
            "Entry -> SQLQueryExpr",
            "HiveCreateTableStatement -> SQLCharExpr",
            "ImpalaCreateTableStatement -> SQLCharExpr",
            "Item -> SQLIdentifierExpr",
            "Item -> SQLRollbackTransactionStatement",
            "MySqlInsertStatement -> ValuesClause",
            "SQLAlterTableDropConstraint -> SQLIdentifierExpr",
            "SQLAlterTableAlterColumn -> SQLNumberExpr",
            "SQLAlterTableEnableConstraint -> SQLIdentifierExpr",
            "SQLAtTimeZoneExpr -> SQLCharExpr",
            "SQLAtTimeZoneExpr -> SQLIdentifierExpr",
            "SQLBlockStatement -> SQLServerInsertStatement",
            "SQLBlockStatement -> SQLCommitTransactionStatement",
            "SQLCallStatement -> SQLIdentifierExpr",
            "SQLCallStatement -> SQLPropertyExpr",
            "SQLColumnDefault -> SQLIntegerExpr",
            "SQLCommentStatement -> SQLCharExpr",
            "SQLCreateProcedureStatement -> SQLPropertyExpr",
            "SQLCreateSequenceStatement -> SQLIntegerExpr",
            "SQLCreateSequenceStatement -> SQLIdentifierExpr",
            "SQLCurrentOfCursorExpr -> SQLIdentifierExpr",
            "SQLDropIndexStatement -> SQLExprTableSource",
            "SQLDropIndexStatement -> SQLIdentifierExpr",
            "SQLDropIndexStatement -> SQLPropertyExpr",
            "SQLGeneratedTableSource -> SQLIdentifierExpr",
            "SQLOptimizeStatement -> SQLIdentifierExpr",
            "SQLPartitionBatch -> SQLIdentifierExpr",
            "SQLPartitionBatch -> SQLIntegerExpr",
            "SQLRaiseStatement -> SQLCharExpr",
            "SQLRaiseStatement -> SQLMethodInvokeExpr",
            "SQLRevokeStatement -> SQLIdentifierExpr",
            "SQLSelectQueryBlock -> SQLSelectItem",
            "SQLSequenceExpr -> SQLIdentifierExpr",
            "SQLShowCreateViewStatement -> SQLIdentifierExpr",
            // --- non-null but pointing outside the traversal path ---
            "SQLForeignKeyImpl -> SQLIdentifierExpr [parent=SQLExprTableSource]",
            "SQLAggregateExpr -> SQLCharExpr [parent=SQLMethodInvokeExpr]",
            "SQLAggregateExpr -> SQLIdentifierExpr [parent=SQLMethodInvokeExpr]"
    ));

    static final class Checker extends SQLASTVisitorAdapter {
        final Deque<SQLObject> stack = new ArrayDeque<>();
        final Set<String> pairs = new TreeSet<>();

        @Override
        public void preVisit(SQLObject x) {
            SQLObject expected = stack.peek();
            if (expected != null && x.getParent() != expected) {
                String pc = expected.getClass().getSimpleName() + " -> " + x.getClass().getSimpleName();
                if (x.getParent() == null) {
                    pairs.add(pc);
                } else if (!stack.contains(x.getParent())) {
                    pairs.add(pc + " [parent=" + x.getParent().getClass().getSimpleName() + "]");
                }
            }
            stack.push(x);
        }

        @Override
        public void postVisit(SQLObject x) {
            if (!stack.isEmpty()) {
                stack.pop();
            }
        }
    }

    @Test
    public void parsedChildrenKeepCorrectParent() throws IOException {
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
                    Checker c = new Checker();
                    try {
                        for (SQLStatement stmt : stmts) {
                            stmt.accept(c);
                        }
                    } catch (Throwable t) {
                        continue;
                    }
                    found.addAll(c.pairs);
                }
            }
        }

        Set<String> regressions = new TreeSet<>(found);
        regressions.removeAll(KNOWN_DEBT);
        assertTrue(regressions.isEmpty(),
                "New parent-pointer integrity violation(s) — a setter/parser attached a child without "
                        + "calling setParent. Fix the attach point (do not add to KNOWN_DEBT):\n  "
                        + String.join("\n  ", regressions));
    }
}

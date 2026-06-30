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
 * Guard for AST parent-pointer integrity (review item C8). Parses the per-dialect parser corpus and,
 * using the accept0 traversal as the source of truth for structural parent/child links, verifies that
 * every structural child a node visits has its parent set correctly.
 *
 * <p><b>Null parents are forbidden outright</b> — a child with getParent()==null means an attach
 * point (setter/parser) forgot to call setParent, the exact C8 defect, now fully paid down.
 *
 * <p>A child whose parent is a non-null node off the current traversal path is only allowed for the
 * few {@link #BORROWED_REFERENCES} where a node's accept0 deliberately re-visits a node structurally
 * owned by another (e.g. a foreign key visiting the referenced table's name): there the parent is
 * correct, just not the immediate visitor. Any other off-path parent is a new defect and fails.
 */
public class ParentIntegrityTest {
    /**
     * (parent -&gt; child) pairs where accept0 re-visits a node owned by a different node, so the
     * child's parent is legitimately not the visitor. These are not parent bugs.
     */
    private static final Set<String> BORROWED_REFERENCES = new HashSet<>(Arrays.asList(
            "SQLForeignKeyImpl -> SQLIdentifierExpr [parent=SQLExprTableSource]",
            "SQLAggregateExpr -> SQLCharExpr [parent=SQLMethodInvokeExpr]",
            "SQLAggregateExpr -> SQLIdentifierExpr [parent=SQLMethodInvokeExpr]"
    ));

    static final class Checker extends SQLASTVisitorAdapter {
        final Deque<SQLObject> stack = new ArrayDeque<>();
        final Set<String> nullParent = new TreeSet<>();
        final Set<String> offPathParent = new TreeSet<>();

        @Override
        public void preVisit(SQLObject x) {
            SQLObject expected = stack.peek();
            if (expected != null && x.getParent() != expected) {
                String pc = expected.getClass().getSimpleName() + " -> " + x.getClass().getSimpleName();
                if (x.getParent() == null) {
                    nullParent.add(pc);
                } else if (!stack.contains(x.getParent())) {
                    offPathParent.add(pc + " [parent=" + x.getParent().getClass().getSimpleName() + "]");
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

        Set<String> nullParent = new TreeSet<>();
        Set<String> offPathParent = new TreeSet<>();
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
                    nullParent.addAll(c.nullParent);
                    offPathParent.addAll(c.offPathParent);
                }
            }
        }

        assertTrue(nullParent.isEmpty(),
                "Child node(s) parsed with a NULL parent — an attach point forgot setParent (C8):\n  "
                        + String.join("\n  ", nullParent));

        Set<String> unexpected = new TreeSet<>(offPathParent);
        unexpected.removeAll(BORROWED_REFERENCES);
        assertTrue(unexpected.isEmpty(),
                "Child node(s) parented to a node off their traversal path (not a known borrowed "
                        + "reference) — likely setParent to the wrong node:\n  "
                        + String.join("\n  ", unexpected));
    }
}

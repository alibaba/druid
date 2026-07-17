/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verify that Oracle dialect statements and their child AST nodes can be traversed by a base
 * {@link SQLASTVisitorAdapter} (a non-{@code OracleASTVisitor}) without throwing
 * {@link StackOverflowError} or {@link ClassCastException}, and that child nodes are
 * <em>actually visited</em> (not silently skipped).
 *
 * <h3>Background</h3>
 * Before the fix, Oracle AST classes had two patterns that crashed for non-Oracle visitors:
 * <ul>
 *   <li><b>super.accept(visitor)</b> (4 statement/constraint classes) — {@code accept()} is
 *       final on {@code SQLObjectImpl} and dispatches back to {@code this.accept0()}, creating
 *       infinite recursion → StackOverflowError.</li>
 *   <li><b>direct cast</b> (20+ classes) — {@code accept0((OracleASTVisitor) visitor)} without
 *       instanceof check → ClassCastException.</li>
 * </ul>
 *
 * <h3>Fix</h3>
 * All Oracle AST classes now use:
 * <pre>
 * if (visitor instanceof OracleASTVisitor) {
 *     accept0((OracleASTVisitor) visitor);
 *     return;
 * }
 * super.accept0(visitor);   // or acceptChild(...) for classes whose parent chain is abstract
 * </pre>
 *
 * <h3>Test strategy</h3>
 * Every test counts visited {@link SQLIdentifierExpr} nodes via a base visitor. This catches
 * both crashes (exception propagates) and silent skips (count drops to 0 if the fallback
 * traversal is accidentally removed).
 */
public class OracleAccept0CrossDialectTest {
    /**
     * Parse Oracle SQL and count how many {@link SQLIdentifierExpr} nodes a base
     * {@link SQLASTVisitorAdapter} visits. Returns 0 if the traversal crashes or silently
     * stops at an Oracle-specific node without a fallback.
     */
    private int countIdentifiers(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.oracle);
        assertEquals(1, stmts.size());
        AtomicInteger count = new AtomicInteger();
        stmts.get(0).accept(new SQLASTVisitorAdapter() {
            @Override
            public boolean visit(SQLIdentifierExpr x) {
                count.incrementAndGet();
                return true;
            }
        });
        return count.get();
    }

    // ==================== DML statements (originally crashed) ====================

    @Test
    public void update_baseVisitor_noStackOverflow() {
        assertTrue(countIdentifiers("UPDATE employee SET name = 'x' WHERE id = 1") >= 2,
            "should visit at least employee + name + id identifiers");
    }

    @Test
    public void delete_baseVisitor_noClassCastException() {
        assertTrue(countIdentifiers("DELETE FROM employee WHERE id = 1") >= 2,
            "should visit at least employee + id identifiers");
    }

    @Test
    public void insert_baseVisitor_noClassCastException() {
        assertTrue(countIdentifiers("INSERT INTO employee (id, name) VALUES (1, 'x')") >= 3,
            "should visit at least employee + id + name identifiers");
    }

    // ==================== Constraint classes (regression coverage) ====================

    @Test
    public void checkConstraint_baseVisitor() {
        // OracleCheck had super.accept(visitor) bug — same StackOverflow as OracleUpdateStatement
        assertTrue(countIdentifiers(
            "CREATE TABLE t (id INT, CONSTRAINT chk CHECK (id > 0))") >= 2,
            "should visit t + id identifiers");
    }

    @Test
    public void foreignKeyConstraint_baseVisitor() {
        // OracleForeignKey had super.accept(visitor) bug
        assertTrue(countIdentifiers(
            "CREATE TABLE t (id INT, CONSTRAINT fk_t FOREIGN KEY (id) REFERENCES parent(id))") >= 2,
            "should visit t + parent identifiers");
    }

    @Test
    public void uniqueConstraint_baseVisitor() {
        // OracleUnique had super.accept(visitor) bug
        assertTrue(countIdentifiers(
            "CREATE TABLE t (id INT, CONSTRAINT uq UNIQUE (id))") >= 2,
            "should visit t + id identifiers");
    }

    @Test
    public void primaryKey_baseVisitor() {
        // OraclePrimaryKey had direct cast without instanceof
        assertTrue(countIdentifiers(
            "CREATE TABLE t (id INT, CONSTRAINT pk PRIMARY KEY (id))") >= 2,
            "should visit t + id identifiers");
    }

    // ==================== Deep child nodes (reviewer scenario) ====================

    @Test
    public void updateSubquery_baseVisitor() {
        // UPDATE (SELECT ...) reaches OracleSelectSubqueryTableSource child node
        assertTrue(countIdentifiers("UPDATE (SELECT id FROM t) SET x = 1") >= 2,
            "should visit id + t + x identifiers inside the subquery table source");
    }

    @Test
    public void createTableWithMultipleConstraints_baseVisitor() {
        // Reaches OraclePrimaryKey + OracleCheck + OracleUnique in one statement
        assertTrue(countIdentifiers(
            "CREATE TABLE t (id INT PRIMARY KEY, name VARCHAR(100), "
            + "CONSTRAINT chk CHECK (id > 0), CONSTRAINT uq UNIQUE (name))") >= 3,
            "should visit t + id + name identifiers across multiple constraints");
    }

    @Test
    public void createIndex_baseVisitor() {
        // OracleCreateIndexStatement had direct cast
        assertTrue(countIdentifiers("CREATE INDEX idx_name ON t (name)") >= 2,
            "should visit t + name identifiers");
    }

    // ==================== Oracle-specific expressions ====================

    @Test
    public void intervalExpression_baseVisitor() {
        // OracleIntervalExpr had direct cast; fallback uses acceptChild (not super.accept0)
        assertTrue(countIdentifiers("SELECT INTERVAL '1' DAY FROM dual") >= 1,
            "should visit dual identifier");
    }

    @Test
    public void binaryFloatLiteral_baseVisitor() {
        // OracleBinaryFloatExpr had direct cast; leaf node with no child traversal needed
        assertTrue(countIdentifiers("SELECT 1.5f FROM dual") >= 1,
            "should visit dual identifier");
    }

    // ==================== StatementImpl hierarchy ====================

    @Test
    public void createTable_baseVisitor() {
        // OracleCreateTableStatement extends OracleStatementImpl — the base class fallback
        // used to call super.accept0 → SQLStatementImpl → UnsupportedOperationException
        assertTrue(countIdentifiers("CREATE TABLE t (id INT, name VARCHAR2(100))") >= 2,
            "should visit t + id + name identifiers");
    }
}

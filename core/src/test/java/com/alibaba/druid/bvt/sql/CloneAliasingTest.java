package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * A clone must not share mutable child nodes with its source: mutating the clone must never change
 * the original's rendered SQL. These cases are invisible to a plain {@code toString()} comparison,
 * which is why they need their own guard.
 */
public class CloneAliasingTest {
    @Test
    public void headHintsAreNotSharedWithClone() {
        SQLStatement stmt = SQLUtils.parseSingleStatement("/*+ HINT1 */ CREATE TABLE t (a int)", DbType.mysql);
        SQLStatement cloned = stmt.clone();

        List<SQLCommentHint> origHints = ((SQLStatementImpl) stmt).getHeadHintsDirect();
        List<SQLCommentHint> cloneHints = ((SQLStatementImpl) cloned).getHeadHintsDirect();
        assertNotNull(origHints);
        assertNotNull(cloneHints);
        assertNotSame(origHints, cloneHints, "clone shares the headHints list with its source");

        String before = stmt.toString();
        cloneHints.get(0).setText("MUTATED");
        assertEquals(before, stmt.toString(), "mutating the clone's hint rewrote the original");
        assertSame(cloned, cloneHints.get(0).getParent());
    }

    @Test
    public void dropTableHintsAreNotSharedWithClone() {
        SQLStatement stmt = SQLUtils.parseSingleStatement("DROP /*+ HINT */ TABLE t", DbType.mysql);
        SQLDropTableStatement cloned = (SQLDropTableStatement) stmt.clone();

        assertEquals(DbType.mysql, cloned.getDbType(), "clone dropped dbType");

        String before = stmt.toString();
        List<SQLCommentHint> cloneHints = cloned.getHints();
        if (cloneHints != null && !cloneHints.isEmpty()) {
            cloneHints.get(0).setText("MUTATED");
        }
        assertEquals(before, stmt.toString(), "mutating the clone's hint rewrote the original");
    }

    @Test
    public void partitionByLifeCycleIsClonedAndReparented() {
        String sql = "CREATE TABLE t (id int) PARTITION BY RANGE(id) (PARTITION p1 VALUES LESS THAN (10))";
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) SQLUtils.parseSingleStatement(sql, DbType.mysql);
        SQLPartitionBy partitionBy = stmt.getPartitioning();
        assertNotNull(partitionBy);
        partitionBy.setLifeCycle(new SQLIntegerExpr(7));
        partitionBy.setAuto(Boolean.TRUE);

        SQLPartitionBy cloned = partitionBy.clone();
        assertNotNull(cloned.getLifeCycle());
        assertNotSame(partitionBy.getLifeCycle(), cloned.getLifeCycle(), "lifeCycle shared with the source");
        assertSame(cloned, cloned.getLifeCycle().getParent());
        assertEquals(Boolean.TRUE, cloned.getAuto(), "auto dropped by clone");

        cloned.getLifeCycle().setNumber(99);
        assertEquals(7, ((SQLIntegerExpr) partitionBy.getLifeCycle()).getNumber().intValue());
    }

    @Test
    public void gaussDbDistributeByIsClonedNotShared() {
        String sql = "CREATE TABLE t1 (c1 int, c2 int) DISTRIBUTE BY HASH (c1)";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.gaussdb);
        SQLStatement cloned = stmt.clone();
        assertEquals(stmt.toString(), cloned.toString());

        Object origDist = invokeGetDistributeBy(stmt);
        Object cloneDist = invokeGetDistributeBy(cloned);
        if (origDist != null) {
            assertNotSame(origDist, cloneDist, "clone shares the DISTRIBUTE BY subtree with its source");
        }
    }

    private static Object invokeGetDistributeBy(Object stmt) {
        try {
            return stmt.getClass().getMethod("getDistributeBy").invoke(stmt);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}

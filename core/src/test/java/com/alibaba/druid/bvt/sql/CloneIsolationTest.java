package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression guards for SQLObjectImpl.cloneTo isolation defects (C3): a clone must not share mutable
 * comment lists with the original, and cloned children must point their parent at the clone tree.
 */
public class CloneIsolationTest {
    @Test
    public void cloneDoesNotShareCommentList() {
        // SQLExprStatement.clone routes through SQLObjectImpl.cloneTo, which previously shallow-copied
        // the attributes map and left the comment list shared between original and clone.
        SQLExprStatement original = new SQLExprStatement(new SQLIdentifierExpr("a"));
        original.addAfterComment("-- keep");

        SQLExprStatement cloned = original.clone();
        cloned.addAfterComment("-- only on clone");

        // mutating the clone's comments must not leak into the original
        List<String> originalComments = original.getAfterCommentsDirect();
        assertEquals(1, originalComments.size(), "clone polluted the original's comment list");
        assertEquals("-- keep", originalComments.get(0));
        assertNotSame(originalComments, cloned.getAfterCommentsDirect());
        assertEquals(2, cloned.getAfterCommentsDirect().size());
    }

    @Test
    public void clonedTableOptionsPointAtClone() {
        String sql = "CREATE TABLE t (id int) ENGINE = InnoDB";

        SQLStatement original = SQLUtils.parseStatements(sql, DbType.mysql).get(0);
        SQLStatement cloned = original.clone();

        // round-trip equivalence: table options must survive the clone
        assertEquals(original.toString(), cloned.toString());
        assertTrue(cloned.toString().toUpperCase().contains("ENGINE"));

        // every descendant of the clone must resolve its ancestry back to the clone, never the original
        cloned.accept(new com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter() {
            @Override
            public void postVisit(com.alibaba.druid.sql.ast.SQLObject node) {
                com.alibaba.druid.sql.ast.SQLObject p = node.getParent();
                while (p != null) {
                    assertSame(cloned, rootOf(p), "a cloned child still points into the original tree");
                    p = p.getParent();
                }
            }
        });
    }

    private static com.alibaba.druid.sql.ast.SQLObject rootOf(com.alibaba.druid.sql.ast.SQLObject node) {
        com.alibaba.druid.sql.ast.SQLObject p = node;
        while (p.getParent() != null) {
            p = p.getParent();
        }
        return p;
    }
}

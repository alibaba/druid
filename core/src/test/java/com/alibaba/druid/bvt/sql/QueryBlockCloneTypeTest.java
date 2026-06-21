package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.clickhouse.ast.CKSelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.dialect.dm.ast.stmt.DmSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oscar.ast.stmt.OscarSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock;
import com.alibaba.druid.sql.dialect.redshift.stmt.RedshiftSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.teradata.ast.TDSelectQueryBlock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guard against dialect SQLSelectQueryBlock subclasses being type-sliced by clone(): the base
 * clone() unconditionally builds a base SQLSelectQueryBlock, so every subclass must override
 * clone()/cloneTo() to return its own runtime type and preserve its dialect-specific fields.
 */
public class QueryBlockCloneTypeTest {
    @Test
    public void cloneReturnsSameRuntimeType() {
        SQLSelectQueryBlock[] blocks = {
                new CKSelectQueryBlock(),
                new PGSelectQueryBlock(),
                new OscarSelectQueryBlock(),
                new RedshiftSelectQueryBlock(),
                new DmSelectQueryBlock(),
                new DB2SelectQueryBlock(),
                new SQLServerSelectQueryBlock(),
                new TDSelectQueryBlock(DbType.teradata),
        };
        for (SQLSelectQueryBlock block : blocks) {
            assertEquals(block.getClass(), block.clone().getClass(),
                    block.getClass().getSimpleName() + " was type-sliced by clone()");
        }
    }

    @Test
    public void clonePreservesDialectFields() {
        CKSelectQueryBlock ck = new CKSelectQueryBlock();
        ck.setFinal(true);
        ck.setWithTotals(true);
        CKSelectQueryBlock ckClone = ck.clone();
        assertTrue(ckClone.isFinal(), "CK isFinal lost on clone");
        assertTrue(ckClone.isWithTotals(), "CK withTotals lost on clone");

        DB2SelectQueryBlock db2 = new DB2SelectQueryBlock();
        db2.setForReadOnly(true);
        assertTrue(db2.clone().isForReadOnly(), "DB2 forReadOnly lost on clone");

        PGSelectQueryBlock pg = new PGSelectQueryBlock();
        pg.getDistinctOn().add(new SQLIdentifierExpr("a"));
        assertEquals(1, pg.clone().getDistinctOn().size(), "PG distinctOn lost on clone");
    }
}

package com.alibaba.druid.bvt.sql.sqlserver;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression guard for dialect clone type-slicing: cloning a SQL Server query block must preserve
 * the dialect-specific {@code TOP n} clause instead of degrading to the base SQLSelectQueryBlock.
 */
public class SQLServerTopCloneTest {
    @Test
    public void clonePreservesTop() {
        String sql = "SELECT TOP 5 * FROM t";

        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.sqlserver);
        assertEquals(1, stmts.size());

        SQLStatement original = stmts.get(0);
        SQLStatement cloned = original.clone();

        assertEquals(original.toString(), cloned.toString());
        assertEquals("SELECT TOP 5 *\nFROM t", cloned.toString());
    }
}

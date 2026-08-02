package com.alibaba.druid.bvt.sql.sqlserver;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerTriggerToggleStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression for issue #6205: SQL Server {@code ENABLE/DISABLE TRIGGER ALL ON table}
 * raised {@code not supported ... token ENABLE/DISABLE}.
 */
public class SqlServerTriggerToggleTest {
    @Test
    public void disableTriggerAllOnTable() {
        String sql = "DISABLE TRIGGER ALL ON tablename";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "sqlserver");
        assertEquals(1, stmts.size());

        SQLServerTriggerToggleStatement stmt = (SQLServerTriggerToggleStatement) stmts.get(0);
        assertFalse(stmt.isEnable());
        assertTrue(stmt.isAll());
        assertEquals("tablename", stmt.getOn().toString());

        assertEquals("DISABLE TRIGGER ALL ON tablename", SQLUtils.toSQLString(stmt, com.alibaba.druid.DbType.sqlserver));
    }

    @Test
    public void enableTriggerAllOnTable() {
        String sql = "ENABLE TRIGGER ALL ON tablename";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "sqlserver");

        SQLServerTriggerToggleStatement stmt = (SQLServerTriggerToggleStatement) stmts.get(0);
        assertTrue(stmt.isEnable());
        assertTrue(stmt.isAll());
        assertEquals("ENABLE TRIGGER ALL ON tablename", SQLUtils.toSQLString(stmt, com.alibaba.druid.DbType.sqlserver));
    }

    @Test
    public void enableTriggerAllServer() {
        String sql = "DISABLE TRIGGER ALL SERVER";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "sqlserver");

        SQLServerTriggerToggleStatement stmt = (SQLServerTriggerToggleStatement) stmts.get(0);
        assertTrue(stmt.isAllServer());
        assertFalse(stmt.isAll());
    }
}

package com.alibaba.druid.bvt.sql.sqlserver.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * SQL Server stored-procedure execution option: EXEC proc ... WITH RECOMPILE.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6487">Issue #6487</a>
 */
public class Issue6487 {
    private static String rt(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.sqlserver);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), DbType.sqlserver).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_exec_with_recompile() {
        assertEquals("EXEC ProcName ?, ?, ?, ? WITH RECOMPILE",
                rt("EXEC ProcName ?,?,?,? WITH RECOMPILE"));
    }

    @Test
    public void test_exec_without_recompile_unchanged() {
        assertEquals("EXEC ProcName ?, ?", rt("EXEC ProcName ?, ?"));
    }
}

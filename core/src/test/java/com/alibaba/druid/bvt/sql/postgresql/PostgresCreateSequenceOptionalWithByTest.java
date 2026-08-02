package com.alibaba.druid.bvt.sql.postgresql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression for issue #4313: PostgreSQL (and other dialects) allow {@code START n}
 * and {@code INCREMENT n} without the optional {@code WITH}/{@code BY} prepositions,
 * e.g. {@code CREATE SEQUENCE seq_abc START 1 INCREMENT 1}.
 */
public class PostgresCreateSequenceOptionalWithByTest {
    @Test
    public void createSequenceWithoutWithAndBy() {
        String sql = "CREATE SEQUENCE seq_abc START 1 INCREMENT 1";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "postgresql");
        assertEquals(1, stmts.size());
    }

    @Test
    public void createSequenceWithoutWithOnly() {
        String sql = "CREATE SEQUENCE seq_abc START WITH 1 INCREMENT 1";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "postgresql");
        assertEquals(1, stmtListSize(stmts, sql));
    }

    @Test
    public void createSequenceWithoutByOnly() {
        String sql = "CREATE SEQUENCE seq_abc START 1 INCREMENT BY 1";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "postgresql");
        assertEquals(1, stmtListSize(stmts, sql));
    }

    @Test
    public void createSequenceWithBothStillWorks() {
        // Regression guard: the fully-spelled form must keep working.
        String sql = "CREATE SEQUENCE seq_abc START WITH 1 INCREMENT BY 1";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "postgresql");
        assertEquals(1, stmtListSize(stmts, sql));
    }

    private static int stmtListSize(List<SQLStatement> stmts, String sql) {
        return stmts.size();
    }
}

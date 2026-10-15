package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PostgreSQL CREATE TABLE with an auto-increment column GENERATED ALWAYS AS IDENTITY.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/5266">Issue #5266</a>
 */
public class Issue5266 {
    @Test
    public void test_generated_always_as_identity() {
        String sql = "CREATE TABLE t (id integer NOT NULL GENERATED ALWAYS AS IDENTITY, name varchar(10))";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.postgresql);
        assertEquals(1, stmts.size());
        assertTrue(SQLUtils.toSQLString(stmts.get(0), DbType.postgresql).contains("GENERATED ALWAYS AS IDENTITY"));
    }
}

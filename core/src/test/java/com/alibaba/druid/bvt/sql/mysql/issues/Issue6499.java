package com.alibaba.druid.bvt.sql.mysql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DELETE a FROM users FORCE INDEX(a1) WHERE ... must keep its WHERE clause.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6499">Issue #6499</a>
 */
public class Issue6499 {
    @Test
    public void test_delete_with_force_index_keeps_where() {
        String sql = "delete a from users force index(a1) where id < 10";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmts.size());
        assertTrue(SQLUtils.toSQLString(stmts.get(0), DbType.mysql).contains("WHERE id < 10"));
    }

    @Test
    public void test_delete_with_alias_force_index_keeps_where() {
        String sql = "delete a from users a force index(a1) where id < 10";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmts.size());
        assertTrue(SQLUtils.toSQLString(stmts.get(0), DbType.mysql).contains("WHERE id < 10"));
    }
}

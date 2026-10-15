package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The parentheses around a function expression that is then subscripted, e.g. (array_agg(name))[1],
 * must be preserved (otherwise the [1] subscript binds wrong and execution fails).
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6385">Issue #6385</a>
 */
public class Issue6385 {
    @Test
    public void test_subscript_on_parenthesized_function_kept() {
        String sql = "SELECT (array_agg(name))[1] FROM fruits GROUP BY id";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.postgresql);
        assertEquals(1, stmts.size());
        assertTrue(SQLUtils.toSQLString(stmts.get(0), DbType.postgresql).contains("(array_agg(name))[1]"));
    }

    @Test
    public void test_pager_count_keeps_parentheses() {
        String count = PagerUtils.count("SELECT (array_agg(name))[1] FROM fruits group by id", DbType.postgresql);
        assertTrue(count.contains("(array_agg(name))[1]"), count);
    }
}

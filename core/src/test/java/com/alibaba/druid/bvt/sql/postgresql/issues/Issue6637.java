package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * openGauss (registered as the postgresql dialect) supports Oracle-style hierarchical queries
 * CONNECT BY ... START WITH ...
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6637">Issue #6637</a>
 */
public class Issue6637 {
    private static String rt(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.postgresql);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), DbType.postgresql).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_connect_by_start_with() {
        String out = rt("select * from t connect by prior id = parent_id start with id = ''");
        assertTrue(out.contains("CONNECT BY PRIOR id = parent_id"), out);
        assertTrue(out.contains("START WITH id = ''"), out);
    }

    @Test
    public void test_start_with_connect_by() {
        String out = rt("select id, name from org start with parent_id is null connect by prior id = parent_id");
        assertTrue(out.contains("START WITH parent_id IS NULL"), out);
        assertTrue(out.contains("CONNECT BY PRIOR id = parent_id"), out);
    }

    @Test
    public void test_plain_select_unaffected() {
        assertEquals("SELECT * FROM t WHERE id = 1", rt("select * from t where id = 1"));
    }
}

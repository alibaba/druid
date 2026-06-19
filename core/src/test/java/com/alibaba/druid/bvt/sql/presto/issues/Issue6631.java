package com.alibaba.druid.bvt.sql.presto.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Presto subscript access col['k'] must keep the base expression and not be rendered as an
 * ARRAY[...] constructor.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6631">Issue #6631</a>
 */
public class Issue6631 {
    private static String rt(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.presto);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), DbType.presto).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_map_subscript_in_select() {
        assertEquals("SELECT ext['c1'] FROM test.test_table",
                rt("select ext['c1'] from test.test_table"));
    }

    @Test
    public void test_subscript_in_where() {
        assertEquals("SELECT * FROM t WHERE ext['c1'] = 1",
                rt("select * from t where ext['c1'] = 1"));
    }

    @Test
    public void test_array_constructor_still_works() {
        assertEquals("SELECT ARRAY[1, 2, 3]", rt("select ARRAY[1,2,3]"));
    }
}

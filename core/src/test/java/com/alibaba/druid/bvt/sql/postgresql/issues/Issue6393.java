package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PostgreSQL VARIADIC function argument, e.g. func(VARIADIC ARRAY[...]).
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6393">Issue #6393</a>
 */
public class Issue6393 {
    private static String rt(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.postgresql);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), DbType.postgresql).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_variadic_array_argument() {
        String out = rt("SELECT jsonb_extract_path_text(a.info, VARIADIC ARRAY['w'::text, 'v'::text])::numeric "
                + "AS usage FROM t a");
        assertTrue(out.contains("VARIADIC ARRAY['w'::text, 'v'::text]"), out);
    }

    @Test
    public void test_variadic_simple() {
        assertTrue(rt("SELECT concat_ws(',', VARIADIC arr) FROM t").contains("VARIADIC arr"));
    }

    @Test
    public void test_variadic_as_column_name() {
        // a column literally named "variadic" must not be swallowed by the VARIADIC prefix
        assertEquals("SELECT variadic FROM t", rt("select variadic from t"));
        assertEquals("SELECT * FROM t WHERE variadic = 1", rt("select * from t where variadic = 1"));
    }
}

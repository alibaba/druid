package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PostgreSQL REFRESH MATERIALIZED VIEW [CONCURRENTLY] name [WITH [NO] DATA].
 *
 * @see <a href="https://github.com/alibaba/druid/issues/4987">Issue #4987</a>
 */
public class Issue4987 {
    private static String rt(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.postgresql);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), DbType.postgresql).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_refresh_concurrently_with_data() {
        assertEquals("REFRESH MATERIALIZED VIEW CONCURRENTLY mv WITH DATA",
                rt("REFRESH MATERIALIZED VIEW CONCURRENTLY mv WITH DATA"));
    }

    @Test
    public void test_refresh_with_no_data() {
        assertEquals("REFRESH MATERIALIZED VIEW mv WITH NO DATA",
                rt("REFRESH MATERIALIZED VIEW mv WITH NO DATA"));
    }
}

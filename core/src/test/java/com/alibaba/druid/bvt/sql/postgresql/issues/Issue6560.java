package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PostgreSQL CTE materialization hint: WITH name AS [NOT] MATERIALIZED (query).
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6560">Issue #6560</a>
 */
public class Issue6560 {
    private static String rt(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.postgresql);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), DbType.postgresql).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_materialized() {
        assertTrue(rt("with t as materialized (select 1) select * from t")
                .contains("AS MATERIALIZED ("), "should keep MATERIALIZED");
    }

    @Test
    public void test_not_materialized() {
        assertTrue(rt("with t as not materialized (select 1) select * from t")
                .contains("AS NOT MATERIALIZED ("), "should keep NOT MATERIALIZED");
    }

    @Test
    public void test_recursive_with_materialized() {
        String out = rt("with t as materialized (with recursive tmp as "
                + "(select id, parent from ml union all select c.id, c.parent from ml c join tmp p on c.parent = p.id) "
                + "select * from tmp) select * from t");
        assertTrue(out.contains("AS MATERIALIZED ("));
        assertTrue(out.contains("WITH RECURSIVE tmp"));
    }

    @Test
    public void test_plain_cte_unchanged() {
        assertEquals("WITH t AS ( SELECT 1 ) SELECT * FROM t",
                rt("with t as (select 1) select * from t"));
    }
}

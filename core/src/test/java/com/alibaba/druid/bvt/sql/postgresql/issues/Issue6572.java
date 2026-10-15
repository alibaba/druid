package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A derived table over a UNION must accept a column-alias list: (subquery) AS t(col, ...).
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6572">Issue #6572</a>
 */
public class Issue6572 {
    private static String rt(DbType db, String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, db);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), db).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_union_derived_table_column_alias() {
        assertTrue(rt(DbType.postgresql, "select * from (select 1 union all select 2) AS t(time)")
                .contains("AS t (time)"), "column alias list must be kept");
    }

    @Test
    public void test_union_derived_table_alias_without_as() {
        assertTrue(rt(DbType.postgresql, "select * from (select 1 union all select 2) t(c1)")
                .contains("t (c1)"));
    }

    @Test
    public void test_plain_subquery_column_alias_unchanged() {
        assertEquals("SELECT * FROM ( SELECT 1 ) AS t (c1)",
                rt(DbType.postgresql, "select * from (select 1) AS t(c1)"));
    }

    @Test
    public void test_mysql_union_derived_table_column_alias() {
        assertTrue(rt(DbType.mysql, "select * from (select 1 union all select 2) AS t(c1)")
                .contains("AS t (c1)"));
    }
}

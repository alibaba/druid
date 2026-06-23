package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PostgreSQL ALTER TABLE ... ALTER COLUMN ... TYPE ... USING ..., and ADD a column named "version".
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6064">Issue #6064</a>
 */
public class Issue6064 {
    private static String rt(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.postgresql);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), DbType.postgresql).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_alter_column_type_using() {
        String out = rt("ALTER TABLE table_test ALTER COLUMN num TYPE int8 USING num::int8");
        assertTrue(out.contains("TYPE int8"), out);
        assertTrue(out.contains("USING num::int8"), out);
    }

    @Test
    public void test_add_column_named_version() {
        String out = rt("ALTER TABLE table_test ADD \"version\" int8 DEFAULT 1 NOT NULL");
        assertTrue(out.contains("\"version\""), out);
    }
}

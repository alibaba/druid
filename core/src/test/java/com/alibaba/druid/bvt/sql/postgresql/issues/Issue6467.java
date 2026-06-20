package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PostgreSQL ALTER TABLE ... ALTER COLUMN ... SET DATA TYPE (and the equivalent TYPE form).
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6467">Issue #6467</a>
 */
public class Issue6467 {
    private static String rt(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.postgresql);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), DbType.postgresql).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_set_data_type() {
        assertEquals("ALTER TABLE IF EXISTS tbl ALTER COLUMN col SET DATA TYPE varchar(20)",
                rt("alter table if exists tbl alter column col set data type varchar(20)"));
    }

    @Test
    public void test_type_shorthand() {
        assertEquals("ALTER TABLE tbl ALTER COLUMN col TYPE varchar(20)",
                rt("alter table tbl alter column col type varchar(20)"));
    }

    @Test
    public void test_existing_set_forms_unchanged() {
        assertTrue(rt("alter table tbl alter column col set default 1").contains("SET DEFAULT 1"));
        assertTrue(rt("alter table tbl alter column col set not null").contains("SET NOT NULL"));
        assertTrue(rt("alter table tbl alter column col drop default").contains("DROP DEFAULT"));
    }
}

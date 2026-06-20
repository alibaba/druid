package com.alibaba.druid.bvt.sql.mysql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MySQL / MariaDB ALTER TABLE ... ADD COLUMN IF NOT EXISTS.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6067">Issue #6067</a>
 */
public class Issue6067 {
    @Test
    public void test_add_column_if_not_exists() {
        String sql = "alter table aaa add column if not exists cc_count int(1) DEFAULT '5'";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmts.size());
        String out = SQLUtils.toSQLString(stmts.get(0), DbType.mysql);
        assertTrue(out.contains("ADD COLUMN IF NOT EXISTS cc_count"), out);
    }

    @Test
    public void test_add_column_without_if_not_exists_unchanged() {
        String sql = "alter table aaa add column cc_count int(1) DEFAULT '5'";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmts.size());
        assertTrue(SQLUtils.toSQLString(stmts.get(0), DbType.mysql).contains("ADD COLUMN cc_count"));
    }
}

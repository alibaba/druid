package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PostgreSQL ALTER TABLE ... ALTER COLUMN ... TYPE ... USING ... (same family as #6064).
 *
 * @see <a href="https://github.com/alibaba/druid/issues/4998">Issue #4998</a>
 */
public class Issue4998 {
    @Test
    public void test_alter_column_type_using() {
        String sql = "alter table dwd_order alter column ospl_status type BIGINT using ospl_status::BIGINT";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.postgresql);
        assertEquals(1, stmts.size());
        String out = SQLUtils.toSQLString(stmts.get(0), DbType.postgresql).replaceAll("\\s+", " ");
        assertTrue(out.contains("TYPE BIGINT"), out);
        assertTrue(out.contains("USING ospl_status::BIGINT"), out);
    }
}

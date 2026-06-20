package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PostgreSQL pagination OFFSET ... ROWS FETCH FIRST ... ROWS ONLY (Hibernate 6 style) must parse
 * (the reported symptom was an error at the FIRST token).
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6146">Issue #6146</a>
 */
public class Issue6146 {
    @Test
    public void test_offset_fetch_first_rows_only() {
        String sql = "select id from t order by id offset ? rows fetch first ? rows only";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.postgresql);
        assertEquals(1, stmts.size());
        String out = SQLUtils.toSQLString(stmts.get(0), DbType.postgresql).toUpperCase();
        assertTrue(out.contains("FETCH FIRST ? ROWS ONLY"), out);
    }
}

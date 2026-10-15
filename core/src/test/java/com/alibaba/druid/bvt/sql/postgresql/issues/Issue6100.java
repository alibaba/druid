package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PostgreSQL ORDER BY ... DESC NULLS LAST.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6100">Issue #6100</a>
 */
public class Issue6100 {
    @Test
    public void test_order_by_nulls_last() {
        String sql = "select * from t ORDER BY case_count DESC NULLS LAST, read_count DESC NULLS LAST LIMIT 5 OFFSET 0";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.postgresql);
        assertEquals(1, stmts.size());
        String out = SQLUtils.toSQLString(stmts.get(0), DbType.postgresql);
        assertTrue(out.contains("DESC NULLS LAST"), out);
    }
}

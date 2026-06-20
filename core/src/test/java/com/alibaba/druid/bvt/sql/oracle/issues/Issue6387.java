package com.alibaba.druid.bvt.sql.oracle.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Oracle max((subquery)) must keep the inner parentheses around the scalar subquery; otherwise it
 * renders as max(subquery) and fails with ORA-00936.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6387">Issue #6387</a>
 */
public class Issue6387 {
    @Test
    public void test_aggregate_over_parenthesized_subquery() {
        String sql = "select max((SELECT a.name FROM tcur a WHERE a.no = d.no)) vc_name from t d";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.oracle);
        assertEquals(1, stmts.size());
        String out = SQLUtils.toSQLString(stmts.get(0), DbType.oracle);
        assertTrue(out.contains("max((") && out.contains("))"), "inner parentheses must be kept:\n" + out);
    }
}

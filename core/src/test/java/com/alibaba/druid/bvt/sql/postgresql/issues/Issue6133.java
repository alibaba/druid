package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PostgreSQL CURRENT_DATE - INTERVAL '1 day' must parse and parameterize without error.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6133">Issue #6133</a>
 */
public class Issue6133 {
    @Test
    public void test_current_date_minus_interval() {
        String sql = "select TO_CHAR(CURRENT_DATE - INTERVAL '1 day', 'YYYYMMDD') from t";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.postgresql);
        assertEquals(1, stmts.size());
        assertTrue(SQLUtils.toSQLString(stmts.get(0), DbType.postgresql).contains("CURRENT_DATE"));
    }

    @Test
    public void test_parameterize_does_not_throw() {
        String sql = "select TO_CHAR(CURRENT_DATE - INTERVAL '1 day', 'YYYYMMDD') from t";
        String template = ParameterizedOutputVisitorUtils.parameterize(sql, DbType.postgresql);
        assertTrue(template != null && template.length() > 0);
    }
}

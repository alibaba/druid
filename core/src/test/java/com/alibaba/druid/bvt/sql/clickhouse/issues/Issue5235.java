package com.alibaba.druid.bvt.sql.clickhouse.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ClickHouse array-predicate functions hasAll / hasAny with array literal arguments.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/5235">Issue #5235</a>
 */
public class Issue5235 {
    @Test
    public void test_has_all_has_any() {
        for (String fn : new String[]{"hasAll", "hasAny"}) {
            String sql = "SELECT " + fn + "([1,2,3], [1,2]) FROM t";
            List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.clickhouse);
            assertEquals(1, stmts.size());
            assertTrue(SQLUtils.toSQLString(stmts.get(0), DbType.clickhouse).contains(fn + "("));
        }
    }
}

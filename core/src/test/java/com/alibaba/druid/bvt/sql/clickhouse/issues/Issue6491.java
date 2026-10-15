package com.alibaba.druid.bvt.sql.clickhouse.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ClickHouse ARRAY JOIN inside a parenthesized table source joined with another table.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6491">Issue #6491</a>
 */
public class Issue6491 {
    @Test
    public void test_array_join_in_parenthesized_table_source() {
        String sql = "SELECT trace_id, b.trace_name "
                + "FROM (default.my_table5 a ARRAY JOIN event_type AS event_type1, device_id AS device_id1) "
                + "LEFT JOIN default.my_table5_trace_id AS b ON a.trace_id = b.trace_id";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.clickhouse);
        assertEquals(1, stmts.size());
        String out = SQLUtils.toSQLString(stmts.get(0), DbType.clickhouse);
        assertTrue(out.contains("ARRAY JOIN"), out);
        assertTrue(out.contains("FROM (default.my_table5"), "parenthesized table source must be kept:\n" + out);
    }
}

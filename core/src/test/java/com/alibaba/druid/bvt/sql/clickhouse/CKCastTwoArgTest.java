package com.alibaba.druid.bvt.sql.clickhouse;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression for issue #4421: ClickHouse's function-style two-argument CAST,
 * e.g. {@code CAST(x, 'String')}, failed to parse (only {@code CAST(x AS type)} was accepted).
 */
public class CKCastTwoArgTest {
    @Test
    public void castTwoArgWithStringType() {
        String sql = "SELECT CAST(data_day, 'String') AS dt";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.clickhouse);
        assertEquals(1, stmts.size());
    }

    @Test
    public void castTwoArgWithNumericType() {
        String sql = "SELECT CAST(value, 'Int64') AS v FROM t";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.clickhouse);
        assertEquals(1, stmts.size());
    }

    @Test
    public void castAsFormStillWorks() {
        // Regression guard: the CAST(x AS type) form must keep working.
        String sql = "SELECT CAST(data_day AS String) AS dt";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.clickhouse);
        assertEquals(1, stmts.size());
    }
}

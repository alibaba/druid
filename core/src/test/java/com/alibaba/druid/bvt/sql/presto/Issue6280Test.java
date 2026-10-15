package com.alibaba.druid.bvt.sql.presto;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Presto try_cast(expr AS type) parsing.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6280">Issue #6280</a>
 */
public class Issue6280Test {
    @Test
    public void test_try_cast_basic() {
        String sql = "SELECT try_cast(dt_day AS int) FROM t";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.presto);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
        assertEquals(
                "SELECT TRY_CAST(dt_day AS int)\n"
                        + "FROM t",
                SQLUtils.toSQLString(stmtList.get(0), DbType.presto));
    }

    @Test
    public void test_try_cast_in_where() {
        String sql = "SELECT * FROM t WHERE try_cast(c AS bigint) > 0";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.presto);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
        assertEquals(
                "SELECT *\n"
                        + "FROM t\n"
                        + "WHERE TRY_CAST(c AS bigint) > 0",
                SQLUtils.toSQLString(stmtList.get(0), DbType.presto));
    }

    @Test
    public void test_try_cast_with_complex_expr() {
        String sql = "SELECT try_cast(dt_day AS int) FROM ads_test.employee_task_info_1d_f WHERE dt_day = '$[yyyyMMdd-1]'";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.presto);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
    }
}

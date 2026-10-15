package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MySqlLateralTest {
    @Test
    public void test_lateral_comma() {
        String sql = "SELECT * FROM t1, LATERAL (SELECT * FROM t2 WHERE t2.id = t1.id) AS lat";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.mysql);
        List<SQLStatement> stmts = parser.parseStatementList();
        assertEquals(1, stmts.size());

        SQLSelectStatement stmt = (SQLSelectStatement) stmts.get(0);
        String output = stmt.toString();
        assertTrue(output.contains("LATERAL"));
        SQLParseAssertUtil.assertParseSql(sql, DbType.mysql);
    }

    @Test
    public void test_lateral_join() {
        String sql = "SELECT * FROM t1 JOIN LATERAL (SELECT * FROM t2 WHERE t2.a = t1.a LIMIT 3) AS sub ON true";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.mysql);
        List<SQLStatement> stmts = parser.parseStatementList();
        assertEquals(1, stmts.size());

        SQLSelectStatement stmt = (SQLSelectStatement) stmts.get(0);
        String output = stmt.toString();
        assertTrue(output.contains("LATERAL"));
        SQLParseAssertUtil.assertParseSql(sql, DbType.mysql);
    }

    @Test
    public void test_lateral_subquery() {
        String sql = "SELECT t1.id, lat.total FROM orders t1, LATERAL (SELECT SUM(amount) AS total FROM order_items WHERE order_id = t1.id) AS lat";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.mysql);
        List<SQLStatement> stmts = parser.parseStatementList();
        assertEquals(1, stmts.size());

        SQLSelectStatement stmt = (SQLSelectStatement) stmts.get(0);
        String output = stmt.toString();
        assertTrue(output.contains("LATERAL"));
        SQLParseAssertUtil.assertParseSql(sql, DbType.mysql);
    }
}

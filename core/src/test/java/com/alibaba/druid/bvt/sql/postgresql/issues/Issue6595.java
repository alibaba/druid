package com.alibaba.druid.bvt.sql.postgresql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @see <a href="https://github.com/alibaba/druid/issues/6595">Issue来源</a>
 */
public class Issue6595 {

    @Test
    public void test_analyze_skip_locked() {
        String sql = "ANALYZE SKIP_LOCKED";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_analyze_verbose() {
        String sql = "ANALYZE VERBOSE";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_analyze_verbose_skip_locked() {
        String sql = "ANALYZE VERBOSE SKIP_LOCKED";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_analyze_verbose_table() {
        String sql = "ANALYZE VERBOSE my_table";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_analyze_bare() {
        String sql = "ANALYZE";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
    }
}

package com.alibaba.druid.bvt.sql.oracle.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @see <a href="https://github.com/alibaba/druid/issues/6702">Issue 6702</a>
 * Oracle parser is too lenient: a typo'd UPDATE (e.g. 'WHER' instead of 'WHERE')
 * is silently split into multiple statements instead of raising an error, while
 * other dialects (mysql, postgresql, ...) already reject it.
 */
public class Issue6702 {
    @Test
    public void test_typo_where_is_rejected() {
        String sql = "UPDATE test SET value = 1 WHER id = 1";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
        assertThrows(ParserException.class, () -> parser.parseStatementList());
    }

    @Test
    public void test_garbage_after_update_is_rejected() {
        String sql = "UPDATE test SET value = 1 XYZ id = 1";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
        assertThrows(ParserException.class, () -> parser.parseStatementList());
    }

    @Test
    public void test_correct_update_parses_as_single_statement() {
        String sql = "UPDATE test SET value = 1 WHERE id = 1";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(1, statementList.size());
    }

    @Test
    public void test_other_dialects_already_reject_typo() {
        // Baseline: MySQL already rejects the same typo'd SQL.
        assertThrows(ParserException.class,
                () -> SQLUtils.parseStatements("UPDATE test SET value = 1 WHER id = 1", DbType.mysql));
    }
}

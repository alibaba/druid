package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Issue6549 {
    @Test
    public void test_skip_line_comment() {
        String sql = "SELECT a,    -- comment 1\n b \n FROM t";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.mysql, SQLParserFeature.SkipComments);
        assertEquals(1, stmtList.size());
        String out = stmtList.get(0).toString().replace("\n", " ");
        assertFalse(out.contains("--"), "line comment should be skipped, but got: " + out);
        assertFalse(out.toLowerCase().contains("comment"), "line comment should be skipped, but got: " + out);
    }

    @Test
    public void test_skip_block_comment() {
        String sql = "SELECT a, /* comment 1 */ b FROM t";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.mysql, SQLParserFeature.SkipComments);
        assertEquals(1, stmtList.size());
        String out = stmtList.get(0).toString().replace("\n", " ");
        assertFalse(out.contains("/*"), "block comment should be skipped, but got: " + out);
        assertFalse(out.toLowerCase().contains("comment"), "block comment should be skipped, but got: " + out);
    }

    @Test
    public void test_keep_comment_by_default() {
        // sanity: without SkipComments, comments are still kept (no regression)
        String sql = "SELECT a, /* comment 1 */ b FROM t";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmtList.size());
        String out = stmtList.get(0).toString().replace("\n", " ");
        assertTrue(out.toLowerCase().contains("comment"), "default behavior should keep comments, but got: " + out);
    }
}

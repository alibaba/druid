package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
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

    @Test
    public void test_skip_block_comment_boolean_entry() {
        // parseStatements(sql, dbType, keepComments=false) reaches the lexer through the
        // boolean convenience overload, which used to pass an empty feature array.
        String sql = "SELECT a, /* comment 1 */ b FROM t";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.mysql, false);
        assertEquals(1, stmtList.size());
        String out = stmtList.get(0).toString().replace("\n", " ");
        assertFalse(out.contains("/*"), "keepComments=false should skip block comments, but got: " + out);
        assertFalse(out.toLowerCase().contains("comment"), "keepComments=false should skip block comments, but got: " + out);
    }

    @Test
    public void test_skip_line_comment_boolean_entry() {
        String sql = "SELECT a, -- comment 1\n b FROM t";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.mysql, false);
        assertEquals(1, stmtList.size());
        String out = stmtList.get(0).toString().replace("\n", " ");
        assertFalse(out.toLowerCase().contains("comment"), "keepComments=false should skip line comments, but got: " + out);
    }

    @Test
    public void test_skip_comments_boolean_entry_for_oracle() {
        // oracle is another dialect whose lexer constructor sets keepComments = true,
        // so it exercises the same leak through a second dialect.
        String sql = "SELECT a, /* comment 1 */ b FROM t";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.oracle, false);
        assertFalse(stmtList.isEmpty());
        String out = stmtList.get(0).toString().replace("\n", " ");
        assertFalse(out.toLowerCase().contains("comment"), "keepComments=false should skip comments for oracle, but got: " + out);
    }

    @Test
    public void test_keep_comments_boolean_entry() {
        String sql = "SELECT a, /* comment 1 */ b FROM t";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.mysql, true);
        assertEquals(1, stmtList.size());
        String out = stmtList.get(0).toString().replace("\n", " ");
        assertTrue(out.toLowerCase().contains("comment"), "keepComments=true should keep comments, but got: " + out);
    }

    @Test
    public void test_skip_comments_wins_over_keep_comments_in_either_order() {
        // SkipComments is authoritative over KeepComments regardless of call order; the
        // same feature set must not produce different output depending on the order in
        // which the two features are configured.
        String sql = "SELECT a, /* comment 1 */ b FROM t";

        List<SQLStatement> skipThenKeep = SQLUtils.parseStatements(
                sql, DbType.mysql, SQLParserFeature.SkipComments, SQLParserFeature.KeepComments);
        assertEquals(1, skipThenKeep.size());
        String out1 = skipThenKeep.get(0).toString().replace("\n", " ");
        assertFalse(out1.toLowerCase().contains("comment"),
                "SkipComments followed by KeepComments must stay comment-free, but got: " + out1);

        List<SQLStatement> keepThenSkip = SQLUtils.parseStatements(
                sql, DbType.mysql, SQLParserFeature.KeepComments, SQLParserFeature.SkipComments);
        assertEquals(1, keepThenSkip.size());
        String out2 = keepThenSkip.get(0).toString().replace("\n", " ");
        assertFalse(out2.toLowerCase().contains("comment"),
                "KeepComments followed by SkipComments must stay comment-free, but got: " + out2);
    }

    @Test
    public void test_keep_comments_only_still_keeps_comments() {
        // The opposite direction must keep working: the dialects default skipComment to
        // true, so KeepComments on its own must not be treated as "SkipComments is on".
        String sql = "SELECT a, /* comment 1 */ b FROM t";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.mysql, SQLParserFeature.KeepComments);
        assertEquals(1, stmtList.size());
        String out = stmtList.get(0).toString().replace("\n", " ");
        assertTrue(out.toLowerCase().contains("comment"),
                "KeepComments alone should keep comments, but got: " + out);
    }

    @Test
    public void test_skip_comments_off_restores_keep_comments() {
        // Turning SkipComments back off must restore the value that was in effect before
        // the suppression, instead of leaving keepComments permanently false.
        Lexer lexer = SQLParserUtils.createLexer("SELECT a FROM t", DbType.mysql);
        assertTrue(lexer.isKeepComments(), "mysql lexer should start with keepComments=true");

        lexer.config(SQLParserFeature.SkipComments, true);
        assertFalse(lexer.isKeepComments(), "SkipComments must suppress keepComments");

        lexer.config(SQLParserFeature.SkipComments, false);
        assertTrue(lexer.isKeepComments(),
                "turning SkipComments off should restore the constructor default");
    }
}

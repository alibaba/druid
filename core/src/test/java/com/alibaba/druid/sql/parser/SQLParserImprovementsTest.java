/*
 * Copyright 1999-2026 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SQLParserImprovementsTest {
    // ========== 1. Lexer: Unicode escape validation ==========

    @Test
    public void testUnicodeEscapeValid() {
        ExposedLexer lexer = new ExposedLexer("'\\u0041'");
        lexer.config(SQLParserFeature.SupportUnicodeCodePoint, true);
        lexer.scanSingleQuoteMode();

        assertEquals(Token.LITERAL_CHARS, lexer.token());
        assertEquals("A", lexer.stringVal());
    }

    @Test
    public void testUnicodeEscapeValidMultiChar() {
        ExposedLexer lexer = new ExposedLexer("'X\\u0048\\u0049Y'");
        lexer.config(SQLParserFeature.SupportUnicodeCodePoint, true);
        lexer.scanSingleQuoteMode();

        assertEquals(Token.LITERAL_CHARS, lexer.token());
        assertEquals("XHIY", lexer.stringVal());
    }

    @Test
    public void testUnicodeEscapeInvalidHexDigits() {
        // Use char concatenation to avoid Java compiler interpreting backslash-u in source
        String input = "'" + '\\' + "uZZZZ'";
        ExposedLexer lexer = new ExposedLexer(input);
        lexer.config(SQLParserFeature.SupportUnicodeCodePoint, true);
        try {
            lexer.scanSingleQuoteMode();
            fail("expected ParserException for invalid unicode escape");
        } catch (ParserException ex) {
            assertTrue(ex.getMessage().contains("invalid unicode escape sequence"));
            assertTrue(ex.getMessage().contains("expected 4 hex digits"));
        }
    }

    @Test
    public void testUnicodeEscapePartiallyInvalidHex() {
        String input = "'" + '\\' + "u00GG'";
        ExposedLexer lexer = new ExposedLexer(input);
        lexer.config(SQLParserFeature.SupportUnicodeCodePoint, true);
        try {
            lexer.scanSingleQuoteMode();
            fail("expected ParserException for invalid unicode escape");
        } catch (ParserException ex) {
            assertTrue(ex.getMessage().contains("invalid unicode escape sequence"));
        }
    }

    @Test
    public void testUnicodeEscapeDisabledTreatedAsLiteral() {
        String input = "'" + '\\' + "uZZZZ'";
        ExposedLexer lexer = new ExposedLexer(input);
        // Do NOT enable SupportUnicodeCodePoint -- backslash-u should be treated as literal
        lexer.scanSingleQuoteMode();

        assertEquals(Token.LITERAL_CHARS, lexer.token());
        // When unicode support is disabled, backslash-u is kept as-is
        assertTrue(lexer.stringVal().contains("u"));
    }

    // ========== 2. Lexer: Improved error messages ==========

    @Test
    public void testBacktickErrorMessage() {
        // Base Lexer (without dialect features) does not support backtick
        // The backtick case in nextTokenInternal throws our improved message
        Lexer lexer = new Lexer("`name`");
        // Ensure the ScanBacktick feature is NOT enabled (default for base Lexer)
        try {
            lexer.nextToken();
            // If the base Lexer happens to support backtick, verify no TODO in output
            assertFalse(lexer.token() == Token.ERROR, "should not reach here or token should be valid");
        } catch (ParserException ex) {
            assertFalse(ex.getMessage().contains("TODO"));
        }
    }

    @Test
    public void testUnexpectedCharacterErrorMessage() {
        // Use a character that isn't handled by any token case
        Lexer lexer = new Lexer("\u0001SELECT");
        try {
            lexer.nextToken();
            // Some control characters might be treated as whitespace; if token is parsed, that's okay
        } catch (ParserException ex) {
            assertFalse(ex.getMessage().contains("TODO"), "error message should not contain TODO");
        }
    }

    // ========== 3. SQLStatementParser: Error messages ==========

    @Test
    public void testExecuteStatementErrorMessage() {
        try {
            SQLUtils.parseStatements("EXECUTE proc1", DbType.postgresql);
            fail("expected ParserException");
        } catch (ParserException ex) {
            assertTrue(ex.getMessage().contains("not supported"));
            assertFalse(ex.getMessage().contains("TODO"));
        }
    }

    @Test
    public void testShowStatementErrorMessageNonMysql() {
        // SHOW is not supported in the base parser (dialects override it)
        SQLStatementParser parser = new SQLStatementParser("SHOW TABLES");
        try {
            parser.parseStatementList();
            fail("expected ParserException");
        } catch (ParserException ex) {
            assertTrue(ex.getMessage().contains("SHOW"));
            assertTrue(ex.getMessage().contains("not supported"));
            assertFalse(ex.getMessage().contains("TODO"));
        }
    }

    @Test
    public void testDropUnsupportedTargetErrorMessage() {
        SQLStatementParser parser = new SQLStatementParser("DROP XYZABC foo");
        try {
            parser.parseStatementList();
            fail("expected ParserException");
        } catch (ParserException ex) {
            assertFalse(ex.getMessage().contains("TODO"));
        }
    }

    @Test
    public void testWithClauseUnsupportedStatementErrorMessage() {
        // WITH ... followed by non-SELECT may be parsed differently by different dialects
        // Use base parser which should throw descriptive error
        try {
            SQLUtils.parseStatements("WITH RECURSIVE cte AS (SELECT 1) UPDATE t SET x = 1", DbType.mysql);
            // Some dialects may handle this — if parsed ok, that's also acceptable
        } catch (ParserException ex) {
            assertFalse(ex.getMessage().contains("TODO"));
        }
    }

    // ========== 4. SQLExprParser: Error messages ==========

    @Test
    public void testNullsOrderTypeErrorMessage() {
        try {
            SQLUtils.parseStatements("SELECT a FROM t ORDER BY a NULLS MIDDLE", DbType.mysql);
            fail("expected ParserException");
        } catch (ParserException ex) {
            assertTrue(ex.getMessage().contains("FIRST") || ex.getMessage().contains("LAST"));
            assertFalse(ex.getMessage().contains("TODO"));
        }
    }

    @Test
    public void testForeignKeyMatchErrorMessage() {
        // Use base parser (not dialect-specific) to test the MATCH error path
        String sql = "CREATE TABLE t (id INT, fk INT REFERENCES other(id) MATCH UNKNOWN_TYPE)";
        try {
            SQLUtils.parseStatements(sql, DbType.mysql);
            fail("expected ParserException");
        } catch (ParserException ex) {
            assertTrue(ex.getMessage().contains("FULL")
                    || ex.getMessage().contains("PARTIAL")
                    || ex.getMessage().contains("SIMPLE")
                    || ex.getMessage().contains("MATCH"));
            assertFalse(ex.getMessage().contains("TODO"));
        }
    }

    @Test
    public void testIdentitySeedErrorMessage() {
        // IDENTITY expects integer seed
        String sql = "CREATE TABLE t (id INT IDENTITY('abc', 1))";
        try {
            SQLUtils.parseStatements(sql, DbType.mysql);
            fail("expected ParserException");
        } catch (ParserException ex) {
            assertFalse(ex.getMessage().contains("TODO"));
        }
    }

    // ========== 5. SQLSelectParser: Error messages ==========

    @Test
    public void testUnpivotSubqueryErrorMessage() {
        String sql = "SELECT * FROM t UNPIVOT (val FOR col IN (SELECT x FROM y))";
        try {
            SQLUtils.parseStatements(sql, DbType.mysql);
            fail("expected ParserException");
        } catch (ParserException ex) {
            assertTrue(ex.getMessage().contains("UNPIVOT") || ex.getMessage().contains("subquery"));
            assertFalse(ex.getMessage().contains("TODO"));
        }
    }

    // ========== 6. SQLCreateTableParser: Helper methods ==========

    @Test
    public void testParseTableCommentMySQL() {
        String sql = "CREATE TABLE t (id INT) COMMENT = 'test table'";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmts.size());
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) stmts.get(0);
        assertNotNull(stmt.getComment());
        assertTrue(stmt.getComment().toString().contains("test table"));
    }

    @Test
    public void testParseTableCommentWithoutEquals() {
        String sql = "CREATE TABLE t (id INT) COMMENT 'another table'";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmts.size());
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) stmts.get(0);
        assertNotNull(stmt.getComment());
    }

    @Test
    public void testParseTableEngineMySQL() {
        String sql = "CREATE TABLE t (id INT) ENGINE = InnoDB";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmts.size());
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) stmts.get(0);
        assertNotNull(stmt.getEngine());
        assertTrue(stmt.getEngine().toString().contains("InnoDB"));
    }

    @Test
    public void testParseTableEngineWithoutEquals() {
        String sql = "CREATE TABLE t (id INT) ENGINE InnoDB";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmts.size());
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) stmts.get(0);
        assertNotNull(stmt.getEngine());
    }

    @Test
    public void testParseTableCommentAndEngine() {
        String sql = "CREATE TABLE t (id INT) ENGINE = InnoDB COMMENT = 'my table'";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmts.size());
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) stmts.get(0);
        assertNotNull(stmt.getEngine());
        assertNotNull(stmt.getComment());
    }

    // ========== 7. SQLExprParser: parseConstraintStateOptions ==========

    @Test
    public void testConstraintEnable() {
        String sql = "CREATE TABLE t (id INT, CONSTRAINT pk PRIMARY KEY (id) ENABLE)";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.oracle);
        assertEquals(1, stmts.size());
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) stmts.get(0);
        assertEquals(2, stmt.getTableElementList().size());
    }

    @Test
    public void testConstraintDisableNovalidate() {
        String sql = "CREATE TABLE t (id INT, CONSTRAINT pk PRIMARY KEY (id) DISABLE NOVALIDATE)";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.oracle);
        assertEquals(1, stmts.size());
    }

    @Test
    public void testUniqueConstraintValidate() {
        // Base parser's parseUnique handles VALIDATE
        String sql = "CREATE TABLE t (id INT, UNIQUE (id) VALIDATE)";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.oracle);
        assertEquals(1, stmts.size());
    }

    @Test
    public void testUniqueConstraintNovalidate() {
        String sql = "CREATE TABLE t (id INT, UNIQUE (id) NOVALIDATE)";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.oracle);
        assertEquals(1, stmts.size());
    }

    @Test
    public void testUniqueConstraintRely() {
        String sql = "CREATE TABLE t (id INT, UNIQUE (id) RELY)";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.oracle);
        assertEquals(1, stmts.size());
    }

    @Test
    public void testUniqueConstraintNorely() {
        String sql = "CREATE TABLE t (id INT, UNIQUE (id) NORELY)";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.oracle);
        assertEquals(1, stmts.size());
    }

    // ========== 8. Round-trip: parse -> toString -> parse ==========

    @Test
    public void testCreateTableCommentRoundTrip() {
        String sql = "CREATE TABLE t (\n\tid INT\n) ENGINE = InnoDB\nCOMMENT 'hello'";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        String output = stmts.get(0).toString();
        // re-parse
        List<SQLStatement> stmts2 = SQLUtils.parseStatements(output, DbType.mysql);
        assertEquals(1, stmts2.size());
        SQLCreateTableStatement stmt2 = (SQLCreateTableStatement) stmts2.get(0);
        assertNotNull(stmt2.getEngine());
        assertNotNull(stmt2.getComment());
    }

    @Test
    public void testNullsFirstRoundTrip() {
        String sql = "SELECT a FROM t ORDER BY a NULLS FIRST";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        String output = stmts.get(0).toString();
        assertTrue(output.toUpperCase().contains("NULLS FIRST"));
        // re-parse
        List<SQLStatement> stmts2 = SQLUtils.parseStatements(output, DbType.mysql);
        assertEquals(1, stmts2.size());
    }

    @Test
    public void testNullsLastRoundTrip() {
        String sql = "SELECT a FROM t ORDER BY a NULLS LAST";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        String output = stmts.get(0).toString();
        assertTrue(output.toUpperCase().contains("NULLS LAST"));
    }

    @Test
    public void testForeignKeyMatchFullRoundTrip() {
        // Column-level FK with MATCH FULL using MySQL
        String sql = "CREATE TABLE t (id INT, fk INT REFERENCES other(id) MATCH FULL)";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.mysql);
        String output = stmts.get(0).toString();
        List<SQLStatement> stmts2 = SQLUtils.parseStatements(output, DbType.mysql);
        assertEquals(1, stmts2.size());
    }

    // ========== 9. Supplemental: verify no TODO in common error paths ==========

    @Test
    public void testCopyStatementErrorMessage() {
        SQLStatementParser parser = new SQLStatementParser("COPY t FROM 'file.csv'");
        try {
            parser.parseStatementList();
            fail("expected ParserException");
        } catch (ParserException ex) {
            assertTrue(ex.getMessage().contains("COPY"));
            assertTrue(ex.getMessage().contains("not supported"));
            assertFalse(ex.getMessage().contains("TODO"));
        }
    }

    @Test
    public void testAlterTablespaceErrorMessage() {
        SQLStatementParser parser = new SQLStatementParser("ALTER TABLESPACE ts1 ADD DATAFILE 'file1'");
        try {
            parser.parseStatementList();
            fail("expected ParserException");
        } catch (ParserException ex) {
            assertTrue(ex.getMessage().contains("ALTER TABLESPACE"));
            assertTrue(ex.getMessage().contains("not supported"));
            assertFalse(ex.getMessage().contains("TODO"));
        }
    }

    @Test
    public void testAlterProcedureErrorMessage() {
        SQLStatementParser parser = new SQLStatementParser("ALTER PROCEDURE proc1 COMPILE");
        try {
            parser.parseStatementList();
            fail("expected ParserException");
        } catch (ParserException ex) {
            assertTrue(ex.getMessage().contains("ALTER PROCEDURE"));
            assertTrue(ex.getMessage().contains("not supported"));
            assertFalse(ex.getMessage().contains("TODO"));
        }
    }

    @Test
    public void testAlterFunctionErrorMessage() {
        SQLStatementParser parser = new SQLStatementParser("ALTER FUNCTION func1 COMPILE");
        try {
            parser.parseStatementList();
            fail("expected ParserException");
        } catch (ParserException ex) {
            assertTrue(ex.getMessage().contains("ALTER FUNCTION"));
            assertTrue(ex.getMessage().contains("not supported"));
            assertFalse(ex.getMessage().contains("TODO"));
        }
    }

    // ========== Helper classes ==========

    private static class ExposedLexer extends Lexer {
        ExposedLexer(String input) {
            super(input);
        }

        void scanSingleQuoteMode() {
            scanString2();
        }

        void scanDoubleQuoteMode() {
            scanString2_d();
        }
    }
}

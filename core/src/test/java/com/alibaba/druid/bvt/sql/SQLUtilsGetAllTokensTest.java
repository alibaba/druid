package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.parser.Token;
import junit.framework.TestCase;

import java.util.List;

/**
 * Test for SQLUtils.getAllTokens method
 */
public class SQLUtilsGetAllTokensTest extends TestCase {
    public void test() {
        String sql = "select id, name, age from users where age > 18 and status = 'active'";

        List<SQLUtils.TokenInfo> tokens = SQLUtils.getAllTokens(sql, DbType.mysql);

        // Display all tokens with details
        System.out.println("=== All Tokens Encountered During Parsing ===");
        System.out.println("SQL: " + sql);
        System.out.println("Total tokens: " + tokens.size());
        System.out.println();

        for (int i = 0; i < tokens.size(); i++) {
            SQLUtils.TokenInfo tokenPair = tokens.get(i);
            int position = tokenPair.getPos();
            Token token = tokenPair.getToken();
            String stringVal = tokenPair.getStringVal();

            // Extract the actual text from SQL for this token
            if (position < sql.length()) {
                int endPos = position + 1;
                while (endPos < sql.length() && !Character.isWhitespace(sql.charAt(endPos))) {
                    endPos++;
                }
            }

            System.out.printf("[%2d] Pos: %2d, Token: %-20s, Text: '%s'",
                    i, position, token, stringVal);

            System.out.println();
        }

        System.out.println();
    }
    public void test_getAllTokens_mysql() throws Exception {
        String sql = "SELECT id, name FROM user WHERE age > 18";
        List<SQLUtils.TokenInfo> tokens = SQLUtils.getAllTokens(sql, DbType.mysql);

        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);
        assertEquals(Token.SELECT, tokens.get(0).getToken());

        // Check if specific tokens exist
        boolean hasFrom = false;
        boolean hasWhere = false;
        for (SQLUtils.TokenInfo info : tokens) {
            if (info.getToken() == Token.FROM) {
                hasFrom = true;
            }
            if (info.getToken() == Token.WHERE) {
                hasWhere = true;
            }
        }
        assertTrue(hasFrom);
        assertTrue(hasWhere);
        assertEquals(Token.EOF, tokens.get(tokens.size() - 1).getToken());

        // Print tokens for debugging
        System.out.println("MySQL Tokens: " + tokens);
    }

    public void test_getAllTokens_oracle() throws Exception {
        String sql = "SELECT * FROM employees WHERE salary > 5000 ORDER BY name";
        List<SQLUtils.TokenInfo> tokens = SQLUtils.getAllTokens(sql, DbType.oracle);

        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);
        assertEquals(Token.SELECT, tokens.get(0).getToken());

        boolean hasFrom = false;
        boolean hasWhere = false;
        boolean hasOrder = false;
        for (SQLUtils.TokenInfo info : tokens) {
            if (info.getToken() == Token.FROM) {
                hasFrom = true;
            }
            if (info.getToken() == Token.WHERE) {
                hasWhere = true;
            }
            if (info.getToken() == Token.ORDER) {
                hasOrder = true;
            }
        }
        assertTrue(hasFrom);
        assertTrue(hasWhere);
        assertTrue(hasOrder);
        assertEquals(Token.EOF, tokens.get(tokens.size() - 1).getToken());

        System.out.println("Oracle Tokens: " + tokens);
    }

    public void test_getAllTokens_with_string_dbType() throws Exception {
        String sql = "INSERT INTO users (id, name) VALUES (1, 'test')";
        List<SQLUtils.TokenInfo> tokens = SQLUtils.getAllTokens(sql, "mysql");

        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);
        assertEquals(Token.INSERT, tokens.get(0).getToken());

        boolean hasInto = false;
        boolean hasValues = false;
        for (SQLUtils.TokenInfo info : tokens) {
            if (info.getToken() == Token.INTO) {
                hasInto = true;
            }
            if (info.getToken() == Token.VALUES) {
                hasValues = true;
            }
        }
        assertTrue(hasInto);
        assertTrue(hasValues);
        assertEquals(Token.EOF, tokens.get(tokens.size() - 1).getToken());

        System.out.println("String DbType Tokens: " + tokens);
    }

    public void test_getAllTokens_with_stringVal() throws Exception {
        String sql = "SELECT name FROM users WHERE id = 123";
        List<SQLUtils.TokenInfo> tokens = SQLUtils.getAllTokens(sql, DbType.mysql);

        assertNotNull(tokens);

        // Print tokens for debugging
        System.out.println("Tokens with stringVal: " + tokens);

        // Check for identifier 'name'
        boolean foundName = false;
        boolean foundUsers = false;
        boolean foundId = false;
        boolean found123 = false;

        for (SQLUtils.TokenInfo info : tokens) {
            if (info.getToken() == Token.IDENTIFIER && "name".equals(info.getStringVal())) {
                foundName = true;
            }
            if (info.getToken() == Token.IDENTIFIER && "users".equalsIgnoreCase(info.getStringVal())) {
                foundUsers = true;
            }
            if (info.getToken() == Token.IDENTIFIER && "id".equals(info.getStringVal())) {
                foundId = true;
            }
            if (info.getToken() == Token.LITERAL_INT && "123".equals(info.getStringVal())) {
                found123 = true;
            }
        }

        assertTrue("Should find identifier 'name'", foundName);
        assertTrue("Should find identifier 'users'", foundUsers);
        assertTrue("Should find identifier 'id'", foundId);
        assertTrue("Should find literal int '123'", found123);
    }

    public void test_getAllTokens_postgresql() throws Exception {
        String sql = "UPDATE products SET price = 100 WHERE category = 'electronics'";
        List<SQLUtils.TokenInfo> tokens = SQLUtils.getAllTokens(sql, DbType.postgresql);

        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);
        assertEquals(Token.UPDATE, tokens.get(0).getToken());

        boolean hasSet = false;
        boolean hasWhere = false;
        for (SQLUtils.TokenInfo info : tokens) {
            if (info.getToken() == Token.SET) {
                hasSet = true;
            }
            if (info.getToken() == Token.WHERE) {
                hasWhere = true;
            }
        }
        assertTrue(hasSet);
        assertTrue(hasWhere);
        assertEquals(Token.EOF, tokens.get(tokens.size() - 1).getToken());

        System.out.println("PostgreSQL Tokens: " + tokens);
    }

    public void test_getAllTokens_empty_sql() throws Exception {
        String sql = "";
        List<SQLUtils.TokenInfo> tokens = SQLUtils.getAllTokens(sql, DbType.mysql);

        assertNotNull(tokens);
        assertEquals(0, tokens.size());
    }

    public void test_getAllTokens_null_sql() throws Exception {
        String sql = null;
        List<SQLUtils.TokenInfo> tokens = SQLUtils.getAllTokens(sql, DbType.mysql);

        assertNotNull(tokens);
        assertEquals(0, tokens.size());
    }

    public void test_getAllTokens_complex_sql() throws Exception {
        String sql = "SELECT u.id, u.name, COUNT(o.id) AS order_count " +
                "FROM users u " +
                "LEFT JOIN orders o ON u.id = o.user_id " +
                "WHERE u.status = 'active' " +
                "GROUP BY u.id, u.name " +
                "HAVING COUNT(o.id) > 5 " +
                "ORDER BY order_count DESC " +
                "LIMIT 10";

        List<SQLUtils.TokenInfo> tokens = SQLUtils.getAllTokens(sql, DbType.mysql);

        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);
        assertEquals(Token.SELECT, tokens.get(0).getToken());
        assertEquals(Token.EOF, tokens.get(tokens.size() - 1).getToken());

        System.out.println("Complex SQL Tokens count: " + tokens.size());
        System.out.println("Complex SQL Tokens: " + tokens);
    }

    public void test_getAllTokens_create_table() throws Exception {
        String sql = "CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100))";
        List<SQLUtils.TokenInfo> tokens = SQLUtils.getAllTokens(sql, DbType.mysql);

        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);
        assertEquals(Token.CREATE, tokens.get(0).getToken());
        assertEquals(Token.EOF, tokens.get(tokens.size() - 1).getToken());

        System.out.println("Create Table Tokens: " + tokens);
    }

    public void test_getAllTokens_delete() throws Exception {
        String sql = "DELETE FROM users WHERE id = 5";
        List<SQLUtils.TokenInfo> tokens = SQLUtils.getAllTokens(sql, DbType.mysql);

        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);
        assertEquals(Token.DELETE, tokens.get(0).getToken());
        assertEquals(Token.EOF, tokens.get(tokens.size() - 1).getToken());

        System.out.println("Delete Tokens: " + tokens);
    }

    public void test_getAllTokens_with_position() throws Exception {
        String sql = "SELECT id FROM users WHERE age > 18";
        List<SQLUtils.TokenInfo> tokens = SQLUtils.getAllTokens(sql, DbType.mysql);

        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);

        // Verify that positions are being captured
        for (SQLUtils.TokenInfo info : tokens) {
            // Position should be >= 0
            assertTrue("Token position should be >= 0", info.getPos() >= 0);
        }

        // Verify the first token is SELECT
        assertEquals(Token.SELECT, tokens.get(0).getToken());
        // Position should be > 0 since it's the position after reading the token
        assertTrue("First token position should be > 0", tokens.get(0).getPos() > 0);

        // Positions should generally increase
        // Just verify the last token has a position >= first token
        assertTrue("Last token position should be >= first token position",
                tokens.get(tokens.size() - 1).getPos() >= tokens.get(0).getPos());

        System.out.println("Tokens with positions: " + tokens);
    }

    public void test_getAllTokens_filter_line_comment() throws Exception {
        String sql = "SELECT id FROM users -- this is a comment\nWHERE age > 18";
        List<SQLUtils.TokenInfo> tokens = SQLUtils.getAllTokens(sql, DbType.mysql);

        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);

        // Verify no LINE_COMMENT tokens in the result
        for (SQLUtils.TokenInfo info : tokens) {
            assertFalse("Should not contain LINE_COMMENT tokens", info.getToken() == Token.LINE_COMMENT);
        }

        // Verify essential tokens are still present
        boolean hasSelect = false;
        boolean hasFrom = false;
        boolean hasWhere = false;
        for (SQLUtils.TokenInfo info : tokens) {
            if (info.getToken() == Token.SELECT) {
                hasSelect = true;
            }
            if (info.getToken() == Token.FROM) {
                hasFrom = true;
            }
            if (info.getToken() == Token.WHERE) {
                hasWhere = true;
            }
        }
        assertTrue("Should contain SELECT token", hasSelect);
        assertTrue("Should contain FROM token", hasFrom);
        assertTrue("Should contain WHERE token", hasWhere);

        System.out.println("Tokens after filtering line comment: " + tokens);
    }

    public void test_getAllTokens_filter_multi_line_comment() throws Exception {
        String sql = "SELECT id /* this is a\nmulti-line comment */ FROM users WHERE age > 18";
        List<SQLUtils.TokenInfo> tokens = SQLUtils.getAllTokens(sql, DbType.mysql);

        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);

        // Verify no MULTI_LINE_COMMENT tokens in the result
        for (SQLUtils.TokenInfo info : tokens) {
            assertFalse("Should not contain MULTI_LINE_COMMENT tokens", info.getToken() == Token.MULTI_LINE_COMMENT);
        }

        // Verify essential tokens are still present
        boolean hasSelect = false;
        boolean hasFrom = false;
        boolean hasWhere = false;
        for (SQLUtils.TokenInfo info : tokens) {
            if (info.getToken() == Token.SELECT) {
                hasSelect = true;
            }
            if (info.getToken() == Token.FROM) {
                hasFrom = true;
            }
            if (info.getToken() == Token.WHERE) {
                hasWhere = true;
            }
        }
        assertTrue("Should contain SELECT token", hasSelect);
        assertTrue("Should contain FROM token", hasFrom);
        assertTrue("Should contain WHERE token", hasWhere);

        System.out.println("Tokens after filtering multi-line comment: " + tokens);
    }

    public void test_getAllTokens_filter_hint() throws Exception {
        String sql = "SELECT /*+ INDEX(users idx_age) */ id FROM users WHERE age > 18";
        List<SQLUtils.TokenInfo> tokens = SQLUtils.getAllTokens(sql, DbType.mysql);

        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);

        // Verify no HINT tokens in the result
        for (SQLUtils.TokenInfo info : tokens) {
            assertFalse("Should not contain HINT tokens", info.getToken() == Token.HINT);
        }

        // Verify essential tokens are still present
        boolean hasSelect = false;
        boolean hasFrom = false;
        boolean hasWhere = false;
        for (SQLUtils.TokenInfo info : tokens) {
            if (info.getToken() == Token.SELECT) {
                hasSelect = true;
            }
            if (info.getToken() == Token.FROM) {
                hasFrom = true;
            }
            if (info.getToken() == Token.WHERE) {
                hasWhere = true;
            }
        }
        assertTrue("Should contain SELECT token", hasSelect);
        assertTrue("Should contain FROM token", hasFrom);
        assertTrue("Should contain WHERE token", hasWhere);

        System.out.println("Tokens after filtering hint: " + tokens);
    }

    public void test_getAllTokens_filter_mixed_comments() throws Exception {
        String sql = "SELECT id, -- select id column\n" +
                "       name /* and name column */ FROM users -- from users table\n" +
                "WHERE age > 18 /* age filter */";
        List<SQLUtils.TokenInfo> tokens = SQLUtils.getAllTokens(sql, DbType.mysql);

        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);

        // Verify no comment tokens in the result
        for (SQLUtils.TokenInfo info : tokens) {
            assertFalse("Should not contain LINE_COMMENT tokens", info.getToken() == Token.LINE_COMMENT);
            assertFalse("Should not contain MULTI_LINE_COMMENT tokens", info.getToken() == Token.MULTI_LINE_COMMENT);
            assertFalse("Should not contain HINT tokens", info.getToken() == Token.HINT);
        }

        // Verify essential tokens are still present
        boolean hasSelect = false;
        boolean hasFrom = false;
        boolean hasWhere = false;
        for (SQLUtils.TokenInfo info : tokens) {
            if (info.getToken() == Token.SELECT) {
                hasSelect = true;
            }
            if (info.getToken() == Token.FROM) {
                hasFrom = true;
            }
            if (info.getToken() == Token.WHERE) {
                hasWhere = true;
            }
        }
        assertTrue("Should contain SELECT token", hasSelect);
        assertTrue("Should contain FROM token", hasFrom);
        assertTrue("Should contain WHERE token", hasWhere);

        System.out.println("Tokens after filtering mixed comments: " + tokens);
    }

    public void test_getAllTokens_oracle_with_comments() throws Exception {
        String sql = "SELECT * FROM employees -- get all employees\n" +
                "WHERE salary > 5000 /* high salary */ ORDER BY name";
        List<SQLUtils.TokenInfo> tokens = SQLUtils.getAllTokens(sql, DbType.oracle);

        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);

        // Verify no comment tokens in the result
        for (SQLUtils.TokenInfo info : tokens) {
            assertFalse("Should not contain LINE_COMMENT tokens", info.getToken() == Token.LINE_COMMENT);
            assertFalse("Should not contain MULTI_LINE_COMMENT tokens", info.getToken() == Token.MULTI_LINE_COMMENT);
        }

        assertEquals(Token.SELECT, tokens.get(0).getToken());
        assertEquals(Token.EOF, tokens.get(tokens.size() - 1).getToken());

        System.out.println("Oracle tokens after filtering comments: " + tokens);
    }

    public void test_getAllTokens_keepComments_true() throws Exception {
        String sql = "SELECT id FROM users -- this is a comment\nWHERE age > 18";
        List<SQLUtils.TokenInfo> tokens = SQLUtils.getAllTokens(sql, DbType.mysql, true);

        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);

        // Verify LINE_COMMENT token is present when keepComments=true
        boolean hasLineComment = false;
        for (SQLUtils.TokenInfo info : tokens) {
            if (info.getToken() == Token.LINE_COMMENT) {
                hasLineComment = true;
                break;
            }
        }
        assertTrue("Should contain LINE_COMMENT token when keepComments=true", hasLineComment);

        System.out.println("Tokens with keepComments=true: " + tokens);
    }

    public void test_getAllTokens_keepComments_false() throws Exception {
        String sql = "SELECT id FROM users -- this is a comment\nWHERE age > 18";
        List<SQLUtils.TokenInfo> tokens = SQLUtils.getAllTokens(sql, DbType.mysql, false);

        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);

        // Verify no LINE_COMMENT tokens when keepComments=false
        for (SQLUtils.TokenInfo info : tokens) {
            assertFalse("Should not contain LINE_COMMENT tokens when keepComments=false",
                    info.getToken() == Token.LINE_COMMENT);
        }

        System.out.println("Tokens with keepComments=false: " + tokens);
    }

    public void test_getAllTokens_keepComments_multiLine() throws Exception {
        String sql = "SELECT id /* comment */ FROM users WHERE age > 18";
        List<SQLUtils.TokenInfo> tokensWithComments = SQLUtils.getAllTokens(sql, DbType.mysql, true);
        List<SQLUtils.TokenInfo> tokensWithoutComments = SQLUtils.getAllTokens(sql, DbType.mysql, false);

        assertNotNull(tokensWithComments);
        assertNotNull(tokensWithoutComments);

        // Verify tokensWithComments contains MULTI_LINE_COMMENT
        boolean hasMultiLineComment = false;
        for (SQLUtils.TokenInfo info : tokensWithComments) {
            if (info.getToken() == Token.MULTI_LINE_COMMENT) {
                hasMultiLineComment = true;
                break;
            }
        }
        assertTrue("Should contain MULTI_LINE_COMMENT when keepComments=true", hasMultiLineComment);

        // Verify tokensWithoutComments does not contain MULTI_LINE_COMMENT
        for (SQLUtils.TokenInfo info : tokensWithoutComments) {
            assertFalse("Should not contain MULTI_LINE_COMMENT when keepComments=false",
                    info.getToken() == Token.MULTI_LINE_COMMENT);
        }

        // Verify tokensWithComments has more tokens than tokensWithoutComments
        assertTrue("Token list with comments should be larger",
                tokensWithComments.size() > tokensWithoutComments.size());

        System.out.println("Tokens with comments count: " + tokensWithComments.size());
        System.out.println("Tokens without comments count: " + tokensWithoutComments.size());
    }

    public void test_getAllTokens_keepComments_hint() throws Exception {
        String sql = "SELECT /*+ INDEX(users idx_age) */ id FROM users WHERE age > 18";
        List<SQLUtils.TokenInfo> tokensWithHints = SQLUtils.getAllTokens(sql, DbType.mysql, true);
        List<SQLUtils.TokenInfo> tokensWithoutHints = SQLUtils.getAllTokens(sql, DbType.mysql, false);

        assertNotNull(tokensWithHints);
        assertNotNull(tokensWithoutHints);

        // Verify tokensWithHints contains HINT
        boolean hasHint = false;
        for (SQLUtils.TokenInfo info : tokensWithHints) {
            if (info.getToken() == Token.HINT) {
                hasHint = true;
                break;
            }
        }
        assertTrue("Should contain HINT when keepComments=true", hasHint);

        // Verify tokensWithoutHints does not contain HINT
        for (SQLUtils.TokenInfo info : tokensWithoutHints) {
            assertFalse("Should not contain HINT when keepComments=false",
                    info.getToken() == Token.HINT);
        }

        System.out.println("Tokens with hint: " + tokensWithHints);
        System.out.println("Tokens without hint: " + tokensWithoutHints);
    }

    public void test_getAllTokens_keepComments_stringDbType() throws Exception {
        String sql = "SELECT id FROM users -- comment\nWHERE age > 18";
        List<SQLUtils.TokenInfo> tokensWithComments = SQLUtils.getAllTokens(sql, "mysql", true);
        List<SQLUtils.TokenInfo> tokensWithoutComments = SQLUtils.getAllTokens(sql, "mysql", false);

        assertNotNull(tokensWithComments);
        assertNotNull(tokensWithoutComments);

        // Verify keepComments parameter works with String dbType
        boolean hasLineComment = false;
        for (SQLUtils.TokenInfo info : tokensWithComments) {
            if (info.getToken() == Token.LINE_COMMENT) {
                hasLineComment = true;
                break;
            }
        }
        assertTrue("Should contain LINE_COMMENT when keepComments=true with String dbType", hasLineComment);

        for (SQLUtils.TokenInfo info : tokensWithoutComments) {
            assertFalse("Should not contain LINE_COMMENT when keepComments=false with String dbType",
                    info.getToken() == Token.LINE_COMMENT);
        }

        System.out.println("String dbType - Tokens with comments: " + tokensWithComments.size());
        System.out.println("String dbType - Tokens without comments: " + tokensWithoutComments.size());
    }
}

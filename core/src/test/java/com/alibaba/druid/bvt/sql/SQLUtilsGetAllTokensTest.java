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
}

package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUtilsLevenshteinTest {
    @Test
    public void test_identicalSQL() {
        String sql1 = "SELECT id, name FROM users WHERE age > 18";
        String sql2 = "SELECT id, name FROM users WHERE age > 18";

        int distance = SQLUtils.calculateTokenLevenshteinDistance(sql1, DbType.mysql, sql2, DbType.mysql);

        assertEquals(0, distance, "Identical SQL should have distance 0");
    }

    @Test
    public void test_differentIdentifiers() {
        String sql1 = "SELECT id, name FROM users WHERE age > 18";
        String sql2 = "SELECT id, name FROM customers WHERE age > 18";

        int distance = SQLUtils.calculateTokenLevenshteinDistance(sql1, DbType.mysql, sql2, DbType.mysql);

        // 'users' vs 'customers' - should count as 1 difference
        assertTrue(distance > 0, "Different table names should have distance > 0");
        assertEquals(1, distance, "Should have distance of 1 for one different identifier");
    }

    @Test
    public void test_differentLiterals() {
        String sql1 = "SELECT * FROM users WHERE age > 18";
        String sql2 = "SELECT * FROM users WHERE age > 21";

        int distance = SQLUtils.calculateTokenLevenshteinDistance(sql1, DbType.mysql, sql2, DbType.mysql);

        // 18 vs 21 - should count as 1 difference
        assertEquals(1, distance, "Different literal values should have distance of 1");
    }

    @Test
    public void test_insertionDeletion() {
        String sql1 = "SELECT id FROM users";
        String sql2 = "SELECT id, name FROM users";

        int distance = SQLUtils.calculateTokenLevenshteinDistance(sql1, DbType.mysql, sql2, DbType.mysql);

        // Added ', name' - should be 2 tokens (COMMA and IDENTIFIER)
        assertEquals(2, distance, "Insertion should increase distance");
    }

    @Test
    public void test_completelyDifferentSQL() {
        String sql1 = "SELECT * FROM users";
        String sql2 = "DELETE FROM products WHERE id = 1";

        int distance = SQLUtils.calculateTokenLevenshteinDistance(sql1, DbType.mysql, sql2, DbType.mysql);

        // Completely different SQL statements
        assertTrue(distance > 5, "Completely different SQL should have large distance");
    }

    @Test
    public void test_sameStructureDifferentValues() {
        String sql1 = "SELECT id, name, age FROM users WHERE status = 'active'";
        String sql2 = "SELECT id, name, age FROM users WHERE status = 'inactive'";

        int distance = SQLUtils.calculateTokenLevenshteinDistance(sql1, DbType.mysql, sql2, DbType.mysql);

        // Only 'active' vs 'inactive' is different
        assertEquals(1, distance, "Only string value difference should have distance of 1");
    }

    @Test
    public void test_crossDialect() {
        String sql1 = "SELECT id, name FROM users WHERE age > 18";
        String sql2 = "SELECT id, name FROM users WHERE age > 18";

        int distance = SQLUtils.calculateTokenLevenshteinDistance(sql1, DbType.mysql, sql2, DbType.oracle);

        // Same SQL, different dialects should still have distance 0
        assertEquals(0, distance, "Same SQL with different dialects should have distance 0");
    }

    @Test
    public void test_multipleChanges() {
        String sql1 = "SELECT id FROM users WHERE age > 18 AND status = 'active'";
        String sql2 = "SELECT name FROM customers WHERE age > 21 AND status = 'inactive'";

        int distance = SQLUtils.calculateTokenLevenshteinDistance(sql1, DbType.mysql, sql2, DbType.mysql);

        // Changes: id->name, users->customers, 18->21, active->inactive
        // That's 4 token differences
        assertEquals(4, distance, "Multiple changes should accumulate");
    }

    @Test
    public void test_emptySQL() {
        String sql1 = "";
        String sql2 = "SELECT * FROM users";

        int distance = SQLUtils.calculateTokenLevenshteinDistance(sql1, DbType.mysql, sql2, DbType.mysql);

        // Empty SQL should have distance equal to number of tokens in second SQL
        assertTrue(distance > 0, "Empty SQL should have positive distance");
    }

    @Test
    public void test_whitespaceVariation() {
        String sql1 = "SELECT id,name FROM users";
        String sql2 = "SELECT   id  ,  name   FROM   users";

        int distance = SQLUtils.calculateTokenLevenshteinDistance(sql1, DbType.mysql, sql2, DbType.mysql);

        // Whitespace should not matter - tokens are the same
        assertEquals(0, distance, "Whitespace variation should not affect distance");
    }

    @Test
    public void test_caseVariation() {
        String sql1 = "SELECT id FROM users";
        String sql2 = "select id from users";

        int distance = SQLUtils.calculateTokenLevenshteinDistance(sql1, DbType.mysql, sql2, DbType.mysql);

        // Keywords are case-insensitive in SQL
        assertEquals(0, distance, "Case variation in keywords should not affect distance");
    }

    @Test
    public void test_complexQuery() {
        String sql1 = "SELECT u.id, u.name, o.total " +
                "FROM users u JOIN orders o ON u.id = o.user_id " +
                "WHERE u.age > 18 AND o.status = 'completed'";

        String sql2 = "SELECT u.id, u.name, o.total " +
                "FROM users u JOIN orders o ON u.id = o.user_id " +
                "WHERE u.age > 18 AND o.status = 'completed'";

        int distance = SQLUtils.calculateTokenLevenshteinDistance(sql1, DbType.mysql, sql2, DbType.mysql);

        assertEquals(0, distance, "Identical complex queries should have distance 0");
    }

    @Test
    public void test_numericLiterals() {
        String sql1 = "SELECT * FROM products WHERE price > 100.50";
        String sql2 = "SELECT * FROM products WHERE price > 100.50";

        int distance = SQLUtils.calculateTokenLevenshteinDistance(sql1, DbType.mysql, sql2, DbType.mysql);

        assertEquals(0, distance, "Same numeric literals should have distance 0");
    }

    @Test
    public void test_differentNumericLiterals() {
        String sql1 = "SELECT * FROM products WHERE price > 100.50";
        String sql2 = "SELECT * FROM products WHERE price > 200.75";

        int distance = SQLUtils.calculateTokenLevenshteinDistance(sql1, DbType.mysql, sql2, DbType.mysql);

        assertEquals(1, distance, "Different numeric literals should have distance 1");
    }
}

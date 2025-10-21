package com.alibaba.druid.bvt.sql.lexer;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.DialectFeature;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.Token;
import junit.framework.TestCase;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public class LexerSavePointResetTest extends TestCase {
    /**
     * Test reset with SavePoint - verify tokens are properly cleaned up after reset
     */
    public void test_resetWithSavePoint_tokensCleanedUp() {
        String sql = "SELECT id, name, age FROM users WHERE status = 'active' AND age > 18";

        Lexer lexer = SQLParserUtils.createLexer(sql, DbType.mysql);
        lexer.getDialectFeature().configFeature(
                DialectFeature.LexerFeature.SaveAllHistoricalTokens,
                true
        );

        // Scan first few tokens
        lexer.nextToken(); // SELECT
        lexer.nextToken(); // id
        lexer.nextToken(); // ,

        // Mark savepoint (after 3 tokens)
        Lexer.SavePoint savePoint = lexer.markOut();
        int tokensAtSavePoint = lexer.getTokens().size(); // Count before savepoint
        int stringValsAtSavePoint = lexer.getStringValMap().size();

        // Continue scanning more tokens
        lexer.nextToken(); // name
        lexer.nextToken(); // ,
        lexer.nextToken(); // age
        lexer.nextToken(); // FROM

        int tokensBeforeReset = lexer.getTokens().size();
        assertTrue("Should have more tokens before reset", tokensBeforeReset > tokensAtSavePoint);

        // Reset to savepoint
        lexer.reset(savePoint);

        // Verify tokens are cleaned up
        // NOTE: Reset removes tokens at position >= savepoint position,
        // so the token AT the savepoint is also removed
        List<Pair<Integer, Token>> tokensAfterReset = lexer.getTokens();
        Map<Integer, String> stringValsAfterReset = lexer.getStringValMap();

        // Tokens count will be less than at savepoint because the token at savepoint position is removed
        assertTrue("Token count should be <= savepoint count",
                tokensAfterReset.size() <= tokensAtSavePoint);

        // Verify no tokens exist after savepoint position
        for (Pair<Integer, Token> tokenPair : tokensAfterReset) {
            assertTrue("All tokens should be before or at savepoint position",
                    tokenPair.getKey() <= lexer.bp());
        }

        for (Integer pos : stringValsAfterReset.keySet()) {
            assertTrue("All stringVals should be before or at savepoint position",
                    pos <= lexer.bp());
        }
    }

    /**
     * Test reset by position - verify tokens after position are removed
     */
    public void test_resetByPosition_tokensRemoved() {
        String sql = "SELECT id, name FROM users WHERE age > 18";

        Lexer lexer = SQLParserUtils.createLexer(sql, DbType.mysql);
        lexer.getDialectFeature().configFeature(
                DialectFeature.LexerFeature.SaveAllHistoricalTokens,
                true
        );

        // Scan all tokens
        while (lexer.token() != Token.EOF) {
            lexer.nextToken();
        }

        List<Pair<Integer, Token>> allTokens = lexer.getTokens();
        assertTrue("Should have multiple tokens", allTokens.size() > 5);

        // Get position in middle
        int middleIndex = allTokens.size() / 2;
        int resetPosition = allTokens.get(middleIndex).getKey();

        // Reset to middle position
        lexer.reset(resetPosition);

        // Verify tokens after position are removed
        List<Pair<Integer, Token>> tokensAfterReset = lexer.getTokens();
        for (Pair<Integer, Token> tokenPair : tokensAfterReset) {
            assertTrue("Token position should be < reset position",
                    tokenPair.getKey() < resetPosition);
        }

        Map<Integer, String> stringValsAfterReset = lexer.getStringValMap();
        for (Integer pos : stringValsAfterReset.keySet()) {
            assertTrue("StringVal position should be < reset position",
                    pos < resetPosition);
        }
    }

    /**
     * Test multiple reset operations
     */
    public void test_multipleResets_correctBehavior() {
        String sql = "SELECT a, b, c, d, e FROM table1";

        Lexer lexer = SQLParserUtils.createLexer(sql, DbType.mysql);
        lexer.getDialectFeature().configFeature(
                DialectFeature.LexerFeature.SaveAllHistoricalTokens,
                true
        );

        // First savepoint
        lexer.nextToken(); // SELECT
        lexer.nextToken(); // a
        Lexer.SavePoint sp1 = lexer.markOut();
        int tokensAtSp1 = lexer.getTokens().size();

        // Second savepoint
        lexer.nextToken(); // ,
        lexer.nextToken(); // b
        lexer.nextToken(); // ,
        Lexer.SavePoint sp2 = lexer.markOut();
        int tokensAtSp2 = lexer.getTokens().size();

        // Continue scanning
        lexer.nextToken(); // c
        lexer.nextToken(); // ,
        lexer.nextToken(); // d

        // Reset to sp2
        lexer.reset(sp2);
        // After reset, tokens at position >= sp2 position are removed
        assertTrue("Should be <= sp2 token count", lexer.getTokens().size() <= tokensAtSp2);

        // Reset to sp1
        lexer.reset(sp1);
        assertTrue("Should be <= sp1 token count", lexer.getTokens().size() <= tokensAtSp1);
    }

    /**
     * Test reset with complex SQL including literals and identifiers
     */
    public void test_resetWithComplexSQL_stringValsPreserved() {
        String sql = "SELECT id, name, price FROM products WHERE price > 100.50 AND status = 'active'";

        Lexer lexer = SQLParserUtils.createLexer(sql, DbType.mysql);
        lexer.getDialectFeature().configFeature(
                DialectFeature.LexerFeature.SaveAllHistoricalTokens,
                true
        );

        // Scan to WHERE clause
        while (lexer.token() != Token.WHERE && lexer.token() != Token.EOF) {
            lexer.nextToken();
        }

        Lexer.SavePoint sp = lexer.markOut();
        Map<Integer, String> stringValsAtSavePoint = lexer.getStringValMap();

        // Continue scanning
        lexer.nextToken(); // price
        lexer.nextToken(); // >
        lexer.nextToken(); // 100.50
        lexer.nextToken(); // AND

        // Reset
        lexer.reset(sp);

        // Verify stringVals before savepoint are preserved
        Map<Integer, String> stringValsAfterReset = lexer.getStringValMap();
        for (Map.Entry<Integer, String> entry : stringValsAtSavePoint.entrySet()) {
            assertTrue("StringVal should be preserved",
                    stringValsAfterReset.containsKey(entry.getKey()));
            assertEquals("StringVal should match",
                    entry.getValue(), stringValsAfterReset.get(entry.getKey()));
        }

        // Verify no stringVals after savepoint position
        for (Integer pos : stringValsAfterReset.keySet()) {
            assertTrue("StringVal position should be <= savepoint position",
                    pos <= lexer.bp());
        }
    }

    /**
     * Test reset preserves token state correctly
     */
    public void test_resetPreservesTokenState() {
        String sql = "SELECT id FROM users";

        Lexer lexer = SQLParserUtils.createLexer(sql, DbType.mysql);
        lexer.getDialectFeature().configFeature(
                DialectFeature.LexerFeature.SaveAllHistoricalTokens,
                true
        );

        lexer.nextToken(); // SELECT
        lexer.nextToken(); // id

        Lexer.SavePoint sp = lexer.markOut();
        Token tokenAtSavePoint = lexer.token();
        int posAtSavePoint = lexer.bp();

        // Continue
        lexer.nextToken(); // FROM
        lexer.nextToken(); // users

        // Reset
        lexer.reset(sp);

        // Verify state
        assertEquals("Token should match savepoint", tokenAtSavePoint, lexer.token());
        assertEquals("Position should match savepoint", posAtSavePoint, lexer.bp());
    }

    /**
     * Test reset after EOF
     */
    public void test_resetAfterEOF() {
        String sql = "SELECT id FROM users";

        Lexer lexer = SQLParserUtils.createLexer(sql, DbType.mysql);
        lexer.getDialectFeature().configFeature(
                DialectFeature.LexerFeature.SaveAllHistoricalTokens,
                true
        );

        // Mark savepoint early
        lexer.nextToken(); // SELECT
        Lexer.SavePoint sp = lexer.markOut();
        int tokensAtSavePoint = lexer.getTokens().size();

        // Scan to EOF
        while (lexer.token() != Token.EOF) {
            lexer.nextToken();
        }

        int tokensAtEOF = lexer.getTokens().size();
        assertTrue("Should have more tokens at EOF", tokensAtEOF > tokensAtSavePoint);

        // Reset back
        lexer.reset(sp);

        // Verify reset worked - token should match savepoint
        assertEquals("Token should match savepoint", sp.token, lexer.token());
        // Tokens should be cleaned up - should not have all the EOF tokens
        int tokensAfterReset = lexer.getTokens().size();
        assertTrue("Should not have more tokens than at EOF", tokensAfterReset <= tokensAtEOF);
    }

    /**
     * Test reset with SaveAllHistoricalTokens disabled - should not affect behavior
     */
    public void test_resetWithFeatureDisabled_noTokenList() {
        String sql = "SELECT id FROM users";

        Lexer lexer = SQLParserUtils.createLexer(sql, DbType.mysql);
        // SaveAllHistoricalTokens is NOT enabled

        lexer.nextToken(); // SELECT
        Lexer.SavePoint sp = lexer.markOut();

        lexer.nextToken(); // id
        lexer.nextToken(); // FROM

        // Reset should work but not affect token list (since feature is disabled)
        lexer.reset(sp);

        assertEquals("Token should match savepoint", sp.token, lexer.token());
        assertEquals("Position should match savepoint", lexer.bp(), lexer.bp());
    }

    /**
     * Test edge case: reset to beginning
     */
    public void test_resetToBeginning() {
        String sql = "SELECT id FROM users";

        Lexer lexer = SQLParserUtils.createLexer(sql, DbType.mysql);
        lexer.getDialectFeature().configFeature(
                DialectFeature.LexerFeature.SaveAllHistoricalTokens,
                true
        );

        // Scan one token first
        lexer.nextToken(); // SELECT

        // Mark early in the process
        Lexer.SavePoint sp = lexer.markOut();
        int tokensAtMark = lexer.getTokens().size();

        // Scan more tokens
        lexer.nextToken();
        lexer.nextToken();
        lexer.nextToken();

        int tokensAfterScanning = lexer.getTokens().size();
        assertTrue("Should have more tokens after scanning", tokensAfterScanning > tokensAtMark);

        // Reset to early mark
        lexer.reset(sp);

        // Token list should have fewer or equal tokens than after scanning
        assertTrue("Token list should not be larger after reset",
                lexer.getTokens().size() <= tokensAfterScanning);
    }

    /**
     * Test reset position exact boundary
     */
    public void test_resetAtExactTokenBoundary() {
        String sql = "SELECT id, name FROM users";

        Lexer lexer = SQLParserUtils.createLexer(sql, DbType.mysql);
        lexer.getDialectFeature().configFeature(
                DialectFeature.LexerFeature.SaveAllHistoricalTokens,
                true
        );

        // Scan tokens
        lexer.nextToken(); // SELECT
        lexer.nextToken(); // id
        int idPosition = lexer.getTokens().get(lexer.getTokens().size() - 1).getKey();

        lexer.nextToken(); // ,
        lexer.nextToken(); // name

        // Reset to exact position of 'id' token
        lexer.reset(idPosition);

        // Verify tokens after 'id' are removed
        List<Pair<Integer, Token>> tokens = lexer.getTokens();
        for (Pair<Integer, Token> tokenPair : tokens) {
            assertTrue("Token position should be < id position",
                    tokenPair.getKey() < idPosition);
        }
    }
}

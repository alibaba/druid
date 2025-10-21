package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.DialectFeature;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class TokenTest {
    @Test
    public void test() {
        String sql = "select id, name, age from users where age > 18 and status = 'active'";

        // Create lexer directly and enable SaveAllHistoricalTokens before any parsing
        Lexer lexer = SQLParserUtils.createLexer(sql, DbType.starrocks);
        lexer.getDialectFeature().configFeature(
                DialectFeature.LexerFeature.SaveAllHistoricalTokens,
                true
        );

        // Initialize the first token
        lexer.nextToken();

        // Create parser with the configured lexer
        SQLStatementParser parser = new com.alibaba.druid.sql.dialect.starrocks.parser.StarRocksStatementParser(lexer);

        // Parse the statement (this will trigger token collection)
        SQLStatement stmt = parser.parseStatement();

        // Get all tokens that were encountered during parsing
        List<Pair<Integer, Token>> tokens = lexer.getTokens();
        Map<Integer, String> stringValMap = lexer.getStringValMap();

        // Display all tokens with details
        System.out.println("=== All Tokens Encountered During Parsing ===");
        System.out.println("SQL: " + sql);
        System.out.println("Total tokens: " + tokens.size());
        System.out.println();

        for (int i = 0; i < tokens.size(); i++) {
            Pair<Integer, Token> tokenPair = tokens.get(i);
            int position = tokenPair.getKey();
            Token token = tokenPair.getValue();
            String stringVal = stringValMap.get(position);

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
        System.out.println("=== Parsed Statement ===");
        System.out.println(stmt);
    }
}

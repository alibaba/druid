package com.alibaba.druid.sql.dialect.blink.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

public class BlinkLexer extends Lexer {
    @Override
    protected Keywords loadKeywords() {
        Map<String, Token> map = new HashMap<String, Token>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("OF", Token.OF);
        map.put("CONCAT", Token.CONCAT);
        map.put("CONTINUE", Token.CONTINUE);
        map.put("MERGE", Token.MERGE);
        map.put("USING", Token.USING);

        map.put("ROW", Token.ROW);
        map.put("LIMIT", Token.LIMIT);
        map.put("IF", Token.IF);
        map.put("PERIOD", Token.PERIOD);

        return new Keywords(map);
    }

    public BlinkLexer(String input) {
        super(input);
        dbType = DbType.blink;
    }

    public BlinkLexer(String input, SQLParserFeature... features) {
        super(input);
        dbType = DbType.blink;
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }
}

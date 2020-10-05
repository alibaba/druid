package com.alibaba.druid.sql.dialect.ads.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

public class AdsLexer extends Lexer {
    public final static Keywords DEFAULT_ADS_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("OF", Token.OF);
        map.put("CONCAT", Token.CONCAT);
        map.put("CONTINUE", Token.CONTINUE);
        map.put("MERGE", Token.MERGE);
        map.put("USING", Token.USING);

        map.put("ROW", Token.ROW);
        map.put("LIMIT", Token.LIMIT);
        map.put("SHOW", Token.SHOW);
        map.put("ALL", Token.ALL);

        DEFAULT_ADS_KEYWORDS = new Keywords(map);
    }

    public AdsLexer(String input) {
        super(input);
        dbType = DbType.ads;
        super.keywods = DEFAULT_ADS_KEYWORDS;
    }

    public AdsLexer(String input, SQLParserFeature... features){
        super(input);
        super.keywods = DEFAULT_ADS_KEYWORDS;
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }
}

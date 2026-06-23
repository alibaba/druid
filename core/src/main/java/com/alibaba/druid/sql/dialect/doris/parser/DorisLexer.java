package com.alibaba.druid.sql.dialect.doris.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.starrocks.parser.StarRocksLexer;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

public class DorisLexer
        extends StarRocksLexer {
    static final Keywords DORIS_KEYWORDS;
    static {
        Map<String, Token> map = new HashMap<>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("DISTINCTROW", Token.DISTINCTROW);
        map.put("EXCEPT", Token.EXCEPT);
        map.put("TABLET", Token.TABLET);
        map.put("PARTITION", Token.PARTITION);
        map.put("ROWS", Token.ROWS);
        map.put("PERCENT", Token.PERCENT);
        map.put("REPEATABLE", Token.REPEATABLE);
        map.put("TABLESAMPLE", Token.TABLESAMPLE);
        map.put("USING", Token.USING);
        map.put("IF", Token.IF);

        map.put("ADD", Token.ADD);
        map.put("BOTH", Token.BOTH);
        map.put("DUAL", Token.DUAL);
        map.put("FALSE", Token.FALSE);
        map.put("FORCE", Token.FORCE);
        map.put("KILL", Token.KILL);
        map.put("BITMAP", Token.BITMAP);
        map.put("INVERTED", Token.INVERTED);

        map.put("TRUE", Token.TRUE);
        map.put("SHOW", Token.SHOW);
        map.put("ANALYZE", Token.ANALYZE);
        map.put("ROW", Token.ROW);
        map.put("MOD", Token.MOD);
        map.put("RLIKE", Token.RLIKE);
        map.put("OVERWRITE", Token.OVERWRITE);

        map.put("MATCH_ALL", Token.MATCH_ALL);
        map.put("MATCH_ANY", Token.MATCH_ANY);
        map.put("MATCH_PHRASE", Token.MATCH_PHRASE);
        map.put("MATCH_PHRASE_PREFIX", Token.MATCH_PHRASE_PREFIX);
        map.put("MATCH_REGEXP", Token.MATCH_REGEXP);

        DORIS_KEYWORDS = new Keywords(map);
    }

    @Override
    protected Keywords loadKeywords() {
        return DORIS_KEYWORDS;
    }

    public DorisLexer(String input, SQLParserFeature... features) {
        super(input);
        this.keepComments = true;
        dbType = DbType.doris;
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }
}

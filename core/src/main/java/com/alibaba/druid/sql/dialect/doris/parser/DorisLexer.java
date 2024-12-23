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
    @Override
    protected Keywords loadKeywords() {
        Map<String, Token> map = new HashMap<String, Token>();

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

        map.put("TRUE", Token.TRUE);
        map.put("SHOW", Token.SHOW);
        map.put("ANALYZE", Token.ANALYZE);
        map.put("ROW", Token.ROW);
        map.put("MOD", Token.MOD);
        map.put("RLIKE", Token.RLIKE);
        map.put("OVERWRITE", Token.OVERWRITE);

        return new Keywords(map);
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

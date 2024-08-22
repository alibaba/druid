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
//        map.put("DISTRIBUTED", Token.DISTRIBUTE);

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

package com.alibaba.druid.sql.dialect.bigquery.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

public class BigQueryLexer extends Lexer {
    public static final Keywords DEFAULT_BIG_QUERY_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("OF", Token.OF);
        map.put("CONCAT", Token.CONCAT);
        map.put("CONTINUE", Token.CONTINUE);
        map.put("MERGE", Token.MERGE);
        map.put("MATCHED", Token.MATCHED);
        map.put("USING", Token.USING);

        map.put("ROW", Token.ROW);
        map.put("LIMIT", Token.LIMIT);
        map.put("PARTITIONED", Token.PARTITIONED);
        map.put("PARTITION", Token.PARTITION);
        map.put("OVERWRITE", Token.OVERWRITE);
//        map.put("SORT", Token.SORT);
        map.put("IF", Token.IF);
        map.put("TRUE", Token.TRUE);
        map.put("FALSE", Token.FALSE);
        map.put("RLIKE", Token.RLIKE);
        map.put("CONSTRAINT", Token.CONSTRAINT);
        map.put("DIV", Token.DIV);

        DEFAULT_BIG_QUERY_KEYWORDS = new Keywords(map);
    }

    {
        dbType = DbType.bigquery;
    }

    public BigQueryLexer(String input, SQLParserFeature... features) {
        super(input);
        dbType = DbType.hive;
        this.skipComment = true;
        this.keepComments = true;
        super.keywords = DEFAULT_BIG_QUERY_KEYWORDS;
        this.features |= SQLParserFeature.SupportUnicodeCodePoint.mask;
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }
}

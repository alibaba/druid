package com.alibaba.druid.sql.dialect.presto.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.druid.sql.parser.DialectFeature.ParserFeature.SQLDateExpr;

public class PrestoLexer extends Lexer {
    @Override
    protected Keywords loadKeywords() {
        Map<String, Token> map = new HashMap<String, Token>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("FETCH", Token.FETCH);
        map.put("FIRST", Token.FIRST);
        map.put("ONLY", Token.ONLY);
        map.put("OPTIMIZE", Token.OPTIMIZE);
        map.put("OF", Token.OF);
        map.put("CONCAT", Token.CONCAT);
        map.put("CONTINUE", Token.CONTINUE);
        map.put("IDENTITY", Token.IDENTITY);
        map.put("MERGE", Token.MERGE);
        map.put("USING", Token.USING);
        map.put("MATCHED", Token.MATCHED);
        map.put("UPSERT", Token.UPSERT);

        map.put("IF", Token.IF);

        return new Keywords(map);
    }

    public PrestoLexer(String input, DbType dbType, SQLParserFeature... features) {
        super(input, dbType);
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }

    public PrestoLexer(String input, SQLParserFeature... features) {
        this(input, DbType.presto, features);
    }
    @Override
    protected void initDialectFeature() {
        super.initDialectFeature();
        this.dialectFeature.configFeature(SQLDateExpr);
    }
}

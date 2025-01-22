package com.alibaba.druid.sql.dialect.supersql.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.presto.parser.PrestoLexer;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

public class SuperSqlLexer extends PrestoLexer {
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
        map.put("OVERWRITE", Token.OVERWRITE);
        map.put("PARTITION", Token.PARTITION);
        map.put("PARTITIONED", Token.PARTITIONED);
        map.put("RLIKE", Token.RLIKE);

        return new Keywords(map);
    }
    public SuperSqlLexer(String input, SQLParserFeature... features) {
        super(input, features);
        this.dbType = DbType.supersql;
    }
}

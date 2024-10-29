package com.alibaba.druid.sql.dialect.gaussdb.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGLexer;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

public class GaussDbLexer extends PGLexer {
    @Override
    protected Keywords loadKeywords() {
        Map<String, Token> map = new HashMap<String, Token>();
        map.put("DISTRIBUTE", Token.DISTRIBUTE);
        map.put("SET", Token.SET);
        map.put("PARTITION", Token.PARTITION);
        map.put("START", Token.START);
        map.put("PARTIAL", Token.PARTIAL);
        map.put("KEY", Token.KEY);
        map.put("OVERWRITE", Token.OVERWRITE);
        map.put("LOCAL", Token.LOCAL);
        map.putAll(super.loadKeywords().getKeywords());
        return new Keywords(map);
    }

    public GaussDbLexer(String input, SQLParserFeature... features) {
        super(input);
        dbType = DbType.gaussdb;
    }
}

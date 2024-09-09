package com.alibaba.druid.sql.dialect.impala.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.hive.parser.HiveLexer;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.Map;

public class ImpalaLexer extends HiveLexer {
    @Override
    protected Keywords loadKeywords() {
        Keywords keywords = super.loadKeywords();
        Map<String, Token> map = keywords.getKeywords();
        map.put("UPSERT", Token.UPSERT);
        return new Keywords(map);
    }

    public ImpalaLexer(String input) {
        super(input);
        dbType = DbType.impala;
    }

    public ImpalaLexer(String input, SQLParserFeature... features) {
        super(input, features);
        dbType = DbType.impala;
    }
}

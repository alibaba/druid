package com.alibaba.druid.sql.dialect.impala.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.hive.parser.HiveLexer;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

public class ImpalaLexer extends HiveLexer {
    static final Keywords IMPALA_KEYWORDS;
    static {
        Map<String, Token> map = new HashMap<>(HiveLexer.HIVE_KEYWORDS.getKeywords());
        map.put("UPSERT", Token.UPSERT);
        IMPALA_KEYWORDS = new Keywords(map);
    }

    @Override
    protected Keywords loadKeywords() {
        return IMPALA_KEYWORDS;
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

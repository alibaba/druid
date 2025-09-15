package com.alibaba.druid.sql.dialect.teradata.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

public class TDLexer extends Lexer {
    static final Keywords TERADATA_KEYWORDS;
    static {
        Map<String, Token> map = new HashMap<>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("SEL", Token.SELECT);
        map.put("TOP", Token.TOP);
        map.put("QUALIFY", Token.QUALIFY);

        TERADATA_KEYWORDS = new Keywords(map);
    }

    @Override
    protected Keywords loadKeywords() {
        return TERADATA_KEYWORDS;
    }

    public TDLexer(String input, SQLParserFeature... features) {
        super(input);
        this.keepComments = true;
        dbType = DbType.teradata;
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }
}

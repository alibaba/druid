package com.alibaba.druid.sql.dialect.clickhouse.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.druid.sql.parser.DialectFeature.ParserFeature.*;

public class CKLexer extends Lexer {
    @Override
    protected Keywords loadKeywords() {
        Map<String, Token> map = new HashMap<String, Token>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("IF", Token.IF);
        map.put("OF", Token.OF);
        map.put("CONCAT", Token.CONCAT);
        map.put("CONTINUE", Token.CONTINUE);
        map.put("MERGE", Token.MERGE);
        map.put("USING", Token.USING);

        map.put("ROW", Token.ROW);
        map.put("LIMIT", Token.LIMIT);
        map.put("SHOW", Token.SHOW);
        map.put("ALL", Token.ALL);
        map.put("GLOBAL", Token.GLOBAL);
        map.put("PARTITION", Token.PARTITION);
        map.put("ILIKE", Token.ILIKE);
        map.put("PREWHERE", Token.PREWHERE);
        map.put("QUALIFY", Token.QUALIFY);
        map.put("FORMAT", Token.FORMAT);
        map.put("SETTINGS", Token.SETTINGS);
        map.put("FINAL", Token.FINAL);
        map.put("TTL", Token.TTL);
        map.put("CODEC", Token.CODEC);

        return new Keywords(map);
    }
    public CKLexer(String input) {
        super(input);
        dbType = DbType.clickhouse;
    }

    public CKLexer(String input, SQLParserFeature... features) {
        super(input);
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }

    @Override
    protected void initDialectFeature() {
        super.initDialectFeature();
        this.dialectFeature.configFeature(
                AsofJoin,
                GlobalJoin,
                JoinRightTableAlias,
                ParseLimitBy,
                TableAliasAsof
        );
    }
}

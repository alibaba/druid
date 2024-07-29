package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

public class StarRocksLexer extends Lexer {
    @Override
    protected Keywords loadKeywords() {
        Map<String, Token> map = new HashMap<String, Token>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        //        map.put("BITMAP", Token.BITMAP);
        map.put("USING", Token.USING);
        map.put("PARTITION", Token.PARTITION);

        return new Keywords(map);
    }

    public StarRocksLexer(String input) {
        this(input, true, true);
    }

    public StarRocksLexer(char[] input, int inputLength, boolean skipComment) {
        super(input, inputLength, skipComment);
        dbType = DbType.starrocks;
    }

    public StarRocksLexer(String input, SQLParserFeature... features) {
        super(input, true);
        this.keepComments = true;
        dbType = DbType.starrocks;

        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }

    public StarRocksLexer(String input, boolean skipComment, boolean keepComments) {
        super(input, skipComment);
        this.skipComment = skipComment;
        this.keepComments = keepComments;
        dbType = DbType.starrocks;
    }
}

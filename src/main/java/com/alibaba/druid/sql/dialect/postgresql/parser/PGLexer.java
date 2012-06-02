package com.alibaba.druid.sql.dialect.postgresql.parser;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.Token;

public class PGLexer extends Lexer {

    public final static Keywords DEFAULT_PG_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("CASCADE", Token.CASCADE);
        map.put("CONTINUE", Token.CONTINUE);
        map.put("CURRENT", Token.CURRENT);
        map.put("FETCH", Token.FETCH);
        map.put("FIRST", Token.FIRST);

        map.put("IDENTITY", Token.IDENTITY);
        map.put("LIMIT", Token.LIMIT);
        map.put("NEXT", Token.NEXT);
        map.put("NOWAIT", Token.NOWAIT);
        map.put("OF", Token.OF);

        map.put("OFFSET", Token.OFFSET);
        map.put("ONLY", Token.ONLY);
        map.put("OVER", Token.OVER);
        map.put("RECURSIVE", Token.RECURSIVE);
        map.put("RESTART", Token.RESTART);

        map.put("RESTRICT", Token.RESTRICT);
        map.put("RETURNING", Token.RETURNING);
        map.put("ROW", Token.ROW);
        map.put("ROWS", Token.ROWS);
        map.put("SHARE", Token.SHARE);

        map.put("USING", Token.USING);
        map.put("WINDOW", Token.WINDOW);
        map.put("WITH", Token.WITH);

        DEFAULT_PG_KEYWORDS = new Keywords(map);
    }

    public PGLexer(String input){
        super(input);
        super.keywods = DEFAULT_PG_KEYWORDS;
    }
}

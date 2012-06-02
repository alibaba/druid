package com.alibaba.druid.sql.dialect.sqlserver.parser;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.Token;

public class SQLServerLexer extends Lexer {

    public final static Keywords DEFAULT_SQL_SERVER_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();
        
        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());
        
        map.put("CURSOR", Token.CURSOR);
        map.put("TOP", Token.TOP);
        map.put("USE", Token.USE);
        map.put("WITH", Token.WITH);

        DEFAULT_SQL_SERVER_KEYWORDS = new Keywords(map);
    }

    public SQLServerLexer(char[] input, int inputLength, boolean skipComment){
        super(input, inputLength, skipComment);
        super.keywods = DEFAULT_SQL_SERVER_KEYWORDS;
    }

    public SQLServerLexer(String input){
        super(input);
        super.keywods = DEFAULT_SQL_SERVER_KEYWORDS;
    }
}

package com.alibaba.druid.sql.dialect.teradata.parser;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.Token;

public class TeradataLexer extends Lexer {
    public final static Keywords DEFAULT_PG_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();
        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());
        
        DEFAULT_PG_KEYWORDS = new Keywords(map);
    }
    
    public TeradataLexer(String input){
        super(input);
        super.keywods = DEFAULT_PG_KEYWORDS;
    }

}

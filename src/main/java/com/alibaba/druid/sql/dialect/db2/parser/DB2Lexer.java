package com.alibaba.druid.sql.dialect.db2.parser;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.Token;


public class DB2Lexer extends Lexer {

    public final static Keywords DEFAULT_DB2_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());
        
        DEFAULT_DB2_KEYWORDS = new Keywords(map);
    }

    public DB2Lexer(String input){
        super(input);
        super.keywods = DEFAULT_DB2_KEYWORDS;
    }
}

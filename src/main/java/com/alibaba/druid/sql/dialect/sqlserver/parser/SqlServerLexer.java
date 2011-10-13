package com.alibaba.druid.sql.dialect.sqlserver.parser;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.Token;

public class SqlServerLexer extends Lexer {

    public final static Keywords DEFAULT_SQL_SERVER_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();
        DEFAULT_SQL_SERVER_KEYWORDS = new Keywords(map);
    }

    public SqlServerLexer(char[] input, int inputLength, boolean skipComment){
        super(input, inputLength, skipComment);
        super.keywods = DEFAULT_SQL_SERVER_KEYWORDS;
    }

    public SqlServerLexer(String input){
        super(input);
        super.keywods = DEFAULT_SQL_SERVER_KEYWORDS;
    }
}

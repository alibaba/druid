package com.alibaba.druid.sql.dialect.dm.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.druid.sql.dialect.mysql.parser.MySqlLexer.DEFAULT_MYSQL_KEYWORDS;

/**
 * @author two brother
 * @date 2021/7/22 11:28
 */
public class DMLexer extends Lexer {
    public final static Keywords DEFAULT_SQL_SERVER_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();
        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());
        map.put("TOP", Token.TOP);
        map.put("PERCENT", Token.PERCENT);
        map.put("TIES", Token.TIES);
        DEFAULT_SQL_SERVER_KEYWORDS = new Keywords(map);
    }
    public DMLexer(String input) {
        super(input);
        super.keywords=DEFAULT_SQL_SERVER_KEYWORDS;
    }
    public DMLexer(String input, CommentHandler commentHandler) {
        super(input, commentHandler);
        super.keywords=DEFAULT_SQL_SERVER_KEYWORDS;
    }
    public DMLexer(String input, CommentHandler commentHandler, DbType dbType) {
        super(input, commentHandler, dbType);
        super.keywords=DEFAULT_SQL_SERVER_KEYWORDS;

    }

    public DMLexer(String input, boolean skipComment) {
        super(input, skipComment);
        super.keywords=DEFAULT_SQL_SERVER_KEYWORDS;

    }

    public DMLexer(char[] input, int inputLength, boolean skipComment) {
        super(input, inputLength, skipComment);
        super.keywords=DEFAULT_SQL_SERVER_KEYWORDS;

    }


    public DMLexer(String sql, SQLParserFeature... features) {
        super(sql,true);
        super.keywords = DEFAULT_SQL_SERVER_KEYWORDS;
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }
}

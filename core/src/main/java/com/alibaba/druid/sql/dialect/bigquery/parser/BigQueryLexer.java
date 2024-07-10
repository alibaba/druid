package com.alibaba.druid.sql.dialect.bigquery.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

public class BigQueryLexer extends Lexer {
    public static final Keywords DEFAULT_BIG_QUERY_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();

//        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("ALL", Token.ALL);
        map.put("AND", Token.AND);
        map.put("ANY", Token.ANY);
        map.put("ALTER", Token.ALTER);
//        map.put("ARRAY", Token.ARRAY);
        map.put("AS", Token.AS);
        map.put("ASC", Token.ASC);
        map.put("BETWEEN", Token.BETWEEN);
        map.put("BY", Token.BY);
        map.put("CASE", Token.CASE);
        map.put("CAST", Token.CAST);
        map.put("COLUMN", Token.COLUMN);
        map.put("CONTAINS", Token.CONTAINS);
        map.put("CREATE", Token.CREATE);
        map.put("CROSS", Token.CROSS);
        map.put("CURRENT", Token.CURRENT);
        map.put("DECLARE", Token.DECLARE);
        map.put("DEFAULT", Token.DEFAULT);
        map.put("DESC", Token.DESC);
        map.put("DISTINCT", Token.DISTINCT);
        map.put("DELETE", Token.DELETE);
        map.put("DROP", Token.DROP);
        map.put("ELSE", Token.ELSE);
        map.put("END", Token.END);
        map.put("ESCAPE", Token.ESCAPE);
        map.put("EXCEPT", Token.EXCEPT);
        map.put("EXISTS", Token.EXISTS);
        map.put("EXTRACT", Token.EXTRACT);
        map.put("FUNCTION", Token.FUNCTION);
        map.put("FALSE", Token.FALSE);
        map.put("FETCH", Token.FETCH);
        map.put("FOR", Token.FOR);
        map.put("FROM", Token.FROM);
        map.put("FULL", Token.FULL);
        map.put("GROUP", Token.GROUP);
        map.put("HAVING", Token.HAVING);
        map.put("INSERT", Token.INSERT);
        map.put("IF", Token.IF);
        map.put("IN", Token.IN);
        map.put("INNER", Token.INNER);
        map.put("INTERSECT", Token.INTERSECT);
        map.put("INTERVAL", Token.INTERVAL);
        map.put("INTO", Token.INTO);
        map.put("IS", Token.IS);
        map.put("JOIN", Token.JOIN);
        map.put("LATERAL", Token.LATERAL);
        map.put("LEFT", Token.LEFT);
        map.put("LIKE", Token.LIKE);
        map.put("LIMIT", Token.LIMIT);
        map.put("MERGE", Token.MERGE);
        map.put("NEW", Token.NEW);
        map.put("NOT", Token.NOT);
        map.put("NULL", Token.NULL);
        map.put("OF", Token.OF);
        map.put("ON", Token.ON);
        map.put("OR", Token.OR);
        map.put("ORDER", Token.ORDER);
        map.put("OUTER", Token.OUTER);
        map.put("OVER", Token.OVER);
        map.put("PARTITION", Token.PARTITION);
        map.put("QUALIFY", Token.QUALIFY);
        map.put("RECURSIVE", Token.RECURSIVE);
        map.put("REPLACE", Token.REPLACE);
        map.put("RIGHT", Token.RIGHT);
        map.put("ROWS", Token.ROWS);
        map.put("SELECT", Token.SELECT);
        map.put("SET", Token.SET);
        map.put("SOME", Token.SOME);
        map.put("TABLE", Token.TABLE);
        map.put("THEN", Token.THEN);
        map.put("TO", Token.TO);
        map.put("TRUE", Token.TRUE);
        map.put("UPDATE", Token.UPDATE);
        map.put("UNION", Token.UNION);
        map.put("USING", Token.USING);
        map.put("VALUES", Token.VALUES);
        map.put("WHEN", Token.WHEN);
        map.put("WHERE", Token.WHERE);
        map.put("WINDOW", Token.WINDOW);
        map.put("WITH", Token.WITH);

        DEFAULT_BIG_QUERY_KEYWORDS = new Keywords(map);
    }

    {
        dbType = DbType.bigquery;
    }

    public BigQueryLexer(String input, SQLParserFeature... features) {
        super(input);
        dbType = DbType.hive;
        this.skipComment = true;
        this.keepComments = true;
        super.keywords = DEFAULT_BIG_QUERY_KEYWORDS;
        this.features |= SQLParserFeature.SupportUnicodeCodePoint.mask;
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }

    protected void scanAlias() {
        if (pos + 2 < text.length()
                && text.charAt(pos + 1) == '"'
                && text.charAt(pos + 2) == '"'
        ) {
            for (int i = pos + 3; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c == '"'
                        && i + 2 < text.length()
                        && text.charAt(i + 1) == '"'
                        && text.charAt(i + 2) == '"'
                ) {
                    stringVal = text.substring(pos + 3, i);
                    token = Token.LITERAL_TEXT_BLOCK;
                    pos = i + 2;
                    scanChar();
                    return;
                }
            }
        }
        super.scanAlias();
    }
}

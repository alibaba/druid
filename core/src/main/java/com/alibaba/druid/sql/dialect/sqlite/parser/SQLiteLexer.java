package com.alibaba.druid.sql.dialect.sqlite.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.druid.sql.parser.Token.IDENTIFIER;

public class SQLiteLexer extends Lexer {
    static final Keywords SQLITE_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<>();
        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("LIMIT", Token.LIMIT);
        map.put("IF", Token.IF);
        map.put("OF", Token.OF);
        map.put("REPLACE", Token.REPLACE);
        map.put("MERGE", Token.MERGE);
        map.put("USING", Token.USING);
        map.put("ROW", Token.ROW);
        map.put("CONCAT", Token.CONCAT);

        SQLITE_KEYWORDS = new Keywords(map);
    }

    @Override
    protected Keywords loadKeywords() {
        return SQLITE_KEYWORDS;
    }

    public SQLiteLexer(String input) {
        super(input, null, DbType.sqlite);
    }

    public SQLiteLexer(String input, SQLParserFeature... features) {
        super(input, null, DbType.sqlite);
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }

    @Override
    protected void scanLBracket() {
        mark = pos;

        if (buf == null) {
            buf = new char[32];
        }

        for (; ; ) {
            if (isEOF()) {
                lexError("unclosed.str.lit");
                return;
            }

            ch = charAt(++pos);

            if (ch == ']') {
                scanChar();
                token = IDENTIFIER;
                break;
            }

            if (bufPos == buf.length) {
                putChar(ch);
            } else {
                buf[bufPos++] = ch;
            }
        }

        stringVal = subString(mark, bufPos + 2);
    }
}

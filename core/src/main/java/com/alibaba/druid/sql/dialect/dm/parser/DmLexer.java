package com.alibaba.druid.sql.dialect.dm.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.*;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.druid.sql.parser.CharTypes.isIdentifierChar;
import static com.alibaba.druid.sql.parser.Token.LITERAL_CHARS;

/**
 * Lexer for Dameng (DM) database SQL dialect.
 *
 * <p>Extends the base {@link Lexer} with DM-specific keywords and Oracle-compatible
 * string scanning (single-quote doubling for escape). DM is an Oracle-compatible
 * Chinese relational database that supports both Oracle and MySQL syntax extensions.</p>
 */
public class DmLexer extends Lexer {
    static final Keywords DM_KEYWORDS;
    static {
        Map<String, Token> map = new HashMap<>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("BEGIN", Token.BEGIN);
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
        map.put("RECURSIVE", Token.RECURSIVE);
        map.put("RESTART", Token.RESTART);

        map.put("RESTRICT", Token.RESTRICT);
        map.put("RETURNING", Token.RETURNING);
        map.put("ROW", Token.ROW);
        map.put("ROWS", Token.ROWS);
        map.put("SHARE", Token.SHARE);
        map.put("SHOW", Token.SHOW);
        map.put("START", Token.START);

        map.put("USING", Token.USING);
        map.put("WINDOW", Token.WINDOW);

        map.put("TRUE", Token.TRUE);
        map.put("FALSE", Token.FALSE);
        map.put("IF", Token.IF);
        map.put("TYPE", Token.TYPE);
        map.put("MERGE", Token.MERGE);
        map.put("MATCHED", Token.MATCHED);
        map.put("PARTITION", Token.PARTITION);

        map.put("TOP", Token.TOP);
        map.put("PERCENT", Token.PERCENT);

        map.put("LOCAL", Token.LOCAL);
        map.put("GLOBAL", Token.GLOBAL);

        map.put("TEMPORARY", Token.TEMPORARY);
        map.put("TEMP", Token.TEMP);

        // DM-specific Oracle-compatible keywords
        map.put("CONNECT", Token.CONNECT);
        map.put("PRIOR", Token.PRIOR);
        map.put("WAIT", Token.WAIT);
        map.put("SEQUENCE", Token.SEQUENCE);
        map.put("TABLESPACE", Token.TABLESPACE);

        DM_KEYWORDS = new Keywords(map);
    }

    @Override
    protected Keywords loadKeywords() {
        return DM_KEYWORDS;
    }

    public DmLexer(String input, SQLParserFeature... features) {
        super(input, true);
        this.keepComments = true;
        super.dbType = DbType.dm;
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }

    @Override
    protected void scanString() {
        mark = pos;
        boolean hasSpecial = false;

        for (;;) {
            if (isEOF()) {
                lexError("unclosed.str.lit");
                return;
            }

            ch = charAt(++pos);

            if (ch == '\'') {
                scanChar();
                if (ch != '\'') {
                    token = LITERAL_CHARS;
                    break;
                } else {
                    if (!hasSpecial) {
                        initBuff(bufPos);
                        arraycopy(mark + 1, buf, 0, bufPos);
                        hasSpecial = true;
                    }
                    putChar('\'');
                    continue;
                }
            }

            if (!hasSpecial) {
                bufPos++;
                continue;
            }

            if (bufPos == buf.length) {
                putChar(ch);
            } else {
                buf[bufPos++] = ch;
            }
        }

        if (!hasSpecial) {
            stringVal = subString(mark + 1, bufPos);
        } else {
            stringVal = new String(buf, 0, bufPos);
        }
    }

    @Override
    protected void scanVariable_at() {
        if (ch != '@') {
            throw new ParserException("illegal variable. " + info());
        }

        mark = pos;
        bufPos = 1;
        char ch;

        final char c1 = charAt(pos + 1);
        if (c1 == '@') {
            pos += 2;
            token = Token.MONKEYS_AT_AT;
            this.ch = charAt(++pos);
            return;
        }

        for (;;) {
            ch = charAt(++pos);

            if (!isIdentifierChar(ch)) {
                break;
            }

            bufPos++;
            continue;
        }

        this.ch = charAt(pos);

        stringVal = addSymbol();
        token = Token.VARIANT;
    }
}

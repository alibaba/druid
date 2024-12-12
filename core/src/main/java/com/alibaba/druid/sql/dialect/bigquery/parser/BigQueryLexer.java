package com.alibaba.druid.sql.dialect.bigquery.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.*;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.druid.sql.parser.DialectFeature.ParserFeature.*;
import static com.alibaba.druid.sql.parser.Token.LITERAL_CHARS;

public class BigQueryLexer extends Lexer {
    @Override
    protected Keywords loadKeywords() {
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
        map.put("DO", Token.DO);
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
        map.put("FOREIGN", Token.FOREIGN);
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
        map.put("KEY", Token.KEY);
        map.put("LATERAL", Token.LATERAL);
        map.put("LEFT", Token.LEFT);
        map.put("LIKE", Token.LIKE);
        map.put("LIMIT", Token.LIMIT);
        map.put("MERGE", Token.MERGE);
        map.put("MATCHED", Token.MATCHED);
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
        map.put("PRIMARY", Token.PRIMARY);
        map.put("QUALIFY", Token.QUALIFY);
        map.put("RECURSIVE", Token.RECURSIVE);
        map.put("REFERENCES", Token.REFERENCES);
        map.put("REPLACE", Token.REPLACE);
        map.put("RIGHT", Token.RIGHT);
        map.put("ROWS", Token.ROWS);
        map.put("SELECT", Token.SELECT);
        map.put("SET", Token.SET);
        map.put("SOME", Token.SOME);
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
        map.put("WHILE", Token.WHILE);
        map.put("LOOP", Token.LOOP);
        map.put("LEAVE", Token.LEAVE);
        map.put("CONTINUE", Token.CONTINUE);
        map.put("VIEW", Token.VIEW);
        map.put("TRUNCATE", Token.TRUNCATE);
        map.put("BEGIN", Token.BEGIN);
        map.put("TABLE", Token.TABLE);
        map.put("EXCEPTION", Token.EXCEPTION);
        map.put("RAISE", Token.RAISE);
        map.put("ELSEIF", Token.ELSEIF);

        return new Keywords(map);
    }

    public BigQueryLexer(String input, SQLParserFeature... features) {
        super(input);
        dbType = DbType.bigquery;
        this.skipComment = true;
        this.keepComments = true;
        this.features |= SQLParserFeature.SupportUnicodeCodePoint.mask;
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }

    public final void nextTokenFullName() {
        nextToken();
    }

    @Override
    public boolean nextIf(Token token) {
        if (this.token == token) {
            boolean setFeature = token == Token.DELETE
                    || token == Token.FROM
                    || token == Token.INTO
                    || token == Token.JOIN;
            if (setFeature) {
                dialectFeature.configFeature(DialectFeature.LexerFeature.ScanSubAsIdentifier);
            }
            nextToken();
            if (setFeature) {
                dialectFeature.configFeature(DialectFeature.LexerFeature.ScanSubAsIdentifier, false);
            }
            return true;
        }
        return false;
    }

    public final void scanIdentifier() {
        scanIdentifier0();
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

        {
            boolean hasSpecial = false;
            int startIndex = pos + 1;
            int endIndex = -1; // text.indexOf('\'', startIndex);
            for (int i = startIndex; i < text.length(); ++i) {
                final char ch = text.charAt(i);
                if (ch == '\\') {
                    hasSpecial = true;
                    continue;
                }
                if (ch == '"') {
                    endIndex = i;
                    break;
                }
            }

            if (endIndex == -1) {
                throw new ParserException("unclosed str. " + info());
            }

            String stringVal;
            if (token == Token.AS) {
                stringVal = text.substring(pos, endIndex + 1);
            } else {
                if (startIndex == endIndex) {
                    stringVal = "";
                } else {
                    stringVal = text.substring(startIndex, endIndex);
                }
            }

            if (!hasSpecial) {
                this.stringVal = stringVal;
                int pos = endIndex + 1;
                char ch = charAt(pos);
                if (ch != '"') {
                    this.pos = pos;
                    this.ch = ch;
                    token = LITERAL_CHARS;
                    return;
                }
            }
        }

        mark = pos;
        boolean hasSpecial = false;
        Token preToken = this.token;

        for (; ; ) {
            if (isEOF()) {
                lexError("unclosed.str.lit");
                return;
            }

            ch = charAt(++pos);

            if (ch == '\\') {
                scanChar();
                if (!hasSpecial) {
                    initBuff(bufPos);
                    arraycopy(mark + 1, buf, 0, bufPos);
                    hasSpecial = true;
                }

                switch (ch) {
                    case '\'':
                        putChar('\'');
                        break;
                    case '"':
                        putChar('"');
                        break;
                    case '\\':
                        putChar('\\');
                        break;
                    default:
                        putChar('\\');
                        putChar(ch);
                        break;
                }

                continue;
            }

            if (ch == '"') {
                scanChar();
                if (ch != '"') {
                    token = LITERAL_CHARS;
                    break;
                } else {
                    if (!hasSpecial) {
                        initBuff(bufPos);
                        arraycopy(mark + 1, buf, 0, bufPos);
                        hasSpecial = true;
                    }
                    putChar('"');
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
            if (preToken == Token.AS) {
                stringVal = subString(mark, bufPos + 2);
            } else {
                stringVal = subString(mark + 1, bufPos);
            }
        } else {
            stringVal = new String(buf, 0, bufPos);
        }
    }

    @Override
    protected void initDialectFeature() {
        super.initDialectFeature();
        this.dialectFeature.configFeature(SQLDateExpr, GroupByAll, InRestSpecificOperation);
    }

    @Override
    public void scanSharp() {
        scanComment();
    }

    @Override
    public void scanComment() {
        if ((ch == '/' && charAt(pos + 1) == '/')
                || (ch == '-' && charAt(pos + 1) == '-')
                || (ch == '#')) {
            scanSingleLineComment();
        } else if (ch == '/' && charAt(pos + 1) == '*') {
            scanMultiLineComment();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    protected void scanString() {
        if (pos + 2 < text.length()
                && text.charAt(pos + 1) == '\''
                && text.charAt(pos + 2) == '\''
        ) {
            for (int i = pos + 3; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c == '\''
                        && i + 2 < text.length()
                        && text.charAt(i + 1) == '\''
                        && text.charAt(i + 2) == '\''
                ) {
                    stringVal = text.substring(pos + 3, i);
                    token = Token.LITERAL_TEXT_BLOCK;
                    pos = i + 2;
                    scanChar();
                    return;
                }
            }
        }

        {
            boolean hasSpecial = false;
            int startIndex = pos + 1;
            int endIndex = -1; // text.indexOf('\'', startIndex);
            for (int i = startIndex; i < text.length(); ++i) {
                final char ch = text.charAt(i);
                if (ch == '\\') {
                    hasSpecial = true;
                    continue;
                }
                if (ch == '\'') {
                    endIndex = i;
                    break;
                }
            }

            if (endIndex == -1) {
                throw new ParserException("unclosed str. " + info());
            }

            String stringVal;
            if (token == Token.AS) {
                stringVal = text.substring(pos, endIndex + 1);
            } else {
                if (startIndex == endIndex) {
                    stringVal = "";
                } else {
                    stringVal = text.substring(startIndex, endIndex);
                }
            }
            // hasSpecial = stringVal.indexOf('\\') != -1;

            if (!hasSpecial) {
                this.stringVal = stringVal;
                int pos = endIndex + 1;
                char ch = charAt(pos);
                if (ch != '\'') {
                    this.pos = pos;
                    this.ch = ch;
                    token = LITERAL_CHARS;
                    return;
                }
            }
        }

        mark = pos;
        boolean hasSpecial = false;
        for (; ; ) {
            if (isEOF()) {
                lexError("unclosed.str.lit");
                return;
            }

            ch = charAt(++pos);

            if (ch == '\\') {
                scanChar();
                if (!hasSpecial) {
                    initBuff(bufPos);
                    arraycopy(mark + 1, buf, 0, bufPos);
                    hasSpecial = true;
                }

                switch (ch) {
                    case '\'':
                        putChar('\'');
                        break;
                    case '"':
                        putChar('"');
                        break;
                    case '\\':
                        putChar('\\');
                        break;
                    default:
                        putChar('\\');
                        putChar(ch);
                        break;
                }

                continue;
            }
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
}

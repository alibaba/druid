/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.dialect.mysql.parser;

import static com.alibaba.druid.sql.parser.CharTypes.isFirstIdentifierChar;
import static com.alibaba.druid.sql.parser.CharTypes.isIdentifierChar;
import static com.alibaba.druid.sql.parser.LayoutCharacters.EOI;
import static com.alibaba.druid.sql.parser.Token.LITERAL_CHARS;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParseException;
import com.alibaba.druid.sql.parser.Token;

public class MySqlLexer extends Lexer {

    public final static Keywords DEFAULT_MYSQL_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();
        map.put("EXISTS", Token.EXISTS);
        map.put("THEN", Token.THEN);
        map.put("AS", Token.AS);
        map.put("GROUP", Token.GROUP);
        map.put("BY", Token.BY);
        map.put("HAVING", Token.HAVING);
        map.put("DELETE", Token.DELETE);
        map.put("ORDER", Token.ORDER);
        map.put("INDEX", Token.INDEX);
        map.put("FOR", Token.FOR);
        map.put("SCHEMA", Token.SCHEMA);
        map.put("FOREIGN", Token.FOREIGN);
        map.put("REFERENCE", Token.REFERENCE);
        map.put("REFERENCES", Token.REFERENCES);
        map.put("CHECK", Token.CHECK);
        map.put("PRIMARY", Token.PRIMARY);
        map.put("KEY", Token.KEY);
        map.put("CONSTRAINT", Token.CONSTRAINT);
        map.put("DEFAULT", Token.DEFAULT);
        map.put("VIEW", Token.VIEW);
        map.put("CREATE", Token.CREATE);
        map.put("VALUES", Token.VALUES);
        map.put("ALTER", Token.ALTER);
        map.put("TABLE", Token.TABLE);
        map.put("DROP", Token.DROP);
        map.put("SET", Token.SET);
        map.put("INTO", Token.INTO);
        map.put("UPDATE", Token.UPDATE);
        map.put("NULL", Token.NULL);
        map.put("IS", Token.IS);
        map.put("NOT", Token.NOT);
        map.put("SELECT", Token.SELECT);
        map.put("INSERT", Token.INSERT);
        map.put("FROM", Token.FROM);
        map.put("WHERE", Token.WHERE);
        map.put("AND", Token.AND);
        map.put("OR", Token.OR);
        map.put("XOR", Token.XOR);
        map.put("DISTINCT", Token.DISTINCT);
        map.put("UNIQUE", Token.UNIQUE);
        map.put("ALL", Token.ALL);
        map.put("UNION", Token.UNION);
        map.put("INTERSECT", Token.INTERSECT);
        map.put("MINUS", Token.MINUS);
        map.put("INNER", Token.INNER);
        map.put("LEFT", Token.LEFT);
        map.put("RIGHT", Token.RIGHT);
        map.put("FULL", Token.FULL);
        map.put("ON", Token.ON);
        map.put("OUTER", Token.OUTER);
        map.put("JOIN", Token.JOIN);
        map.put("NEW", Token.NEW);
        map.put("CASE", Token.CASE);
        map.put("WHEN", Token.WHEN);
        map.put("END", Token.END);
        map.put("WHEN", Token.WHEN);
        map.put("ELSE", Token.ELSE);
        map.put("EXISTS", Token.EXISTS);
        map.put("CAST", Token.CAST);
        map.put("IN", Token.IN);
        map.put("ASC", Token.ASC);
        map.put("DESC", Token.DESC);
        map.put("LIKE", Token.LIKE);
        map.put("ESCAPE", Token.ESCAPE);
        map.put("BETWEEN", Token.BETWEEN);
        map.put("INTERVAL", Token.INTERVAL);
        map.put("LOCK", Token.LOCK);
        map.put("SOME", Token.SOME);
        map.put("ANY", Token.ANY);
        map.put("TRUNCATE", Token.TRUNCATE);

        map.put("TRUE", Token.TRUE);
        map.put("FALSE", Token.FALSE);
        map.put("LIMIT", Token.LIMIT);
        map.put("KILL", Token.KILL);

        DEFAULT_MYSQL_KEYWORDS = new Keywords(map);
    }

    public MySqlLexer(char[] input, int inputLength, boolean skipComment){
        super(input, inputLength, skipComment);
        super.keywods = DEFAULT_MYSQL_KEYWORDS;
    }

    public MySqlLexer(String input){
        super(input);
        super.keywods = DEFAULT_MYSQL_KEYWORDS;
    }

    public void scanIdentifier() {
        final char first = ch;

        if (ch == '`') {
            int hash = first;

            np = bp;
            sp = 1;
            char ch;
            for (;;) {
                ch = buf[++bp];

                if (ch == '`') {
                    sp++;
                    ch = buf[++bp];
                    break;
                } else if (ch == EOI) {
                    throw new SQLParseException("illegal identifier");
                }

                hash = 31 * hash + ch;

                sp++;
                continue;
            }

            this.ch = buf[bp];

            stringVal = symbolTable.addSymbol(buf, np, sp, hash);
            Token tok = keywods.getKeyword(stringVal);
            if (tok != null) {
                token = tok;
            } else {
                token = Token.IDENTIFIER;
            }
        } else {

            final boolean firstFlag = isFirstIdentifierChar(first);
            if (!firstFlag) {
                throw new SQLParseException("illegal identifier");
            }

            int hash = first;

            np = bp;
            sp = 1;
            char ch;
            for (;;) {
                ch = buf[++bp];

                if (!isIdentifierChar(ch)) {
                    break;
                }

                hash = 31 * hash + ch;

                sp++;
                continue;
            }

            this.ch = buf[bp];

            stringVal = symbolTable.addSymbol(buf, np, sp, hash);
            Token tok = keywods.getKeyword(stringVal);
            if (tok != null) {
                token = tok;
            } else {
                token = Token.IDENTIFIER;
            }
        }
    }

    protected void scanString() {
        np = bp;
        boolean hasSpecial = false;

        for (;;) {
            if (bp >= buflen) {
                lexError(tokenPos, "unclosed.str.lit");
                return;
            }

            ch = buf[++bp];

            if (ch == '\\') {
                scanChar();
                if (!hasSpecial) {
                    System.arraycopy(buf, np + 1, sbuf, 0, sp);
                    hasSpecial = true;
                }

                switch (ch) {
                    case '\0':
                        putChar('\0');
                        break;
                    case '\'':
                        putChar('\'');
                        break;
                    case '"':
                        putChar('"');
                        break;
                    case 'b':
                        putChar('\b');
                        break;
                    case 'n':
                        putChar('\n');
                        break;
                    case 'r':
                        putChar('\r');
                        break;
                    case 't':
                        putChar('\t');
                        break;
                    case '\\':
                        putChar('\\');
                        break;
                    case 'Z':
                        putChar((char) 0x1A); // ctrl + Z
                        break;
                    default:
                        putChar(ch);
                        break;
                }
                scanChar();
            }

            if (ch == '\'') {
                scanChar();
                if (ch != '\'') {
                    token = LITERAL_CHARS;
                    break;
                } else {
                    System.arraycopy(buf, np + 1, sbuf, 0, sp);
                    hasSpecial = true;
                    putChar('\'');
                    continue;
                }
            }

            if (!hasSpecial) {
                sp++;
                continue;
            }

            if (sp == sbuf.length) {
                putChar(ch);
            } else {
                sbuf[sp++] = ch;
            }
        }

        if (!hasSpecial) {
            stringVal = new String(buf, np + 1, sp);
        } else {
            stringVal = new String(sbuf, 0, sp);
        }
    }
}

/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.postgresql.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.*;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.druid.sql.parser.CharTypes.isIdentifierChar;
import static com.alibaba.druid.sql.parser.Token.LITERAL_CHARS;

public class PGLexer extends Lexer {

    public final static Keywords DEFAULT_PG_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();

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
        map.put("ARRAY", Token.ARRAY);
        map.put("IF", Token.IF);
        map.put("TYPE", Token.TYPE);
        map.put("ILIKE", Token.ILIKE);
        map.put("MERGE", Token.MERGE);
        map.put("MATCHED", Token.MATCHED);
        map.put("PARTITION", Token.PARTITION);

        DEFAULT_PG_KEYWORDS = new Keywords(map);
    }

    public PGLexer(String input, SQLParserFeature... features){
        super(input, true);
        this.keepComments = true;
        super.keywords = DEFAULT_PG_KEYWORDS;
        super.dbType = DbType.postgresql;
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }
    
    protected void scanString() {
        mark = pos;
        boolean hasSpecial = false;

        for (;;) {
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

                putChar('\\');
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
                        putChar('b');
                        break;
                    case 'n':
                        putChar('n');
                        break;
                    case 'r':
                        putChar('r');
                        break;
                    case 't':
                        putChar('t');
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
    
    public void scanSharp() {
        scanChar();
        if (ch == '>') {
            scanChar();
            if (ch == '>') {
                scanChar();
                token = Token.POUNDGTGT;
            } else {
                token = Token.POUNDGT;
            }
        } else {
            token = Token.POUND;
        }
    }

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
        } else if (c1 == '>') {
            pos += 2;
            token = Token.MONKEYS_AT_GT;
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

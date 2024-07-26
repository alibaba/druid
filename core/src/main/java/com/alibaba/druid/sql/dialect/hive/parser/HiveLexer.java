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
package com.alibaba.druid.sql.dialect.hive.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.druid.sql.parser.Token.LITERAL_CHARS;

public class HiveLexer extends Lexer {
    public static final Keywords DEFAULT_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("OF", Token.OF);
        map.put("CONCAT", Token.CONCAT);
        map.put("CONTINUE", Token.CONTINUE);
        map.put("MERGE", Token.MERGE);
        map.put("MATCHED", Token.MATCHED);
        map.put("USING", Token.USING);

        map.put("ROW", Token.ROW);
        map.put("LIMIT", Token.LIMIT);
        map.put("PARTITIONED", Token.PARTITIONED);
        map.put("PARTITION", Token.PARTITION);
        map.put("OVERWRITE", Token.OVERWRITE);
//        map.put("SORT", Token.SORT);
        map.put("IF", Token.IF);
        map.put("TRUE", Token.TRUE);
        map.put("FALSE", Token.FALSE);
        map.put("RLIKE", Token.RLIKE);
        map.put("CONSTRAINT", Token.CONSTRAINT);
        map.put("DIV", Token.DIV);

        DEFAULT_KEYWORDS = new Keywords(map);
    }

    public HiveLexer(String input) {
        super(input);
        this.skipComment = true;
        this.keepComments = true;
        dbType = DbType.hive;
        this.features |= SQLParserFeature.SupportUnicodeCodePoint.mask;
        super.keywords = DEFAULT_KEYWORDS;
    }

    public HiveLexer(String input, SQLParserFeature... features) {
        super(input);
        dbType = DbType.hive;
        this.skipComment = true;
        this.keepComments = true;
        super.keywords = DEFAULT_KEYWORDS;
        this.features |= SQLParserFeature.SupportUnicodeCodePoint.mask;
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }

    @Override
    protected final void scanString() {
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
                    case '0':
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
                    case '%':
                        putChar('%');
                        break;
                    case '_':
                        putChar('_');
                        break;
                    case 'u':
                        if ((features & SQLParserFeature.SupportUnicodeCodePoint.mask) != 0) {
                            char c1 = charAt(++pos);
                            char c2 = charAt(++pos);
                            char c3 = charAt(++pos);
                            char c4 = charAt(++pos);

                            int intVal = Integer.parseInt(new String(new char[]{c1, c2, c3, c4}), 16);

                            putChar((char) intVal);
                        } else {
                            putChar(ch);
                        }
                        break;
                    default:
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

    public void scanComment() {
        scanHiveComment();
    }

    protected boolean supportNextTokenColon() {
        return true;
    }

    @Override
    protected boolean supportScanAliasU() {
        return true;
    }
}

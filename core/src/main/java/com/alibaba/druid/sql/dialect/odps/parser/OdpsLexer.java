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
package com.alibaba.druid.sql.dialect.odps.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.hive.parser.HiveLexer;
import com.alibaba.druid.sql.parser.*;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.druid.sql.parser.CharTypes.*;
import static com.alibaba.druid.sql.parser.LayoutCharacters.EOI;
import static com.alibaba.druid.sql.parser.Token.LITERAL_CHARS;

public class OdpsLexer extends HiveLexer {
    public static final Keywords DEFAULT_ODPS_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("SHOW", Token.SHOW);
        map.put("PARTITION", Token.PARTITION);
        map.put("PARTITIONED", Token.PARTITIONED);
        map.put("OVERWRITE", Token.OVERWRITE);
        map.put("OVER", Token.OVER);
        map.put("LIMIT", Token.LIMIT);
        map.put("IF", Token.IF);
        map.put("DISTRIBUTE", Token.DISTRIBUTE);
        map.put("TRUE", Token.TRUE);
        map.put("FALSE", Token.FALSE);
        map.put("RLIKE", Token.RLIKE);
        map.put("DIV", Token.DIV);
        map.put("LATERAL", Token.LATERAL);
        map.put("QUALIFY", Token.QUALIFY);
        map.put("；", Token.SEMI);

        DEFAULT_ODPS_KEYWORDS = new Keywords(map);
    }

    public OdpsLexer(String input, SQLParserFeature... features) {
        super(input);

        init();

        dbType = DbType.odps;
        super.keywords = DEFAULT_ODPS_KEYWORDS;
        this.skipComment = true;
        this.keepComments = false;

        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }

    private void init() {
        if (ch == '】' || ch == ' ' || ch == '，' || ch == '：' || ch == '、' || ch == '\u200C' || ch == '；') {
            ch = charAt(++pos);
        }

        if (ch == '上' && charAt(pos + 1) == '传') {
            pos += 2;
            ch = charAt(pos);

            while (isWhitespace(ch)) {
                ch = charAt(++pos);
            }
        }
    }

    public void scanIdentifier() {
        hashLCase = 0;
        hash = 0;

        final char first = ch;

        if (first == '`') {
            mark = pos;
            bufPos = 1;
            char ch;
            for (; ; ) {
                ch = charAt(++pos);

                if (ch == '`') {
                    bufPos++;
                    ch = charAt(++pos);
                    if (ch == '`') {
                        ch = charAt(++pos);
                        continue;
                    }
                    break;
                } else if (ch == EOI) {
                    throw new ParserException("illegal identifier. " + info());
                }

                bufPos++;
                continue;
            }

            this.ch = charAt(pos);

            stringVal = subString(mark, bufPos);
            token = Token.IDENTIFIER;

            return;
        }

        final boolean firstFlag = isFirstIdentifierChar(first)
                || ch == 'å'
                || ch == 'ß'
                || ch == 'ç';
        if (!firstFlag) {
            throw new ParserException("illegal identifier. " + info());
        }

        mark = pos;
        bufPos = 1;
        char ch;
        for (; ; ) {
            ch = charAt(++pos);

            if (ch != 'ó'
                    && ch != 'å'
                    && ch != 'é'
                    && ch != 'í'
                    && ch != 'ß'
                    && ch != 'ü'
                    && !isIdentifierChar(ch)) {
                if (ch == '{' && charAt(pos - 1) == '$') {
                    int endIndex = this.text.indexOf('}', pos);
                    if (endIndex != -1) {
                        bufPos += (endIndex - pos + 1);
                        pos = endIndex;
                        continue;
                    }
                }

                if (ch == '-'
                        && bufPos > 7
                        && text.regionMatches(false, mark, "ALIYUN$", 0, 7)) {
                    continue;
                }
                break;
            }

            if (ch == '；') {
                break;
            }

            bufPos++;
            continue;
        }

        this.ch = charAt(pos);

        if (ch == '@') { // for user identifier
            bufPos++;
            for (; ; ) {
                ch = charAt(++pos);

                if (ch != '-' && ch != '.' && !isIdentifierChar(ch)) {
                    break;
                }

                bufPos++;
                continue;
            }
        }
        this.ch = charAt(pos);

        // bufPos
        {
            final int LEN = "USING#CODE".length();
            if (bufPos == LEN && text.regionMatches(mark, "USING#CODE", 0, LEN)) {
                bufPos = "USING".length();
                pos -= 5;
                this.ch = charAt(pos);
            }
        }

        stringVal = addSymbol();
        Token tok = keywords.getKeyword(stringVal);
        if (tok != null) {
            token = tok;
        } else {
            token = Token.IDENTIFIER;
        }
    }

    public void scanVariable() {
        if (ch == ':') {
            token = Token.COLON;
            ch = charAt(++pos);
            return;
        }

        if (ch == '#'
                && (charAt(pos + 1) == 'C' || charAt(pos + 1) == 'c')
                && (charAt(pos + 2) == 'O' || charAt(pos + 2) == 'o')
                && (charAt(pos + 3) == 'D' || charAt(pos + 3) == 'd')
                && (charAt(pos + 4) == 'E' || charAt(pos + 4) == 'e')
        ) {
            int p1 = text.indexOf("#END CODE", pos + 1);
            int p2 = text.indexOf("#end code", pos + 1);
            if (p1 == -1) {
                p1 = p2;
            } else if (p1 > p2 && p2 != -1) {
                p1 = p2;
            }

            if (p1 != -1) {
                int end = p1 + "#END CODE".length();
                stringVal = text.substring(pos, end);
                token = Token.CODE;
                pos = end;
                ch = charAt(pos);
                return;
            }
        }

        super.scanVariable();
    }

    protected void scanVariable_at() {
        scanVariable();
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

    @Override
    protected void initLexerSettings() {
        super.initLexerSettings();
        this.lexerSettings.setEnableScanSQLTypeBlockComment(true);
        this.lexerSettings.setEnableScanSQLTypeWithSemi(true);
        this.lexerSettings.setEnableScanSQLTypeWithFunction(true);
        this.lexerSettings.setEnableScanSQLTypeWithBegin(true);
        this.lexerSettings.setEnableScanSQLTypeWithAt(true);
        this.lexerSettings.setEnableScanVariableAt(true);
        this.lexerSettings.setEnableScanVariableMoveToSemi(true);
        this.lexerSettings.setEnableScanVariableSkipIdentifiers(true);
        this.lexerSettings.setEnableScanNumberPrefixB(false);
        this.lexerSettings.setEnableScanNumberCommonProcess(false);
        this.lexerSettings.setEnableScanAliasU(false);
        this.lexerSettings.setEnableScanHiveCommentDoubleSpace(true);
    }
}

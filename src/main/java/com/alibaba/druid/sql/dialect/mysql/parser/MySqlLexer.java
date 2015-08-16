/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
import static com.alibaba.druid.sql.parser.LayoutCharacters.EOI;
import static com.alibaba.druid.sql.parser.Token.LITERAL_CHARS;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.sql.parser.CharTypes;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.NotAllowCommentException;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.Token;

public class MySqlLexer extends Lexer {

    public final static Keywords DEFAULT_MYSQL_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("DUAL", Token.DUAL);
        map.put("FALSE", Token.FALSE);
        map.put("IDENTIFIED", Token.IDENTIFIED);
        map.put("IF", Token.IF);
        map.put("KILL", Token.KILL);

        map.put("LIMIT", Token.LIMIT);
        map.put("TRUE", Token.TRUE);
        map.put("BINARY", Token.BINARY);
        map.put("SHOW", Token.SHOW);
        map.put("CACHE", Token.CACHE);
        map.put("ANALYZE", Token.ANALYZE);
        map.put("OPTIMIZE", Token.OPTIMIZE);
        map.put("ROW", Token.ROW);
        map.put("BEGIN", Token.BEGIN);
        map.put("END", Token.END);

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

    public void scanSharp() {
        if (ch != '#') {
            throw new ParserException("illegal stat");
        }

        if (charAt(pos + 1) == '{') {
            scanVariable();
            return;
        }

        Token lastToken = this.token;

        scanChar();
        mark = pos;
        bufPos = 0;
        for (;;) {
            if (ch == '\r') {
                if (charAt(pos + 1) == '\n') {
                    bufPos += 2;
                    scanChar();
                    break;
                }
                bufPos++;
                break;
            } else if (ch == EOI) {
                break;
            }

            if (ch == '\n') {
                scanChar();
                bufPos++;
                break;
            }

            scanChar();
            bufPos++;
        }

        stringVal = subString(mark - 1, bufPos + 1);
        token = Token.LINE_COMMENT;
        if (keepComments) {
            addComment(stringVal);
        }

        if (commentHandler != null && commentHandler.handle(lastToken, stringVal)) {
            return;
        }
        
        endOfComment = isEOF();

        if (!isAllowComment() && (isEOF() || !isSafeComment(stringVal))) {
            throw new NotAllowCommentException();
        }
    }

    public void scanVariable() {
        if (ch != '@' && ch != ':' && ch != '#' && ch != '$') {
            throw new ParserException("illegal variable");
        }

        mark = pos;
        bufPos = 1;

        if (charAt(pos + 1) == '@') {
            ch = charAt(++pos);
            bufPos++;
        }

        if (charAt(pos + 1) == '`') {
            ++pos;
            ++bufPos;
            char ch;
            for (;;) {
                ch = charAt(++pos);

                if (ch == '`') {
                    bufPos++;
                    ch = charAt(++pos);
                    break;
                } else if (ch == EOI) {
                    throw new ParserException("illegal identifier");
                }

                bufPos++;
                continue;
            }

            this.ch = charAt(pos);

            stringVal = subString(mark, bufPos);
            token = Token.VARIANT;
        } else if (charAt(pos + 1) == '{') {
            ++pos;
            ++bufPos;
            char ch;
            for (;;) {
                ch = charAt(++pos);

                if (ch == '}') {
                    bufPos++;
                    ch = charAt(++pos);
                    break;
                } else if (ch == EOI) {
                    throw new ParserException("illegal identifier");
                }

                bufPos++;
                continue;
            }

            this.ch = charAt(pos);

            stringVal = subString(mark, bufPos);
            token = Token.VARIANT;
        } else {
            for (;;) {
                ch = charAt(++pos);

                if (!isIdentifierChar(ch)) {
                    break;
                }

                bufPos++;
                continue;
            }
        }

        this.ch = charAt(pos);

        stringVal = subString(mark, bufPos);
        token = Token.VARIANT;
    }

    public void scanIdentifier() {
        final char first = ch;

        if (ch == '`') {

            mark = pos;
            bufPos = 1;
            char ch;
            for (;;) {
                ch = charAt(++pos);

                if (ch == '`') {
                    bufPos++;
                    ch = charAt(++pos);
                    break;
                } else if (ch == EOI) {
                    throw new ParserException("illegal identifier");
                }

                bufPos++;
                continue;
            }

            this.ch = charAt(pos);

            stringVal = subString(mark, bufPos);
            Token tok = keywods.getKeyword(stringVal);
            if (tok != null) {
                token = tok;
            } else {
                token = Token.IDENTIFIER;
            }
        } else {

            final boolean firstFlag = isFirstIdentifierChar(first);
            if (!firstFlag) {
                throw new ParserException("illegal identifier");
            }

            mark = pos;
            bufPos = 1;
            char ch;
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
            Token tok = keywods.getKeyword(stringVal);
            if (tok != null) {
                token = tok;
            } else {
                token = Token.IDENTIFIER;
            }
        }
    }

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
                throw new ParserException("unclosed str");
            }

            String stringVal = subString(startIndex, endIndex - startIndex);
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

                switch (ch) {
                    case '\0':
                        putChar(ch = '\0');
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
        Token lastToken = this.token;
        
        if (ch == '-') {
            char next_2 = charAt(pos + 2);
            if (isDigit(next_2)) {
                scanChar();
                token = Token.SUB;
                return;
            }
        } else if (ch != '/') {
            throw new IllegalStateException();
        }

        mark = pos;
        bufPos = 0;
        scanChar();

        // /*+ */
        if (ch == '*') {
            scanChar();
            bufPos++;

            while (ch == ' ') {
                scanChar();
                bufPos++;
            }

            boolean isHint = false;
            int startHintSp = bufPos + 1;
            if (ch == '!') {
                isHint = true;
                scanChar();
                bufPos++;
            }

            for (;;) {
                if (ch == EOI) {
                    this.token = Token.ERROR;
                    return;
                }
                if (ch == '*' && charAt(pos + 1) == '/') {
                    bufPos += 3;
                    scanChar();
                    scanChar();
                    break;
                }

                scanChar();
                bufPos++;
            }

            if (isHint) {
                stringVal = subString(mark + startHintSp, (bufPos - startHintSp) - 2);
                token = Token.HINT;
            } else {
                stringVal = subString(mark, bufPos);
                token = Token.MULTI_LINE_COMMENT;
                if (keepComments) {
                    addComment(stringVal);
                }
            }

            endOfComment = isEOF();
            
            if (commentHandler != null && commentHandler.handle(lastToken, stringVal)) {
                return;
            }

            if (!isHint && !isAllowComment() && !isSafeComment(stringVal)) {
                throw new NotAllowCommentException();
            }

            return;
        }
        if (ch == '/' || ch == '-') {
            scanChar();
            bufPos++;

            for (;;) {
                if (ch == '\r') {
                    if (charAt(pos + 1) == '\n') {
                        bufPos += 2;
                        scanChar();
                        break;
                    }
                    bufPos++;
                    break;
                } else if (ch == EOI) {
                    break;
                }

                if (ch == '\n') {
                    scanChar();
                    bufPos++;
                    break;
                }

                scanChar();
                bufPos++;
            }

            stringVal = subString(mark, bufPos + 1);
            token = Token.LINE_COMMENT;
            if (keepComments) {
                addComment(stringVal);
            }

            if (commentHandler != null && commentHandler.handle(lastToken, stringVal)) {
                return;
            }

            endOfComment = isEOF();
            
            if (!isAllowComment() && (isEOF() || !isSafeComment(stringVal))) {
                throw new NotAllowCommentException();
            }

            return;
        }
    }

    private boolean isIdentifierChar(char c) {
        return c != '#' && CharTypes.isIdentifierChar(c);
    }

    public void scanNumber() {
        mark = pos;

        if (ch == '-') {
            bufPos++;
            ch = charAt(++pos);
        }

        for (;;) {
            if (ch >= '0' && ch <= '9') {
                bufPos++;
            } else {
                break;
            }
            ch = charAt(++pos);
        }

        boolean isDouble = false;

        if (ch == '.') {
            if (charAt(pos + 1) == '.') {
                token = Token.LITERAL_INT;
                return;
            }
            bufPos++;
            ch = charAt(++pos);
            isDouble = true;

            for (;;) {
                if (ch >= '0' && ch <= '9') {
                    bufPos++;
                } else {
                    break;
                }
                ch = charAt(++pos);
            }
        }

        if (ch == 'e' || ch == 'E') {
            bufPos++;
            ch = charAt(++pos);

            if (ch == '+' || ch == '-') {
                bufPos++;
                ch = charAt(++pos);
            }

            for (;;) {
                if (ch >= '0' && ch <= '9') {
                    bufPos++;
                } else {
                    break;
                }
                ch = charAt(++pos);
            }

            isDouble = true;
        }

        if (isDouble) {
            token = Token.LITERAL_FLOAT;
        } else {
            if (isFirstIdentifierChar(ch) && !(ch == 'b' && bufPos == 1 && charAt(pos - 1) == '0')) {
                bufPos++;
                for (;;) {
                    ch = charAt(++pos);

                    if (!isIdentifierChar(ch)) {
                        break;
                    }

                    bufPos++;
                    continue;
                }

                stringVal = addSymbol();
                token = Token.IDENTIFIER;
            } else {
                token = Token.LITERAL_INT;
            }
        }
    }
}

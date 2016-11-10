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

import java.util.HashMap;
import java.util.Map;

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
        
        // for oceanbase & mysql 5.7
        map.put("PARTITION", Token.PARTITION);
        
        map.put("CONTINUE", Token.CONTINUE);
        map.put("UNDO", Token.UNDO);
        map.put("SQLSTATE", Token.SQLSTATE);
        map.put("CONDITION", Token.CONDITION);

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
        commentCount++;
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
            char ch = '\0', last_ch;
            for (;;) {
                last_ch = ch;
                ch = charAt(++pos);

                if (!isIdentifierChar(ch)) {
                    if (ch == '-' && pos < text.length() - 1) {
                        if (mark > 0 && text.charAt(mark - 1) == '.') {
                            break;
                        }

                        char next_char = text.charAt(pos + 1);
                        if (isIdentifierChar(next_char)) {
                            bufPos++;
                            continue;
                        }
                    }
                    if (last_ch == '-' && charAt(pos-2) != '-') {
                        ch = last_ch;
                        bufPos--;
                        pos--;
                    }
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
        scanString2();
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
            if (ch == '!' //
                    || ch == '+' // oceanbase hints
                    ) {
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
                commentCount++;
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
            commentCount++;
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
    
    private final static boolean[] identifierFlags = new boolean[256];
    static {
        for (char c = 0; c < identifierFlags.length; ++c) {
            if (c >= 'A' && c <= 'Z') {
                identifierFlags[c] = true;
            } else if (c >= 'a' && c <= 'z') {
                identifierFlags[c] = true;
            } else if (c >= '0' && c <= '9') {
                identifierFlags[c] = true;
            }
        }
        // identifierFlags['`'] = true;
        identifierFlags['_'] = true;
        //identifierFlags['-'] = true; // mysql
    }

    public static boolean isIdentifierChar(char c) {
        if (c <= identifierFlags.length) {
            return identifierFlags[c];
        }
        return c != '　' && c != '，';
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

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
package com.alibaba.druid.sql.dialect.mysql.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.*;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.druid.sql.parser.CharTypes.isFirstIdentifierChar;
import static com.alibaba.druid.sql.parser.CharTypes.isWhitespace;
import static com.alibaba.druid.sql.parser.LayoutCharacters.EOI;
import static com.alibaba.druid.sql.parser.Token.LITERAL_CHARS;

public class MySqlLexer extends Lexer {
    public static SymbolTable quoteTable = new SymbolTable(8192);

    public final static Keywords DEFAULT_MYSQL_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("DUAL", Token.DUAL);
        map.put("FALSE", Token.FALSE);
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
        map.put("DIV", Token.DIV);
        map.put("MERGE", Token.MERGE);
        
        // for oceanbase & mysql 5.7
        map.put("PARTITION", Token.PARTITION);
        
        map.put("CONTINUE", Token.CONTINUE);
        map.put("UNDO", Token.UNDO);
        map.put("SQLSTATE", Token.SQLSTATE);
        map.put("CONDITION", Token.CONDITION);
        map.put("MOD", Token.MOD);
        map.put("CONTAINS", Token.CONTAINS);
        map.put("RLIKE", Token.RLIKE);
        map.put("FULLTEXT", Token.FULLTEXT);

        DEFAULT_MYSQL_KEYWORDS = new Keywords(map);
    }

    {
        dbType = DbType.mysql;
    }

    public MySqlLexer(char[] input, int inputLength, boolean skipComment){
        super(input, inputLength, skipComment);
        super.keywods = DEFAULT_MYSQL_KEYWORDS;
    }

    public MySqlLexer(String input){
        this(input, true, true);
    }

    public MySqlLexer(String input, SQLParserFeature... features){
        super(input, true);
        this.keepComments = true;
        super.keywods = DEFAULT_MYSQL_KEYWORDS;

        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }

    public MySqlLexer(String input, boolean skipComment, boolean keepComments){
        super(input, skipComment);
        this.skipComment = skipComment;
        this.keepComments = keepComments;
        super.keywods = DEFAULT_MYSQL_KEYWORDS;
    }

    public void scanSharp() {
        if (ch != '#') {
            throw new ParserException("illegal stat. " + info());
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
        if (ch != ':' && ch != '#' && ch != '$') {
            throw new ParserException("illegal variable. " + info());
        }

        mark = pos;
        bufPos = 1;

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
                    throw new ParserException("illegal identifier. " + info());
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
                    throw new ParserException("illegal identifier. " + info());
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

    protected void scanVariable_at() {
        if (ch != '@') {
            throw new ParserException("illegal variable. " + info());
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
                    ++pos;
                    break;
                } else if (ch == EOI) {
                    throw new ParserException("illegal identifier. " + info());
                }

                bufPos++;
                continue;
            }

            this.ch = charAt(pos);

            stringVal = subString(mark, bufPos);
            token = Token.VARIANT;
        } else {
            for (; ; ) {
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
        hash_lower = 0;
        hash = 0;

        final char first = ch;

        if (first == 'U'
                && isEnabled(SQLParserFeature.Presto)
                && charAt(pos + 1) == '&'
                && charAt(pos + 2) == '\'') {
            initBuff(32);
            pos += 3;

            for (;;pos++) {
                ch = charAt(pos);
                if (isEOF()) {
                    lexError("unclosed.str.lit");
                    return;
                }

                if (ch == '\'') {
                    ch = charAt(++pos);
                    break;
                }

                if (ch == '\\') {
                    char c1 = charAt(++pos);
                    char c2 = charAt(++pos);
                    char c3 = charAt(++pos);
                    char c4 = charAt(++pos);

                    String tmp;
                    if (ch == '+') {
                        char c5 = charAt(++pos);
                        char c6 = charAt(++pos);
                        tmp = new String(new char[]{c1, c2, c3, c4, c5, c6});
                    } else {
                        tmp = new String(new char[]{c1, c2, c3, c4});
                    }
                    int intVal = Integer.parseInt(tmp, 16);
                    putChar((char) intVal);
                } else {
                    putChar(ch);
                }
            }
            stringVal = new String(buf, 0, bufPos);
            token = LITERAL_CHARS;
            return;
        }

        if ((ch == 'b' || ch == 'B')
                && charAt(pos + 1) == '\'') {
            int i = 2;
            int mark = pos + 2;
            for (;;++i) {
                char ch = charAt(pos + i);
                if (ch == '0' || ch == '1') {
                    continue;
                } else if (ch == '\'') {
                    bufPos += i;
                    pos += (i + 1);
                    stringVal = subString(mark, i - 2);
                    this.ch = charAt(pos);
                    token = Token.BITS;
                    return;
                } else if (ch == EOI) {
                    throw new ParserException("illegal identifier. " + info());
                } else {
                    break;
                }
            }
        }

        if (ch == '`') {
            mark = pos;
            bufPos = 1;
            char ch;

            int startPos = pos + 1;
            int quoteIndex;

            hash_lower = 0xcbf29ce484222325L;
            hash = 0xcbf29ce484222325L;

            for (int i = startPos;; ++i) {
                if (i >= text.length()) {
                    throw new ParserException("illegal identifier. " + info());
                }

                ch = text.charAt(i);

                if ('`' == ch) {
                    if (i + 1 < text.length() && '`' == text.charAt(i + 1)) {
                        ++i;
                    } else {
                        // End of identifier.
                        quoteIndex = i;
                        break;
                    }
                }

                hash_lower ^= ((ch >= 'A' && ch <= 'Z') ? (ch + 32) : ch);
                hash_lower *= 0x100000001b3L;

                hash ^= ch;
                hash *= 0x100000001b3L;
            }

            stringVal = quoteTable.addSymbol(text, pos, quoteIndex + 1 - pos, hash);
            //stringVal = text.substring(mark, pos);
            pos = quoteIndex + 1;
            this.ch = charAt(pos);
            token = Token.IDENTIFIER;
        } else {
            final boolean firstFlag = isFirstIdentifierChar(first);
            if (!firstFlag) {
                throw new ParserException("illegal identifier. " + info());
            }

            hash_lower = 0xcbf29ce484222325L;
            hash = 0xcbf29ce484222325L;

            hash_lower ^= ((ch >= 'A' && ch <= 'Z') ? (ch + 32) : ch);
            hash_lower *= 0x100000001b3L;

            hash ^= ch;
            hash *= 0x100000001b3L;

            mark = pos;
            bufPos = 1;
            char ch = '\0';
            for (;;) {
                ch = charAt(++pos);

                if (!isIdentifierChar(ch)) {
                    break;
                }

                bufPos++;

                hash_lower ^= ((ch >= 'A' && ch <= 'Z') ? (ch + 32) : ch);
                hash_lower *= 0x100000001b3L;

                hash ^= ch;
                hash *= 0x100000001b3L;

                continue;
            }

            this.ch = charAt(pos);

            if (bufPos == 1) {
                token = Token.IDENTIFIER;
                stringVal = CharTypes.valueOf(first);
                if (stringVal == null) {
                    stringVal = Character.toString(first);
                }
                return;
            }

            Token tok = keywods.getKeyword(hash_lower);
            if (tok != null) {
                token = tok;
                if (token == Token.IDENTIFIER) {
                    stringVal = SymbolTable.global.addSymbol(text, mark, bufPos, hash);
                } else {
                    stringVal = null;
                }
            } else {
                token = Token.IDENTIFIER;
                stringVal = SymbolTable.global.addSymbol(text, mark, bufPos, hash);
            }

        }
    }

    protected final void scanString() {
        scanString2();
    }

    public void skipFirstHintsOrMultiCommentAndNextToken() {
        int starIndex = pos + 2;

        for (;;) {
            starIndex = text.indexOf('*', starIndex);
            if (starIndex == -1 || starIndex == text.length() - 1) {
                this.token = Token.ERROR;
                return;
            }

            int slashIndex = starIndex + 1;
            if (charAt(slashIndex) == '/') {
                pos = slashIndex + 1;
                ch = text.charAt(pos);
                if (pos < text.length() - 6) {
                    int pos_6 = pos + 6;
                    char c0 = ch;
                    char c1 = text.charAt(pos + 1);
                    char c2 = text.charAt(pos + 2);
                    char c3 = text.charAt(pos + 3);
                    char c4 = text.charAt(pos + 4);
                    char c5 = text.charAt(pos + 5);
                    char c6 = text.charAt(pos_6);
                    if (c0 == 's' && c1 == 'e' && c2 == 'l' && c3 == 'e' && c4 == 'c' && c5 == 't' && c6 == ' ') {
                        this.comments = null;
                        reset(pos_6, ' ', Token.SELECT);
                        return;
                    }

                    if (c0 == 'i' && c1 == 'n' && c2 == 's' && c3 == 'e' && c4 == 'r' && c5 == 't' && c6 == ' ') {
                        this.comments = null;
                        reset(pos_6, ' ', Token.INSERT);
                        return;
                    }

                    if (c0 == 'u' && c1 == 'p' && c2 == 'd' && c3 == 'a' && c4 == 't' && c5 == 'e' && c6 == ' ') {
                        this.comments = null;
                        reset(pos_6, ' ', Token.UPDATE);
                        return;
                    }


                    if (c0 == 'd' && c1 == 'e' && c2 == 'l' && c3 == 'e' && c4 == 't' && c5 == 'e' && c6 == ' ') {
                        this.comments = null;
                        reset(pos_6, ' ', Token.DELETE);
                        return;
                    }

                    if (c0 == 'S' && c1 == 'E' && c2 == 'L' && c3 == 'E' && c4 == 'C' && c5 == 'T' && c6 == ' ') {
                        this.comments = null;
                        reset(pos_6, ' ', Token.SELECT);
                        return;
                    }

                    if (c0 == 'I' && c1 == 'N' && c2 == 'S' && c3 == 'E' && c4 == 'R' && c5 == 'T' && c6 == ' ') {
                        this.comments = null;
                        reset(pos_6, ' ', Token.INSERT);
                        return;
                    }

                    if (c0 == 'U' && c1 == 'P' && c2 == 'D' && c3 == 'A' && c4 == 'T' && c5 == 'E' && c6 == ' ') {
                        this.comments = null;
                        reset(pos_6, ' ', Token.UPDATE);
                        return;
                    }

                    if (c0 == 'D' && c1 == 'E' && c2 == 'L' && c3 == 'E' && c4 == 'T' && c5 == 'E' && c6 == ' ') {
                        this.comments = null;
                        reset(pos_6, ' ', Token.DELETE);
                        return;
                    }

                    nextToken();
                    return;
                } else {
                    nextToken();
                    return;
                }
            }
            starIndex++;
        }
    }

    public void scanComment() {
        Token lastToken = this.token;

        if(ch == '-') {
            /*
             * just for tddl test case;
             * test case : MySqlSelectTest_plus_sub_comment.java
             */
            char before_1 = pos == 0 ? ' ' : charAt(pos - 1);
            char next_2 = charAt(pos + 2);
            if(isDigit(next_2)) {
                scanChar();
                token = Token.SUB;
                return;
            } else if ((before_1 == ' ' || (before_1 != '-' && before_1 != '+')) && (next_2 == ' ' || next_2 == EOI || next_2 == '\n')) {
                // it is comments
            } else if ((before_1 == '-' || before_1 == '+') && next_2 == ' ') {
                throw new ParserException("illegal state. " + info());
            } else {
                if (ch == '-') {
                    scanChar();
                    token = Token.SUB;
                    return;
                } else if (ch == '+') {
                    scanChar();
                    token = Token.PLUS;
                    return;
                }
            }
        } else if (ch != '/') {
            throw new ParserException("illegal state. " + info());
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

                    || ((ch == 'T' && //TDDL hint
                            charAt(pos + 1) == 'D' //
                            && charAt(pos + 2) == 'D' //
                            && charAt(pos + 3) == 'L')
                           && isEnabled(SQLParserFeature.TDDLHint))
                    ) {
                isHint = true;
                scanChar();
                bufPos++;
            }

            int starIndex = pos;

            // Dealing with nesting hint.
            int depth = 1;
            while (true) {
                char ch = charAt(starIndex);
                if (ch == '/' && charAt(starIndex + 1) == '*') {
                    starIndex += 2;
                    ch = charAt(starIndex);
                    if (ch == '!' || ch == '+') {
                        ++depth;
                        ++starIndex;
                        continue;
                    }
                } else if (ch == '*' && charAt(starIndex + 1) == '/') {
                    if (0 == --depth) {
                        break;
                    }
                    starIndex += 2;
                    continue;
                }

                if (ch == EOI) {
                    this.token = Token.ERROR;
                    return;
                }
                ++starIndex;
            }

            if (isHint) {
                stringVal = this.subString(mark + startHintSp, starIndex - startHintSp - mark);
                token = Token.HINT;
            } else {
                if (!optimizedForParameterized) {
                    stringVal = this.subString(mark, starIndex + 2 - mark);
                }
                token = Token.MULTI_LINE_COMMENT;
                commentCount++;
                if (keepComments) {
                    stringVal = this.subString(mark, starIndex + 2 - mark);
                    addComment(stringVal);
                }
            }
            pos = starIndex + 2;
            ch = charAt(pos);

            endOfComment = isEOF();
            
            if (commentHandler != null
                    && commentHandler.handle(lastToken, stringVal)) {
                return;
            }

            if (!isHint && !isAllowComment() && !isSafeComment(stringVal)) {
                throw new NotAllowCommentException();
            }

            return;
        }

        if (ch == '!' && isEnabled(SQLParserFeature.TDDLHint)) { // TDDL HINT
            scanChar();
            bufPos++;

            while (ch == ' ') {
                scanChar();
                bufPos++;
            }

            int startHintSp = bufPos + 1;

            int starIndex = pos;

            for (;;) {
                starIndex = text.indexOf('*', starIndex);
                if (starIndex == -1 || starIndex == text.length() - 1) {
                    this.token = Token.ERROR;
                    return;
                }
                if (charAt(starIndex + 1) == '/') {
                    stringVal = this.subString(mark + startHintSp, starIndex - startHintSp - mark);
                    token = Token.HINT;
                    pos = starIndex + 2;
                    ch = charAt(pos);
                    break;
                }
                starIndex++;
            }

            endOfComment = isEOF();

            if (commentHandler != null
                && commentHandler.handle(lastToken, stringVal)) {
                return;
            }

            if (!isAllowComment() && !isSafeComment(stringVal)) {
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

            stringVal = subString(mark, bufPos);
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
        identifierFlags['$'] = true;
        //identifierFlags['-'] = true; // mysql
    }

    public static boolean isIdentifierChar(char c) {
        if (c <= identifierFlags.length) {
            return identifierFlags[c];
        }
        return c != '　' && c != '，';
    }
}

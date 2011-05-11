/*
 * Copyright 2011 Alibaba Group.
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
package com.alibaba.druid.sql.parser;

import java.sql.SQLSyntaxErrorException;

/**
 * support MySQL 5.5 token
 * 
 * @author <a href="mailto:shuo.qius@alibaba-inc.com">QIU Shuo</a>
 * @author wenshao<szujobs@hotmail.com>
 */
public class SQLLexer {
    protected final char[] buf;
    /** always be {@link #buf}.length - 1 */
    protected final int eofIndex;

    /** current index of {@link #buf} */
    protected int curIndex = -1;
    /** always be {@link #buf}[{@link #curIndex}] */
    protected char ch;

    /** current token, set by {@link #nextToken()} */
    protected int tokenPos = 0;
    protected Token token;

    /** A character buffer for literals. */
    protected final static ThreadLocal<char[]> sbufRef = new ThreadLocal<char[]>();
    protected char[] sbuf;

    protected String stringVal;
    protected SymbolTable symbolTable = new SymbolTable();

    public SQLLexer(char[] sql) {
        super();
        if ((this.sbuf = sbufRef.get()) == null) {
            this.sbuf = new char[1024];
            sbufRef.set(this.sbuf);
        }
        if (CharTypes.isWhitespace(sql[sql.length - 1])) {
            this.buf = sql;
        } else {
            this.buf = new char[sql.length + 1];
            System.arraycopy(sql, 0, this.buf, 0, sql.length);
        }
        this.eofIndex = this.buf.length - 1;
        this.buf[this.eofIndex] = LayoutCharacters.EOI;
        scanChar();
    }

    public SQLLexer(String sql) {
        this(sql.toCharArray());
    }

    protected final Token token() {
        return token;
    }

    protected final char scanChar() {
        return ch = buf[++curIndex];
    }

    /**
     * @param skip if 1, then equals to {@link #scanChar()}
     */
    protected final char scanChar(int skip) {
        return ch = buf[curIndex += skip];
    }

    protected final boolean hasChars(int howMany) {
        return curIndex + howMany <= eofIndex;
    }

    protected final boolean eof() {
        return curIndex >= eofIndex;
    }

    public void nextToken() throws SQLSyntaxErrorException {
        skipSeparator();
        tokenPos = curIndex;
        switch (ch) {
        case '0':
            switch (buf[curIndex + 1]) {
            case 'x':
                scanChar(2);
                scanHexaDecimal(false);
                return;
            case 'b':
                scanChar(2);
                scanBitField(false);
                return;
            }
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            scanNumber();
            return;
        case '.':
            if (CharTypes.isDigit(buf[curIndex + 1])) {
                scanNumber();
            } else {
                scanChar();
                token = Token.DOT;
            }
            return;
        case '\'':
        case '"':
            scanString();
            return;
        case 'n':
        case 'N':
            if (buf[curIndex + 1] == '\'') {
                scanChar();
                scanString();
                token = Token.LITERAL_NCHARS;
                return;
            }
            scanIdentifier();
            return;
        case 'x':
        case 'X':
            if (buf[curIndex + 1] == '\'') {
                scanChar(2);
                scanHexaDecimal(true);
                return;
            }
            scanIdentifier();
            return;
        case 'b':
        case 'B':
            if (buf[curIndex + 1] == '\'') {
                scanChar(2);
                scanBitField(true);
                return;
            }
            scanIdentifier();
            return;
        case '@':
            if (buf[curIndex + 1] == '@') {
                scanSystemVariable();
                return;
            }
            scanUserVariable();
            return;
        case '?':
            scanChar();
            token = Token.QUES;
            return;
        case '(':
            scanChar();
            token = Token.LPAREN;
            return;
        case ')':
            scanChar();
            token = Token.RPAREN;
            return;
        case '[':
            scanChar();
            token = Token.LBRACKET;
            return;
        case ']':
            scanChar();
            token = Token.RBRACKET;
            return;
        case '{':
            scanChar();
            token = Token.LBRACE;
            return;
        case '}':
            scanChar();
            token = Token.RBRACE;
            return;
        case ',':
            scanChar();
            token = Token.COMMA;
            return;
        case ';':
            scanChar();
            token = Token.SEMI;
            return;
        case ':':
            if (buf[curIndex + 1] == '=') {
                scanChar(2);
                token = Token.COLONEQ;
                return;
            }
            scanChar();
            token = Token.COLON;
            return;
        case '=':
            scanChar();
            token = Token.EQ;
            return;
        case '~':
            scanChar();
            token = Token.TILDE;
            return;
        case '*':
            scanChar();
            token = Token.STAR;
            return;
        case '-':
            scanChar();
            token = Token.SUB;
            return;
        case '+':
            scanChar();
            token = Token.PLUS;
            return;
        case '^':
            scanChar();
            token = Token.CARET;
            return;
        case '/':
            scanChar();
            token = Token.SLASH;
            return;
        case '%':
            scanChar();
            token = Token.PERCENT;
            return;
        case '&':
            if (buf[curIndex + 1] == '&') {
                scanChar(2);
                token = Token.AMPAMP;
                return;
            }
            scanChar();
            token = Token.AMP;
            return;
        case '|':
            if (buf[curIndex + 1] == '|') {
                scanChar(2);
                token = Token.BARBAR;
                return;
            }
            scanChar();
            token = Token.BAR;
            return;
        case '!':
            if (buf[curIndex + 1] == '=') {
                scanChar(2);
                token = Token.BANGEQ;
                return;
            }
            scanChar();
            token = Token.BANG;
            return;
        case '>':
            switch (buf[curIndex + 1]) {
            case '=':
                scanChar(2);
                token = Token.GTEQ;
                return;
            case '>':
                scanChar(2);
                token = Token.GTGT;
                return;
            default:
                scanChar();
                token = Token.GT;
                return;
            }
        case '<':
            switch (buf[curIndex + 1]) {
            case '=':
                if (buf[curIndex + 2] == '>') {
                    scanChar(3);
                    token = Token.LTEQGT;
                    return;
                }
                scanChar(2);
                token = Token.LTEQ;
                return;
            case '>':
                scanChar(2);
                token = Token.LTGT;
                return;
            case '<':
                scanChar(2);
                token = Token.LTLT;
                return;
            default:
                scanChar();
                token = Token.LT;
                return;
            }
        case '`':
            scanIdentifierWithAccent();
            return;
        default:
            if (CharTypes.isIdentifierChar(ch)) {
                scanIdentifier();
            } else if (eof()) {
                token = Token.EOF;
                tokenPos = curIndex = eofIndex;
            } else {
                throw err("unsupported character: " + ch);
            }
            return;
        }
    }

    protected int offsetCache;
    protected int sizeCache;

    /**
     * first <code>@</code> is included
     */
    protected void scanUserVariable() throws SQLSyntaxErrorException {
        if (ch != '@') throw err("first char must be @");
        offsetCache = curIndex;
        sizeCache = 1;

        int hash = ch;
        for (; CharTypes.isIdentifierChar(scanChar()); ++sizeCache) {
            hash = 31 * hash + ch;
        }

        stringVal = symbolTable.addSymbol(buf, offsetCache, sizeCache, hash);
        token = Token.USR_VAR;
    }

    /**
     * first <code>@@</code> is included
     */
    protected void scanSystemVariable() throws SQLSyntaxErrorException {
        if (ch != '@' || buf[curIndex + 1] != '@') throw err("first char must be @@");
        offsetCache = curIndex;
        sizeCache = 2;

        int hash = 31 * ch + ch;
        for (scanChar(); CharTypes.isIdentifierChar(scanChar()); ++sizeCache) {
            hash = 31 * hash + ch;
        }

        stringVal = symbolTable.addSymbol(buf, offsetCache, sizeCache, hash);
        token = Token.SYS_VAR;
    }

    protected void scanString() throws SQLSyntaxErrorException {
        boolean dq = false;
        if (ch == '\'') {
        } else if (ch == '"') {
            dq = true;
        } else {
            throw err("first char must be \" or '");
        }

        offsetCache = curIndex;
        int size = 1;
        sbuf[0] = '\'';
        if (dq) {
            loop: while (true) {
                switch (scanChar()) {
                case '\'':
                    putChar('\\', size++);
                    putChar('\'', size++);
                    break;
                case '\\':
                    putChar('\\', size++);
                    putChar(scanChar(), size++);
                    continue;
                case '"':
                    if (buf[curIndex + 1] == '"') {
                        putChar('"', size++);
                        scanChar();
                        continue;
                    }
                    putChar('\'', size++);
                    scanChar();
                    break loop;
                default:
                    if (eof()) {
                        throw err("unclosed string");
                    }
                    putChar(ch, size++);
                    continue;
                }
            }
        } else {
            loop: while (true) {
                switch (scanChar()) {
                case '\\':
                    putChar('\\', size++);
                    putChar(scanChar(), size++);
                    continue;
                case '\'':
                    if (buf[curIndex + 1] == '\'') {
                        putChar('\\', size++);
                        putChar(scanChar(), size++);
                        continue;
                    }
                    putChar('\'', size++);
                    scanChar();
                    break loop;
                default:
                    if (eof()) {
                        throw err("unclosed string");
                    }
                    putChar(ch, size++);
                    continue;
                }
            }
        }

        sizeCache = curIndex - offsetCache;
        stringVal = new String(sbuf, 0, size);
        token = Token.LITERAL_CHARS;
    }

    /**
     * @param quoteMode if false: first <code>0x</code> has been skipped; if
     *            true: first <code>x'</code> has been skipped
     */
    protected void scanHexaDecimal(boolean quoteMode) throws SQLSyntaxErrorException {
        offsetCache = curIndex;
        for (; CharTypes.isHex(ch); scanChar());

        if ((sizeCache = curIndex - offsetCache) <= 0) {
            throw err("expect at least one hexdigit");
        }
        if (quoteMode) {
            if (ch != '\'') {
                throw err("invalid char for hex: " + ch);
            }
        } else if (CharTypes.isIdentifierChar(ch)) {
            ch = buf[curIndex = offsetCache -= 2];
            scanIdentifierFromNumber(offsetCache, sizeCache + 2);
            return;
        }

        token = Token.LITERAL_HEX;
    }

    /**
     * @param quoteMode if false: first <code>0b</code> has been skipped; if
     *            true: first <code>b'</code> has been skipped
     */
    protected void scanBitField(boolean quoteMode) throws SQLSyntaxErrorException {
        offsetCache = curIndex;
        for (; ch == '0' || ch == '1'; scanChar());

        if ((sizeCache = curIndex - offsetCache) <= 0) {
            throw err("expect at least one bit");
        }
        if (quoteMode) {
            if (ch != '\'') {
                throw err("invalid char for bit: " + ch);
            }
        } else if (CharTypes.isIdentifierChar(ch)) {
            ch = buf[curIndex = offsetCache -= 2];
            scanIdentifierFromNumber(offsetCache, sizeCache + 2);
            return;
        }

        token = Token.LITERAL_BIT;
    }

    /**
     * if first char is <code>.</code>, token may be {@link Token#DOT} if
     * invalid char is presented after <code>.</code>
     */
    protected void scanNumber() throws SQLSyntaxErrorException {
        offsetCache = curIndex;
        sizeCache = 1;
        final boolean fstDot = ch == '.';
        boolean dot = fstDot;
        boolean sign = false;
        int state = fstDot ? 1 : 0;

        for (; scanChar() != LayoutCharacters.EOI; ++sizeCache) {
            switch (state) {
            case 0:
                if (CharTypes.isDigit(ch)) {
                } else if (ch == '.') {
                    dot = true;
                    state = 1;
                } else if (ch == 'e' || ch == 'E') {
                    state = 3;
                } else if (CharTypes.isIdentifierChar(ch)) {
                    scanIdentifierFromNumber(offsetCache, sizeCache);
                    return;
                } else {
                    token = Token.LITERAL_NUM_PURE_DIGIT;
                    return;
                }
                break;
            case 1:
                if (CharTypes.isDigit(ch)) {
                    state = 2;
                } else if (ch == 'e' || ch == 'E') {
                    state = 3;
                } else if (CharTypes.isIdentifierChar(ch)) {
                    if (fstDot) {
                        sizeCache = 1;
                        ch = buf[curIndex = offsetCache + 1];
                        token = Token.DOT;
                        return;
                    } else {
                        throw err("invalid char after '.': " + ch);
                    }
                } else {
                    token = Token.LITERAL_NUM_MIX_DIGIT;
                    return;
                }
                break;
            case 2:
                if (CharTypes.isDigit(ch)) {
                } else if (ch == 'e' || ch == 'E') {
                    state = 3;
                } else if (CharTypes.isIdentifierChar(ch)) {
                    if (fstDot) {
                        sizeCache = 1;
                        ch = buf[curIndex = offsetCache + 1];
                        token = Token.DOT;
                        return;
                    } else {
                        throw err("invalid char after '.' for as part of number: " + ch);
                    }
                } else {
                    token = Token.LITERAL_NUM_MIX_DIGIT;
                    return;
                }
                break;
            case 3:
                if (CharTypes.isDigit(ch)) {
                    state = 5;
                } else if (ch == '+' || ch == '-') {
                    sign = true;
                    state = 4;
                } else if (CharTypes.isIdentifierChar(ch)) {
                    if (fstDot) {
                        sizeCache = 1;
                        ch = buf[curIndex = offsetCache + 1];
                        token = Token.DOT;
                        return;
                    } else if (!dot) {
                        scanIdentifierFromNumber(offsetCache, sizeCache);
                        return;
                    } else {
                        throw err("invalid char after '.' and 'e' for as part of number: " + ch);
                    }
                } else if (!dot) {
                    token = Token.IDENTIFIER;
                    stringVal = symbolTable.addSymbol(buf, offsetCache, sizeCache);
                    return;
                } else {
                    throw err("invalid char after '.' and 'e' for as part of number: " + ch);
                }
                break;
            case 4:
                if (CharTypes.isDigit(ch)) {
                    state = 5;
                    break;
                } else if (fstDot) {
                    sizeCache = 1;
                    ch = buf[curIndex = offsetCache + 1];
                    token = Token.DOT;
                } else if (!dot) {
                    ch = buf[--curIndex];
                    --sizeCache;
                    token = Token.IDENTIFIER;
                    stringVal = symbolTable.addSymbol(buf, offsetCache, sizeCache);
                } else {
                    throw err("expect digit char after SIGN for 'e': " + ch);
                }
                return;
            case 5:
                if (CharTypes.isDigit(ch)) {
                    break;
                } else if (CharTypes.isIdentifierChar(ch)) {
                    if (fstDot) {
                        sizeCache = 1;
                        ch = buf[curIndex = offsetCache + 1];
                        token = Token.DOT;
                    } else if (!dot) {
                        if (sign) {
                            sizeCache = 1;
                            ch = buf[curIndex = offsetCache];
                            scanIdentifierFromNumber(curIndex, 0);
                        } else {
                            scanIdentifierFromNumber(offsetCache, sizeCache);
                        }
                    } else {
                        throw err("expect digit char after SIGN for 'e': " + ch);
                    }
                } else {
                    token = Token.LITERAL_NUM_MIX_DIGIT;
                }
                return;
            }
        }
        token = state == 0 ? Token.LITERAL_NUM_PURE_DIGIT : Token.LITERAL_NUM_MIX_DIGIT;
    }

    /**
     * @param initSize how many char has already been consumed
     */
    private void scanIdentifierFromNumber(int initOffset, int initSize) throws SQLSyntaxErrorException {
        offsetCache = initOffset;
        sizeCache = initSize;
        for (; CharTypes.isIdentifierChar(ch); ++sizeCache) {
            scanChar();
        }
        stringVal = symbolTable.addSymbol(buf, offsetCache, sizeCache);
        token = Token.IDENTIFIER;
    }

    /**
     * id is NOT included in <code>`</code>.
     */
    protected void scanIdentifier() throws SQLSyntaxErrorException {
        scanIdentifierFromNumber(curIndex, 0);
    }

    /**
     * id is included in <code>`</code>. first <code>`</code> is included
     */
    protected void scanIdentifierWithAccent() throws SQLSyntaxErrorException {
        offsetCache = curIndex;
        for (; scanChar() != LayoutCharacters.EOI;) {
            if (ch == '`') {
                if (scanChar() != '`') {
                    break;
                }
            }
        }
        stringVal = symbolTable.addSymbol(buf, offsetCache, sizeCache = curIndex - offsetCache);
        token = Token.IDENTIFIER;
    }

    /**
     * Append a character to sbuf.
     */
    protected final void putChar(char ch, int index) {
        if (index >= sbuf.length) {
            char[] newsbuf = new char[sbuf.length * 2];
            System.arraycopy(sbuf, 0, newsbuf, 0, sbuf.length);
            sbuf = newsbuf;
        }
        sbuf[index] = ch;
    }

    /**
     * skip whitespace and comment
     */
    protected void skipSeparator() {
        while (!eof()) {
            while (CharTypes.isWhitespace(ch)) { // imply: ch!=eof
                scanChar();
                continue;
            }

            switch (ch) {
            case '#': // MySQL specified
                while (!eof()) {
                    if (scanChar() == '\n') {
                        scanChar();
                        continue;
                    }
                }
                continue;
            case '/':
                if (hasChars(2) && '*' == buf[curIndex + 1] && '!' != buf[curIndex + 2]) {
                    scanChar(2);
                    for (int state = 0; !eof(); scanChar()) {
                        if (state == 0) {
                            if ('*' == ch) {
                                state = 1;
                            }
                        } else {
                            if ('*' != ch) {
                                state = 0;
                            } else if ('/' == ch) {
                                scanChar();
                                continue;
                            }
                        }
                    }
                    continue;
                }
                return;
            case '-':
                if (hasChars(3) && '-' == buf[curIndex + 1] && CharTypes.isWhitespace(buf[curIndex + 2])) {
                    scanChar(3);
                    for (; !eof(); scanChar()) {
                        if ('\n' == ch) {
                            scanChar();
                            continue;
                        }
                    }
                }
                return;
            }
            return;
        }
    }

    /**
     * always throw SQLSyntaxErrorException
     */
    protected SQLSyntaxErrorException err(String msg) throws SQLSyntaxErrorException {
        String errMsg = msg + ". " + toString();
        throw new SQLSyntaxErrorException(errMsg);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append('@').append(hashCode()).append('{');
        String sqlLeft = new String(buf, curIndex, buf.length - curIndex);
        sb.append("curIndex=")
          .append(curIndex)
          .append(", ch=")
          .append(ch)
          .append(", token=")
          .append(token)
          .append(", sqlLeft=")
          .append(sqlLeft)
          .append(", sql=")
          .append(buf);
        sb.append('}');
        return sb.toString();
    }

    //
    //    public static void main(String[] args) {
    //        char[] dd = new char[333];
    //        for (int i = 0; i < dd.length; ++i) {
    //            dd[i] = 'd';
    //        }
    //        char t = 'c';
    //        char t1 = 'd';
    //        long start = System.currentTimeMillis();
    //        long end = System.currentTimeMillis();
    //        for (int i = 0; i < 10; ++i) {
    //            int r = i % dd.length;
    //            t = dd[r];
    //        }
    //
    //        int loop = 500000000;
    //        start = System.currentTimeMillis();
    //        for (int i = 0; i < loop; ++i) {
    //            int r = i % dd.length;
    //            t = t1;
    //        }
    //        end = System.currentTimeMillis();
    //        System.out.println((end - start) * 1.0d / (loop / 1000000));
    //    }

}

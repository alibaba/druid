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
package com.alibaba.druid.sql.parser;

import static com.alibaba.druid.sql.parser.CharTypes.isFirstIdentifierChar;
import static com.alibaba.druid.sql.parser.CharTypes.isIdentifierChar;
import static com.alibaba.druid.sql.parser.CharTypes.isWhitespace;
import static com.alibaba.druid.sql.parser.LayoutCharacters.EOI;
import static com.alibaba.druid.sql.parser.Token.COLONEQ;
import static com.alibaba.druid.sql.parser.Token.COMMA;
import static com.alibaba.druid.sql.parser.Token.EOF;
import static com.alibaba.druid.sql.parser.Token.ERROR;
import static com.alibaba.druid.sql.parser.Token.LBRACE;
import static com.alibaba.druid.sql.parser.Token.LBRACKET;
import static com.alibaba.druid.sql.parser.Token.LITERAL_ALIAS;
import static com.alibaba.druid.sql.parser.Token.LITERAL_CHARS;
import static com.alibaba.druid.sql.parser.Token.LPAREN;
import static com.alibaba.druid.sql.parser.Token.RBRACE;
import static com.alibaba.druid.sql.parser.Token.RBRACKET;
import static com.alibaba.druid.sql.parser.Token.RPAREN;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class Lexer {

    protected final char[]                     buf;
    protected int                              bp;
    protected int                              buflen;
    // QS_TODO what is the purpose?
    protected int                              eofPos;

    /** The current character. */
    protected char                             ch;

    /** The token's position, 0-based offset from beginning of text. */
    protected int                              tokenPos;

    /** A character buffer for literals. */
    protected final static ThreadLocal<char[]> sbufRef      = new ThreadLocal<char[]>();
    protected char[]                           sbuf;

    /** string point as size */
    protected int                              sp;

    /** string point as offset */
    protected int                              np;

    protected SymbolTable                      symbolTable  = new SymbolTable();

    /**
     * The token, set by nextToken().
     */
    protected Token                            token;

    protected Keywords                         keywods      = Keywords.DEFAULT_KEYWORDS;

    protected String                           stringVal;

    protected boolean                          skipComment  = true;

    private SavePoint                          savePoint    = null;

    /*
     * anti sql injection
     */
    private boolean                            allowComment = true;

    private int                                varIndex     = -1;

    public Lexer(String input){
        this(input, true);
    }

    public boolean isAllowComment() {
        return allowComment;
    }

    public void setAllowComment(boolean allowComment) {
        this.allowComment = allowComment;
    }
    
    public int nextVarIndex() {
        return ++varIndex;
    }

    private static class SavePoint {

        int   bp;
        int   sp;
        int   np;
        char  ch;
        Token token;
    }

    public Keywords getKeywods() {
        return keywods;
    }

    public void mark() {
        SavePoint savePoint = new SavePoint();
        savePoint.bp = bp;
        savePoint.sp = sp;
        savePoint.np = np;
        savePoint.ch = ch;
        savePoint.token = token;
        this.savePoint = savePoint;
    }

    public void reset() {
        this.bp = savePoint.bp;
        this.sp = savePoint.sp;
        this.np = savePoint.np;
        this.ch = savePoint.ch;
        this.token = savePoint.token;
    }

    public Lexer(String input, boolean skipComment){
        this(input.toCharArray(), input.length(), skipComment);
    }

    public Lexer(char[] input, int inputLength, boolean skipComment){
        this.skipComment = skipComment;
        this.sbuf = sbufRef.get(); // new char[1024];
        if (this.sbuf == null) {
            this.sbuf = new char[1024];
            sbufRef.set(sbuf);
        }

        this.eofPos = inputLength;

        // QS_TODO ?
        if (inputLength == input.length) {
            if (input.length > 0 && isWhitespace(input[input.length - 1])) {
                inputLength--;
            } else {
                char[] newInput = new char[inputLength + 1];
                System.arraycopy(input, 0, newInput, 0, input.length);
                input = newInput;
            }
        }
        this.buf = input;
        this.buflen = inputLength;
        this.buf[this.buflen] = EOI;
        this.bp = -1;

        scanChar();
    }

    protected final void scanChar() {
        ch = buf[++bp];
    }

    protected void unscan() {
        ch = buf[--bp];
    }

    /**
     * Report an error at the given position using the provided arguments.
     */
    protected void lexError(int pos, String key, Object... args) {
        token = ERROR;
    }


    /**
     * Return the current token, set by nextToken().
     */
    public final Token token() {
        return token;
    }

    public String info() {
        return this.token + " " + this.stringVal();
    }

    public final void nextToken() {
        sp = 0;

        for (;;) {
            tokenPos = bp;

            if (isWhitespace(ch)) {
                scanChar();
                continue;
            }
            
            if (ch == '$' && buf[bp + 1] == '{') {
                scanVariable();
                return;
            }

            if (isFirstIdentifierChar(ch)) {
                if (ch == 'N') {
                    if (buf[bp + 1] == '\'') {
                        ++bp;
                        ch = '\'';
                        scanString();
                        token = Token.LITERAL_NCHARS;
                        return;
                    }
                }

                scanIdentifier();
                return;
            }

            switch (ch) {
                case '0':
                    if (buf[bp + 1] == 'x') {
                        scanChar();
                        scanChar();
                        scanHexaDecimal();
                    } else {
                        scanNumber();
                    }
                    return;
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
                case ',':
                    scanChar();
                    token = COMMA;
                    return;
                case '(':
                    scanChar();
                    token = LPAREN;
                    return;
                case ')':
                    scanChar();
                    token = RPAREN;
                    return;
                case '[':
                    scanChar();
                    token = LBRACKET;
                    return;
                case ']':
                    scanChar();
                    token = RBRACKET;
                    return;
                case '{':
                    scanChar();
                    token = LBRACE;
                    return;
                case '}':
                    scanChar();
                    token = RBRACE;
                    return;
                case ':':
                    scanChar();
                    if (ch == '=') {
                        scanChar();
                        token = COLONEQ;
                    } else {
                        if (isDigit(ch)) {
                            unscan();
                            scanVariable();
                        } else {
                            unscan();
                            scanVariable();
                        }
                    }
                    return;
                case '#':
                    scanVariable();
                    return;
                case '.':
                    scanChar();
                    if (isDigit(ch)) {
                        unscan();
                        scanNumber();
                        return;
                    } else if (ch == '.') {
                        scanChar();
                        if (ch == '.') {
                            scanChar();
                            token = Token.DOTDOTDOT;
                        } else {
                            token = Token.DOTDOT;
                        }
                    } else {
                        token = Token.DOT;
                    }
                    return;
                case '\'':
                    scanString();
                    return;
                case '\"':
                    scanAlias();
                    return;
                case '*':
                    scanChar();
                    token = Token.STAR;
                    return;
                case '?':
                    scanChar();
                    token = Token.QUES;
                    return;
                case ';':
                    scanChar();
                    token = Token.SEMI;
                    return;
                case '`':
                    throw new SQLParseException("TODO"); // TODO
                case '@':
                    scanVariable();
                    return;
                case '-':
                    int subNextChar = buf[bp + 1];
                    if (subNextChar == '-') {
                        scanComment();
                        if ((token() == Token.LINE_COMMENT || token() == Token.MULTI_LINE_COMMENT) && skipComment) {
                            sp = 0;
                            continue;
                        }
                    } else {
                        scanOperator();
                    }
                    return;
                case '/':
                    int nextChar = buf[bp + 1];
                    if (nextChar == '/' || nextChar == '*') {
                        scanComment();
                        if ((token() == Token.LINE_COMMENT || token() == Token.MULTI_LINE_COMMENT) && skipComment) {
                            sp = 0;
                            continue;
                        }
                    } else {
                        token = Token.SLASH;
                        scanChar();
                    }
                    return;
                default:
                    if (Character.isLetter(ch)) {
                        scanIdentifier();
                        return;
                    }

                    if (isOperator(ch)) {
                        scanOperator();
                        return;
                    }

                    // QS_TODO ?
                    if (bp == buflen || ch == EOI && bp + 1 == buflen) { // JLS
                        token = EOF;
                        tokenPos = bp = eofPos;
                    } else {
                        lexError(tokenPos, "illegal.char", String.valueOf((int) ch));
                        scanChar();
                    }

                    return;
            }
        }

    }

    private final void scanOperator() {
        switch (ch) {
            case '+':
                scanChar();
                token = Token.PLUS;
                break;
            case '-':
                scanChar();
                token = Token.SUB;
                break;
            case '*':
                scanChar();
                token = Token.STAR;
                break;
            case '/':
                scanChar();
                token = Token.SLASH;
                break;
            case '&':
                scanChar();
                if (ch == '&') {
                    scanChar();
                    token = Token.AMPAMP;
                } else {
                    token = Token.AMP;
                }
                break;
            case '|':
                scanChar();
                if (ch == '|') {
                    scanChar();
                    token = Token.BARBAR;
                } else {
                    token = Token.BAR;
                }
                break;
            case '^':
                scanChar();
                token = Token.CARET;
                break;
            case '%':
                scanChar();
                token = Token.PERCENT;
                break;
            case '=':
                scanChar();
                if (ch == '=') {
                    scanChar();
                    token = Token.EQEQ;
                } else {
                    token = Token.EQ;
                }
                break;
            case '>':
                scanChar();
                if (ch == '=') {
                    scanChar();
                    token = Token.GTEQ;
                } else if (ch == '>') {
                    scanChar();
                    token = Token.GTGT;
                } else {
                    token = Token.GT;
                }
                break;
            case '<':
                scanChar();
                if (ch == '=') {
                    scanChar();
                    if (ch == '>') {
                        token = Token.LTEQGT;
                        scanChar();
                    } else {
                        token = Token.LTEQ;
                    }
                } else if (ch == '>') {
                    scanChar();
                    token = Token.LTGT;
                } else if (ch == '<') {
                    scanChar();
                    token = Token.LTLT;
                } else {
                    token = Token.LT;
                }
                break;
            case '!':
                scanChar();
                if (ch == '=') {
                    scanChar();
                    token = Token.BANGEQ;
                } else if (ch == '>') {
                    scanChar();
                    token = Token.BANGGT;
                } else if (ch == '<') {
                    scanChar();
                    token = Token.BANGLT;
                } else {
                    token = Token.BANG;
                }
                break;
            case '?':
                scanChar();
                token = Token.QUES;
                break;
            case '~':
                scanChar();
                token = Token.TILDE;
                break;
            default:
                throw new SQLParseException("TODO");
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

            if (ch == '\'') {
                scanChar();
                if (ch != '\'') {
                    token = LITERAL_CHARS;
                    break;
                } else {
                    if (!hasSpecial) {
                        System.arraycopy(buf, np + 1, sbuf, 0, sp);
                        hasSpecial = true;
                    }
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

    private final void scanAlias() {
        np = bp;

        for (;;) {
            if (bp >= buflen) {
                lexError(tokenPos, "unclosed.str.lit");
                return;
            }

            ch = buf[++bp];

            if (ch == '\"') {
                scanChar();
                token = LITERAL_ALIAS;
                break;
            }

            if (sp == sbuf.length) {
                putChar(ch);
            } else {
                sbuf[sp++] = ch;
            }
        }

        stringVal = new String(buf, np + 1, sp);
    }

    public void scanVariable() {
        final char first = ch;

        if (ch != '@' && ch != ':' && ch != '#' && ch != '$') {
            throw new SQLParseException("illegal variable");
        }

        int hash = first;

        np = bp;
        sp = 1;
        char ch;

        boolean mybatisFlag = false;
        if (buf[bp + 1] == '@') {
            ch = buf[++bp];
            hash = 31 * hash + ch;

            sp++;
        } else if (buf[bp + 1] == '{') {
            hash = 31 * hash + '"';
            bp++;
            sp++;
            mybatisFlag = true;
        }

        for (;;) {
            ch = buf[++bp];

            if (!isIdentifierChar(ch)) {
                break;
            }

            hash = 31 * hash + ch;

            sp++;
            continue;
        }
        
        if (mybatisFlag) {
            if (ch != '}') {
                throw new SQLParseException("syntax error");
            }
            hash = 31 * hash + '"';
            ++bp;
            sp++;
        }

        this.ch = buf[bp];

        stringVal = symbolTable.addSymbol(buf, np, sp, hash);
        token = Token.VARIANT;
    }

    public void scanComment() {
        if (!allowComment) {
            throw new NotAllowCommentException();
        }

        if (ch != '/') {
            throw new IllegalStateException();
        }

        np = bp;
        sp = 0;
        scanChar();

        if (ch == '*') {
            scanChar();
            sp++;

            for (;;) {
                if (ch == '*' && buf[bp + 1] == '/') {
                    sp += 2;
                    scanChar();
                    scanChar();
                    break;
                }

                scanChar();
                sp++;
            }

            stringVal = new String(buf, np, sp);
            token = Token.MULTI_LINE_COMMENT;
            return;
        }

        if (ch == '/') {
            scanChar();
            sp++;

            for (;;) {
                if (ch == '\r') {
                    if (buf[bp + 1] == '\n') {
                        sp += 2;
                        scanChar();
                        break;
                    }
                    sp++;
                    break;
                }

                if (ch == '\n') {
                    scanChar();
                    sp++;
                    break;
                }

                scanChar();
                sp++;
            }

            stringVal = new String(buf, np + 1, sp);
            token = Token.LINE_COMMENT;
            return;
        }
    }

    public void scanIdentifier() {
        final char first = ch;

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

    public void scanNumber() {
        np = bp;

        if (ch == '-') {
            sp++;
            ch = buf[++bp];
        }

        for (;;) {
            if (ch >= '0' && ch <= '9') {
                sp++;
            } else {
                break;
            }
            ch = buf[++bp];
        }

        boolean isDouble = false;

        if (ch == '.') {
            if (buf[bp + 1] == '.') {
                token = Token.LITERAL_INT;
                return;
            }
            sp++;
            ch = buf[++bp];
            isDouble = true;

            for (;;) {
                if (ch >= '0' && ch <= '9') {
                    sp++;
                } else {
                    break;
                }
                ch = buf[++bp];
            }
        }

        if (ch == 'e' || ch == 'E') {
            sp++;
            ch = buf[++bp];

            if (ch == '+' || ch == '-') {
                sp++;
                ch = buf[++bp];
            }

            for (;;) {
                if (ch >= '0' && ch <= '9') {
                    sp++;
                } else {
                    break;
                }
                ch = buf[++bp];
            }

            isDouble = true;
        }

        if (isDouble) {
            token = Token.LITERAL_FLOAT;
        } else {
            token = Token.LITERAL_INT;
        }
    }

    public void scanHexaDecimal() {
        np = bp;

        if (ch == '-') {
            sp++;
            ch = buf[++bp];
        }

        for (;;) {
            if (CharTypes.isHex(ch)) {
                sp++;
            } else {
                break;
            }
            ch = buf[++bp];
        }

        token = Token.LITERAL_HEX;
    }

    public String hexString() throws NumberFormatException {
        return new String(buf, np, sp);
    }

    public final boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * Append a character to sbuf.
     */
    protected final void putChar(char ch) {
        if (sp == sbuf.length) {
            char[] newsbuf = new char[sbuf.length * 2];
            System.arraycopy(sbuf, 0, newsbuf, 0, sbuf.length);
            sbuf = newsbuf;
        }
        sbuf[sp++] = ch;
    }

    /**
     * Return the current token's position: a 0-based offset from beginning of the raw input stream (before unicode
     * translation)
     */
    public final int pos() {
        return tokenPos;
    }

    /**
     * The value of a literal token, recorded as a string. For integers, leading 0x and 'l' suffixes are suppressed.
     */
    public final String stringVal() {
        return stringVal;
    }

    private boolean isOperator(char ch) {
        switch (ch) {
            case '!':
            case '%':
            case '&':
            case '*':
            case '+':
            case '-':
            case '<':
            case '=':
            case '>':
            case '^':
            case '|':
            case '~':
            case ';':
                return true;
            default:
                return false;
        }
    }

    private static final long  MULTMIN_RADIX_TEN   = Long.MIN_VALUE / 10;
    private static final long  N_MULTMAX_RADIX_TEN = -Long.MAX_VALUE / 10;

    private final static int[] digits              = new int[(int) '9' + 1];

    static {
        for (int i = '0'; i <= '9'; ++i) {
            digits[i] = i - '0';
        }
    }

    // QS_TODO negative number is invisible for lexer
    public Number integerValue() throws NumberFormatException {
        long result = 0;
        boolean negative = false;
        int i = np, max = np + sp;
        long limit;
        long multmin;
        int digit;

        if (buf[np] == '-') {
            negative = true;
            limit = Long.MIN_VALUE;
            i++;
        } else {
            limit = -Long.MAX_VALUE;
        }
        multmin = negative ? MULTMIN_RADIX_TEN : N_MULTMAX_RADIX_TEN;
        if (i < max) {
            digit = digits[buf[i++]];
            result = -digit;
        }
        while (i < max) {
            // Accumulating negatively avoids surprises near MAX_VALUE
            digit = digits[buf[i++]];
            if (result < multmin) {
                return new BigInteger(numberString());
            }
            result *= 10;
            if (result < limit + digit) {
                return new BigInteger(numberString());
            }
            result -= digit;
        }

        if (negative) {
            if (i > np + 1) {
                if (result >= Integer.MIN_VALUE) {
                    return (int) result;
                }
                return result;
            } else { /* Only got "-" */
                throw new NumberFormatException(numberString());
            }
        } else {
            result = -result;
            if (result <= Integer.MAX_VALUE) {
                return (int) result;
            }
            return result;
        }
    }

    public int bp() {
        return this.bp;
    }

    public char current() {
        return this.ch;
    }

    public void reset(int mark, char markChar, Token token) {
        this.bp = mark;
        this.ch = markChar;
        this.token = token;
    }

    public final String numberString() {
        return new String(buf, np, sp);
    }

    public BigDecimal decimalValue() {
        return new BigDecimal(buf, np, sp);
    }
}

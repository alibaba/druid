/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
import static com.alibaba.druid.sql.parser.SQLParserFeature.KeepComments;
import static com.alibaba.druid.sql.parser.SQLParserFeature.OptimizedForParameterized;
import static com.alibaba.druid.sql.parser.SQLParserFeature.SkipComments;
import static com.alibaba.druid.sql.parser.Token.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlLexer;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.StringUtils;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public class Lexer {
    protected static SymbolTable symbols_l2 = new SymbolTable(512);

    protected int          features       = 0; //SQLParserFeature.of(SQLParserFeature.EnableSQLBinaryOpExprGroup);
    public    final String text;
    protected int          pos;
    protected int          mark;

    protected char         ch;

    protected char[]       buf;
    protected int          bufPos;

    protected Token        token;

    protected Keywords     keywods        = Keywords.DEFAULT_KEYWORDS;

    protected String       stringVal;
    protected long         hash_lower; // fnv1a_64
    protected long         hash;

    protected int            commentCount = 0;
    protected List<String>   comments     = null;
    protected boolean        skipComment  = true;
    private SavePoint        savePoint    = null;

    /*
     * anti sql injection
     */
    private boolean          allowComment = true;
    private int              varIndex     = -1;
    protected CommentHandler commentHandler;
    protected boolean        endOfComment = false;
    protected boolean        keepComments = false;
    protected int            line         = 0;
    protected int            lines        = 0;
    protected String         dbType;

    protected boolean        optimizedForParameterized = false;

    private int startPos;
    private int posLine;
    private int posColumn;

    public Lexer(String input){
        this(input, null);
    }
    
    public Lexer(String input, CommentHandler commentHandler){
        this(input, true);
        this.commentHandler = commentHandler;
    }

    public Lexer(String input, CommentHandler commentHandler, String dbType){
        this(input, true);
        this.commentHandler = commentHandler;
        this.dbType = dbType;

        if (JdbcConstants.SQLITE.equals(dbType)) {
            this.keywods = Keywords.SQLITE_KEYWORDS;
        }
    }
    
    public boolean isKeepComments() {
        return keepComments;
    }
    
    public void setKeepComments(boolean keepComments) {
        this.keepComments = keepComments;
    }

    public CommentHandler getCommentHandler() {
        return commentHandler;
    }

    public void setCommentHandler(CommentHandler commentHandler) {
        this.commentHandler = commentHandler;
    }

    public final char charAt(int index) {
        if (index >= text.length()) {
            return EOI;
        }

        return text.charAt(index);
    }

    public final String addSymbol() {
        return subString(mark, bufPos);
    }

    public final String subString(int offset, int count) {
        return text.substring(offset, offset + count);
    }

    public final char[] sub_chars(int offset, int count) {
        char[] chars = new char[count];
        text.getChars(offset, offset + count, chars, 0);
        return chars;
    }

    protected void initBuff(int size) {
        if (buf == null) {
            if (size < 32) {
                buf = new char[32];
            } else {
                buf = new char[size + 32];
            }
        } else if (buf.length < size) {
            buf = Arrays.copyOf(buf, size);
        }
    }

    public void arraycopy(int srcPos, char[] dest, int destPos, int length) {
        text.getChars(srcPos, srcPos + length, dest, destPos);
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

    public static class SavePoint {
        int   bp;
        int   sp;
        int   np;
        char  ch;
        long hash;
        long hash_lower;
        public Token token;
        String stringVal;
    }

    public Keywords getKeywods() {
        return keywods;
    }

    public SavePoint mark() {
        SavePoint savePoint = new SavePoint();
        savePoint.bp = pos;
        savePoint.sp = bufPos;
        savePoint.np = mark;
        savePoint.ch = ch;
        savePoint.token = token;
        savePoint.stringVal = stringVal;
        savePoint.hash = hash;
        savePoint.hash_lower = hash_lower;
        return this.savePoint = savePoint;
    }

    public void reset(SavePoint savePoint) {
        this.pos = savePoint.bp;
        this.bufPos = savePoint.sp;
        this.mark = savePoint.np;
        this.ch = savePoint.ch;
        this.token = savePoint.token;
        this.stringVal = savePoint.stringVal;
        this.hash = savePoint.hash;
        this.hash_lower = savePoint.hash_lower;
    }

    public void reset() {
        this.reset(this.savePoint);
    }

    public void reset(int pos) {
        this.pos = pos;
        this.ch = charAt(pos);
    }

    public Lexer(String input, boolean skipComment){
        this.skipComment = skipComment;

        this.text = input;
        this.pos = 0;
        ch = charAt(pos);
    }

    public Lexer(char[] input, int inputLength, boolean skipComment){
        this(new String(input, 0, inputLength), skipComment);
    }

    protected final void scanChar() {
        ch = charAt(++pos);
    }
    
    protected void unscan() {
        ch = charAt(--pos);
    }

    public boolean isEOF() {
        return pos >= text.length();
    }

    /**
     * Report an error at the given position using the provided arguments.
     */
    protected void lexError(String key, Object... args) {
        token = ERROR;
    }

    /**
     * Return the current token, set by nextToken().
     */
    public final Token token() {
        return token;
    }

    public final String getDbType() {
        return this.dbType;
    }

    public String info() {
        int line = 1;
        int column = 1;
        for (int i = 0; i < startPos; ++i, column++) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                column = 1;
                line++;
            }
        }

        this.posLine = line;
        this.posColumn = column;

        StringBuilder buf = new StringBuilder();
        buf
                .append("pos ")
                .append(pos)
                .append(", line ")
                .append(line)
                .append(", column ")
                .append(column)
                .append(", token ")
                .append(token);

        if (token == Token.IDENTIFIER || token == Token.LITERAL_ALIAS || token == Token.LITERAL_CHARS) {
            buf.append(" ").append(stringVal);
        }

        return buf.toString();
    }

    public final void nextTokenComma() {
        if (ch == ' ') {
            scanChar();
        }

        if (ch == ',' || ch == '，') {
            scanChar();
            token = COMMA;
            return;
        }

        if (ch == ')' || ch == '）') {
            scanChar();
            token = RPAREN;
            return;
        }

        if (ch == '.') {
            scanChar();
            token = DOT;
            return;
        }

        if (ch == 'a' || ch == 'A') {
            char ch_next = charAt(pos + 1);
            if (ch_next == 's' || ch_next == 'S') {
                char ch_next_2 = charAt(pos + 2);
                if (ch_next_2 == ' ') {
                    pos += 2;
                    ch = ' ';
                    token = Token.AS;
                    stringVal = "AS";
                    return;
                }
            }
        }

        nextToken();
    }

    public final void nextTokenCommaValue() {
        if (ch == ' ') {
            scanChar();
        }

        if (ch == ',' || ch == '，') {
            scanChar();
            token = COMMA;
            return;
        }

        if (ch == ')' || ch == '）') {
            scanChar();
            token = RPAREN;
            return;
        }

        if (ch == '.') {
            scanChar();
            token = DOT;
            return;
        }

        if (ch == 'a' || ch == 'A') {
            char ch_next = charAt(pos + 1);
            if (ch_next == 's' || ch_next == 'S') {
                char ch_next_2 = charAt(pos + 2);
                if (ch_next_2 == ' ') {
                    pos += 2;
                    ch = ' ';
                    token = Token.AS;
                    stringVal = "AS";
                    return;
                }
            }
        }

        nextTokenValue();
    }

    public final void nextTokenEq() {
        if (ch == ' ') {
            scanChar();
        }

        if (ch == '=') {
            scanChar();
            token = EQ;
            return;
        }

        if (ch == '.') {
            scanChar();
            token = DOT;
            return;
        }

        if (ch == 'a' || ch == 'A') {
            char ch_next = charAt(pos + 1);
            if (ch_next == 's' || ch_next == 'S') {
                char ch_next_2 = charAt(pos + 2);
                if (ch_next_2 == ' ') {
                    pos += 2;
                    ch = ' ';
                    token = Token.AS;
                    stringVal = "AS";
                    return;
                }
            }
        }

        nextToken();
    }

    public final void nextTokenLParen() {
        if (ch == ' ') {
            scanChar();
        }

        if (ch == '(' || ch == '（') {
            scanChar();
            token = LPAREN;
            return;
        }
        nextToken();
    }

    public final void nextTokenValue() {
        this.startPos = pos;
        if (ch == ' ') {
            scanChar();
        }

        if (ch == '\'') {
            bufPos = 0;
            scanString();
            return;
        }

        if (ch == '"') {
            bufPos = 0;
            scanString2_d();
            return;
        }

        if (ch == '0') {
            bufPos = 0;
            if (charAt(pos + 1) == 'x') {
                scanChar();
                scanChar();
                scanHexaDecimal();
            } else {
                scanNumber();
            }
            return;
        }

        if (ch > '0' && ch <= '9') {
            bufPos = 0;
            scanNumber();
            return;
        }

        if (ch == '?') {
            scanChar();
            token = Token.QUES;
            return;
        }

        if (ch == 'n' || ch == 'N') {
            char c1 = 0, c2, c3, c4;
            if (pos + 4 < text.length()
                    && ((c1 = text.charAt(pos + 1)) == 'u' || c1 == 'U')
                    && ((c2 = text.charAt(pos + 2)) == 'l' || c2 == 'L')
                    && ((c3 = text.charAt(pos + 3)) == 'l' || c3 == 'L')
                    && (isWhitespace(c4 = text.charAt(pos + 4)) || c4 == ',' || c4 == ')')) {
                pos += 4;
                ch = c4;
                token = Token.NULL;
                stringVal = "NULL";
                return;
            }

            if (c1 == '\'') {
                bufPos = 0;
                ++pos;
                ch = '\'';
                scanString();
                token = Token.LITERAL_NCHARS;
                return;
            }
        }

        if (ch == ')') {
            scanChar();
            token = Token.RPAREN;
            return;
        }

        if (isFirstIdentifierChar(ch)) {
            scanIdentifier();
            return;
        }

        nextToken();
    }

    public final void nextTokenBy() {
        while (ch == ' ') {
            scanChar();
        }

        if (ch == 'b' || ch == 'B') {
            char ch_next = charAt(pos + 1);
            if (ch_next == 'y' || ch_next == 'Y') {
                char ch_next_2 = charAt(pos + 2);
                if (ch_next_2 == ' ') {
                    pos += 2;
                    ch = ' ';
                    token = Token.BY;
                    stringVal = "BY";
                    return;
                }
            }
        }

        nextToken();
    }

    public final void nextTokenNotOrNull() {
        while (ch == ' ') {
            scanChar();
        }


        if ((ch == 'n' || ch == 'N') && pos + 3 < text.length()) {
            char c1 = text.charAt(pos + 1);
            char c2 = text.charAt(pos + 2);
            char c3 = text.charAt(pos + 3);

            if ((c1 == 'o' || c1 == 'O')
                    && (c2 == 't' || c2 == 'T')
                    && isWhitespace(c3)) {
                pos += 3;
                ch = c3;
                token = Token.NOT;
                stringVal = "NOT";
                return;
            }

            char c4;
            if (pos + 4 < text.length()
                    && (c1 == 'u' || c1 == 'U')
                    && (c2 == 'l' || c2 == 'L')
                    && (c3 == 'l' || c3 == 'L')
                    && isWhitespace(c4 = text.charAt(pos + 4))) {
                pos += 4;
                ch = c4;
                token = Token.NULL;
                stringVal = "NULL";
                return;
            }
        }

        nextToken();
    }

    public final void nextTokenIdent() {
        while (ch == ' ') {
            scanChar();
        }

        if (isFirstIdentifierChar(ch)) {
            scanIdentifier();
            return;
        }

        if (ch == ')') {
            scanChar();
            token = RPAREN;
            return;
        }

        nextToken();
    }

    public final void nextToken() {
        startPos = pos;
        bufPos = 0;
        if (comments != null && comments.size() > 0) {
            comments = null;
        }

        this.lines = 0;
        int startLine = line;
        
        for (;;) {
            if (isWhitespace(ch)) {
                if (ch == '\n') {
                    line++;
                    
                    lines = line - startLine;
                }

                ch = charAt(++pos);
                continue;
            }

            if (ch == '$' && charAt(pos + 1) == '{') {
                scanVariable();
                return;
            }

            if (isFirstIdentifierChar(ch)) {
                if (ch == '（') {
                    scanChar();
                    token = LPAREN;
                    return;
                } else if (ch == '）') {
                    scanChar();
                    token = RPAREN;
                    return;
                }

                if (ch == 'N' || ch == 'n') {
                    if (charAt(pos + 1) == '\'') {
                        ++pos;
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
                    if (charAt(pos + 1) == 'x') {
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
                case '，':
                    scanChar();
                    token = COMMA;
                    return;
                case '(':
                case '（':
                    scanChar();
                    token = LPAREN;
                    return;
                case ')':
                case '）':
                    scanChar();
                    token = RPAREN;
                    return;
                case '[':
                    scanLBracket();
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
                    } else if (ch == ':') {
                        scanChar();
                        token = COLONCOLON;
                    } else {
                        unscan();
                        scanVariable();
                    }
                    return;
                case '#':
                    scanSharp();
                    if ((token == Token.LINE_COMMENT || token == Token.MULTI_LINE_COMMENT) && skipComment) {
                        bufPos = 0;
                        continue;
                    }
                    return;
                case '.':
                    scanChar();
                    if (isDigit(ch) && !isFirstIdentifierChar(charAt(pos - 2))) {
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
                    if (ch == '?' && JdbcConstants.POSTGRESQL.equals(dbType)) {
                        scanChar();
                        if (ch == '|') {
                            scanChar();
                            token = Token.QUESBAR;
                        } else {
                            token = Token.QUESQUES;
                        }
                    } else if (ch == '|' && JdbcConstants.POSTGRESQL.equals(dbType)) {
                        scanChar();
                        if (ch == '|') {
                            unscan();
                            token = Token.QUES;
                        } else {
                            token = Token.QUESBAR;
                        }
                    } else if (ch == '&' && JdbcConstants.POSTGRESQL.equals(dbType)) {
                        scanChar();
                        token = Token.QUESAMP;
                    } else {
                        token = Token.QUES;
                    }
                    return;
                case ';':
                    scanChar();
                    token = Token.SEMI;
                    return;
                case '`':
                    throw new ParserException("TODO. " + info()); // TODO
                case '@':
                    scanVariable_at();
                    return;
                case '-':
                    if (charAt(pos +1) == '-') {
                        scanComment();
                        if ((token == Token.LINE_COMMENT || token == Token.MULTI_LINE_COMMENT) && skipComment) {
                            bufPos = 0;
                            continue;
                        }
                    } else {
                        scanOperator();
                    }
                    return;
                case '/':
                    int nextChar = charAt(pos + 1);
                    if (nextChar == '/' || nextChar == '*') {
                        scanComment();
                        if ((token == Token.LINE_COMMENT || token == Token.MULTI_LINE_COMMENT) && skipComment) {
                            bufPos = 0;
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

                    if (ch == '\\' && charAt(pos + 1) == 'N'
                            && JdbcConstants.MYSQL.equals(dbType)) {
                        scanChar();
                        scanChar();
                        token = Token.NULL;
                        return;
                    }

                    // QS_TODO ?
                    if (isEOF()) { // JLS
                        token = EOF;
                    } else {
                        lexError("illegal.char", String.valueOf((int) ch));
                        scanChar();
                    }

                    return;
            }
        }

    }

    protected void scanLBracket() {
        scanChar();
        token = LBRACKET;
    }

    private final void scanOperator() {
        switch (ch) {
            case '+':
                scanChar();
                token = Token.PLUS;
                break;
            case '-':
                scanChar();
                if (ch == '>') {
                    scanChar();
                    if (ch == '>') {
                        scanChar();
                        token = Token.SUBGTGT;
                    } else {
                        token = Token.SUBGT;
                    }
                } else {
                    token = Token.SUB;    
                }
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
                    if (ch == '/') {
                        scanChar();
                        token = Token.BARBARSLASH; 
                    } else {
                        token = Token.BARBAR;
                    }
                } else if (ch == '/') {
                    scanChar();
                    token = Token.BARSLASH;
                } else {
                    token = Token.BAR;
                }
                break;
            case '^':
                scanChar();
                if (ch == '=') {
                    scanChar();
                    token = Token.CARETEQ;
                } else {
                    token = Token.CARET;
                }
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
                } else if (ch == '>') {
                    scanChar();
                    token = Token.EQGT;
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
                } else if (ch == '@') {
                    scanChar();
                    token = Token.LT_MONKEYS_AT;
                } else if (ch == '-' && charAt(pos + 1) == '>') {
                    scanChar();
                    scanChar();
                    token = Token.LT_SUB_GT;
                } else {
                    if (ch == ' ') {
                        char c1 = charAt(pos + 1);
                        if (c1 == '=') {
                            scanChar();
                            scanChar();
                            if (ch == '>') {
                                token = Token.LTEQGT;
                                scanChar();
                            } else {
                                token = Token.LTEQ;
                            }
                        } else if (c1 == '>') {
                            scanChar();
                            scanChar();
                            token = Token.LTGT;
                        } else if (c1 == '<') {
                            scanChar();
                            scanChar();
                            token = Token.LTLT;
                        } else if (c1 == '@') {
                            scanChar();
                            scanChar();
                            token = Token.LT_MONKEYS_AT;
                        } else if (c1 == '-' && charAt(pos + 2) == '>') {
                            scanChar();
                            scanChar();
                            scanChar();
                            token = Token.LT_SUB_GT;
                        } else {
                            token = Token.LT;
                        }
                    } else {
                        token = Token.LT;
                    }
                }
                break;
            case '!':
                scanChar();
                while (isWhitespace(ch)) {
                    scanChar();
                }
                if (ch == '=') {
                    scanChar();
                    token = Token.BANGEQ;
                } else if (ch == '>') {
                    scanChar();
                    token = Token.BANGGT;
                } else if (ch == '<') {
                    scanChar();
                    token = Token.BANGLT;
                } else if (ch == '!') {
                    scanChar();
                    token = Token.BANGBANG; // postsql
                } else if (ch == '~') {
                    scanChar();
                    if (ch == '*') {
                        scanChar();
                        token = Token.BANG_TILDE_STAR; // postsql
                    } else {
                        token = Token.BANG_TILDE; // postsql
                    }
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
                if (ch == '*') {
                    scanChar();
                    token = Token.TILDE_STAR;
                } else if (ch == '=') {
                    scanChar();
                    token = Token.TILDE_EQ; // postsql
                } else {
                    token = Token.TILDE;
                }
                break;
            default:
                throw new ParserException("TODO. " + info());
        }
    }

    protected void scanString() {
        mark = pos;
        boolean hasSpecial = false;
        Token preToken = this.token;

        for (;;) {
            if (isEOF()) {
                lexError("unclosed.str.lit");
                return;
            }

            ch = charAt(++pos);

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
            if (preToken == Token.AS) {
                stringVal = subString(mark, bufPos + 2);
            } else {
                stringVal = subString(mark + 1, bufPos);
            }
        } else {
            stringVal = new String(buf, 0, bufPos);
        }
    }
    
    protected final void scanString2() {
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
                stringVal = subString(pos, endIndex + 1 - pos);
            } else {
                stringVal = subString(startIndex, endIndex - startIndex);
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
                    case '_':
                        if(JdbcConstants.MYSQL.equals(dbType)) {
                            putChar('\\');
                        }
                        putChar('_');
                        break;
                    case 'Z':
                        putChar((char) 0x1A); // ctrl + Z
                        break;
                    case '%':
                        putChar('\\');
                        putChar(ch);
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

    protected final void scanString2_d() {
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
                stringVal = subString(pos, endIndex + 1 - pos);
            } else {
                stringVal = subString(startIndex, endIndex - startIndex);
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
                        if(JdbcConstants.MYSQL.equals(dbType)) {
                            putChar('\\');
                        }
                        putChar('%');
                        break;
                    case '_':
                        if(JdbcConstants.MYSQL.equals(dbType)) {
                            putChar('\\');
                        }
                        putChar('_');
                        break;
                    default:
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
            stringVal = subString(mark + 1, bufPos);
        } else {
            stringVal = new String(buf, 0, bufPos);
        }
    }

    protected final void scanAlias() {
        final char quote = ch;
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
                if (ch == quote) {
                    if (i + 1 < text.length()) {
                        char ch_next = charAt(i + 1);
                        if (ch_next == quote) {
                            hasSpecial = true;
                            i++;
                            continue;
                        }
                    }

                    endIndex = i;
                    break;
                }
            }

            if (endIndex == -1) {
                throw new ParserException("unclosed str. " + info());
            }

            String stringVal = subString(pos, endIndex + 1 - pos);
            // hasSpecial = stringVal.indexOf('\\') != -1;

            if (!hasSpecial) {
                this.stringVal = stringVal;
                int pos = endIndex + 1;
                char ch = charAt(pos);
                if (ch != '\'') {
                    this.pos = pos;
                    this.ch = ch;
                    token = LITERAL_ALIAS;
                    return;
                }
            }
        }

        mark = pos;
        initBuff(bufPos);
        //putChar(ch);

        putChar(ch);
        for (;;) {
            if (isEOF()) {
                lexError("unclosed.str.lit");
                return;
            }

            ch = charAt(++pos);

            if (ch == '\\') {
                scanChar();

                switch (ch) {
                    case '0':
                        putChar('\0');
                        break;
                    case '\'':
                        if (ch == quote) {
                            putChar('\\');
                        }
                        putChar('\'');
                        break;
                    case '"':
                        if (ch == quote) {
                            putChar('\\');
                        }
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

            if (ch == quote) {
                char ch_next = charAt(pos + 1);

                if (ch_next == quote) {
                    putChar('\\');
                    putChar(ch);
                    scanChar();
                    continue;
                }

                putChar(ch);
                scanChar();
                token = LITERAL_ALIAS;
                break;
            }

            if (bufPos == buf.length) {
                putChar(ch);
            } else {
                buf[bufPos++] = ch;
            }
        }

        stringVal = new String(buf, 0, bufPos);
    }
    
    public void scanSharp() {
        scanVariable();
    }

    public void scanVariable() {
        if (ch != ':' && ch != '#' && ch != '$') {
            throw new ParserException("illegal variable. " + info());
        }

        mark = pos;
        bufPos = 1;
        char ch;

        final char c1 = charAt(pos + 1);
        if (c1 == '>' && JdbcConstants.POSTGRESQL.equalsIgnoreCase(dbType)) {
            pos += 2;
            token = Token.MONKEYS_AT_GT;
            this.ch = charAt(++pos);
            return;
        } else if (c1 == '{') {
            pos++;
            bufPos++;
            
            for (;;) {
                ch = charAt(++pos);

                if (ch == '}') {
                    break;
                }

                bufPos++;
                continue;
            }
            
            if (ch != '}') {
                throw new ParserException("syntax error. " + info());
            }
            ++pos;
            bufPos++;
            
            this.ch = charAt(pos);

            stringVal = addSymbol();
            token = Token.VARIANT;
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

    protected void scanVariable_at() {
        if (ch != '@') {
            throw new ParserException("illegal variable. " + info());
        }

        mark = pos;
        bufPos = 1;
        char ch;

        final char c1 = charAt(pos + 1);
        if (c1 == '@') {
            ++pos;
            bufPos++;
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

    public void scanComment() {
        if (!allowComment) {
            throw new NotAllowCommentException();
        }

        if ((ch == '/' && charAt(pos + 1) == '/')
                || (ch == '-' && charAt(pos + 1) == '-')) {
            scanSingleLineComment();
        } else if (ch == '/' && charAt(pos + 1) == '*') {
            scanMultiLineComment();
        } else {
            throw new IllegalStateException();
        }
    }

    private void scanMultiLineComment() {
        Token lastToken = this.token;
        
        scanChar();
        scanChar();
        mark = pos;
        bufPos = 0;

        for (;;) {
            if (ch == '*' && charAt(pos + 1) == '/') {
                scanChar();
                scanChar();
                break;
            }
            
			// multiline comment结束符错误
			if (ch == EOI) {
				throw new ParserException("unterminated /* comment. " + info());
			}
            scanChar();
            bufPos++;
        }

        stringVal = subString(mark, bufPos);
        token = Token.MULTI_LINE_COMMENT;
        commentCount++;
        if (keepComments) {
            addComment(stringVal);
        }
        
        if (commentHandler != null && commentHandler.handle(lastToken, stringVal)) {
            return;
        }
        
        if (!isAllowComment() && !isSafeComment(stringVal)) {
            throw new NotAllowCommentException();
        }
    }

    private void scanSingleLineComment() {
        Token lastToken = this.token;
        
        scanChar();
        scanChar();
        mark = pos;
        bufPos = 0;

        for (;;) {
            if (ch == '\r') {
                if (charAt(pos + 1) == '\n') {
                    line++;
                    scanChar();
                    break;
                }
                bufPos++;
                break;
            }

            if (ch == '\n') {
                line++;
                scanChar();
                break;
            }
            
			// single line comment结束符错误
			if (ch == EOI) {
				throw new ParserException("syntax error at end of input. " + info());
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
        
        if (!isAllowComment() && !isSafeComment(stringVal)) {
            throw new NotAllowCommentException();
        }
    }

    public void scanIdentifier() {
        this.hash_lower = 0;
        this.hash = 0;

        final char first = ch;

        if (ch == '`') {
            mark = pos;
            bufPos = 1;
            char ch;

            int startPos = pos + 1;
            int quoteIndex = text.indexOf('`', startPos);
            if (quoteIndex == -1) {
                throw new ParserException("illegal identifier. " + info());
            }

            hash_lower = 0xcbf29ce484222325L;
            hash = 0xcbf29ce484222325L;

            for (int i = startPos; i < quoteIndex; ++i) {
                ch = text.charAt(i);

                hash_lower ^= ((ch >= 'A' && ch <= 'Z') ? (ch + 32) : ch);
                hash_lower *= 0x100000001b3L;

                hash ^= ch;
                hash *= 0x100000001b3L;
            }

            stringVal = MySqlLexer.quoteTable.addSymbol(text, pos, quoteIndex + 1 - pos, hash);
            //stringVal = text.substring(mark, pos);
            pos = quoteIndex + 1;
            this.ch = charAt(pos);
            token = Token.IDENTIFIER;
            return;
        }

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
        char ch;
        for (;;) {
            ch = charAt(++pos);

            if (!isIdentifierChar(ch)) {
                break;
            }

            hash_lower ^= ((ch >= 'A' && ch <= 'Z') ? (ch + 32) : ch);
            hash_lower *= 0x100000001b3L;

            hash ^= ch;
            hash *= 0x100000001b3L;

            bufPos++;
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
            token = Token.LITERAL_INT;
        }
    }

    public void scanHexaDecimal() {
        mark = pos;

        if (ch == '-') {
            bufPos++;
            ch = charAt(++pos);
        }

        for (;;) {
            if (CharTypes.isHex(ch)) {
                bufPos++;
            } else {
                break;
            }
            ch = charAt(++pos);
        }

        token = Token.LITERAL_HEX;
    }

    public String hexString() {
        return subString(mark, bufPos);
    }

    public final boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * Append a character to sbuf.
     */
    protected final void putChar(char ch) {
        if (bufPos == buf.length) {
            char[] newsbuf = new char[buf.length * 2];
            System.arraycopy(buf, 0, newsbuf, 0, buf.length);
            buf = newsbuf;
        }
        buf[bufPos++] = ch;
    }

    /**
     * Return the current token's position: a 0-based offset from beginning of the raw input stream (before unicode
     * translation)
     */
    public final int pos() {
        return pos;
    }

    /**
     * The value of a literal token, recorded as a string. For integers, leading 0x and 'l' suffixes are suppressed.
     */
    public final String stringVal() {
        if (stringVal == null) {
            stringVal = subString(mark, bufPos);
        }
        return stringVal;
    }

    private final void stringVal(StringBuffer out) {
        if (stringVal != null) {
            out.append(stringVal);
            return;
        }

        out.append(text, mark, mark + bufPos);
    }

    public final boolean identifierEquals(String text) {
        if (token != Token.IDENTIFIER) {
            return false;
        }

        if (stringVal == null) {
            stringVal = subString(mark, bufPos);
        }
        return text.equalsIgnoreCase(stringVal);
    }

    public final boolean identifierEquals(long hash_lower) {
        if (token != Token.IDENTIFIER) {
            return false;
        }

        if (this.hash_lower == 0) {
            if (stringVal == null) {
                stringVal = subString(mark, bufPos);
            }
            this.hash_lower = FnvHash.fnv1a_64_lower(stringVal);
        }
        return this.hash_lower == hash_lower;
    }

    public final long hash_lower() {
        if (this.hash_lower == 0) {
            if (stringVal == null) {
                stringVal = subString(mark, bufPos);
            }
            this.hash_lower = FnvHash.fnv1a_64_lower(stringVal);
        }
        return hash_lower;
    }
    
    public final List<String> readAndResetComments() {
        List<String> comments = this.comments;
        
        this.comments = null;
        
        return comments;
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
    public Number integerValue() {
        long result = 0;
        boolean negative = false;
        int i = mark, max = mark + bufPos;
        long limit;
        long multmin;
        int digit;

        if (charAt(mark) == '-') {
            negative = true;
            limit = Long.MIN_VALUE;
            i++;
        } else {
            limit = -Long.MAX_VALUE;
        }
        multmin = negative ? MULTMIN_RADIX_TEN : N_MULTMAX_RADIX_TEN;
        if (i < max) {
            digit = charAt(i++) - '0';
            result = -digit;
        }
        while (i < max) {
            // Accumulating negatively avoids surprises near MAX_VALUE
            digit = charAt(i++) - '0';
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
            if (i > mark + 1) {
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
        return this.pos;
    }

    public char current() {
        return this.ch;
    }

    public void reset(int mark, char markChar, Token token) {
        this.pos = mark;
        this.ch = markChar;
        this.token = token;
    }

    public final String numberString() {
        return subString(mark, bufPos);
    }

    public BigDecimal decimalValue() {
        char[] value = sub_chars(mark, bufPos);
        if (!StringUtils.isNumber(value)){
            throw new ParserException(value+" is not a number! " + info());
        }
        return new BigDecimal(value);
    }

    public SQLNumberExpr numberExpr() {
        char[] value = sub_chars(mark, bufPos);
        if (!StringUtils.isNumber(value)){
            throw new ParserException(value+" is not a number! " + info());
        }

        return new SQLNumberExpr(value);
    }

    public SQLNumberExpr numberExpr(boolean negate) {
        char[] value = sub_chars(mark, bufPos);
        if (!StringUtils.isNumber(value)){
            throw new ParserException(value+" is not a number! " + info());
        }

        if (negate) {
            char[] chars = new char[value.length + 1];
            chars[0] = '-';
            System.arraycopy(value, 0, chars, 1, value.length);
            return new SQLNumberExpr(chars);
        } else {
            return new SQLNumberExpr(value);
        }
    }

    public static interface CommentHandler {
        boolean handle(Token lastToken, String comment);
    }

    public boolean hasComment() {
        return comments != null;
    }

    public int getCommentCount() {
        return commentCount;
    }
    
    public void skipToEOF() {
        pos = text.length();
        this.token = Token.EOF;
    }

    public boolean isEndOfComment() {
        return endOfComment;
    }
    
    protected boolean isSafeComment(String comment) {
        if (comment == null) {
            return true;
        }
        comment = comment.toLowerCase();
        if (comment.indexOf("select") != -1 //
            || comment.indexOf("delete") != -1 //
            || comment.indexOf("insert") != -1 //
            || comment.indexOf("update") != -1 //
            || comment.indexOf("into") != -1 //
            || comment.indexOf("where") != -1 //
            || comment.indexOf("or") != -1 //
            || comment.indexOf("and") != -1 //
            || comment.indexOf("union") != -1 //
            || comment.indexOf('\'') != -1 //
            || comment.indexOf('=') != -1 //
            || comment.indexOf('>') != -1 //
            || comment.indexOf('<') != -1 //
            || comment.indexOf('&') != -1 //
            || comment.indexOf('|') != -1 //
            || comment.indexOf('^') != -1 //
        ) {
            return false;
        }
        return true;
    }

    protected void addComment(String comment) {
        if (comments == null) {
            comments = new ArrayList<String>(2);
        }
        comments.add(stringVal);
    }
    
    public int getLine() {
        return line;
    }

    public void computeRowAndColumn() {
        int line = 1;
        int column = 1;
        for (int i = 0; i < pos; ++i) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                column = 1;
                line++;
            }
        }

        this.posLine = line;
        this.posColumn = posColumn;
    }

    public int getPosLine() {
        return posLine;
    }

    public int getPosColumn() {
        return posColumn;
    }

    public void config(SQLParserFeature feature, boolean state) {
        features = SQLParserFeature.config(features, feature, state);

        if (feature == OptimizedForParameterized) {
            optimizedForParameterized = state;
        } else if (feature == KeepComments) {
            this.keepComments = state;
        } else if (feature == SkipComments) {
            this.skipComment = state;
        }
    }

    public final boolean isEnabled(SQLParserFeature feature) {
        return SQLParserFeature.isEnabled(this.features, feature);
    }

    public static String parameterize(String sql, String dbType) {
        Lexer lexer = SQLParserUtils.createLexer(sql, dbType);
        lexer.optimizedForParameterized = true; // optimized

        lexer.nextToken();

        StringBuffer buf = new StringBuffer();

        for_:
        for (;;) {
            Token token = lexer.token;
            switch (token) {
                case LITERAL_ALIAS:
                case LITERAL_FLOAT:
                case LITERAL_CHARS:
                case LITERAL_INT:
                case LITERAL_NCHARS:
                case LITERAL_HEX:
                case VARIANT:
                    if (buf.length() != 0) {
                        buf.append(' ');
                    }
                    buf.append('?');
                    break;
                case COMMA:
                    buf.append(',');
                    break;
                case EQ:
                    buf.append('=');
                    break;
                case EOF:
                    break for_;
                case ERROR:
                    return sql;
                case SELECT:
                    buf.append("SELECT");
                    break;
                case UPDATE:
                    buf.append("UPDATE");
                    break;
                default:
                    if (buf.length() != 0) {
                        buf.append(' ');
                    }
                    lexer.stringVal(buf);
                    break;
            }

            lexer.nextToken();
        }

        return buf.toString();
    }

    public String getSource() {
        return text;
    }
}

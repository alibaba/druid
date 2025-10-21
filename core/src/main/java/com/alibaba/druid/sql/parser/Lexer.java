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
package com.alibaba.druid.sql.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlLexer;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.alibaba.druid.sql.parser.CharTypes.*;
import static com.alibaba.druid.sql.parser.DialectFeature.LexerFeature.*;
import static com.alibaba.druid.sql.parser.LayoutCharacters.EOI;
import static com.alibaba.druid.sql.parser.SQLParserFeature.*;
import static com.alibaba.druid.sql.parser.Token.*;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public class Lexer {
    protected static final DialectFeature FEATURE = new DialectFeature();
    protected static SymbolTable symbols_l2 = new SymbolTable(512);

    protected int features; //SQLParserFeature.of(SQLParserFeature.EnableSQLBinaryOpExprGroup);
    protected TimeZone timeZone;
    public final String text;
    protected int pos;
    protected int mark;
    protected int numberSale;
    protected boolean numberExp;

    protected char ch;

    protected char[] buf;
    protected int bufPos;

    protected Token token;
    protected List<Pair<Integer, Token>> tokens;
    protected Map<Integer, String> stringValMap;

    protected Keywords keywords;

    protected String stringVal;
    protected long hashLCase; // fnv1a_64
    protected long hash;

    protected int commentCount;
    protected List<String> comments;
    protected boolean skipComment = true;
    private SavePoint savePoint;

    /*
     * anti sql injection
     */
    private boolean allowComment = true;
    private int varIndex = -1;
    protected CommentHandler commentHandler;
    protected boolean endOfComment;
    protected boolean keepComments;
    protected int line;
    protected int lines;
    protected DbType dbType;

    protected boolean optimizedForParameterized;
    protected boolean keepSourceLocation;

    protected int startPos;
    protected int posLine;
    protected int posColumn;
    protected DialectFeature dialectFeature;

    public List<Pair<Integer, Token>> getTokens() {
        return tokens;
    }

    public Map<Integer, String> getStringValMap() {
        return stringValMap;
    }

    public DialectFeature getDialectFeature() {
        return dialectFeature;
    }

    public Lexer(String input) {
        this(input, (CommentHandler) null);
    }

    public Lexer(String input, DbType dbType) {
        this(input, null, dbType);
    }

    public Lexer(String input, CommentHandler commentHandler) {
        this(input, true);
        this.commentHandler = commentHandler;
    }

    public Lexer(String input, CommentHandler commentHandler, DbType dbType) {
        this(input, true);
        this.commentHandler = commentHandler;
        this.dbType = dbType;

        if (DbType.sqlite == dbType) {
            this.keywords = Keywords.SQLITE_KEYWORDS;
        } else if (DbType.dm == dbType) {
            this.keywords = Keywords.DM_KEYWORDS;
        }
    }
    protected Keywords loadKeywords() {
        return Keywords.DEFAULT_KEYWORDS;
    }

    public boolean isKeepSourceLocation() {
        return keepSourceLocation;
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

    public final String subString(int offset) {
        return text.substring(offset);
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
        int bp;
        int startPos;
        int sp;
        int np;
        char ch;
        long hash;
        long hashLCase;
        public Token token;
        String stringVal;
    }

    public Keywords getKeywords() {
        return keywords;
    }

    //出现多次调用mark()后调用reset()会有问题
    @Deprecated
    public SavePoint mark() {
        return this.savePoint = markOut();
    }

    public SavePoint markOut() {
        SavePoint savePoint = new SavePoint();
        savePoint.bp = pos;
        savePoint.sp = bufPos;
        savePoint.np = mark;
        savePoint.ch = ch;
        savePoint.token = token;
        savePoint.stringVal = stringVal;
        savePoint.hash = hash;
        savePoint.hashLCase = hashLCase;
        savePoint.startPos = startPos;
        return savePoint;
    }

    public void reset(SavePoint savePoint) {
        this.pos = savePoint.bp;
        this.bufPos = savePoint.sp;
        this.mark = savePoint.np;
        this.ch = savePoint.ch;
        this.token = savePoint.token;
        this.stringVal = savePoint.stringVal;
        this.hash = savePoint.hash;
        this.hashLCase = savePoint.hashLCase;
        this.startPos = savePoint.startPos;
        if (dialectFeatureEnabled(SaveAllHistoricalTokens)) {
            removeTokensAfterPos(pos);
        }
    }

    //出现多次调用mark()后调用reset()会有问题
    @Deprecated
    public void reset() {
        this.reset(this.savePoint);
    }

    public void reset(int pos) {
        this.pos = pos;
        this.ch = charAt(pos);
        if (dialectFeatureEnabled(SaveAllHistoricalTokens)) {
            removeTokensAfterPos(pos);
        }
    }

    protected void removeTokensAfterPos(int pos) {
        int label = -1;
        for (int i = tokens.size() - 1; i >= 0; i--) {
            if (tokens.get(i).getKey() < pos) {
                label = i;
                break;
            }
        }
        if (label >= 0) {
            tokens.subList(label + 1, tokens.size()).clear();
        }
        stringValMap.entrySet().removeIf(entry -> entry.getKey() >= pos);
    }
    public Lexer(String input, boolean skipComment) {
        this.skipComment = skipComment;
        this.tokens = new ArrayList<>();
        this.stringValMap = new java.util.HashMap<>();
        this.text = input;
        this.pos = 0;
        ch = charAt(pos);
        while (ch == '\u200B' || ch == '\n') {
            if (ch == '\n') {
                line++;
            }
            ch = charAt(++pos);
        }
        initDialectFeature();
        this.keywords = loadKeywords();
    }

    protected void initDialectFeature() {
        this.dialectFeature = FEATURE;
    }

    public Lexer(char[] input, int inputLength, boolean skipComment) {
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
        setToken(ERROR);
    }

    /**
     * Return the current token, set by nextToken().
     */
    public final Token token() {
        return token;
    }

    public boolean nextIf(Token token) {
        if (this.token == token) {
            nextToken();
            return true;
        }
        return false;
    }

    public final boolean nextIfComma() {
        if (this.token == COMMA) {
            nextToken();
            return true;
        }
        return false;
    }

    public final boolean nextIfIdentifier(String identifier) {
        if (this.identifierEquals(identifier)) {
            nextToken();
            return true;
        }
        return false;
    }

    public final boolean nextIfIdentifier(long hashCode54) {
        if (this.identifierEquals(hashCode54)) {
            nextToken();
            return true;
        }
        return false;
    }

    private int tokenPos; // position where the current token was stored
    private int lastStringValPos = -1; // position where last stringVal was stored

    public void setToken(Token token) {
        if (dialectFeatureEnabled(SaveAllHistoricalTokens)) {
            this.tokenPos = pos;
            this.tokens.add(Pair.of(pos, token));
            // If a stringVal was set before this token, move it to the token's position
            if (lastStringValPos >= 0 && lastStringValPos != pos) {
                String val = this.stringValMap.remove(lastStringValPos);
                if (val != null) {
                    this.stringValMap.put(pos, val);
                }
                lastStringValPos = -1;
            } else if (lastStringValPos == -1 &&
                    (token == Token.IDENTIFIER || token == Token.LITERAL_INT ||
                     token == Token.LITERAL_FLOAT || token == Token.LITERAL_CHARS ||
                     token == Token.LITERAL_NCHARS || token == Token.LITERAL_HEX)) {
                // Eagerly compute and save stringVal for tokens that need it
                String val = subString(mark, bufPos);
                this.stringValMap.put(pos, val);
                this.stringVal = val;
            }
        }
        this.token = token;
    }

    public void setStringVal(String val) {
        if (dialectFeatureEnabled(SaveAllHistoricalTokens) && val != null) {
            this.stringValMap.put(pos, val);
            this.lastStringValPos = pos;
        }
        this.stringVal = val;
    }

    public final DbType getDbType() {
        return this.dbType;
    }

    public String info() {
        computeRowAndColumn();

        StringBuilder buf = new StringBuilder();
        buf.append("pos ")
                .append(pos)
                .append(", line ")
                .append(this.getPosLine())
                .append(", column ")
                .append(this.getPosColumn());
        if (token != null) {
            if (token.name != null) {
                buf.append(", token ").append(token.name);
            } else {
                buf.append(", token ").append(token.name());
            }
        }

        if (token == Token.IDENTIFIER
                || token == Token.LITERAL_ALIAS
                || token == Token.LITERAL_CHARS) {
            buf.append(" ")
                    .append(
                            stringVal());
        }

        if (isEnabled(SQLParserFeature.PrintSQLWhileParsingFailed)) {
            buf.append(", SQL : ");
            buf.append(text);
        }

        return buf.toString();
    }

    public final void nextTokenComma() {
        if (ch == ' ') {
            scanChar();
        }

        if (ch == ',' || ch == '，') {
            scanChar();
            setToken(COMMA);
            return;
        }

        if (ch == ')' || ch == '）') {
            scanChar();
            setToken(RPAREN);
            return;
        }

        if (ch == '.') {
            scanChar();
            setToken(DOT);
            return;
        }

        if (ch == 'a' || ch == 'A') {
            char ch_next = charAt(pos + 1);
            if (ch_next == 's' || ch_next == 'S') {
                char ch_next_2 = charAt(pos + 2);
                if (ch_next_2 == ' ') {
                    pos += 2;
                    ch = ' ';
                    setToken(Token.AS);
                    setStringVal("AS");
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
            setToken(COMMA);
            return;
        }

        if (ch == ')' || ch == '）') {
            scanChar();
            setToken(RPAREN);
            return;
        }

        if (ch == '.') {
            scanChar();
            setToken(DOT);
            return;
        }

        if (ch == 'a' || ch == 'A') {
            char ch_next = charAt(pos + 1);
            if (ch_next == 's' || ch_next == 'S') {
                char ch_next_2 = charAt(pos + 2);
                if (ch_next_2 == ' ') {
                    pos += 2;
                    ch = ' ';
                    setToken(Token.AS);
                    setStringVal("AS");
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
            if (ch == '=') {
                scanChar();
                setToken(EQEQ);
            } else if (ch == '>') {
                scanChar();
                setToken(EQGT);
            }
            setToken(EQ);
            return;
        }

        if (ch == '.') {
            scanChar();
            setToken(DOT);
            return;
        }

        if (ch == 'a' || ch == 'A') {
            char ch_next = charAt(pos + 1);
            if (ch_next == 's' || ch_next == 'S') {
                char ch_next_2 = charAt(pos + 2);
                if (ch_next_2 == ' ') {
                    pos += 2;
                    ch = ' ';
                    setToken(Token.AS);
                    setStringVal("AS");
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
            setToken(LPAREN);
            return;
        }
        nextToken();
    }

    public final void nextTokenValue() {
        while (ch == ' ') {
            scanChar();
        }

        this.startPos = pos;
        if (ch == '\'') {
            bufPos = 0;
            scanString();
            return;
        }

        if (ch == '"' && !isEnabled(SQLParserFeature.KeepNameQuotes)) {
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

        if (ch == '-') {
            char next = charAt(pos + 1);
            if (next >= '0' && next <= '9') {
                bufPos = 0;
                scanNumber();
                return;
            }
        }

        if (ch == '?') {
            scanChar();
            setToken(Token.QUES);
            return;
        }

        if (ch == 'n' || ch == 'N') {
            char c1 = 0, c2, c3, c4;
            if (pos + 4 < text.length()
                    && ((c1 = text.charAt(pos + 1)) == 'u' || c1 == 'U')
                    && ((c2 = text.charAt(pos + 2)) == 'l' || c2 == 'L')
                    && ((c3 = text.charAt(pos + 3)) == 'l' || c3 == 'L')
                    && (isWhitespace(c4 = text.charAt(pos + 4)) || c4 == ',' || c4 == ')' || c4 == '）')) {
                pos += 4;
                ch = c4;
                setToken(Token.NULL);
                setStringVal("NULL");
                return;
            }

            if (c1 == '\'') {
                bufPos = 0;
                ++pos;
                ch = '\'';
                scanString();
                setToken(Token.LITERAL_NCHARS);
                return;
            }
        }

        if (ch == ')' || ch == '）') {
            scanChar();
            setToken(Token.RPAREN);
            return;
        }

        if (ch == '（') {
            scanChar();
            setToken(Token.LPAREN);
            return;
        }

        if (ch == '$' && isVariantChar(charAt(pos + 1))) {
            scanVariable();
            return;
        }

        if (isFirstIdentifierChar(ch)) {
            scanIdentifier();
            return;
        }

        nextToken();
    }

    static boolean isVariantChar(char ch) {
        return ch == '{' || (ch >= '0' && ch <= '9');
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
                    setToken(Token.BY);
                    setStringVal("BY");
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
                setToken(Token.NOT);
                setStringVal("NOT");
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
                setToken(Token.NULL);
                setStringVal("NULL");
                return;
            }
        }

        nextToken();
    }

    public final void nextTokenIdent() {
        while (ch == ' ') {
            scanChar();
        }

        if (ch == '$' && isVariantChar(charAt(pos + 1))) {
            scanVariable();
            return;
        }

        if (isFirstIdentifierChar(ch) && ch != '（') {
            scanIdentifier();
            return;
        }

        if (ch == ')') {
            scanChar();
            setToken(RPAREN);
            return;
        }

        nextToken();
    }

    public final SQLType scanSQLType() {
        _loop:
        for (; ; ) {
            while (isWhitespace(ch)) {
                ch = charAt(++pos);
            }

            if (ch == '/') {
                if (pos + 1 < text.length() && text.charAt(pos + 1) == '*') {
                    int index = text.indexOf("*/", pos + 2);
                    if (index == -1) {
                        return SQLType.UNKNOWN;
                    }

                    pos = index + 2;
                    ch = charAt(pos);
                    continue;
                } else if (pos + 2 < text.length()
                        && text.charAt(pos + 1) == ' '
                        && text.charAt(pos + 2) == '*'
                        && dialectFeatureEnabled(ScanSQLTypeBlockComment)
                ) {
                    int index = text.indexOf("* /", pos + 3);
                    if (index == -1) {
                        return SQLType.UNKNOWN;
                    }

                    pos = index + 3;
                    ch = charAt(pos);
                    continue;
                }
            }

            if (dialectFeatureEnabled(ScanSQLTypeWithSemi)) {
                while (ch == ';') {
                    ch = charAt(++pos);

                    if (isEOF()) {
                        return SQLType.EMPTY;
                    }

                    while (isWhitespace(ch)) {
                        ch = charAt(++pos);
                    }
                    continue _loop;
                }
            }

            if (pos + 1 < text.length()
                    && ((ch == '-' && text.charAt(pos + 1) == '-') || (ch == '—' && text.charAt(pos + 1) == '—'))) {
                int index = text.indexOf('\n', pos + 2);
                if (index == -1) {
                    reset(0);
                    nextToken();
                    if (token == EOF) {
                        return SQLType.EMPTY;
                    }

                    return SQLType.UNKNOWN;
                }

                pos = index + 1;
                ch = charAt(pos);
                continue;
            }

            break;
        }

        while (ch == '(') {
            ch = charAt(++pos);

            while (isWhitespace(ch)) {
                ch = charAt(++pos);
            }
        }

        long hashCode = FnvHash.BASIC;
        for (; ; ) {
            char c;
            if (ch >= 'a' && ch <= 'z') {
                c = ch;
            } else if (ch >= 'A' && ch <= 'Z') {
                c = (char) (ch + 32);
            } else {
                break;
            }
            hashCode ^= c;
            hashCode *= FnvHash.PRIME;

            ch = charAt(++pos);
        }

        if (ch == '_' || (ch >= '0' && ch <= '9')) {
            return SQLType.UNKNOWN;
        }

        if (hashCode == FnvHash.Constants.SELECT) {
            return SQLType.SELECT;
        } else if (hashCode == FnvHash.Constants.INSERT) {
            return SQLType.INSERT;
        } else if (hashCode == FnvHash.Constants.DELETE) {
            return SQLType.DELETE;
        } else if (hashCode == FnvHash.Constants.UPDATE) {
            return SQLType.UPDATE;
        } else if (hashCode == FnvHash.Constants.REPLACE) {
            return SQLType.REPLACE;
        } else if (hashCode == FnvHash.Constants.TRUNCATE) {
            return SQLType.TRUNCATE;
        } else if (hashCode == FnvHash.Constants.MERGE) {
            return SQLType.MERGE;
        } else if (hashCode == FnvHash.Constants.CREATE) {
            return SQLType.CREATE;
        } else if (hashCode == FnvHash.Constants.ALTER) {
            return SQLType.ALTER;
        } else if (hashCode == FnvHash.Constants.SHOW) {
            nextToken();
            if (identifierEquals(FnvHash.Constants.STATISTIC)) {
                return SQLType.SHOW_STATISTIC;
            } else if (identifierEquals(FnvHash.Constants.STATISTIC_LIST)) {
                return SQLType.SHOW_STATISTIC_LIST;
            } else if (identifierEquals(FnvHash.Constants.TABLES) || identifierEquals("TABLES；")) {
                return SQLType.SHOW_TABLES;
            } else if (identifierEquals(FnvHash.Constants.PARTITIONS)) {
                return SQLType.SHOW_PARTITIONS;
            } else if (identifierEquals(FnvHash.Constants.CATALOGS)) {
                return SQLType.SHOW_CATALOGS;
            } else if (identifierEquals(FnvHash.Constants.FUNCTIONS)) {
                return SQLType.SHOW_FUNCTIONS;
            } else if (identifierEquals(FnvHash.Constants.ROLES)) {
                return SQLType.SHOW_ROLES;
            } else if (identifierEquals(FnvHash.Constants.ROLE)) {
                return SQLType.SHOW_ROLE;
            } else if (identifierEquals(FnvHash.Constants.LABEL)) {
                return SQLType.SHOW_LABEL;
            } else if (identifierEquals(FnvHash.Constants.GRANTS)) {
                return SQLType.SHOW_GRANTS;
            } else if (identifierEquals(FnvHash.Constants.GRANT)
                    || token == GRANT
            ) {
                return SQLType.SHOW_GRANT;
            } else if (identifierEquals(FnvHash.Constants.RECYCLEBIN)) {
                return SQLType.SHOW_RECYCLEBIN;
            } else if (identifierEquals("VARIABLES")) {
                return SQLType.SHOW_VARIABLES;
            } else if (identifierEquals("HISTORY")) {
                return SQLType.SHOW_HISTORY;
            } else if (identifierEquals("PACKAGES")) {
                return SQLType.SHOW_PACKAGES;
            } else if (identifierEquals("PACKAGE")) {
                return SQLType.SHOW_PACKAGE;
            } else if (identifierEquals("CHANGELOGS")) {
                return SQLType.SHOW_CHANGELOGS;
            } else if (identifierEquals("ACL")) {
                return SQLType.SHOW_ACL;
            } else if (token == CREATE) {
                nextToken();
                if (token == TABLE) {
                    return SQLType.SHOW_CREATE_TABLE;
                }
            }
            return SQLType.SHOW;
        } else if (hashCode == FnvHash.Constants.DESC) {
            return SQLType.DESC;
        } else if (hashCode == FnvHash.Constants.DESCRIBE) {
            return SQLType.DESC;
        } else if (hashCode == FnvHash.Constants.SET) {
            nextToken();
            if (identifierEquals(FnvHash.Constants.LABEL)) {
                return SQLType.SET_LABEL;
            } else if (identifierEquals("PROJECT")) {
                return SQLType.SET_PROJECT;
            }
            return SQLType.SET;
        } else if (hashCode == FnvHash.Constants.SETPROJECT) {
            return SQLType.SET_PROJECT;
        } else if (hashCode == FnvHash.Constants.KILL) {
            return SQLType.KILL;
        } else if (hashCode == FnvHash.Constants.MSCK) {
            return SQLType.MSCK;
        } else if (hashCode == FnvHash.Constants.USE) {
            return SQLType.USE;
        } else if (hashCode == FnvHash.Constants.DROP) {
            return SQLType.DROP;
        } else if (hashCode == FnvHash.Constants.LIST) {
            nextToken();
            if (identifierEquals(FnvHash.Constants.USERS)) {
                return SQLType.LIST_USERS;
            } else if (identifierEquals(FnvHash.Constants.TABLES)) {
                return SQLType.LIST_TABLES;
            } else if (identifierEquals(FnvHash.Constants.ROLES)) {
                return SQLType.LIST_ROLES;
            } else if (identifierEquals(FnvHash.Constants.TEMPORARY)) {
                return SQLType.LIST_TEMPORARY_OUTPUT;
            } else if (identifierEquals("TENANT")) {
                nextToken();
                if (identifierEquals(FnvHash.Constants.ROLES)) {
                    return SQLType.LIST_TENANT_ROLES;
                }
            } else if (identifierEquals("TRUSTEDPROJECTS")) {
                return SQLType.LIST_TRUSTEDPROJECTS;
            } else if (identifierEquals("ACCOUNTPROVIDERS")) {
                return SQLType.LIST_ACCOUNTPROVIDERS;
            }
            return SQLType.LIST;
        } else if (hashCode == FnvHash.Constants.ROLLBACK) {
            return SQLType.ROLLBACK;
        } else if (hashCode == FnvHash.Constants.COMMIT) {
            return SQLType.COMMIT;
        } else if (hashCode == FnvHash.Constants.WHO) {
            return SQLType.WHO;
        } else if (hashCode == FnvHash.Constants.GRANT) {
            return SQLType.GRANT;
        } else if (hashCode == FnvHash.Constants.REVOKE) {
            return SQLType.REVOKE;
        } else if (hashCode == FnvHash.Constants.ANALYZE) {
            return SQLType.ANALYZE;
        } else if (hashCode == FnvHash.Constants.EXPLAIN) {
            return SQLType.EXPLAIN;
        } else if (hashCode == FnvHash.Constants.READ) {
            return SQLType.READ;
        } else if (hashCode == FnvHash.Constants.WITH) {
            return SQLType.WITH;
        } else if (hashCode == FnvHash.Constants.DUMP) {
            nextToken();
            if (identifierEquals(FnvHash.Constants.DATA)) {
                return SQLType.DUMP_DATA;
            }
        } else if (hashCode == FnvHash.Constants.ADD) {
            nextToken();
            if (token == Token.USER || identifierEquals(FnvHash.Constants.USER)) {
                return SQLType.ADD_USER;
            } else if (token == TABLE) {
                return SQLType.ADD_TABLE;
            } else if (token == FUNCTION) {
                return SQLType.ADD_FUNCTION;
            } else if (identifierEquals(FnvHash.Constants.STATISTIC)) {
                return SQLType.ADD_STATISTIC;
            } else if (identifierEquals(FnvHash.Constants.RESOURCE)) {
                return SQLType.ADD_RESOURCE;
            } else if (identifierEquals("VOLUME")) {
                return SQLType.ADD_VOLUME;
            } else if (identifierEquals("ACCOUNTPROVIDER")) {
                return SQLType.ADD_ACCOUNTPROVIDER;
            } else if (identifierEquals("TRUSTEDPROJECT")) {
                return SQLType.ADD_TRUSTEDPROJECT;
            } else {
                return SQLType.ADD;
            }
        } else if (hashCode == FnvHash.Constants.REMOVE) {
            nextToken();
            if (token == Token.USER || identifierEquals(FnvHash.Constants.USER)) {
                return SQLType.REMOVE_USER;
            }

            if (identifierEquals(FnvHash.Constants.RESOURCE)) {
                return SQLType.REMOVE_RESOURCE;
            }
            return SQLType.REMOVE;
        } else if (hashCode == FnvHash.Constants.TUNNEL) {
            nextToken();
            if (identifierEquals(FnvHash.Constants.DOWNLOAD)) {
                return SQLType.TUNNEL_DOWNLOAD;
            }
        } else if (hashCode == FnvHash.Constants.UPLOAD) {
            return SQLType.UPLOAD;
        } else if (hashCode == FnvHash.Constants.WHOAMI) {
            return SQLType.WHOAMI;
        } else if (hashCode == FnvHash.Constants.COUNT) {
            return SQLType.COUNT;
        } else if (hashCode == FnvHash.Constants.CLONE) {
            return SQLType.CLONE;
        } else if (hashCode == FnvHash.Constants.LOAD) {
            return SQLType.LOAD;
        } else if (hashCode == FnvHash.Constants.INSTALL) {
            return SQLType.INSTALL;
        } else if (hashCode == FnvHash.Constants.UNLOAD) {
            return SQLType.UNLOAD;
        } else if (hashCode == FnvHash.Constants.ALLOW) {
            return SQLType.ALLOW;
        } else if (hashCode == FnvHash.Constants.PURGE) {
            return SQLType.PURGE;
        } else if (hashCode == FnvHash.Constants.RESTORE) {
            return SQLType.RESTORE;
        } else if (hashCode == FnvHash.Constants.EXSTORE) {
            return SQLType.EXSTORE;
        } else if (hashCode == FnvHash.Constants.UNDO) {
            return SQLType.UNDO;
        } else if (hashCode == FnvHash.Constants.REMOVE) {
            return SQLType.REMOVE;
        } else if (hashCode == FnvHash.Constants.FROM && dialectFeatureEnabled(ScanSQLTypeWithFrom)) {
            return SQLType.INSERT_MULTI;
        } else if (hashCode == FnvHash.Constants.ADD) {
            return SQLType.ADD;
        } else if (hashCode == FnvHash.Constants.IF) {
            return SQLType.SCRIPT;
        } else if (hashCode == FnvHash.Constants.FUNCTION && dialectFeatureEnabled(ScanSQLTypeWithFunction)) {
            return SQLType.SCRIPT;
        } else if (hashCode == FnvHash.Constants.BEGIN && dialectFeatureEnabled(ScanSQLTypeWithBegin)) {
            return SQLType.SCRIPT;
        } else if (ch == '@') {
            nextToken();
            if (token == VARIANT && dialectFeatureEnabled(ScanSQLTypeWithAt)) {
                nextToken();

                if (token == TABLE) {
                    return SQLType.SCRIPT;
                }

                // datatype
                if (token == IDENTIFIER) {
                    nextToken();
                }

                if (token == COLONEQ || token == SEMI) {
                    return SQLType.SCRIPT;
                }
            }
        }

        if (ch == EOI) {
            return SQLType.EMPTY;
        }
        return SQLType.UNKNOWN;
    }

    public final SQLType scanSQLTypeV2() {
        SQLType sqlType = scanSQLType();
        if (sqlType == SQLType.CREATE) {
            nextToken();
            if (token == Token.USER || identifierEquals(FnvHash.Constants.USER)) {
                return SQLType.CREATE_USER;
            }

            _for:
            for (int i = 0; i < 1000; ++i) {
                switch (token) {
                    case EOF:
                    case ERROR:
                        break _for;
                    case TABLE:
                        sqlType = SQLType.CREATE_TABLE;
                        break;
                    case VIEW:
                        if (sqlType == SQLType.CREATE) {
                            sqlType = SQLType.CREATE_VIEW;
                            break _for;
                        }
                        break;
                    case FUNCTION:
                        if (sqlType == SQLType.CREATE) {
                            sqlType = SQLType.CREATE_FUNCTION;
                            break _for;
                        }
                        break;
                    case SELECT:
                        if (sqlType == SQLType.CREATE_TABLE) {
                            sqlType = SQLType.CREATE_TABLE_AS_SELECT;
                            break _for;
                        }
                        break;
                    default:
                        if (sqlType == SQLType.CREATE && identifierEquals(FnvHash.Constants.ROLE)) {
                            sqlType = SQLType.CREATE_ROLE;
                            break _for;
                        }
                        if (sqlType == SQLType.CREATE && identifierEquals(FnvHash.Constants.PACKAGE)) {
                            sqlType = SQLType.CREATE_PACKAGE;
                            break _for;
                        }
                        break;
                }

                nextToken();
            }
        } else if (sqlType == SQLType.DROP) {
            nextToken();
            if (token == Token.USER || identifierEquals(FnvHash.Constants.USER)) {
                return SQLType.DROP_USER;
            } else if (token == TABLE) {
                return SQLType.DROP_TABLE;
            } else if (token == VIEW) {
                return SQLType.DROP_VIEW;
            } else if (token == FUNCTION) {
                return SQLType.DROP_FUNCTION;
            } else if (identifierEquals(FnvHash.Constants.ROLE)) {
                return SQLType.DROP_ROLE;
            } else if (identifierEquals(FnvHash.Constants.RESOURCE)) {
                return SQLType.DROP_RESOURCE;
            } else if (identifierEquals(FnvHash.Constants.MATERIALIZED)) {
                nextToken();
                if (token == VIEW) {
                    return SQLType.DROP_MATERIALIZED_VIEW;
                }
            }
        } else if (sqlType == SQLType.ALTER) {
            nextToken();
            if (token == Token.USER || identifierEquals(FnvHash.Constants.USER)) {
                return SQLType.ALTER_USER;
            } else if (token == TABLE) {
                return SQLType.ALTER_TABLE;
            } else if (token == VIEW) {
                return SQLType.ALTER_VIEW;
            }
        } else if (sqlType == SQLType.INSERT) {
            nextToken();
            boolean overwrite = token == OVERWRITE;
            for (int i = 0; i < 1000; ++i) {
                nextToken();

                if (token == SELECT) {
                    return overwrite
                            ? SQLType.INSERT_OVERWRITE_SELECT
                            : SQLType.INSERT_INTO_SELECT;
                } else if (token == VALUES) {
                    return overwrite ? SQLType.INSERT_OVERWRITE_VALUES : SQLType.INSERT_INTO_VALUES;
                } else if (token == ERROR || token == EOF) {
                    if (overwrite) {
                        sqlType = SQLType.INSERT_OVERWRITE;
                    }
                    break;
                }
            }
            return sqlType;
        }

        return sqlType;
    }

    public final void nextTokenAlias() {
        startPos = pos;
        bufPos = 0;
        while (isWhitespace(ch)) {
            if (ch == '\n') {
                line++;
            }

            ch = charAt(++pos);
            startPos = pos;
            continue;
        }

        if (ch == '"') {
            scanAlias();
        } else if (ch == '\'') {
            scanAlias();

            int p;
            if (stringVal.length() > 1
                    && stringVal.indexOf('"') == -1
                    && ((p = stringVal.indexOf('\'', 1)) == -1 || p == stringVal.length() - 1)
            ) {
                char[] chars = stringVal.toCharArray();
                chars[0] = '"';
                chars[chars.length - 1] = '"';
                setStringVal(new String(chars));
            }
            setToken(LITERAL_ALIAS);
        } else {
            nextToken();
        }
    }

    public final void nextPath() {
        while (isWhitespace(ch)) {
            ch = charAt(++pos);
        }
        setStringVal(null);
        mark = pos;
        while (!isWhitespace(ch) && ch != ';' && pos < text.length()) {
            ch = charAt(++pos);
        }
        bufPos = pos - mark;

        if (ch != ';') {
            ch = charAt(++pos);
        }

        setToken(LITERAL_PATH);
    }

    public final void nextTokenForSet() {
        while (isWhitespace(ch)) {
            ch = charAt(++pos);
        }

        char first = ch;
        if (first == '$') {
            scanVariable();
            return;
        }

        if (isFirstIdentifierChar(first) || (first >= '0' && first <= '9') || first == '{') {
            setStringVal(null);
            mark = pos;
            while (ch != ';' && ch != EOI) {
                ch = charAt(++pos);
            }

            bufPos = pos - mark;

            setStringVal(this.subString(mark, bufPos));
            setToken(IDENTIFIER);
            return;
        }

        nextToken();
    }

    public final boolean skipToNextLine(int startPosition) {
        for (int i = 0; ; ++i) {
            int pos = startPosition + i;
            char ch = charAt(pos);
            if (ch == '\n') {
                this.pos = pos;
                this.ch = charAt(this.pos);
                return true;
            }

            if (ch == EOI) {
                this.pos = pos;
                break;
            }
        }

        return false;
    }

    public final boolean skipToNextLineOrParameter(int startPosition) {
        for (int i = 0; ; ++i) {
            int pos = startPosition + i;
            char ch = charAt(pos);
            if (ch == '\n') {
                this.pos = pos;
                this.ch = charAt(this.pos);
                return true;
            }
            if (ch == '$' && charAt(pos + 1) == '{') {
                this.pos = pos;
                this.ch = charAt(this.pos);
                return true;
            }

            if (ch == EOI) {
                this.pos = pos;
                break;
            }
        }

        return false;
    }

    protected void nextTokenQues() {
        setToken(Token.QUES);
    }

    public final void nextToken() {
        startPos = pos;
        bufPos = 0;
        if (comments != null && comments.size() > 0) {
            comments = null;
        }

        this.lines = 0;
        int startLine = line;

        for (; ; ) {
            if (isWhitespace(ch)) {
                if (ch == '\n') {
                    line++;

                    lines = line - startLine;
                }

                ch = charAt(++pos);
                startPos = pos;
                continue;
            }

            if (ch == EOI && pos < text.length()) {
                ch = charAt(++pos);
                continue;
            }

            if (ch == '$') {
                scanVariable();
                return;
            }

            if (isFirstIdentifierChar(ch)) {
                if (ch == '（') {
                    scanChar();
                    setToken(LPAREN);
                    return;
                } else if (ch == '）') {
                    scanChar();
                    setToken(RPAREN);
                    return;
                }

                if (ch == 'N' || ch == 'n') {
                    if (charAt(pos + 1) == '\'') {
                        ++pos;
                        ch = '\'';
                        scanString();
                        setToken(Token.LITERAL_NCHARS);
                        return;
                    }
                }

                if (ch == '—' && charAt(pos + 1) == '—' && charAt(pos + 2) == '\n') {
                    pos += 3;
                    ch = charAt(pos);
                    continue;
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
                    setToken(COMMA);
                    return;
                case '(':
                case '（':
                    scanChar();
                    setToken(LPAREN);
                    return;
                case ')':
                case '）':
                    scanChar();
                    setToken(RPAREN);
                    return;
                case '[':
                    scanLBracket();
                    return;
                case ']':
                    scanChar();
                    setToken(RBRACKET);
                    return;
                case '{':
                    scanChar();
                    setToken(LBRACE);
                    return;
                case '}':
                    scanChar();
                    setToken(RBRACE);
                    return;
                case ':':
                    scanChar();
                    if (ch == '=') {
                        scanChar();
                        setToken(COLONEQ);
                    } else if (ch == ':') {
                        scanChar();
                        setToken(COLONCOLON);
                    } else {
                        if (isEnabled(SQLParserFeature.TDDLHint) || dialectFeatureEnabled(NextTokenColon)) {
                            setToken(COLON);
                            return;
                        }
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
                    if (isDigit(ch)
                            && (pos == 1 || token != IDENTIFIER)
                    ) {
                        unscan();
                        scanNumber();
                        return;
                    } else if (ch == '.') {
                        scanChar();
                        if (ch == '.') {
                            scanChar();
                            setToken(Token.DOTDOTDOT);
                        } else {
                            setToken(Token.DOTDOT);
                        }
                    } else {
                        setToken(Token.DOT);
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
                    setToken(Token.STAR);
                    return;
                case '?':
                    scanChar();
                    nextTokenQues();
                    return;
                case ';':
                    scanChar();
                    setToken(Token.SEMI);
                    return;
                case '`':
                    throw new ParserException("TODO. " + info()); // TODO
                case '@':
                    scanVariable_at();
                    return;
                case '-':
                    char next = charAt(pos + 1);
                    if (next == '-') {
                        scanComment();
                        if ((token == Token.LINE_COMMENT || token == Token.MULTI_LINE_COMMENT) && skipComment) {
                            bufPos = 0;
                            continue;
                        }
                    } else if (next >= '0' && next <= '9') {
                        if (token == null) {
                            scanNumber();
                            return;
                        }
                        switch (token) {
                            case COMMA:
                            case LPAREN:
                            case WITH:
                            case BY:
                                scanNumber();
                                break;
                            default:
                                scanOperator();
                                break;
                        }
                    } else {
                        scanOperator();
                    }
                    return;
                case '/':
                    char nextChar = charAt(pos + 1);
                    if (nextChar == '/'
                            || nextChar == '*'
                            || (nextChar == '!' && isEnabled(SQLParserFeature.TDDLHint))) {
                        scanComment();
                        if ((token == Token.LINE_COMMENT || token == Token.MULTI_LINE_COMMENT) && skipComment) {
                            bufPos = 0;
                            continue;
                        }
                    } else if (nextChar == ' ' && charAt(pos + 2) == '*') {
                        scanComment();
                        if ((token == Token.LINE_COMMENT || token == Token.MULTI_LINE_COMMENT) && skipComment) {
                            bufPos = 0;
                            continue;
                        }
                    } else if (nextChar == ' ' && charAt(pos + 2) == ' ' && charAt(pos + 3) == '*') {
                        scanComment();
                        if ((token == Token.LINE_COMMENT || token == Token.MULTI_LINE_COMMENT) && skipComment) {
                            bufPos = 0;
                            continue;
                        }
                    } else {
                        setToken(Token.SLASH);
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
                            && dialectFeatureEnabled(NextTokenPrefixN)) {
                        scanChar();
                        scanChar();
                        setToken(Token.NULL);
                        return;
                    }

                    // QS_TODO ?
                    if (isEOF()) { // JLS
                        setToken(EOF);
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
        setToken(LBRACKET);
    }

    private void scanOperator() {
        switch (ch) {
            case '+':
                scanChar();
                setToken(Token.PLUS);
                break;
            case '-':
                scanChar();
                if (ch == '>') {
                    scanChar();
                    if (ch == '>') {
                        scanChar();
                        setToken(Token.SUBGTGT);
                    } else {
                        setToken(Token.SUBGT);
                    }
                } else {
                    setToken(Token.SUB);
                }
                break;
            case '*':
                scanChar();
                setToken(Token.STAR);
                break;
            case '/':
                scanChar();
                setToken(Token.SLASH);
                break;
            case '&':
                scanChar();
                if (ch == '&') {
                    scanChar();
                    setToken(Token.AMPAMP);
                } else {
                    setToken(Token.AMP);
                }
                break;
            case '|':
                scanChar();
                if (ch == '|') {
                    scanChar();
                    if (ch == '/') {
                        if (charAt(pos + 1) == '*') {
                            setToken(Token.BARBAR);
                            break;
                        }
                        scanChar();
                        setToken(Token.BARBARSLASH);
                    } else {
                        setToken(Token.BARBAR);
                    }
                } else if (ch == '/') {
                    scanChar();
                    setToken(Token.BARSLASH);
                } else {
                    setToken(Token.BAR);
                }
                break;
            case '^':
                scanChar();
                if (ch == '=') {
                    scanChar();
                    setToken(Token.CARETEQ);
                } else {
                    setToken(Token.CARET);
                }
                break;
            case '%':
                scanChar();
                setToken(Token.PERCENT);
                break;
            case '=':
                scanChar();
                if (ch == ' ') {
                    scanChar();
                }

                if (ch == '=') {
                    scanChar();
                    setToken(Token.EQEQ);
                } else if (ch == '>') {
                    scanChar();
                    setToken(Token.EQGT);
                } else {
                    setToken(Token.EQ);
                }
                break;
            case '>':
                scanChar();
                if (ch == '=') {
                    scanChar();
                    setToken(Token.GTEQ);
                } else if (ch == '>') {
                    scanChar();
                    if (ch == '>') {
                        scanChar();
                        setToken(Token.GTGTGT);
                    } else {
                        setToken(Token.GTGT);
                    }
                } else {
                    setToken(Token.GT);
                }
                break;
            case '<':
                scanChar();
                if (ch == '=') {
                    scanChar();
                    if (ch == '>') {
                        setToken(Token.LTEQGT);
                        scanChar();
                    } else {
                        setToken(Token.LTEQ);
                    }
                } else if (ch == '>') {
                    scanChar();
                    setToken(Token.LTGT);
                } else if (ch == '<') {
                    scanChar();

                    if (ch == '<') {
                        scanChar();
                        setToken(Token.LTLTLT);
                    } else {
                        setToken(Token.LTLT);
                    }
                } else if (ch == '@') {
                    scanChar();
                    setToken(Token.LT_MONKEYS_AT);
                } else if (ch == '-' && charAt(pos + 1) == '>') {
                    scanChar();
                    scanChar();
                    setToken(Token.LT_SUB_GT);
                } else {
                    if (ch == ' ') {
                        char c1 = charAt(pos + 1);
                        if (c1 == '=') {
                            scanChar();
                            scanChar();
                            if (ch == '>') {
                                setToken(Token.LTEQGT);
                                scanChar();
                            } else {
                                setToken(Token.LTEQ);
                            }
                        } else if (c1 == '>') {
                            scanChar();
                            scanChar();
                            setToken(Token.LTGT);
                        } else if (c1 == '<') {
                            scanChar();
                            scanChar();
                            setToken(Token.LTLT);
                        } else if (c1 == '@') {
                            scanChar();
                            scanChar();
                            setToken(Token.LT_MONKEYS_AT);
                        } else if (c1 == '-' && charAt(pos + 2) == '>') {
                            scanChar();
                            scanChar();
                            scanChar();
                            setToken(Token.LT_SUB_GT);
                        } else {
                            setToken(Token.LT);
                        }
                    } else {
                        setToken(Token.LT);
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
                    setToken(Token.BANGEQ);
                } else if (ch == '>') {
                    scanChar();
                    setToken(Token.BANGGT);
                } else if (ch == '<') {
                    scanChar();
                    setToken(Token.BANGLT);
                } else if (ch == '!') {
                    scanChar();
                    setToken(Token.BANGBANG); // postsql
                } else if (ch == '~') {
                    scanChar();
                    if (ch == '*') {
                        scanChar();
                        setToken(Token.BANG_TILDE_STAR); // postsql
                    } else {
                        setToken(Token.BANG_TILDE); // postsql
                    }
                } else {
                    setToken(Token.BANG);
                }
                break;
            case '?':
                scanChar();
                setToken(Token.QUES);
                break;
            case '~':
                scanChar();
                if (ch == '*') {
                    scanChar();
                    setToken(Token.TILDE_STAR);
                } else if (ch == '=') {
                    scanChar();
                    setToken(Token.TILDE_EQ); // postsql
                } else {
                    setToken(Token.TILDE);
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

        for (; ; ) {
            if (isEOF()) {
                lexError("unclosed.str.lit");
                return;
            }

            ch = charAt(++pos);

            if (ch == '\'') {
                scanChar();
                if (ch != '\'') {
                    setToken(LITERAL_CHARS);
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
                setStringVal(subString(mark, bufPos + 2));
            } else {
                setStringVal(subString(mark + 1, bufPos));
            }
        } else {
            setStringVal(new String(buf, 0, bufPos));
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
                setStringVal(stringVal);
                int pos = endIndex + 1;
                char ch = charAt(pos);
                if (ch != '\'') {
                    this.pos = pos;
                    this.ch = ch;
                    setToken(LITERAL_CHARS);
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
                        if (dialectFeatureEnabled(ScanString2PutDoubleBackslash)) {
                            putChar('\\');
                        }
                        putChar('%');
                        break;
                    case '_':
                        if (dialectFeatureEnabled(ScanString2PutDoubleBackslash)) {
                            putChar('\\');
                        }
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
                    setToken(LITERAL_CHARS);
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
            setStringVal(subString(mark + 1, bufPos));
        } else {
            setStringVal(new String(buf, 0, bufPos));
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
                    int i1 = i + 1;
                    if (i1 < text.length()) {
                        hasSpecial = true;
                        ++i;
                        continue;
                    }
                }

                if (ch == '"') {
                    int i1 = i + 1;
                    if (i1 < text.length() && text.charAt(i1) == '"') {
                        hasSpecial = true;
                        ++i;
                        continue;
                    }
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
                if (charAt(endIndex + 1) == '.') {
                    stringVal = subString(startIndex - 1, (endIndex - startIndex) + 2);
                } else {
                    stringVal = subString(startIndex, endIndex - startIndex);
                }
            }
            // hasSpecial = stringVal.indexOf('\\') != -1;

            if (!hasSpecial) {
                setStringVal(stringVal);
                int pos = endIndex + 1;
                char ch = charAt(pos);
                if (ch == '.') {
                    this.pos = pos;
                    this.ch = ch;
                    setToken(Token.IDENTIFIER);
                    return;
                }

                if (ch != '\'') {
                    this.pos = pos;
                    this.ch = ch;
                    setToken(LITERAL_CHARS);
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
                        if (dialectFeatureEnabled(ScanString2PutDoubleBackslash)) {
                            putChar('\\');
                        }
                        putChar('%');
                        break;
                    case '_':
                        if (dialectFeatureEnabled(ScanString2PutDoubleBackslash)) {
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
                    if (buf != null && bufPos > 0) {
                        setStringVal(new String(buf, 0, bufPos));
                    }
                    setToken(LITERAL_CHARS);
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
            setStringVal(subString(mark + 1, bufPos));
        } else {
            setStringVal(new String(buf, 0, bufPos));
        }
    }

    protected void scanAlias() {
        final char quote = ch;
        {
            boolean hasSpecial = false;
            int startIndex = pos + 1;
            int endIndex = -1; // text.indexOf('\'', startIndex);
            for (int i = startIndex; i < text.length(); ++i) {
                final char ch = text.charAt(i);
                if (ch == '\\') {
                    hasSpecial = true;
                    i++;
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
                setStringVal(stringVal);
                int pos = endIndex + 1;
                char ch = charAt(pos);
                if (ch != '\'') {
                    this.pos = pos;
                    this.ch = ch;
                    setToken(LITERAL_ALIAS);
                    return;
                }
            }
        }

        mark = pos;
        initBuff(bufPos);
        //putChar(ch);

        putChar(ch);
        for (; ; ) {
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
                    case 'u':
                        if (dialectFeatureEnabled(ScanAliasU)) {
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
                setToken(LITERAL_ALIAS);
                break;
            }

            if (bufPos == buf.length) {
                putChar(ch);
            } else {
                buf[bufPos++] = ch;
            }
        }

        setStringVal(new String(buf, 0, bufPos));
    }

    public void scanSharp() {
        scanVariable();
    }

    public void scanVariable() {
        if (ch != ':' && ch != '#' && ch != '$' && !(ch == '@' && dialectFeatureEnabled(ScanVariableAt))) {
            throw new ParserException("illegal variable. " + info());
        }
        mark = pos;
        bufPos = 1;
        char ch;

        final char c1 = charAt(pos + 1);
        if (c1 == '>' && dialectFeatureEnabled(ScanVariableGreaterThan)) {
            pos += 2;
            setToken(Token.MONKEYS_AT_GT);
            this.ch = charAt(++pos);
            return;
        } else if (c1 == '{') {
            pos++;
            bufPos++;

            boolean ident = false;
            for (; ; ) {
                ch = charAt(++pos);
                if (isEOF() || ch == ';' || ch == '；' || ch == '\r' || ch == '\n') {
                    pos--;
                    bufPos--;
                    break;
                }

                if (ch == '}' && !ident) {
                    if (isIdentifierChar(charAt(pos + 1))) {
                        bufPos++;
                        ident = true;
                        continue;
                    }
                    break;
                }

                if (ident && ch == '$') {
                    if (charAt(pos + 1) == '{') {
                        bufPos++;
                        ident = false;
                        continue;
                    }
                }

                if (ident) {
                    if (isWhitespace(ch)) {
                        pos--;
                        break;
                    } else if (ch == ',' || ch == ')' || ch == '(') {
                        pos--;
                        break;
                    }
                }

                bufPos++;
            }

            if (ch != '}' && !ident) {
                throw new ParserException("syntax error. " + info());
            }
            ++pos;
            bufPos++;

            char endChar = ch;
            this.ch = charAt(pos);

            if (dialectFeatureEnabled(ScanVariableMoveToSemi) && !isWhitespace(endChar)) {
                while (isIdentifierChar(this.ch) && ch != ';' && ch != '；') {
                    ++pos;
                    bufPos++;
                    this.ch = charAt(pos);
                }
            }

            setStringVal(addSymbol());
            setToken(ident ? IDENTIFIER : Token.VARIANT);
            return;
        } else if (c1 == '$' && charAt(pos + 2) == '{') {
            pos += 2;
            bufPos += 2;

            for (; ; ) {
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

            if (dialectFeatureEnabled(ScanVariableSkipIdentifiers)) {
                while (isIdentifierChar(this.ch)) {
                    ++pos;
                    bufPos++;
                    this.ch = charAt(pos);
                }
            }

            setStringVal(addSymbol());
            setToken(Token.VARIANT);
            return;
        }

        for (; ; ) {
            ch = charAt(++pos);

            if (!isIdentifierChar(ch)) {
                break;
            }

            bufPos++;
            continue;
        }

        this.ch = charAt(pos);

        setStringVal(addSymbol());

        setToken(Token.VARIANT);
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

        for (; ; ) {
            ch = charAt(++pos);

            if (!isIdentifierChar(ch)) {
                break;
            }

            bufPos++;
            continue;
        }

        this.ch = charAt(pos);

        setStringVal(addSymbol());
        setToken(Token.VARIANT);
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

    public List<String> scanLineArgument() {
        List<String> args = new ArrayList<>();
        while (ch == ' ') {
            scanChar();
        }

        int start = pos;
        for (; ; ) {
            if (ch == ' '
                    || ch == '\r'
                    || ch == '\n'
                    || ch == EOI
                    || (ch == ';' && (pos >= text.length() - 1 || isWhitespace(text.charAt(pos + 1))))
            ) {
                while (pos < text.length() - 1) {
                    char c1 = text.charAt(pos);
                    if (c1 == ' ' || c1 == '\r' || c1 == '\n' || c1 == '\t') {
                        pos++;
                        continue;
                    }
                    break;
                }

                String arg = text.substring(start, pos);
                arg = arg.trim();
                if (arg.length() > 0) {
                    args.add(arg);
                }
                if (ch == ';') {
                    break;
                }
                scanChar();
                start = pos - 1;
                if (ch == '\r' || ch == '\n' || ch == EOI) {
                    break;
                } else {
                    continue;
                }
            }

            scanChar();
        }
        if (ch == EOI) {
            setToken(EOF);
        } else if (ch == ';') {
            setToken(COMMA);
        } else {
            nextToken();
        }
        return args;
    }

    protected void scanMultiLineComment() {
        Token lastToken = this.token;
        int depth = 1;

        scanChar();
        scanChar();
        mark = pos;
        bufPos = 0;

        for (; ; ) {
            if (ch == '/' && charAt(pos + 1) == '*') {
                scanChar();
                scanChar();
                ++depth;
            }

            if (ch == '*' && charAt(pos + 1) == '/') {
                int curPos = pos;
                boolean terminated = true;
                // If '*/' has leading '--' in the same line, just skip it. For example '-- xxxx */'.
                while (curPos > 0 && charAt(curPos) != '\n') {
                    if (charAt(curPos) == '-' && (curPos + 1) < text.length() && charAt(curPos + 1) == '-') {
                        terminated = false;
                    }
                    curPos--;
                }
                if (terminated) {
                    scanChar();
                    scanChar();
                    if (0 == --depth) {
                        break;
                    }
                }
            }

            // multiline comment结束符错误
            if (ch == EOI) {
                throw new ParserException("unterminated /* comment. " + info());
            }
            scanChar();
            bufPos++;
        }

        setStringVal(subString(mark - 2, bufPos + 4));
        setToken(Token.MULTI_LINE_COMMENT);
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

    protected void scanSingleLineComment() {
        Token lastToken = this.token;

        mark = pos;
        bufPos = 2;

        scanChar();
        scanChar();

        for (; ; ) {
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

            if (ch == EOI) {
                break;
            }

            scanChar();
            bufPos++;
        }

        setStringVal(subString(mark, bufPos));
        setToken(Token.LINE_COMMENT);
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
        this.hashLCase = 0;
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

            hashLCase = 0xcbf29ce484222325L;
            hash = 0xcbf29ce484222325L;

            for (int i = startPos; i < quoteIndex; ++i) {
                ch = text.charAt(i);

                hashLCase ^= ((ch >= 'A' && ch <= 'Z') ? (ch + 32) : ch);
                hashLCase *= 0x100000001b3L;

                hash ^= ch;
                hash *= 0x100000001b3L;
            }

            setStringVal(MySqlLexer.quoteTable.addSymbol(text, pos, quoteIndex + 1 - pos, hash));
            //stringVal = text.substring(mark, pos);
            pos = quoteIndex + 1;
            this.ch = charAt(pos);
            setToken(Token.IDENTIFIER);
            return;
        }

        final boolean firstFlag = isFirstIdentifierChar(first);
        if (!firstFlag) {
            throw new ParserException("illegal identifier. " + info());
        }

        hashLCase = 0xcbf29ce484222325L;
        hash = 0xcbf29ce484222325L;

        hashLCase ^= ((ch >= 'A' && ch <= 'Z') ? (ch + 32) : ch);
        hashLCase *= 0x100000001b3L;

        hash ^= ch;
        hash *= 0x100000001b3L;

        mark = pos;
        bufPos = 1;
        char ch = 0;
        boolean hasLeftBrace = false;
        for (; ; ) {
            char c0 = ch;
            ch = charAt(++pos);

            if (!isIdentifierChar(ch)) {
                if (((ch == '（' || ch == '）') && c0 > 256) || ch == '$') {
                    bufPos++;
                    continue;
                } else if (ch == '{' && c0 == '$') {
                    hasLeftBrace = true;
                    bufPos++;
                    continue;
                } else if (ch == '}' && hasLeftBrace) {
                    hasLeftBrace = false;
                    bufPos++;
                    continue;
                } else if (ch == '-'
                        && isIdentifierChar(charAt(pos + 1))
                        && dialectFeatureEnabled(ScanSubAsIdentifier)) {
                    hash ^= ch;
                    hash *= 0x100000001b3L;

                    hashLCase ^= ch;
                    hashLCase *= 0x100000001b3L;

                    bufPos++;
                    continue;
                }
                break;
            }

            hashLCase ^= ((ch >= 'A' && ch <= 'Z') ? (ch + 32) : ch);
            hashLCase *= 0x100000001b3L;

            hash ^= ch;
            hash *= 0x100000001b3L;

            bufPos++;
            continue;
        }

        this.ch = charAt(pos);

        if (bufPos == 1) {
            switch (first) {
                case '（':
                    setToken(Token.LPAREN);
                    return;
                case '）':
                    setToken(Token.RPAREN);
                    return;
                default:
                    break;
            }
            setToken(Token.IDENTIFIER);
            setStringVal(CharTypes.valueOf(first));
            if (stringVal == null) {
                setStringVal(Character.toString(first));
            }
            return;
        }

        Token tok = keywords.getKeyword(hashLCase);
        if (tok != null) {
            setToken(tok);
            if (token == Token.IDENTIFIER) {
                setStringVal(SymbolTable.global.addSymbol(text, mark, bufPos, hash));
            } else {
                setStringVal(null);
            }
        } else {
            setToken(Token.IDENTIFIER);
            setStringVal(SymbolTable.global.addSymbol(text, mark, bufPos, hash));
        }
    }

    protected boolean isFirstIdentifierChar0(char ch) {
        return CharTypes.letterOrUnderScore(ch);
    }

    protected void scanIdentifier0() {
        this.hashLCase = 0;
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

            hashLCase = 0xcbf29ce484222325L;
            hash = 0xcbf29ce484222325L;

            for (int i = startPos; i < quoteIndex; ++i) {
                ch = text.charAt(i);

                hashLCase ^= ((ch >= 'A' && ch <= 'Z') ? (ch + 32) : ch);
                hashLCase *= 0x100000001b3L;

                hash ^= ch;
                hash *= 0x100000001b3L;
            }

            setStringVal(MySqlLexer.quoteTable.addSymbol(text, pos, quoteIndex + 1 - pos, hash));
            //stringVal = text.substring(mark, pos);
            pos = quoteIndex + 1;
            this.ch = charAt(pos);
            setToken(Token.IDENTIFIER);
            return;
        }

        if (!isFirstIdentifierChar0(first)) {
            throw new ParserException("illegal identifier. " + info());
        }

        hashLCase = 0xcbf29ce484222325L;
        hash = 0xcbf29ce484222325L;

        hashLCase ^= ((ch >= 'A' && ch <= 'Z') ? (ch + 32) : ch);
        hashLCase *= 0x100000001b3L;

        hash ^= ch;
        hash *= 0x100000001b3L;

        mark = pos;
        bufPos = 1;
        char ch = 0;
        boolean hasLeftBrace = false;
        for (; ; ) {
            char c0 = ch;
            ch = charAt(++pos);

            if (!isIdentifierChar(ch)) {
                if (((ch == '（' || ch == '）') && c0 > 256) || ch == '$') {
                    bufPos++;
                    continue;
                } else if (ch == '{' && c0 == '$') {
                    hasLeftBrace = true;
                    bufPos++;
                    continue;
                } else if (ch == '}' && hasLeftBrace) {
                    hasLeftBrace = false;
                    bufPos++;
                    continue;
                } else if (ch == '-'
                        && isIdentifierChar(charAt(pos + 1))
                        && dialectFeatureEnabled(ScanSubAsIdentifier)) {
                    hash ^= ch;
                    hash *= 0x100000001b3L;

                    hashLCase ^= ch;
                    hashLCase *= 0x100000001b3L;

                    bufPos++;
                    continue;
                }
                break;
            }

            hashLCase ^= ((ch >= 'A' && ch <= 'Z') ? (ch + 32) : ch);
            hashLCase *= 0x100000001b3L;

            hash ^= ch;
            hash *= 0x100000001b3L;

            bufPos++;
            continue;
        }

        this.ch = charAt(pos);

        if (bufPos == 1) {
            switch (first) {
                case '（':
                    setToken(Token.LPAREN);
                    return;
                case '）':
                    setToken(Token.RPAREN);
                    return;
                default:
                    break;
            }
            setToken(Token.IDENTIFIER);
            setStringVal(CharTypes.valueOf(first));
            if (stringVal == null) {
                setStringVal(Character.toString(first));
            }
            return;
        }

        Token tok = keywords.getKeyword(hashLCase);
        if (tok != null) {
            setToken(tok);
            if (token == Token.IDENTIFIER) {
                setStringVal(SymbolTable.global.addSymbol(text, mark, bufPos, hash));
            } else {
                setStringVal(null);
            }
        } else {
            setToken(Token.IDENTIFIER);
            setStringVal(SymbolTable.global.addSymbol(text, mark, bufPos, hash));
        }
    }

    public void scanNumber() {
        mark = pos;
        numberSale = 0;
        numberExp = false;
        bufPos = 0;

        if (ch == '0' && charAt(pos + 1) == 'b' && dialectFeatureEnabled(ScanNumberPrefixB)) {
            int i = 2;
            int mark = pos + 2;
            for (; ; ++i) {
                char ch = charAt(pos + i);
                if (ch == '0' || ch == '1') {
                    continue;
                } else if (ch >= '2' && ch <= '9') {
                    break;
                } else {
                    bufPos += i;
                    pos += i;
                    setStringVal(subString(mark, i - 2));
                    this.ch = charAt(pos);
                    setToken(Token.BITS);
                    return;
                }
            }
        }

        if (ch == '-') {
            bufPos++;
            ch = charAt(++pos);
        }

        for (; ; ) {
            if (ch >= '0' && ch <= '9') {
                bufPos++;
            } else {
                break;
            }
            ch = charAt(++pos);
        }

        if (ch == '.') {
            if (charAt(pos + 1) == '.') {
                setStringVal(subString(mark, bufPos));
                setToken(Token.LITERAL_INT);
                return;
            }
            bufPos++;
            ch = charAt(++pos);

            for (numberSale = 0; ; numberSale++) {
                if (ch >= '0' && ch <= '9') {
                    bufPos++;
                } else {
                    break;
                }
                ch = charAt(++pos);
            }

            numberExp = true;
        }

        if ((ch == 'e' || ch == 'E')
                && (isDigit(charAt(pos + 1)) || (isDigit2(charAt(pos + 1)) && isDigit2(charAt(pos + 2))))) {
            numberExp = true;

            bufPos++;
            ch = charAt(++pos);

            if (ch == '+' || ch == '-') {
                bufPos++;
                ch = charAt(++pos);
            }

            for (; ; ) {
                if (ch >= '0' && ch <= '9') {
                    bufPos++;
                } else {
                    break;
                }
                ch = charAt(++pos);
            }

            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                numberExp = false;
            }
        } else if (ch == 'd' || ch == 'D') {
            if (!isIdentifierChar(charAt(pos + 1))) {
                ch = charAt(++pos);
                setStringVal(subString(mark, bufPos));
                setToken(Token.LITERAL_FLOAT);
                return;
            }
        }

        if (numberSale > 0 || numberExp) {
            if (text.charAt(mark) == '.' && isIdentifierChar(ch)) {
                pos = mark + 1;
                ch = charAt(pos);
                setToken(Token.DOT);
                return;
            }
            setStringVal(subString(mark, bufPos));
            setToken(Token.LITERAL_FLOAT);
            return;
        }

        if (ch != '`') {
            if (isFirstIdentifierChar(ch)
                    && ch != '）'
                    && !(ch == 'b' && bufPos == 1 && charAt(pos - 1) == '0' && dialectFeatureEnabled(
                    ScanNumberCommonProcess))
            ) {
                bufPos++;
                boolean brace = false;
                for (; ; ) {
                    char c0 = ch;
                    ch = charAt(++pos);

                    if (isEOF()) {
                        break;
                    }

                    if (!isIdentifierChar(ch)) {
                        if (ch == '{' && charAt(pos - 1) == '$' && !brace) {
                            bufPos++;
                            brace = true;
                            continue;
                        }

                        if (ch == '}' && brace) {
                            bufPos++;
                            brace = false;
                            continue;
                        }

                        if ((ch == '（' || ch == '）')
                                && c0 > 256) {
                            bufPos++;
                            continue;
                        }
                        break;
                    }

                    bufPos++;
                    continue;
                }

                setStringVal(addSymbol());
                hashLCase = FnvHash.hashCode64(stringVal);
                setToken(Token.IDENTIFIER);
                return;
            }
        }

        setStringVal(subString(mark, bufPos));
        setToken(Token.LITERAL_INT);
    }

    public void scanHexaDecimal() {
        mark = pos;
        bufPos = 0;

        if (ch == '-') {
            bufPos++;
            ch = charAt(++pos);
        }

        for (; ; ) {
            if (CharTypes.isHex(ch)) {
                bufPos++;
            } else {
                break;
            }
            ch = charAt(++pos);
        }

        if (isIdentifierChar(ch)) {
            for (; ; ) {
                bufPos++;
                ch = charAt(++pos);
                if (!isIdentifierChar(ch)) {
                    break;
                }
            }
            mark -= 2;
            bufPos += 2;
            setStringVal(addSymbol());
            hashLCase = FnvHash.hashCode64(stringVal);
            setToken(Token.IDENTIFIER);
            return;
        }

        setStringVal(subString(mark, bufPos));
        setToken(Token.LITERAL_HEX);
    }

    public String hexString() {
        return subString(mark, bufPos);
    }

    public final boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    protected static final boolean isDigit2(char ch) {
        return ch == '+' || ch == '-' || (ch >= '0' && ch <= '9');
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
            setStringVal(subString(mark, bufPos));
        }
        return stringVal;
    }

    private void stringVal(StringBuilder out) {
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
            setStringVal(subString(mark, bufPos));
        }
        return text.equalsIgnoreCase(stringVal);
    }

    public final boolean identifierEquals(long hash_lower) {
        if (token != Token.IDENTIFIER) {
            return false;
        }

        if (this.hashLCase == 0) {
            if (stringVal == null) {
                setStringVal(subString(mark, bufPos));
            }
            this.hashLCase = FnvHash.fnv1a_64_lower(stringVal);
        }
        return this.hashLCase == hash_lower;
    }

    public final long hashLCase() {
        if (this.hashLCase == 0) {
            if (stringVal == null) {
                setStringVal(subString(mark, bufPos));
            }
            this.hashLCase = FnvHash.fnv1a_64_lower(stringVal);
        }
        return hashLCase;
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

    private static final long MULTMIN_RADIX_TEN = Long.MIN_VALUE / 10;
    private static final long N_MULTMAX_RADIX_TEN = -Long.MAX_VALUE / 10;

    public final boolean isNegativeIntegerValue() {
        return charAt(mark) == '-';
    }

    // QS_TODO negative number is invisible for lexer
    public final Number integerValue() {
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
                return new BigInteger(
                        numberString());
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

    //todo fix reset reset字段会导致lexer的游标不对齐 不建议使用
    @Deprecated
    public void reset(int mark, char markChar, Token token) {
        this.pos = mark;
        this.ch = markChar;
        this.token = token;
    }

    public final String numberString() {
        return text.substring(mark, mark + bufPos);
        // return subString(mark, bufPos);
    }

    public BigDecimal decimalValue() {
        if (numberSale > 0 && !numberExp) {
            int len = bufPos;

            if (len < 20) {
                long unscaleVal = 0;

                boolean negative = false;
                int i = 0;
                char first = text.charAt(mark);
                if (first == '+') {
                    i++;
                } else if (first == '-') {
                    i++;
                    negative = true;
                }
                for (; i < len; ++i) {
                    char ch = text.charAt(mark + i);
                    if (ch == '.') {
                        continue;
                    }
                    int digit = ch - '0';
                    unscaleVal = unscaleVal * 10 + digit;
                }
                return BigDecimal.valueOf(negative ? -unscaleVal : unscaleVal, numberSale);
            }
        }

        char[] value = sub_chars(mark, bufPos);
        if (!StringUtils.isNumber(value)) {
            throw new ParserException(value + " is not a number! " + info());
        }
        return new BigDecimal(value);
    }

    public SQLNumberExpr numberExpr() {
        char[] value = sub_chars(mark, bufPos);
        if (!StringUtils.isNumber(value)) {
            throw new ParserException(value + " is not a number! " + info());
        }

        return new SQLNumberExpr(value);
    }

    public SQLNumberExpr numberExpr(SQLObject parent) {
        char[] value = sub_chars(mark, bufPos);
        if (!StringUtils.isNumber(value)) {
            throw new ParserException(value + " is not a number! " + info());
        }

        return new SQLNumberExpr(value, parent);
    }

    public SQLNumberExpr numberExpr(boolean negate) {
        char[] value = sub_chars(mark, bufPos);
        if (!StringUtils.isNumber(value)) {
            throw new ParserException(value + " is not a number! " + info());
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
        setToken(Token.EOF);
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
        comments.add(comment);
    }

    public final List<String> getComments() {
        return comments;
    }

    public int getLine() {
        return line;
    }

    public void computeRowAndColumn(SQLObject x) {
        if (!keepSourceLocation) {
            return;
        }

        int line = 1;
        int column = 1;
        for (int i = 0; i < startPos; ++i) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                column = 1;
                line++;
            } else {
                column++;
            }
        }
        x.setSource(line, column);
    }

    public void computeRowAndColumn() {
        int line = 1;
        int column = 1;
        for (int i = 0; i < startPos; ++i) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                column = 1;
                line++;
            } else {
                column++;
            }
        }

        this.posLine = line;
        this.posColumn = column;
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
        } else if (feature == KeepSourceLocation) {
            this.keepSourceLocation = state;
        } else if (feature == SkipComments) {
            this.skipComment = state;
        }
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public final boolean isEnabled(SQLParserFeature feature) {
        return SQLParserFeature.isEnabled(this.features, feature);
    }

    public static String parameterize(String sql, DbType dbType) {
        Lexer lexer = SQLParserUtils.createLexer(sql, dbType);
        lexer.optimizedForParameterized = true; // optimized

        lexer.nextToken();

        StringBuilder buf = new StringBuilder();

        for_:
        for (; ; ) {
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

    protected boolean dialectFeatureEnabled(DialectFeature.LexerFeature feature) {
        return dialectFeature.isEnabled(feature);
    }
}

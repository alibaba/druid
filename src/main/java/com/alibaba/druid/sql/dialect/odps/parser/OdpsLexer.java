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
import com.alibaba.druid.sql.parser.*;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.druid.sql.parser.CharTypes.*;
import static com.alibaba.druid.sql.parser.LayoutCharacters.EOI;

public class OdpsLexer extends Lexer {

    public final static Keywords DEFAULT_ODPS_KEYWORDS;

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
        map.put("；", Token.SEMI);

        DEFAULT_ODPS_KEYWORDS = new Keywords(map);
    }

    public OdpsLexer(String input, SQLParserFeature... features){
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
    
    public OdpsLexer(String input, boolean skipComment, boolean keepComments){
        super(input, skipComment);

        init();

        dbType = DbType.odps;
        this.skipComment = skipComment;
        this.keepComments = keepComments;
        super.keywords = DEFAULT_ODPS_KEYWORDS;
    }

    public OdpsLexer(String input, CommentHandler commentHandler){
        super(input, commentHandler);

        init();

        dbType = DbType.odps;
        super.keywords = DEFAULT_ODPS_KEYWORDS;
    }
    
    private void init() {
        if (ch == '】' || ch == ' ' || ch == '，' || ch == '：' || ch == '、' || ch == '\u200C' || ch == '；') {
            ch = charAt(++pos);
        }

        if (ch == '上' && charAt(pos + 1) == '传') {
            pos += 2;
            ch = charAt(pos);

            while(isWhitespace(ch)) {
                ch = charAt(++pos);
            }
        }
    }
    
    public void scanComment() {
       scanHiveComment();
    }

    public void scanIdentifier() {
        hash_lower = 0;
        hash = 0;

        final char first = ch;
        
        if (first == '`') {

            mark = pos;
            bufPos = 1;
            char ch;
            for (;;) {
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
        for (;;) {
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
            for (;;) {
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
            } else if (p1 > p2 && p2 != -1){
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

    protected final void scanString() {
        scanString2();
    }
}

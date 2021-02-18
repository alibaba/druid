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
package com.alibaba.druid.sql.dialect.sqlserver.parser;

import com.alibaba.druid.sql.parser.*;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.druid.sql.parser.LayoutCharacters.EOI;
import static com.alibaba.druid.sql.parser.Token.IDENTIFIER;

public class SQLServerLexer extends Lexer {

    public final static Keywords DEFAULT_SQL_SERVER_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();
        
        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());
        
        map.put("CURSOR", Token.CURSOR);
        map.put("TOP", Token.TOP);
        map.put("USE", Token.USE);
        map.put("WITH", Token.WITH);
        map.put("PERCENT", Token.PERCENT);
        map.put("IDENTITY", Token.IDENTITY);
        map.put("DECLARE", Token.DECLARE);
        map.put("IF", Token.IF);
        map.put("ELSE", Token.ELSE);
        map.put("BEGIN", Token.BEGIN);
        map.put("END", Token.END);

        map.put("MERGE", Token.MERGE);
        map.put("USING", Token.USING);
        map.put("MATCHED", Token.MATCHED);

        DEFAULT_SQL_SERVER_KEYWORDS = new Keywords(map);
    }

    public SQLServerLexer(char[] input, int inputLength, boolean skipComment){
        super(input, inputLength, skipComment);
        super.keywords = DEFAULT_SQL_SERVER_KEYWORDS;
    }

    public SQLServerLexer(String input){
        super(input);
        super.keywords = DEFAULT_SQL_SERVER_KEYWORDS;
    }

    public SQLServerLexer(String input, SQLParserFeature... features){
        super(input);
        super.keywords = DEFAULT_SQL_SERVER_KEYWORDS;
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }
    
    public void scanComment() {
        if (ch != '/' && ch != '-') {
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
                if (ch == '*' && charAt(pos + 1) == '/') {
                    bufPos += 2;
                    scanChar();
                    scanChar();
                    break;
                }

                scanChar();
                bufPos++;
            }

            if (isHint) {
                stringVal = subString(mark + startHintSp, (bufPos - startHintSp) - 1);
                token = Token.HINT;
            } else {
                stringVal = subString(mark, bufPos);
                token = Token.MULTI_LINE_COMMENT;
                commentCount++;
                if (keepComments) {
                    addComment(stringVal);
                }
            }

            if (token != Token.HINT && !isAllowComment() && !isSafeComment(stringVal)) {
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

            stringVal = subString(mark + 1, bufPos);
            token = Token.LINE_COMMENT;
            commentCount++;
            if (keepComments) {
                addComment(stringVal);
            }
            endOfComment = isEOF();
            
            if (!isAllowComment() && (isEOF() || !isSafeComment(stringVal))) {
                throw new NotAllowCommentException();
            }
            return;
        }
    }
    
    protected void scanLBracket() {
        mark = pos;

        if (buf == null) {
            buf = new char[32];
        }

        for (;;) {
            if (isEOF()) {
                lexError("unclosed.str.lit");
                return;
            }

            ch = charAt(++pos);

            if (ch == ']') {
                scanChar();
                token = IDENTIFIER;
                break;
            }

            if (bufPos == buf.length) {
                putChar(ch);
            } else {
                buf[bufPos++] = ch;
            }
        }

        stringVal = subString(mark, bufPos + 2);
    }
}

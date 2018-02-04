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
package com.alibaba.druid.support.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.parser.CharTypes;

public class JSONParser {

    private String text;
    private int    index = 0;
    private char   ch;

    private Token  token;
    private String stringValue;
    private long   longValue;
    private double doubleValue;

    public JSONParser(String text){
        this.text = text;
        ch = text.charAt(0);
        nextToken();
    }

    public Object parse() {
        if (token == Token.LBRACE) {
            return parseMap();
        }

        if (token == Token.INT) {
            Object value;
            if (this.longValue >= Integer.MIN_VALUE && this.longValue <= Integer.MAX_VALUE) {
                value = (int) this.longValue;
            } else {
                value = this.longValue;
            }

            nextToken();
            return value;
        }

        if (token == Token.DOUBLE) {
            Object value = this.doubleValue;
            nextToken();
            return value;
        }

        if (token == Token.STRING) {
            Object value = this.stringValue;
            nextToken();
            return value;
        }

        if (token == Token.LBRACKET) {
            return parseArray();
        }

        if (token == Token.TRUE) {
            nextToken();
            return true;
        }

        if (token == Token.FALSE) {
            nextToken();
            return false;
        }

        if (token == Token.NULL) {
            nextToken();
            return null;
        }

        throw new IllegalArgumentException("illegal token : " + token);
    }

    public List<Object> parseArray() {
        accept(Token.LBRACKET);
        ArrayList<Object> list = new ArrayList<Object>();

        for (;;) {
            if (token == Token.RBRACKET) {
                break;
            }

            if (token == Token.COMMA) {
                nextToken();
                continue;
            }

            Object item = parse();
            list.add(item);
        }

        accept(Token.RBRACKET);
        return list;
    }

    public Map<String, Object> parseMap() {
        accept(Token.LBRACE);
        Map<String, Object> map = new LinkedHashMap<String, Object>();

        for (;;) {
            if (token == Token.RBRACE) {
                break;
            }

            if (token == Token.COMMA) {
                nextToken();
                continue;
            }

            String key;
            {
                if (token != Token.STRING) {
                    throw new IllegalArgumentException("illegal json, " + token + " : " + text);
                }
                key = this.stringValue;
                nextToken();
            }

            accept(Token.COLON);

            Object value = parse();

            map.put(key, value);
        }

        accept(Token.RBRACE);
        return map;
    }

    void accept(Token token) {
        if (this.token == token) {
            nextToken();
            return;
        }

        throw new IllegalArgumentException("illegal token : " + this.token + ", expect " + token);
    }

    final void nextToken() {
        if (index == Integer.MIN_VALUE) {
            token = Token.EOF;
            return;
        }

        for (;;) {
            if (CharTypes.isWhitespace(ch)) {
                nextChar();
                continue;
            }

            if (index >= text.length()) {
                token = Token.EOF;
                return;
            }

            break;
        }

        switch (ch) {
            case '{':
                token = Token.LBRACE;
                nextChar();
                break;
            case '}':
                token = Token.RBRACE;
                nextChar();
                break;
            case '[':
                token = Token.LBRACKET;
                nextChar();
                break;
            case ']':
                token = Token.RBRACKET;
                nextChar();
                break;
            case ',':
                token = Token.COMMA;
                nextChar();
                break;
            case ':':
                token = Token.COLON;
                nextChar();
                break;
            case '"':
                scanString();
                break;
            default:
                if (isDigit(ch) || ch == '-') {
                    scanDigit();
                    return;
                }

                if (text.startsWith("null", index)) {
                    token = Token.NULL;
                    index += 3;
                    nextChar();
                    return;
                }

                if (text.startsWith("true", index)) {
                    token = Token.TRUE;
                    index += 3;
                    nextChar();
                    return;
                }

                if (text.startsWith("false", index)) {
                    token = Token.FALSE;
                    index += 4;
                    nextChar();
                    return;
                }
                throw new IllegalArgumentException("illegal json char : " + ch);
        }
    }

    private void scanDigit() {
        boolean isNegate = false;
        if (ch == '-') {
            isNegate = true;
            nextChar();
        }

        int dotCount = 0;
        StringBuilder digitBuf = new StringBuilder();
        for (;;) {
            digitBuf.append(ch);
            nextChar();

            if (ch == '.') {
                dotCount++;
                digitBuf.append('.');
                nextChar();
                continue;
            }

            if (!isDigit(ch)) {
                break;
            }
        }

        if (dotCount == 0) {
            long longValue = Long.parseLong(digitBuf.toString());
            if (isNegate) {
                longValue = -longValue;
            }
            this.longValue = longValue;
            token = Token.INT;
        } else {
            double doubleValue = Double.parseDouble(digitBuf.toString());
            if (isNegate) {
                doubleValue = -doubleValue;
            }
            this.doubleValue = doubleValue;
            token = Token.DOUBLE;
        }
    }

    private void scanString() {
        nextChar();
        StringBuilder strBuf = new StringBuilder();
        for (;;) {
            if (index >= text.length()) {
                throw new IllegalArgumentException("illegal string : " + strBuf);
            }
            if (ch == '"') {
                nextChar();
                break;
            }

            if (ch == '\\') {
                nextChar();
                if (ch == '"' || ch == '\\' || ch == '/') {
                    strBuf.append(ch);
                } else if (ch == 'n') {
                    strBuf.append('\n');
                } else if (ch == 'r') {
                    strBuf.append('\r');
                } else if (ch == 'b') {
                    strBuf.append('\b');
                } else if (ch == 'f') {
                    strBuf.append('\f');
                } else if (ch == 't') {
                    strBuf.append('\t');
                } else if (ch == 'u') {
                    nextChar();
                    char c1 = ch;
                    nextChar();
                    char c2 = ch;
                    nextChar();
                    char c3 = ch;
                    nextChar();
                    char c4 = ch;
                    int val = Integer.parseInt(new String(new char[] { c1, c2, c3, c4 }), 16);
                    strBuf.append((char) val);
                } else {
                    throw new IllegalArgumentException("illegal string : " + strBuf);
                }
            } else {
                strBuf.append(ch);
            }
            nextChar();
        }
        stringValue = strBuf.toString();
        token = Token.STRING;
    }

    static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    void nextChar() {
        ++index;
        if (index >= text.length()) {
            index = Integer.MIN_VALUE;
            return;
        }

        ch = text.charAt(index);
    }

    enum Token {
        INT, //
        DOUBLE, //
        STRING, //
        BOOLEAN, //
        TRUE, //
        FALSE, //
        NULL, //
        EOF, //

        LBRACE("{"), //
        RBRACE("}"), //
        LBRACKET("["), //
        RBRACKET("]"), //
        COMMA(","), //
        COLON(":"),

        ;

        public final String name;

        Token(){
            this(null);
        }

        Token(String name){
            this.name = name;
        }
    }
}

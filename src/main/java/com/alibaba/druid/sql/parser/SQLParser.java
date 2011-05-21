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

public class SQLParser {

    protected final Lexer lexer;

    public SQLParser(String sql){
        this(new Lexer(sql));
        this.lexer.nextToken();
    }

    public SQLParser(Lexer lexer){
        this.lexer = lexer;
    }

    protected boolean identifierEquals(String text) {
        return lexer.token() == Token.IDENTIFIER && lexer.stringVal().equalsIgnoreCase(text);
    }

    protected void acceptIdentifier(String text) {
        if (identifierEquals(text)) {
            lexer.nextToken();
        } else {
            setErrorEndPos(lexer.pos());
            throw new SQLParseException("syntax error, expect " + text + ", actual " + lexer.token());
        }
    }

    protected final String as() throws ParserException {
        String alias = null;

        if (lexer.token() == Token.AS) {
            lexer.nextToken();

            if (lexer.token() == Token.LITERAL_ALIAS) {
                alias = '"' + lexer.stringVal() + '"';
                lexer.nextToken();
                return alias;
            }

            if (lexer.token() == Token.IDENTIFIER) {
                alias = lexer.stringVal();
                lexer.nextToken();
                return alias;
            }

            if (lexer.token() == Token.LITERAL_CHARS) {
                alias = "'" + lexer.stringVal() + "'";
                lexer.nextToken();
                return alias;
            }

            throw new ParserException("Error", 0, 0);
        }

        if (lexer.token() == Token.LITERAL_ALIAS) {
            alias = '"' + lexer.stringVal() + '"';
            lexer.nextToken();
        } else if (lexer.token() == Token.IDENTIFIER) {
            alias = lexer.stringVal();
            lexer.nextToken();
        } else if (lexer.token() == Token.LITERAL_CHARS) {
            alias = "'" + lexer.stringVal() + "'";
            lexer.nextToken();
        }
        return alias;
    }

    public void accept(Token token) {
        if (lexer.token() == token) {
            lexer.nextToken();
        } else {
            setErrorEndPos(lexer.pos());
            throw new SQLParseException("syntax error, expect " + token + ", actual " + lexer.token());
        }
    }

    private int errorEndPos = -1;

    protected void setErrorEndPos(int errPos) {
        if (errPos > errorEndPos) errorEndPos = errPos;
    }
}

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

    public final Lexer getLexer() {
        return lexer;
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

    protected String as()  {
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

            if (lexer.token() == Token.KEY || lexer.token() == Token.CASE) {
                alias = lexer.token.name();
                lexer.nextToken();
                return alias;
            }

            switch (lexer.token()) {
                case KEY:
                    alias = lexer.token().name();
                    lexer.nextToken();
                    return alias;
                default:
                    break;
            }
            
            if (lexer.token() == Token.LPAREN) {
                return null;
            }

            throw new ParserException("Error : " + lexer.token());
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
        } else if (lexer.token() == Token.CASE) {
            alias = lexer.token.name();
            lexer.nextToken();
        }

        switch (lexer.token()) {
            case KEY:
                alias = lexer.token().name();
                lexer.nextToken();
                return alias;
            default:
                break;
        }

        return alias;
    }

    public void accept(Token token) {
        if (lexer.token() == token) {
            lexer.nextToken();
        } else {
            setErrorEndPos(lexer.pos());
            throw new SQLParseException("syntax error, expect " + token + ", actual " + lexer.token() + " "
                                        + lexer.stringVal());
        }
    }

    public void match(Token token) {
        if (lexer.token() != token) {
            throw new SQLParseException("syntax error, expect " + token + ", actual " + lexer.token() + " "
                                        + lexer.stringVal());
        }
    }

    private int errorEndPos = -1;

    protected void setErrorEndPos(int errPos) {
        if (errPos > errorEndPos) {
            errorEndPos = errPos;
        }
    }

}

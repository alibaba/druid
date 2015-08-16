/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

    protected String      dbType;

    public SQLParser(String sql, String dbType){
        this(new Lexer(sql), dbType);
        this.lexer.nextToken();
    }

    public SQLParser(String sql){
        this(sql, null);
    }

    public SQLParser(Lexer lexer){
        this(lexer, null);
    }

    public SQLParser(Lexer lexer, String dbType){
        this.lexer = lexer;
        this.dbType = dbType;
    }

    public final Lexer getLexer() {
        return lexer;
    }

    public String getDbType() {
        return dbType;
    }

    protected boolean identifierEquals(String text) {
        return lexer.token() == Token.IDENTIFIER && lexer.stringVal().equalsIgnoreCase(text);
    }

    protected void acceptIdentifier(String text) {
        if (identifierEquals(text)) {
            lexer.nextToken();
        } else {
            setErrorEndPos(lexer.pos());
            throw new ParserException("syntax error, expect " + text + ", actual " + lexer.token());
        }
    }

    protected String as() {
        String alias = null;

        if (lexer.token() == Token.AS) {
            lexer.nextToken();

            if (lexer.token() == Token.LITERAL_ALIAS) {
                alias = '"' + lexer.stringVal() + '"';
                lexer.nextToken();
            } else if (lexer.token() == Token.IDENTIFIER) {
                alias = lexer.stringVal();
                lexer.nextToken();
            } else if (lexer.token() == Token.LITERAL_CHARS) {
                alias = "'" + lexer.stringVal() + "'";
                lexer.nextToken();
            } else {
                switch (lexer.token()) {
                    case KEY:
                    case INDEX:
                    case CASE:
                    case MODEL:
                    case PCTFREE:
                    case INITRANS:
                    case MAXTRANS:
                    case SEGMENT:
                    case CREATION:
                    case IMMEDIATE:
                    case DEFERRED:
                    case STORAGE:
                    case NEXT:
                    case MINEXTENTS:
                    case MAXEXTENTS:
                    case MAXSIZE:
                    case PCTINCREASE:
                    case FLASH_CACHE:
                    case CELL_FLASH_CACHE:
                    case KEEP:
                    case NONE:
                    case LOB:
                    case STORE:
                    case ROW:
                    case CHUNK:
                    case CACHE:
                    case NOCACHE:
                    case LOGGING:
                    case NOCOMPRESS:
                    case KEEP_DUPLICATES:
                    case EXCEPTIONS:
                    case PURGE:
                    case INITIALLY:
                    case END:
                    case COMMENT:
                    case ENABLE:
                    case DISABLE:
                    case SEQUENCE:
                    case USER:
                    case ANALYZE:
                    case OPTIMIZE:
                    case GRANT:
                    case REVOKE:
                    case FULL:
                    case TO:
                    case NEW:
                    case INTERVAL:
                    case LOCK:
                    case LIMIT:
                    case IDENTIFIED:
                    case PASSWORD:
                    case BINARY:
                    case WINDOW:
                    case OFFSET:
                    case SHARE:
                    case START:
                    case CONNECT:
                    case MATCHED:
                    case ERRORS:
                    case REJECT:
                    case UNLIMITED:
                    case BEGIN:
                    case EXCLUSIVE:
                    case MODE:
                    case ADVISE:
                    case TYPE:
                        alias = lexer.stringVal();
                        lexer.nextToken();
                        return alias;
                    case QUES:
                        alias = "?";
                        lexer.nextToken();
                    default:
                        break;
                }
            }

            if (alias != null) {
                while (lexer.token() == Token.DOT) {
                    lexer.nextToken();
                    alias += ('.' + lexer.token().name());
                    lexer.nextToken();
                }

                return alias;
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
        } else if (lexer.token() == Token.USER) {
            alias = lexer.stringVal();
            lexer.nextToken();
        } else if (lexer.token() == Token.END) {
            alias = lexer.stringVal();
            lexer.nextToken();
        } 

        switch (lexer.token()) {
            case KEY:
            case INTERVAL:
            case CONSTRAINT:
                alias = lexer.token().name();
                lexer.nextToken();
                return alias;
            default:
                break;
        }

        return alias;
    }

    protected void printError(Token token) {
        String arround;
        if (lexer.mark >= 0 && (lexer.text.length() > lexer.mark + 30)) {
            if (lexer.mark - 5 > 0) {
                arround = lexer.text.substring(lexer.mark - 5, lexer.mark + 30);
            } else {
                arround = lexer.text.substring(lexer.mark, lexer.mark + 30);
            }

        } else if (lexer.mark >= 0) {
            if (lexer.mark - 5 > 0) {
                arround = lexer.text.substring(lexer.mark - 5);
            } else {
                arround = lexer.text.substring(lexer.mark);
            }
        } else {
            arround = lexer.text;
        }

        // throw new
        // ParserException("syntax error, error arround:'"+arround+"',expect "
        // + token + ", actual " + lexer.token() + " "
        // + lexer.stringVal() + ", pos " + this.lexer.pos());
        throw new ParserException("syntax error, error in :'" + arround + "',expect " + token + ", actual "
                                  + lexer.token() + " " + lexer.stringVal());
    }

    public void accept(Token token) {
        if (lexer.token() == token) {
            lexer.nextToken();
        } else {
            setErrorEndPos(lexer.pos());
            printError(token);
        }
    }

    public void match(Token token) {
        if (lexer.token() != token) {
            throw new ParserException("syntax error, expect " + token + ", actual " + lexer.token() + " "
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

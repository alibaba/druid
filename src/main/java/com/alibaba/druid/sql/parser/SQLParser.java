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

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.StringUtils;

public class SQLParser {
    protected final Lexer lexer;
    protected String      dbType;

    public SQLParser(String sql, String dbType){
        this(new Lexer(sql, null, dbType), dbType);
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
        return lexer.identifierEquals(text);
    }

    protected void acceptIdentifier(String text) {
        if (lexer.identifierEquals(text)) {
            lexer.nextToken();
        } else {
            setErrorEndPos(lexer.pos());
            throw new ParserException("syntax error, expect " + text + ", actual " + lexer.token + ", " + lexer.info());
        }
    }

    protected String tableAlias() {
        return tableAlias(false);
    }

    protected String tableAlias(boolean must) {
        final Token token = lexer.token;
        if (token == Token.CONNECT
                || token == Token.START
                || token == Token.SELECT
                || token == Token.FROM
                || token == Token.WHERE) {
            if (must) {
                throw new ParserException("illegal alias. " + lexer.info());
            }
            return null;
        }

        if (token == Token.IDENTIFIER) {
            String ident = lexer.stringVal;
            long hash = lexer.hash_lower;
            if (isEnabled(SQLParserFeature.IgnoreNameQuotes) && ident.length() > 1) {
                ident = StringUtils.removeNameQuotes(ident);
            }

            if (hash == FnvHash.Constants.START
                    || hash == FnvHash.Constants.CONNECT
                    || hash == FnvHash.Constants.NATURAL
                    || hash == FnvHash.Constants.CROSS
                    || hash == FnvHash.Constants.OFFSET
                    || hash == FnvHash.Constants.LIMIT) {
                if (must) {
                    throw new ParserException("illegal alias. " + lexer.info());
                }

                Lexer.SavePoint mark = lexer.mark();
                lexer.nextToken();
                switch (lexer.token) {
                    case EOF:
                    case COMMA:
                    case WHERE:
                    case INNER:
                        return ident;
                    default:
                        lexer.reset(mark);
                        break;
                }

                return null;
            }

            if (!must) {
                if (hash == FnvHash.Constants.MODEL) {
                    Lexer.SavePoint mark = lexer.mark();
                    lexer.nextToken();
                    if (lexer.token == Token.PARTITION
                            || lexer.token == Token.UNION
                            || lexer.identifierEquals(FnvHash.Constants.DIMENSION)
                            || lexer.identifierEquals(FnvHash.Constants.IGNORE)
                            || lexer.identifierEquals(FnvHash.Constants.KEEP)) {
                        lexer.reset(mark);
                        return null;
                    }
                    return ident;
                } else if (hash == FnvHash.Constants.WINDOW) {
                    Lexer.SavePoint mark = lexer.mark();
                    lexer.nextToken();
                    if (lexer.token == Token.IDENTIFIER) {
                        lexer.reset(mark);
                        return null;
                    }
                    return ident;
                } else if (hash == FnvHash.Constants.DISTRIBUTE
                        || hash == FnvHash.Constants.SORT
                        || hash == FnvHash.Constants.CLUSTER
                ) {
                    Lexer.SavePoint mark = lexer.mark();
                    lexer.nextToken();
                    if (lexer.token == Token.BY) {
                        lexer.reset(mark);
                        return null;
                    }
                    return ident;
                }
            }
        }

        return this.as();
    }

    protected String as() {
        String alias = null;

        final Token token = lexer.token;

        if (token == Token.COMMA) {
            return null;
        }

        if (token == Token.AS) {
            lexer.nextToken();
            alias = lexer.stringVal();
            lexer.nextToken();

            if (alias != null) {
                while (lexer.token == Token.DOT) {
                    lexer.nextToken();
                    alias += ('.' + lexer.token.name());
                    lexer.nextToken();
                }

                return alias;
            }

            if (lexer.token == Token.LPAREN) {
                return null;
            }

            throw new ParserException("Error : " + lexer.info());
        }

        if (lexer.token == Token.LITERAL_ALIAS) {
            alias = lexer.stringVal();
            lexer.nextToken();
        } else if (lexer.token == Token.IDENTIFIER) {
            alias = lexer.stringVal();
            lexer.nextToken();
        } else if (lexer.token == Token.LITERAL_CHARS) {
            alias = "'" + lexer.stringVal() + "'";
            lexer.nextToken();
        } else {
            switch (lexer.token) {
                case CASE:
                case USER:
                case LOB:
                case END:
                case DEFERRED:
                case OUTER:
                case DO:
                case STORE:
                case MOD:
                    alias = lexer.stringVal();
                    lexer.nextToken();
                    break;
                default:
                    break;
            }
        }

        switch (lexer.token) {
            case KEY:
            case INTERVAL:
            case CONSTRAINT:
                alias = lexer.token.name();
                lexer.nextToken();
                return alias;
            default:
                break;
        }

        return alias;
    }

    protected String alias() {
        String alias = null;
        if (lexer.token == Token.LITERAL_ALIAS) {
            alias = lexer.stringVal();
            lexer.nextToken();
        } else if (lexer.token == Token.IDENTIFIER) {
            alias = lexer.stringVal();
            lexer.nextToken();
        } else if (lexer.token == Token.LITERAL_CHARS) {
            alias = "'" + lexer.stringVal() + "'";
            lexer.nextToken();
        } else {
            switch (lexer.token) {
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
                case CLOSE:
                case OPEN:
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
        // + token + ", actual " + lexer.token + " "
        // + lexer.stringVal() + ", pos " + this.lexer.pos());
        throw new ParserException("syntax error, error in :'" + arround + "', expect " + token + ", actual "
                                  + lexer.token + " " + lexer.info());
    }

    public void accept(Token token) {
        if (lexer.token == token) {
            lexer.nextToken();
        } else {
            setErrorEndPos(lexer.pos());
            printError(token);
        }
    }

    public int acceptInteger() {
        if (lexer.token == Token.LITERAL_INT) {
            int intVal = ((Integer) lexer.integerValue()).intValue();
            lexer.nextToken();
            return intVal;
        } else {
            throw new ParserException("syntax error, expect int, actual " + lexer.token + " "
                    + lexer.info());
        }
    }

    public void match(Token token) {
        if (lexer.token != token) {
            throw new ParserException("syntax error, expect " + token + ", actual " + lexer.token + " "
                                      + lexer.info());
        }
    }

    private int errorEndPos = -1;

    protected void setErrorEndPos(int errPos) {
        if (errPos > errorEndPos) {
            errorEndPos = errPos;
        }
    }

    public void config(SQLParserFeature feature, boolean state) {
        this.lexer.config(feature, state);
    }

    public final boolean isEnabled(SQLParserFeature feature) {
        return lexer.isEnabled(feature);
    }

    protected SQLCreateTableStatement newCreateStatement() {
        return new SQLCreateTableStatement(getDbType());
    }
}

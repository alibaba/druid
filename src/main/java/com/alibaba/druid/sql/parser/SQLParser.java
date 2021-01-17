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
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.StringUtils;

import java.util.TimeZone;

public class SQLParser {
    protected final Lexer lexer;
    protected DbType      dbType;

    public SQLParser(String sql, DbType dbType, SQLParserFeature... features){
        this(new Lexer(sql, null, dbType), dbType);
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }

        this.lexer.nextToken();
    }

    public SQLParser(String sql){
        this(sql, null);
    }

    public SQLParser(Lexer lexer){
        this(lexer, null);
        if (dbType == null) {
            dbType = lexer.dbType;
        }
    }

    public SQLParser(Lexer lexer, DbType dbType){
        this.lexer = lexer;
        this.dbType = dbType;
    }

    public final Lexer getLexer() {
        return lexer;
    }

    public DbType getDbType() {
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
                    case ON:
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
                        || hash == FnvHash.Constants.CLUSTER) {
                    Lexer.SavePoint mark = lexer.mark();
                    lexer.nextToken();
                    if (lexer.token == Token.BY) {
                        lexer.reset(mark);
                        return null;
                    }
                    return ident;
                } else if (hash == FnvHash.Constants.ASOF && dbType == DbType.clickhouse) {
                    Lexer.SavePoint mark = lexer.mark();
                    lexer.nextToken();
                    if (lexer.token == Token.LEFT || lexer.token == Token.JOIN) {
                        lexer.reset(mark);
                        return null;
                    }
                    return ident;
                }
            }
        }

        if (!must) {
            switch (token) {
                case LEFT:
                case RIGHT:
                case INNER:
                case FULL: {
                    Lexer.SavePoint mark = lexer.mark();
                    String strVal = lexer.stringVal();
                    lexer.nextToken();
                    if (lexer.token == Token.OUTER
                            || lexer.token == Token.JOIN
                            || lexer.identifierEquals(FnvHash.Constants.ANTI)
                            || lexer.identifierEquals(FnvHash.Constants.SEMI)) {
                        lexer.reset(mark);
                        break;
                    } else {
                        return strVal;
                    }
                }
                case IN: {
                    Lexer.SavePoint mark = lexer.mark();
                    String strVal = lexer.stringVal();
                    lexer.nextToken();
                    switch (lexer.token) {
                        case WHERE:
                        case RPAREN: {
                            return strVal;
                        }
                        default:
                            lexer.reset(mark);
                            break;
                    }
                    break;
                }
                case FOR:
                case GRANT:
                    if (dbType == DbType.odps){
                        String strVal = lexer.stringVal();
                        lexer.nextToken();
                        return strVal;
                    }
                    break;
                case TABLE:
                    if (dbType == DbType.odps){
                        Lexer.SavePoint mark = lexer.mark();
                        String strVal = lexer.stringVal();
                        lexer.nextToken();
                        switch (lexer.token) {
                            case FROM:
                            case GROUP:
                                return strVal;
                            default:
                                lexer.reset(mark);
                                break;
                        }
                    }
                    break;
                case SHOW:
                case REPEAT:
                case USE:
                case OUT: {
                    String strVal = lexer.stringVal();
                    lexer.nextToken();
                    return strVal;
                }
                default:
                    break;
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
            lexer.nextTokenAlias();
            if (lexer.token == Token.LPAREN) {
                return null;
            }

            // for oracle
            if (dbType == DbType.oracle && (lexer.token == Token.COMMA || lexer.token == Token.FROM)) {
                return null;
            }

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
                case LOOP:
                case STORE:
                case MOD:
                case ANY:
                case BEGIN:
                case CAST:
                case COMPUTE:
                case ESCAPE:
                case FULL:
                case MERGE:
                case OPEN:
                case SOME:
                case TRUNCATE:
                case UNTIL:
                case VIEW:
                case KILL:
                case COMMENT:
                    alias = lexer.stringVal();
                    lexer.nextToken();
                    break;
                case INTERSECT:
                case EXCEPT:
                case DESC:
                case MINUS: {
                    alias = lexer.stringVal();

                    Lexer.SavePoint mark = lexer.mark();
                    lexer.nextToken();
                    if (lexer.token() != Token.COMMA
                            && lexer.token() != Token.RPAREN) {
                        alias = null;
                        lexer.reset(mark);
                    }
                    break;
                }
                case CHECK:
                case INDEX:
                case ALL:
                case INNER:
                case CLOSE:
                case VALUES:
                case SHOW:
                case SEQUENCE:
                case TO:
                    if (dbType == DbType.odps) {
                        alias = lexer.stringVal();
                        lexer.nextToken();
                        break;
                    }
                    break;
                case GROUP:
                case ORDER:
                case DEFAULT:
                    if (dbType == DbType.odps) {
                        Lexer.SavePoint mark = lexer.mark();
                        alias = lexer.stringVal();
                        lexer.nextToken();
                        if (lexer.token == Token.BY) {
                            lexer.reset(mark);
                            alias = null;
                        }
                        break;
                    }
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

        if (isEnabled(SQLParserFeature.IgnoreNameQuotes) && alias != null && alias.length() > 1) {
            alias = StringUtils.removeNameQuotes(alias);
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
//                case MODEL:
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
                case ANY:
                case CAST:
                case COMPUTE:
                case ESCAPE:
                case INTERSECT:
                case MERGE:
                case MINUS:
                case SOME:
                case TRUNCATE:
                case UNTIL:
                case VIEW:
                case FUNCTION:
                case DESC:
                case KILL:
                case SHOW:
                case NULL:
                case ORDER:
                case GROUP:
                case ALL:
                case CONSTRAINT:
                case INNER:
                case LEFT:
                case RIGHT:
                case VALUES:
                case SCHEMA:
                case PARTITION:
                case UPDATE:
                case DO:
                case LOOP:
                case REPEAT:
                case DEFAULT:
                case LIKE:
                case IS:
                case UNIQUE:
                case CHECK:
                case INOUT:
                case DECLARE:
                case TABLE:
                case TRIGGER:
                case IN:
                case OUT:
                case BY:
                case EXCEPT:
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
            int begin, end;
            if (lexer.mark - 5 > 0) {
                begin = lexer.mark - 5;
                end = lexer.mark + 30;
            } else {
                begin = lexer.mark;
                end = lexer.mark + 30;
            }

            if (begin < 10) {
                begin = 0;
            } else {
                for (int i = 1; i < 10 && i < begin; ++i) {
                    char ch = lexer.text.charAt(begin - i);
                    if (ch == ' ' || ch == '\n') {
                        begin = begin - i + 1;
                    }
                }
            }

            arround = lexer.text.substring(begin, end);
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
        StringBuilder buf = new StringBuilder()
                .append("syntax error, error in :'")
                .append(arround);
        if (token != lexer.token) {
            buf.append("', expect ")
                    .append(token.name)
                    .append(", actual ")
                    .append(lexer.token.name);
        }
        buf.append(", ")
                .append(
                        lexer.info());

        throw new ParserException(buf.toString());
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


    public TimeZone getTimeZone() {
        return lexer.getTimeZone();
    }

    public void setTimeZone(TimeZone timeZone) {
        this.lexer.setTimeZone(timeZone);
    }

    public final boolean isEnabled(SQLParserFeature feature) {
        return lexer.isEnabled(feature);
    }

    protected SQLCreateTableStatement newCreateStatement() {
        return new SQLCreateTableStatement(dbType);
    }
}

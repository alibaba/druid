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
    protected DbType dbType;

    public SQLParser(String sql, DbType dbType, SQLParserFeature... features) {
        this(new Lexer(sql, null, dbType), dbType);
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }

        this.lexer.nextToken();
    }

    public SQLParser(String sql) {
        this(sql, null);
    }

    public SQLParser(Lexer lexer) {
        this(lexer, null);
        if (dbType == null) {
            dbType = lexer.dbType;
        }
    }

    public SQLParser(Lexer lexer, DbType dbType) {
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
            if (token == Token.WHERE && dbType == DbType.odps) {
                return null;
            }

            if (must) {
                throw new ParserException("illegal alias. " + lexer.info());
            }
            return null;
        }

        if (token == Token.IDENTIFIER) {
            String ident = lexer.stringVal;
            long hash = lexer.hashLCase;
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
                    case LEFT:
                    case RIGHT:
                    case FULL:
                    case ON:
                    case GROUP:
                    case ORDER:
                        return ident;
                    case JOIN:
                        if (hash != FnvHash.Constants.NATURAL
                                && hash != FnvHash.Constants.CROSS) {
                            return ident;
                        }
                        lexer.reset(mark);
                        break;
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
                        || hash == FnvHash.Constants.ZORDER
                ) {
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
                        return null;
                    } else {
                        return strVal;
                    }
                }
                case OUTER:
                case IN:
                case SET:
                case BY: {
                    Lexer.SavePoint mark = lexer.mark();
                    String strVal = lexer.stringVal();
                    lexer.nextToken();
                    switch (lexer.token) {
                        case WHERE:
                        case GROUP:
                        case ORDER:
                        case LEFT:
                        case RIGHT:
                        case FULL:
                        case RPAREN:
                        case ON:
                        case JOIN:
                        case SEMI: {
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
                case CHECK:
                case LEAVE:
                case TRIGGER:
                case CREATE:
                case ASC:
                case INOUT:
                case DESC:
                case SCHEMA:
                case IS:
                case DECLARE:
                case DROP:
                case FETCH:
                case LOCK:
                    if (dbType == DbType.odps || dbType == DbType.hive) {
                        String strVal = lexer.stringVal();
                        lexer.nextToken();
                        return strVal;
                    }
                    break;
                case PARTITION:
                    if (dbType == DbType.odps || dbType == DbType.hive) {
                        Lexer.SavePoint mark = lexer.mark();
                        String strVal = lexer.stringVal();
                        lexer.nextToken();
                        if (lexer.token == Token.LPAREN) {
                            lexer.reset(mark);
                            return null;
                        }
                        return strVal;
                    }
                    break;
                case TABLE:
                    if (dbType == DbType.odps) {
                        Lexer.SavePoint mark = lexer.mark();
                        String strVal = lexer.stringVal();
                        lexer.nextToken();
                        switch (lexer.token) {
                            case FROM:
                            case GROUP:
                            case ORDER:
                            case ON:
                                return strVal;
                            default:
                                lexer.reset(mark);
                                break;
                        }
                    }
                    break;
                case SHOW:
                case REFERENCES:
                case REPEAT:
                case USE:
                case MOD:
                case OUT: {
                    String strVal = lexer.stringVal();
                    lexer.nextToken();
                    return strVal;
                }
                case QUALIFY: {
                    String strVal = lexer.stringVal();
                    Lexer.SavePoint mark = lexer.mark();
                    lexer.nextToken();
                    if (lexer.token != Token.WHERE
                            && lexer.token != Token.GROUP
                            && lexer.token != Token.HAVING
                            && lexer.token != Token.WINDOW
                            && lexer.token != Token.ORDER
                            && lexer.token != Token.LIMIT
                            && lexer.token != Token.EOF
                            && lexer.token != Token.COMMA
                    ) {
                        lexer.reset(mark);
                        return null;
                    } else {
                        return strVal;
                    }
                }
                case DISTRIBUTE: {
                    String strVal = lexer.stringVal();
                    Lexer.SavePoint mark = lexer.mark();
                    lexer.nextToken();
                    if (lexer.token == Token.BY) {
                        lexer.reset(mark);
                        return null;
                    } else {
                        return strVal;
                    }
                }
                case MINUS:
                case EXCEPT:
                case LIMIT:
                case BETWEEN:
                    if (dbType == DbType.odps) {
                        Lexer.SavePoint mark = lexer.mark();
                        String strVal = lexer.stringVal();
                        lexer.nextToken();
                        switch (lexer.token) {
                            case EOF:
                            case COMMA:
                            case WHERE:
                            case GROUP:
                            case ORDER:
                            case LEFT:
                            case RIGHT:
                            case FULL:
                            case RPAREN:
                            case ON:
                            case JOIN:
                            case SEMI:
                                return strVal;
                            default:
                                lexer.reset(mark);
                                break;
                        }
                    }
                    break;
                case UNION: {
                    Lexer.SavePoint mark = lexer.mark();
                    String strVal = lexer.stringVal();
                    lexer.nextToken();
                    switch (lexer.token) {
                        case GROUP:
                        case ORDER:
                        case SEMI:
                        case LEFT:
                        case RIGHT:
                        case INNER:
                        case JOIN:
                        case RPAREN:
                            return strVal;
                        default:
                            lexer.reset(mark);
                            return null;
                    }
                }
                default:
                    break;
            }
        }

        if (must) {
            if (dbType == DbType.odps) {
                switch (lexer.token) {
                    case GROUP:
                    case ORDER: {
                        Lexer.SavePoint mark = lexer.mark();
                        String strVal = lexer.stringVal();
                        lexer.nextToken();
                        if (lexer.token == Token.BY) {
                            lexer.reset(mark);
                            return null;
                        } else {
                            return strVal;
                        }
                    }
                    case UNION: {
                        Lexer.SavePoint mark = lexer.mark();
                        String strVal = lexer.stringVal();
                        lexer.nextToken();
                        if (lexer.token == Token.ALL) {
                            lexer.reset(mark);
                            return null;
                        } else {
                            return strVal;
                        }
                    }
                    case LIMIT: {
                        Lexer.SavePoint mark = lexer.mark();
                        String strVal = lexer.stringVal();
                        lexer.nextToken();
                        if (lexer.token == Token.LITERAL_INT) {
                            lexer.reset(mark);
                            return null;
                        } else {
                            return strVal;
                        }
                    }
                    case BETWEEN: {
                        Lexer.SavePoint mark = lexer.mark();
                        String strVal = lexer.stringVal();
                        lexer.nextToken();
                        switch (lexer.token) {
                            case GROUP:
                            case ORDER:
                            case SEMI:
                            case LEFT:
                            case RIGHT:
                            case INNER:
                            case JOIN:
                                return strVal;
                            default:
                                lexer.reset(mark);
                                return null;
                        }
                    }
                    default:
                        break;
                }
            }
            return this.alias();
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
            boolean skip = false;
            if (dbType == DbType.hive || dbType == DbType.odps) {
                skip = "TBLPROPERTIES".equalsIgnoreCase(alias);
            }
            if (skip) {
                alias = null;
            } else {
                lexer.nextToken();
            }
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
                case TABLESPACE:
                case REPEAT:
                case PRIMARY:
                case FOREIGN:
                case UNIQUE:
                case LEAVE:
                case ENABLE:
                case DISABLE:
                case REPLACE:
                    alias = lexer.stringVal();
                    lexer.nextToken();
                    break;
                case INTERSECT:
                case EXCEPT:
                case DESC:
                case INOUT:
                case MINUS:
                case UPDATE:
                case DELETE:
                case TABLE:
                case UNION:
                case EXPLAIN:
                case CREATE:
                case LIMIT:
                case USE:
                case BY:
                case ALTER:
                case IN:
                case INTO:
                case ASC: {
                    alias = lexer.stringVal();

                    Lexer.SavePoint mark = lexer.mark();
                    lexer.nextToken();
                    if (lexer.token() != Token.COMMA
                            && lexer.token() != Token.RPAREN
                            && lexer.token() != Token.FROM
                            && lexer.token() != Token.SEMI
                            && lexer.token() != Token.SEMI
                    ) {
                        alias = null;
                        lexer.reset(mark);
                    }
                    break;
                }
                case CLOSE:
                case SEQUENCE:
                    if (dbType == DbType.mysql || dbType == DbType.odps || dbType == DbType.hive) {
                        alias = lexer.stringVal();
                        lexer.nextToken();
                        break;
                    }
                    break;
                case CHECK:
                case INDEX:
                case ALL:
                case INNER:
                case VALUES:
                case SHOW:
                case TO:
                case REFERENCES:
                case LIKE:
                case RLIKE:
                case NULL:
                case RIGHT:
                case LEFT:
                case DATABASE:
                    if (dbType == DbType.odps || dbType == DbType.hive) {
                        alias = lexer.stringVal();
                        lexer.nextToken();
                        break;
                    }
                    break;
                case GROUP:
                case ORDER:
                case DISTRIBUTE:
                case DEFAULT:
                    if (dbType == DbType.odps || dbType == DbType.hive) {
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
        } else if (lexer.token == Token.LITERAL_FLOAT && dbType == DbType.odps) {
            String numStr = lexer.numberString();
            lexer.nextToken();
            if (lexer.token == Token.IDENTIFIER) {
                numStr += lexer.stringVal();
                lexer.nextToken();
            }
            return numStr;
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
                case TABLESPACE:
                case CREATE:
                case DELETE:
                case PRIMARY:
                case FOREIGN:
                case REFERENCES:
                case INTO:
                case USE:
                case LEAVE:
                case DISTRIBUTE:
                case AS:
                case FOR:
                case PARTITIONED:
                case REPLACE:
                case ALTER:
                case EXPLAIN:
                case ASC:
                case DATABASE:
                case TRUE:
                case FALSE:
                case OUTER:
                case DROP:
                    alias = lexer.stringVal();
                    lexer.nextToken();
                    return alias;
                case GROUP:
                case ORDER:
                    {
                        Lexer.SavePoint mark = lexer.mark();
                        alias = lexer.stringVal();
                        lexer.nextToken();
                        if (lexer.token == Token.BY) {
                            lexer.reset(mark);
                            alias = null;
                        }
                    }
                    break;
                case QUES:
                    alias = "?";
                    lexer.nextToken();
                    return alias;
                case UNION: {
                    Lexer.SavePoint mark = lexer.mark();
                    String strVal = lexer.stringVal();
                    lexer.nextToken();
                    if (lexer.token == Token.ALL) {
                        lexer.reset(mark);
                        return null;
                    } else {
                        return strVal;
                    }
                }
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

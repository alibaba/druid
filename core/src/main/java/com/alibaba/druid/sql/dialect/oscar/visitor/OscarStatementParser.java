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
package com.alibaba.druid.sql.dialect.oscar.visitor;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTypeStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleFunctionDataType;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleProcedureDataType;
import com.alibaba.druid.sql.dialect.oscar.ast.stmt.*;
import com.alibaba.druid.sql.dialect.oscar.parser.OscarCreateTableParser;
import com.alibaba.druid.sql.dialect.oscar.parser.OscarExprParser;
import com.alibaba.druid.sql.dialect.oscar.parser.OscarSelectParser;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.*;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

import java.util.ArrayList;
import java.util.List;

public class OscarStatementParser extends SQLStatementParser {
    public static final String TIME_ZONE = "TIME ZONE";
    public static final String TIME = "TIME";
    public static final String LOCAL = "LOCAL";

    public OscarStatementParser(OscarExprParser parser) {
        super(parser);
    }

    public OscarStatementParser(String sql) {
        super(new OscarExprParser(sql));
    }

    public OscarStatementParser(String sql, SQLParserFeature... features) {
        super(new OscarExprParser(sql, features));
    }

    public OscarStatementParser(Lexer lexer) {
        super(new OscarExprParser(lexer));
    }

    public OscarSelectParser createSQLSelectParser() {
        return new OscarSelectParser(this.exprParser, selectListCache);
    }

    public SQLUpdateStatement parseUpdateStatement() {
        accept(Token.UPDATE);

        OscarUpdateStatement udpateStatement = new OscarUpdateStatement();

        SQLSelectParser selectParser = this.exprParser.createSelectParser();
        SQLTableSource tableSource = selectParser.parseTableSource();
        udpateStatement.setTableSource(tableSource);

        parseUpdateSet(udpateStatement);

        if (lexer.token() == Token.FROM) {
            lexer.nextToken();
            SQLTableSource from = selectParser.parseTableSource();
            udpateStatement.setFrom(from);
        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            udpateStatement.setWhere(this.exprParser.expr());
        }

        if (lexer.token() == Token.RETURNING) {
            lexer.nextToken();

            for (;;) {
                udpateStatement.getReturning().add(this.exprParser.expr());
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
        }

        return udpateStatement;
    }

    public OscarInsertStatement parseInsert() {
        OscarInsertStatement stmt = new OscarInsertStatement();

        if (lexer.token() == Token.INSERT) {
            lexer.nextToken();
            accept(Token.INTO);

            SQLName tableName = this.exprParser.name();
            stmt.setTableName(tableName);

            if (lexer.token() == Token.AS) {
                lexer.nextToken();
                stmt.setAlias(lexer.stringVal());
                lexer.nextToken();
            } else if (lexer.token() == Token.IDENTIFIER) {
                stmt.setAlias(lexer.stringVal());
                lexer.nextToken();
            }

        }

        if (lexer.token() == Token.DEFAULT) {
            lexer.nextToken();
            accept(Token.VALUES);
            stmt.setDefaultValues(true);
        }

        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();
            this.exprParser.exprList(stmt.getColumns(), stmt);
            accept(Token.RPAREN);
        }

        if (lexer.token() == (Token.VALUES)) {
            lexer.nextToken();

            for (;;) {
                accept(Token.LPAREN);
                SQLInsertStatement.ValuesClause valuesCaluse = new SQLInsertStatement.ValuesClause();
                this.exprParser.exprList(valuesCaluse.getValues(), valuesCaluse);
                stmt.addValueCause(valuesCaluse);

                accept(Token.RPAREN);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
        } else if (lexer.token() == (Token.SELECT)) {
            SQLQueryExpr queryExpr = (SQLQueryExpr) this.exprParser.expr();
            stmt.setQuery(queryExpr.getSubQuery());
        }

        if (lexer.token() == Token.ON) {
            lexer.nextToken();
            if (lexer.identifierEquals(FnvHash.Constants.CONFLICT)) {
                lexer.nextToken();

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    List<SQLExpr> onConflictTarget = new ArrayList<SQLExpr>();
                    this.exprParser.exprList(onConflictTarget, stmt);
                    stmt.setOnConflictTarget(onConflictTarget);
                    accept(Token.RPAREN);
                }

                if (lexer.token() == Token.ON) {
                    lexer.nextToken();
                    accept(Token.CONSTRAINT);
                    SQLName constraintName = this.exprParser.name();
                    stmt.setOnConflictConstraint(constraintName);
                }

                if (lexer.token() == Token.WHERE) {
                    lexer.nextToken();
                    SQLExpr where = this.exprParser.expr();
                    stmt.setOnConflictWhere(where);
                }

                if (lexer.token() == Token.DO) {
                    lexer.nextToken();

                    if (lexer.identifierEquals(FnvHash.Constants.NOTHING)) {
                        lexer.nextToken();
                        stmt.setOnConflictDoNothing(true);
                    } else {
                        accept(Token.UPDATE);
                        accept(Token.SET);

                        for (;;) {
                            SQLUpdateSetItem item = this.exprParser.parseUpdateSetItem();
                            stmt.addConflicUpdateItem(item);

                            if (lexer.token() != Token.COMMA) {
                                break;
                            }

                            lexer.nextToken();
                        }
                        if (lexer.token() == Token.WHERE) {
                            lexer.nextToken();
                            SQLExpr where = this.exprParser.expr();
                            stmt.setOnConflictUpdateWhere(where);
                        }
                    }
                }
            }
        }

        if (lexer.token() == Token.RETURNING) {
            lexer.nextToken();
            SQLExpr returning = this.exprParser.expr();

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                SQLListExpr list = new SQLListExpr();
                list.addItem(returning);

                this.exprParser.exprList(list.getItems(), list);

                returning = list;
            }

            stmt.setReturning(returning);
        }
        return stmt;
    }

    public OscarCreateSchemaStatement parseCreateSchema() {
        accept(Token.CREATE);
        accept(Token.SCHEMA);

        OscarCreateSchemaStatement stmt = new OscarCreateSchemaStatement();
        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);

            stmt.setIfNotExists(true);
        }

        if (lexer.token() == Token.IDENTIFIER) {
            if (lexer.identifierEquals("AUTHORIZATION")) {
                lexer.nextToken();
                stmt.setAuthorization(true);

                SQLIdentifierExpr userName = (SQLIdentifierExpr) this.exprParser.expr();
                stmt.setUserName(userName);
            } else {
                SQLIdentifierExpr schemaName = (SQLIdentifierExpr) this.exprParser.expr();
                stmt.setSchemaName(schemaName);

                if (lexer.identifierEquals("AUTHORIZATION")) {
                    lexer.nextToken();
                    stmt.setAuthorization(true);

                    SQLIdentifierExpr userName = (SQLIdentifierExpr) this.exprParser.expr();
                    stmt.setUserName(userName);
                }
            }
        } else {
            throw new ParserException("TODO " + lexer.info());
        }

        return stmt;
    }

    protected SQLStatement parseAlterSchema() {
        accept(Token.ALTER);
        accept(Token.SCHEMA);

        OscarAlterSchemaStatement stmt = new OscarAlterSchemaStatement();
        stmt.setSchemaName(this.exprParser.identifier());

        if (lexer.identifierEquals(FnvHash.Constants.RENAME)) {
            lexer.nextToken();
            accept(Token.TO);
            stmt.setNewName(this.exprParser.identifier());
        } else if (lexer.identifierEquals(FnvHash.Constants.OWNER)) {
            lexer.nextToken();
            accept(Token.TO);
            stmt.setNewOwner(this.exprParser.identifier());
        }

        return stmt;
    }

    public OscarDropSchemaStatement parseDropSchema() {
        OscarDropSchemaStatement stmt = new OscarDropSchemaStatement();

        if (lexer.token() == Token.SCHEMA) {
            lexer.nextToken();
        } else {
            accept(Token.DATABASE);
        }

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        SQLIdentifierExpr name = this.exprParser.identifier();
        stmt.setSchemaName(name);

        if (lexer.identifierEquals(FnvHash.Constants.RESTRICT)) {
            lexer.nextToken();
            stmt.setRestrict(true);
        } else if (lexer.token() == Token.CASCADE || lexer.identifierEquals(FnvHash.Constants.CASCADE)) {
            lexer.nextToken();
            stmt.setCascade(true);
        } else {
            stmt.setCascade(false);
        }

        return stmt;
    }

    public OscarDeleteStatement parseDeleteStatement() {
        lexer.nextToken();
        OscarDeleteStatement deleteStatement = new OscarDeleteStatement();

        if (lexer.token() == (Token.FROM)) {
            lexer.nextToken();
        }
        if (lexer.token() == (Token.ONLY)) {
            lexer.nextToken();
            deleteStatement.setOnly(true);
        }

        SQLName tableName = exprParser.name();

        deleteStatement.setTableName(tableName);

        if (lexer.token() == Token.AS) {
            accept(Token.AS);
        }
        if (lexer.token() == Token.IDENTIFIER) {
            deleteStatement.setAlias(lexer.stringVal());
            lexer.nextToken();
        }

        if (lexer.token() == Token.USING) {
            lexer.nextToken();

            SQLTableSource tableSource = createSQLSelectParser().parseTableSource();
            deleteStatement.setUsing(tableSource);
        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();

            if (lexer.token() == Token.CURRENT) {
                lexer.nextToken();
                accept(Token.OF);
                SQLName cursorName = this.exprParser.name();
                SQLExpr where = new SQLCurrentOfCursorExpr(cursorName);
                deleteStatement.setWhere(where);
            } else {
                SQLExpr where = this.exprParser.expr();
                deleteStatement.setWhere(where);
            }
        }

        if (lexer.token() == Token.RETURNING) {
            lexer.nextToken();
            accept(Token.STAR);
            deleteStatement.setReturning(true);
        }

        return deleteStatement;
    }

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        switch (lexer.token()) {
            case BEGIN:
            case START: {
                OscarStartTransactionStatement stmt = parseBegin();
                statementList.add(stmt);
                return true;
            }
            case WITH:
                statementList.add(parseWith());
                return true;
            default:
                if (lexer.identifierEquals(FnvHash.Constants.CONNECT)) {
                    SQLStatement stmt = parseConnectTo();
                    statementList.add(stmt);
                    return true;
                }

                break;
        }
        return false;
    }

    protected OscarStartTransactionStatement parseBegin() {
        OscarStartTransactionStatement stmt = new OscarStartTransactionStatement();
        if (lexer.token() == Token.START) {
            lexer.nextToken();
            acceptIdentifier("TRANSACTION");
        } else {
            accept(Token.BEGIN);
        }

        return stmt;
    }

    public SQLStatement parseConnectTo() {
        acceptIdentifier("CONNECT");
        accept(Token.TO);

        OscarConnectToStatement stmt = new OscarConnectToStatement();
        SQLName target = this.exprParser.name();
        stmt.setTarget(target);

        return stmt;
    }

    public OscarSelectStatement parseSelect() {
        OscarSelectParser selectParser = createSQLSelectParser();
        SQLSelect select = selectParser.select();
        return new OscarSelectStatement(select);
    }

    public SQLStatement parseWith() {
        SQLWithSubqueryClause with = this.parseWithQuery();
        // OscarWithClause with = this.parseWithClause();
        if (lexer.token() == Token.INSERT) {
            OscarInsertStatement stmt = this.parseInsert();
            stmt.setWith(with);
            return stmt;
        }

        if (lexer.token() == Token.SELECT) {
            OscarSelectStatement stmt = this.parseSelect();
            stmt.getSelect().setWithSubQuery(with);
            return stmt;
        }

        if (lexer.token() == Token.DELETE) {
            OscarDeleteStatement stmt = this.parseDeleteStatement();
            stmt.setWith(with);
            return stmt;
        }

        if (lexer.token() == Token.UPDATE) {
            OscarUpdateStatement stmt = (OscarUpdateStatement) this.parseUpdateStatement();
            stmt.setWith(with);
            return stmt;
        }

        throw new ParserException("TODO. " + lexer.info());
    }

    protected SQLAlterTableAlterColumn parseAlterColumn() {
        if (lexer.token() == Token.COLUMN) {
            lexer.nextToken();
        }

        SQLColumnDefinition column = this.exprParser.parseColumn();

        SQLAlterTableAlterColumn alterColumn = new SQLAlterTableAlterColumn();
        alterColumn.setColumn(column);

        if (column.getDataType() == null && column.getConstraints().isEmpty()) {
            if (lexer.token() == Token.SET) {
                lexer.nextToken();
                if (lexer.token() == Token.NOT) {
                    lexer.nextToken();
                    accept(Token.NULL);
                    alterColumn.setSetNotNull(true);
                } else {
                    accept(Token.DEFAULT);
                    SQLExpr defaultValue = this.exprParser.expr();
                    alterColumn.setSetDefault(defaultValue);
                }
            } else if (lexer.token() == Token.DROP) {
                lexer.nextToken();
                if (lexer.token() == Token.NOT) {
                    lexer.nextToken();
                    accept(Token.NULL);
                    alterColumn.setDropNotNull(true);
                } else {
                    accept(Token.DEFAULT);
                    alterColumn.setDropDefault(true);
                }
            }
        }
        return alterColumn;
    }

    public SQLStatement parseShow() {
        accept(Token.SHOW);
        OscarShowStatement stmt = new OscarShowStatement();
        switch (lexer.token()) {
        case ALL:
            stmt.setExpr(new SQLIdentifierExpr(Token.ALL.name()));
            lexer.nextToken();
            break;
        default:
            stmt.setExpr(this.exprParser.expr());
            break;
        }
        return stmt;
    }

    @Override
    public SQLStatement parseCommit() {
        SQLCommitStatement stmt = new SQLCommitStatement();
        stmt.setDbType(this.dbType);
        lexer.nextToken();
        return stmt;
    }

    @Override
    public SQLStatement parseSet() {
        accept(Token.SET);
        Token token = lexer.token();
        String range = "";

        SQLSetStatement.Option option = null;
        if (token == Token.SESSION) {
            lexer.nextToken();
            range = Token.SESSION.name();
            option = SQLSetStatement.Option.SESSION;
        } else if (token == Token.IDENTIFIER && LOCAL.equalsIgnoreCase(lexer.stringVal())) {
            range = LOCAL;
            option = SQLSetStatement.Option.LOCAL;
            lexer.nextToken();
        }

        long hash = lexer.hashLCase();
        String parameter = lexer.stringVal();
        SQLExpr paramExpr;
        List<SQLExpr> values = new ArrayList<SQLExpr>();
        if (hash == FnvHash.Constants.TIME) {
            lexer.nextToken();
            acceptIdentifier("ZONE");
            paramExpr = new SQLIdentifierExpr("TIME ZONE");
            String value = lexer.stringVal();
            if (lexer.token() == Token.IDENTIFIER) {
                values.add(new SQLIdentifierExpr(value.toUpperCase()));
            } else {
                values.add(new SQLCharExpr(value));
            }
            lexer.nextToken();
//            return new PGSetStatement(range, TIME_ZONE, exprs);
        } else if (hash == FnvHash.Constants.ROLE) {
            paramExpr = new SQLIdentifierExpr(parameter);
            lexer.nextToken();
            values.add(this.exprParser.primary());
            lexer.nextToken();
        } else {
            paramExpr = new SQLIdentifierExpr(parameter);
            lexer.nextToken();

            while (!lexer.isEOF()) {
                lexer.nextToken();
                if (lexer.token() == Token.LITERAL_CHARS) {
                    values.add(new SQLCharExpr(lexer.stringVal()));
                } else if (lexer.token() == Token.LITERAL_INT) {
                    values.add(new SQLIdentifierExpr(lexer.numberString()));
                } else if (lexer.identifierEquals(FnvHash.Constants.JSON_SET)
                        || lexer.identifierEquals(FnvHash.Constants.JSONB_SET)) {
                    SQLExpr json_set = this.exprParser.expr();
                    values.add(json_set);
                } else {
                    values.add(new SQLIdentifierExpr(lexer.stringVal()));
                }
                // skip comma
                lexer.nextToken();
            }
        }

        // value | 'value' | DEFAULT

        SQLExpr valueExpr;
        if (values.size() == 1) {
            valueExpr = values.get(0);
        } else {
            SQLListExpr listExpr = new SQLListExpr();
            for (SQLExpr value : values) {
                listExpr.addItem(value);
            }
            valueExpr = listExpr;
        }
        SQLSetStatement stmt = new SQLSetStatement(paramExpr, valueExpr, dbType);
        stmt.setOption(option);
        return stmt;
    }

    public SQLCreateIndexStatement parseCreateIndex(boolean acceptCreate) {
        if (acceptCreate) {
            accept(Token.CREATE);
        }

        SQLCreateIndexStatement stmt = new SQLCreateIndexStatement(getDbType());
        if (lexer.token() == Token.UNIQUE) {
            lexer.nextToken();
            if (lexer.identifierEquals("CLUSTERED")) {
                lexer.nextToken();
                stmt.setType("UNIQUE CLUSTERED");
            } else {
                stmt.setType("UNIQUE");
            }
        } else if (lexer.identifierEquals("FULLTEXT")) {
            stmt.setType("FULLTEXT");
            lexer.nextToken();
        } else if (lexer.identifierEquals("NONCLUSTERED")) {
            stmt.setType("NONCLUSTERED");
            lexer.nextToken();
        }

        accept(Token.INDEX);

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);
            stmt.setIfNotExists(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.CONCURRENTLY)) {
            lexer.nextToken();
            stmt.setConcurrently(true);
        }

        if (lexer.token() != Token.ON) {
            stmt.setName(this.exprParser.name());
        }

        accept(Token.ON);

        stmt.setTable(this.exprParser.name());

        if (lexer.token() == Token.USING) {
            lexer.nextToken();
            String using = lexer.stringVal();
            accept(Token.IDENTIFIER);
            stmt.setUsing(using);
        }

        accept(Token.LPAREN);

        for (;;) {
            SQLSelectOrderByItem item = this.exprParser.parseSelectOrderByItem();
            item.setParent(stmt);
            stmt.addItem(item);
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        accept(Token.RPAREN);

        if (lexer.token() == Token.WITH) {
            lexer.nextToken();
            accept(Token.LPAREN);

            for (;;) {
                String optionName = lexer.stringVal();
                accept(Token.IDENTIFIER);
                accept(Token.EQ);
                SQLExpr option = this.exprParser.expr();
                option.setParent(stmt);

                stmt.addOption(optionName, option);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.TABLESPACE) {
            lexer.nextToken();
            SQLName tablespace = this.exprParser.name();
            stmt.setTablespace(tablespace);
        }

        return stmt;
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new OscarCreateTableParser(this.exprParser);
    }

    public SQLCreateFunctionStatement parseCreateFunction() {
        SQLCreateFunctionStatement stmt = (SQLCreateFunctionStatement) parseFunction();
        stmt.setCreate(true);
        return stmt;
    }

    public SQLStatement parseFunction() {
        SQLCreateFunctionStatement stmt = new SQLCreateFunctionStatement();
        stmt.setDbType(dbType);

        if (lexer.token() == Token.CREATE) {
            lexer.nextToken();
            if (lexer.token() == Token.OR) {
                lexer.nextToken();
                accept(Token.REPLACE);
                stmt.setOrReplace(true);
            }
        } else {
            if (lexer.token() == Token.DECLARE) {
                lexer.nextToken();
            }
            stmt.setCreate(false);
        }

        accept(Token.FUNCTION);

        SQLName functionName = this.exprParser.name();
        stmt.setName(functionName);

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            parserParameters(stmt.getParameters(), stmt);
            accept(Token.RPAREN);
        }

        if (lexer.identifierEquals(FnvHash.Constants.WRAPPED)) {
            lexer.nextToken();
            int pos = lexer.text.indexOf(';', lexer.pos());
            if (pos != -1) {
                String wrappedString = lexer.subString(lexer.pos(), pos - lexer.pos());
                stmt.setWrappedSource(wrappedString);
                lexer.reset(pos, ';', Token.LITERAL_CHARS);
                lexer.nextToken();
                stmt.setAfterSemi(true);
            } else {
                String wrappedString = lexer.text.substring(lexer.pos());
                stmt.setWrappedSource(wrappedString);
                lexer.reset(lexer.text.length(), (char) LayoutCharacters.EOI, Token.EOF);
                return stmt;
            }

            return stmt;
        }

        accept(Token.RETURN);
        SQLDataType returnDataType = this.exprParser.parseDataType(false);
        stmt.setReturnDataType(returnDataType);

        if (identifierEquals("PIPELINED")) {
            lexer.nextToken();
            stmt.setPipelined(true);
        }

        if (identifierEquals("DETERMINISTIC")) {
            lexer.nextToken();
            stmt.setDeterministic(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.AUTHID)) {
            lexer.nextToken();
            String strVal = lexer.stringVal();
            if (lexer.identifierEquals(FnvHash.Constants.CURRENT_USER)) {
                lexer.nextToken();
            } else {
                acceptIdentifier("DEFINER");
            }
            SQLName authid = new SQLIdentifierExpr(strVal);
            stmt.setAuthid(authid);
        }

        if (identifierEquals("RESULT_CACHE")) {
            lexer.nextToken();
            stmt.setResultCache(true);
        }

        if (lexer.token() == Token.SEMI) {
            lexer.nextToken();
            return stmt;
        }

        if (lexer.token() == Token.IS || lexer.token() == Token.AS) {
            lexer.nextToken();
        }

        if (lexer.identifierEquals("LANGUAGE")) {
            lexer.nextToken();
            if (lexer.identifierEquals("JAVA")) {
                lexer.nextToken();
                acceptIdentifier("NAME");
                String javaCallSpec = lexer.stringVal();
                accept(Token.LITERAL_CHARS);
                stmt.setJavaCallSpec(javaCallSpec);
            } else {
                throw new ParserException("TODO : " + lexer.info());
            }
            return stmt;
        }

        if (lexer.identifierEquals("PARALLEL_ENABLE")) {
            lexer.nextToken();
            stmt.setParallelEnable(true);
        }

        if (lexer.identifierEquals("AGGREGATE")) {
            lexer.nextToken();
            stmt.setAggregate(true);
        }

        if (lexer.token() == Token.USING) {
            lexer.nextToken();
            SQLName using = this.exprParser.name();
            stmt.setUsing(using);
        }

//        SQLStatement block;
//        if (lexer.token() == Token.BEGIN) {
//            block = this.parseBlock();
//        } else {
//            block = this.parseStatement();
//        }
        SQLStatement block;
        if (lexer.token() == Token.SEMI) {
            stmt.setAfterSemi(true);
            lexer.nextToken();
            block = null;
        } else {
            block = this.parseBlock();
        }

        stmt.setBlock(block);

        if (lexer.identifierEquals(functionName.getSimpleName())) {
            lexer.nextToken();
        }

        // return stmt;

        if (lexer.identifierEquals(functionName.getSimpleName())) {
            lexer.nextToken();
        }

        return stmt;
    }

    public SQLBlockStatement parseBlock() {
        SQLBlockStatement block = new SQLBlockStatement();
        block.setDbType(dbType);

        accept(Token.BEGIN);
        List<SQLStatement> statementList = block.getStatementList();
        this.parseStatementList(statementList, -1, block);

        if (lexer.token() != Token.END
                && statementList.size() > 0
                && (statementList.get(statementList.size() - 1) instanceof SQLCommitStatement
                || statementList.get(statementList.size() - 1) instanceof SQLRollbackStatement)) {
            block.setEndOfCommit(true);
            return block;
        }
        accept(Token.END);

        return block;
    }

    private void parserParameters(List<SQLParameter> parameters, SQLObject parent) {
        for (; ; ) {
            SQLParameter parameter = new SQLParameter();
            parameter.setParent(parent);

            if (parent instanceof OracleCreateTypeStatement) {
                if (lexer.identifierEquals(FnvHash.Constants.MAP)) {
                    lexer.nextToken();
                    parameter.setMap(true);
                } else if (lexer.token() == Token.ORDER) {
                    lexer.nextToken();
                    parameter.setOrder(true);
                }

                // acceptIdentifier("MEMBER");
            }

            SQLName name;
            SQLDataType dataType = null;
            if (lexer.token() == Token.CURSOR) {
                lexer.nextToken();

                dataType = new SQLDataTypeImpl();
                dataType.setName("CURSOR");

                name = this.exprParser.name();

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    this.parserParameters(parameter.getCursorParameters(), parameter);
                    accept(Token.RPAREN);
                }

                accept(Token.IS);
                SQLSelect select = this.createSQLSelectParser().select();
                parameter.setDefaultValue(new SQLQueryExpr(select));

            } else if (lexer.token() == Token.PROCEDURE
                    || lexer.token() == Token.END
                    || lexer.token() == Token.TABLE) {
                break;
            } else if (lexer.identifierEquals(FnvHash.Constants.TYPE)) {
                lexer.nextToken();
                name = this.exprParser.name();
                accept(Token.IS);

                if (lexer.identifierEquals("REF")) {
                    lexer.nextToken();
                    accept(Token.CURSOR);

                    dataType = new SQLDataTypeImpl("REF CURSOR");
                    dataType.setDbType(dbType);
                } else if (lexer.token() == Token.TABLE) {
                    lexer.nextToken();
                    accept(Token.OF);

                    name = this.exprParser.name();

                    if (lexer.token() == Token.PERCENT) {
                        lexer.nextToken();

                        String typeName;
                        if (lexer.identifierEquals(FnvHash.Constants.ROWTYPE)) {
                            lexer.nextToken();
                            typeName = "TABLE OF " + name.toString() + "%ROWTYPE";
                        } else {
                            acceptIdentifier("TYPE");
                            typeName = "TABLE OF " + name.toString() + "%TYPE";
                        }

                        dataType = new SQLDataTypeImpl(typeName);
                    } else if (lexer.token() == Token.LPAREN) {
                        lexer.nextToken();
                        String typeName = name.toString();

                        SQLIntegerExpr lenExpr = (SQLIntegerExpr) this.exprParser.expr();
                        int len = lenExpr.getNumber().intValue();
                        dataType = new SQLDataTypeImpl(typeName, len);
                        accept(Token.RPAREN);

                        if (lexer.token() == Token.INDEX) {
                            lexer.nextToken();
                            accept(Token.BY);
                            SQLExpr indexBy = this.exprParser.primary();
                            ((SQLDataTypeImpl) dataType).setIndexBy(indexBy);
                        }
                    }
                    dataType.setDbType(dbType);
                } else if (lexer.identifierEquals("VARRAY")) {
                    lexer.nextToken();
                    accept(Token.LPAREN);
                    int len = this.exprParser.acceptInteger();
                    accept(Token.RPAREN);
                    accept(Token.OF);

                    if (lexer.identifierEquals("NUMBER")) {
                        lexer.nextToken();
                        String typeName = "VARRAY(" + len + ") OF NUMBER";

                        if (lexer.token() == Token.LPAREN) {
                            accept(Token.LPAREN);
                            int numLen = this.exprParser.acceptInteger();
                            accept(Token.RPAREN);
                            typeName += "(" + numLen + ")";
                        }
                        dataType = new SQLDataTypeImpl(typeName);
                        dataType.setDbType(dbType);
                    } else if (lexer.identifierEquals("VARCHAR2")) {
                        lexer.nextToken();
                        String typeName = "VARRAY(" + len + ") OF VARCHAR2";
                        dataType = new SQLDataTypeImpl(typeName);
                        dataType.setDbType(dbType);

                        if (lexer.token() == Token.LPAREN) {
                            lexer.nextToken();
                            this.exprParser.exprList(dataType.getArguments(), dataType);
                            accept(Token.RPAREN);
                        }
                    } else {
                        throw new ParserException("TODO : " + lexer.info());
                    }
                } else {
                    throw new ParserException("TODO : " + lexer.info());
                }
            } else {
                if (lexer.token() == Token.KEY) {
                    name = new SQLIdentifierExpr(lexer.stringVal());
                    lexer.nextToken();
                } else {
                    name = this.exprParser.name();
                }

                if (lexer.token() == Token.IN) {
                    lexer.nextToken();

                    if (lexer.token() == Token.OUT) {
                        lexer.nextToken();
                        parameter.setParamType(SQLParameter.ParameterType.INOUT);
                    } else {
                        parameter.setParamType(SQLParameter.ParameterType.IN);
                    }
                } else if (lexer.token() == Token.OUT) {
                    lexer.nextToken();

                    if (lexer.token() == Token.IN) {
                        lexer.nextToken();
                        parameter.setParamType(SQLParameter.ParameterType.INOUT);
                    } else {
                        parameter.setParamType(SQLParameter.ParameterType.OUT);
                    }
                } else if (lexer.token() == Token.INOUT) {
                    lexer.nextToken();
                    parameter.setParamType(SQLParameter.ParameterType.INOUT);
                }

                if (lexer.identifierEquals("NOCOPY")) {
                    lexer.nextToken();
                    parameter.setNoCopy(true);
                }

                if (lexer.identifierEquals("CONSTANT")) {
                    lexer.nextToken();
                    parameter.setConstant(true);
                }

                if ((name.nameHashCode64() == FnvHash.Constants.MEMBER
                        || name.nameHashCode64() == FnvHash.Constants.STATIC)
                        && lexer.token() == Token.FUNCTION) {
                    if (name.nameHashCode64() == FnvHash.Constants.MEMBER) {
                        parameter.setMember(true);
                    }
                    OracleFunctionDataType functionDataType = new OracleFunctionDataType();
                    functionDataType.setStatic(name.nameHashCode64() == FnvHash.Constants.STATIC);
                    lexer.nextToken();
                    functionDataType.setName(lexer.stringVal());
                    accept(Token.IDENTIFIER);
                    if (lexer.token() == Token.LPAREN) {
                        lexer.nextToken();
                        this.parserParameters(functionDataType.getParameters(), functionDataType);
                        accept(Token.RPAREN);
                    }
                    accept(Token.RETURN);
                    functionDataType.setReturnDataType(this.exprParser.parseDataType(false));
                    dataType = functionDataType;
                    name = null;

                    if (lexer.token() == Token.IS) {
                        lexer.nextToken();
                        SQLStatement block = this.parseBlock();
                        functionDataType.setBlock(block);
                    }
                } else if ((name.nameHashCode64() == FnvHash.Constants.MEMBER
                        || name.nameHashCode64() == FnvHash.Constants.STATIC)
                        && lexer.token() == Token.PROCEDURE) {
                    if (name.nameHashCode64() == FnvHash.Constants.MEMBER) {
                        parameter.setMember(true);
                    }
                    OracleProcedureDataType procedureDataType = new OracleProcedureDataType();
                    procedureDataType.setStatic(name.nameHashCode64() == FnvHash.Constants.STATIC);
                    lexer.nextToken();
                    procedureDataType.setName(lexer.stringVal());
                    accept(Token.IDENTIFIER);
                    if (lexer.token() == Token.LPAREN) {
                        lexer.nextToken();
                        this.parserParameters(procedureDataType.getParameters(), procedureDataType);
                        accept(Token.RPAREN);
                    }

                    dataType = procedureDataType;
                    name = null;

                    if (lexer.token() == Token.IS) {
                        lexer.nextToken();
                        SQLStatement block = this.parseBlock();
                        procedureDataType.setBlock(block);
                    }
                } else {
                    dataType = this.exprParser.parseDataType(false);
                }
                if (lexer.token() == Token.COLONEQ || lexer.token() == Token.DEFAULT) {
                    lexer.nextToken();
                    parameter.setDefaultValue(this.exprParser.expr());
                }
            }

            parameter.setName(name);
            parameter.setDataType(dataType);

            parameters.add(parameter);
            Token token = lexer.token();
            if (token == Token.COMMA || token == Token.SEMI || token == Token.IS) {
                lexer.nextToken();
            }

            token = lexer.token();
            if (token != Token.BEGIN
                    && token != Token.RPAREN
                    && token != Token.EOF
                    && token != Token.FUNCTION
                    && !lexer.identifierEquals("DETERMINISTIC")) {
                continue;
            }

            break;
        }
    }

    public SQLStatement parseIf() {
        accept(Token.IF);

        SQLIfStatement stmt = new SQLIfStatement();
        stmt.setDbType(dbType);

        stmt.setCondition(this.exprParser.expr());

        accept(Token.THEN);

        this.parseStatementList(stmt.getStatements(), -1, stmt);

        while (lexer.token() == Token.ELSIF) {
            lexer.nextToken();

            SQLIfStatement.ElseIf elseIf = new SQLIfStatement.ElseIf();

            elseIf.setCondition(this.exprParser.expr());
            elseIf.setParent(stmt);

            accept(Token.THEN);
            this.parseStatementList(elseIf.getStatements(), -1, stmt);

            stmt.getElseIfList().add(elseIf);
        }

        if (lexer.token() == Token.ELSE) {
            lexer.nextToken();

            SQLIfStatement.Else elseItem = new SQLIfStatement.Else();
            this.parseStatementList(elseItem.getStatements(), -1, elseItem);
            stmt.setElseItem(elseItem);
        }

        accept(Token.END);
//        if (lexer.token() != Token.SEMI) {
//            accept(Token.IF);
//        }
        accept(Token.IF);
//        if (lexer.token() == Token.SEMI) {
//            lexer.nextToken();
//        }
        accept(Token.SEMI);
        stmt.setAfterSemi(true);

        return stmt;
    }

    public SQLStatement parseCase() {
        SQLCaseStatement caseStmt = new SQLCaseStatement();
        caseStmt.setDbType(dbType);
        lexer.nextToken();
        if (lexer.token() != Token.WHEN) {
            caseStmt.setValueExpr(this.exprParser.expr());
        }

        accept(Token.WHEN);
        SQLExpr testExpr = this.exprParser.expr();
        accept(Token.THEN);
        SQLStatement stmt = this.parseStatement();
        if (lexer.token() == Token.SEMI) {
            lexer.nextToken();
        }
        SQLCaseStatement.Item caseItem = new SQLCaseStatement.Item(testExpr, stmt);
        caseStmt.addItem(caseItem);

        while (lexer.token() == Token.WHEN) {
            lexer.nextToken();
            testExpr = this.exprParser.expr();
            accept(Token.THEN);
            stmt = this.parseStatement();
            if (lexer.token() == Token.SEMI) {
                lexer.nextToken();
            }
            caseItem = new SQLCaseStatement.Item(testExpr, stmt);
            caseStmt.addItem(caseItem);
        }

        if (lexer.token() == Token.ELSE) {
            lexer.nextToken();
            this.parseStatementList(caseStmt.getElseStatements(), -1, caseStmt);
        }

        accept(Token.END);
        accept(Token.CASE);
        accept(Token.SEMI);
        return caseStmt;
    }
}

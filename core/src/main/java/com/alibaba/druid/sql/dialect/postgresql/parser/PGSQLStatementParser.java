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
package com.alibaba.druid.sql.dialect.postgresql.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.*;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcUtils;

import java.util.ArrayList;
import java.util.List;

public class PGSQLStatementParser extends SQLStatementParser {
    public static final String TIME_ZONE = "TIME ZONE";
    public static final String TIME = "TIME";
    public static final String LOCAL = "LOCAL";

    public PGSQLStatementParser(PGExprParser parser) {
        super(parser);
    }

    public PGSQLStatementParser(String sql) {
        super(new PGExprParser(sql));
    }

    public PGSQLStatementParser(String sql, SQLParserFeature... features) {
        super(new PGExprParser(sql, features));
    }

    public PGSQLStatementParser(Lexer lexer) {
        super(new PGExprParser(lexer));
    }

    public PGSelectParser createSQLSelectParser() {
        return new PGSelectParser(this.exprParser, selectListCache);
    }

    public SQLUpdateStatement parseUpdateStatement() {
        accept(Token.UPDATE);

        PGUpdateStatement updateStatement = new PGUpdateStatement();

        SQLSelectParser selectParser = this.exprParser.createSelectParser();
        SQLTableSource tableSource = selectParser.parseTableSource();
        updateStatement.setTableSource(tableSource);

        parseUpdateSet(updateStatement);

        if (lexer.token() == Token.FROM) {
            lexer.nextToken();
            SQLTableSource from = selectParser.parseTableSource();
            updateStatement.setFrom(from);
        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            updateStatement.setWhere(this.exprParser.expr());
        }

        if (lexer.token() == Token.RETURNING) {
            lexer.nextToken();

            for (; ; ) {
                updateStatement.getReturning().add(this.exprParser.expr());
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
        }

        return updateStatement;
    }

    public PGInsertStatement parseInsert() {
        PGInsertStatement stmt = new PGInsertStatement();

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

            for (; ; ) {
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

                        for (; ; ) {
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

    public PGCreateSchemaStatement parseCreateSchema() {
        accept(Token.CREATE);
        accept(Token.SCHEMA);

        PGCreateSchemaStatement stmt = new PGCreateSchemaStatement();
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

        PGAlterSchemaStatement stmt = new PGAlterSchemaStatement();
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

    public PGDropSchemaStatement parseDropSchema() {
        PGDropSchemaStatement stmt = new PGDropSchemaStatement();

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

    public PGDeleteStatement parseDeleteStatement() {
        lexer.nextToken();
        PGDeleteStatement deleteStatement = new PGDeleteStatement();

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
                PGStartTransactionStatement stmt = parseBegin();
                statementList.add(stmt);
                return true;
            }
            case END: {
                PGEndTransactionStatement stmt = parseEnd();
                statementList.add(stmt);
                return true;
            }
            case WITH:
                statementList.add(parseWith());
                return true;
            case DO:
                PGDoStatement pgDoStatement = parseDo();
                statementList.add(pgDoStatement);
                return true;
            default:
                if (lexer.identifierEquals(FnvHash.Constants.CONNECT)) {
                    SQLStatement stmt = parseConnectTo();
                    statementList.add(stmt);
                    return true;
                }

                break;
        }

        String strVal = lexer.stringVal();
        if (strVal.equalsIgnoreCase("ANALYZE")) {
            PGAnalyzeStatement stmt = this.parseAnalyzeTable();
            statementList.add(stmt);
            return true;
        }
        if (strVal.equalsIgnoreCase("VACUUM")) {
            PGVacuumStatement stmt = this.parseVacuumTable();
            statementList.add(stmt);
            return true;
        }
        return false;
    }

    public PGDoStatement parseDo() {
        PGDoStatement stmt = new PGDoStatement();
        stmt.setDbType(dbType);

        accept(Token.DO);

        stmt.setFuncName(this.exprParser.name());

        if (lexer.token() == Token.DECLARE) {
            parseVariables(stmt);
        }

        SQLStatement block;
        if (lexer.token() == Token.BEGIN) {
            block = this.parseBlock();
        } else {
            block = this.parseStatement();
        }
        stmt.setBlock(block);
        if (lexer.token() == Token.IDENTIFIER) {
            SQLName endFuncName = this.exprParser.name();
            if (!stmt.getFuncName().equals(endFuncName)) {
                printError(lexer.token());
            }
        }
        return stmt;
    }

    public void parseVariables(PGDoStatement stmt) {
        accept(Token.DECLARE);
        if (lexer.token() != Token.BEGIN) {
            // todo: parseVariables
            throw new ParserException("TODO " + lexer.info());
        }
    }

    public SQLBlockStatement parseBlock() {
        SQLBlockStatement block = new SQLBlockStatement();
        block.setDbType(dbType);
        block.setHaveBeginEnd(false);
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

    @Override
    public SQLStatement parseIf() {
        accept(Token.IF);
        SQLIfStatement stmt = new SQLIfStatement();
        stmt.setCondition(this.exprParser.expr());
        accept(Token.THEN);
        this.parseStatementList(stmt.getStatements(), -1, stmt);
        while (lexer.token() == Token.ELSE) {
            lexer.nextToken();
            if (lexer.token() == Token.IF) {
                lexer.nextToken();
                SQLIfStatement.ElseIf elseIf = new SQLIfStatement.ElseIf();
                elseIf.setCondition(this.exprParser.expr());
                elseIf.setParent(stmt);
                accept(Token.THEN);
                this.parseStatementList(elseIf.getStatements(), -1, elseIf);
                stmt.getElseIfList().add(elseIf);
            } else {
                SQLIfStatement.Else elseItem = new SQLIfStatement.Else();
                this.parseStatementList(elseItem.getStatements(), -1, elseItem);
                stmt.setElseItem(elseItem);
                break;
            }
        }
        accept(Token.END);
        accept(Token.IF);
        accept(Token.SEMI);
        stmt.setAfterSemi(true);
        return stmt;
    }

    protected PGStartTransactionStatement parseBegin() {
        PGStartTransactionStatement stmt = new PGStartTransactionStatement();
        if (lexer.token() == Token.START) {
            lexer.nextToken();
            acceptIdentifier("TRANSACTION");
        } else {
            accept(Token.BEGIN);
            stmt.setUseBegin(true);
        }

        return stmt;
    }

    @Override
    public PGEndTransactionStatement parseEnd() {
        PGEndTransactionStatement stmt = new PGEndTransactionStatement();
        accept(Token.END);
        return stmt;
    }
    public SQLStatement parseAlter() {
        Lexer.SavePoint mark = lexer.mark();
        accept(Token.ALTER);
        if (lexer.token() == Token.DATABASE) {
            lexer.nextToken();
            return parseAlterDatabase();
        }
        lexer.reset(mark);
        return super.parseAlter();
    }
    public SQLStatement parseConnectTo() {
        acceptIdentifier("CONNECT");
        accept(Token.TO);

        PGConnectToStatement stmt = new PGConnectToStatement();
        SQLName target = this.exprParser.name();
        stmt.setTarget(target);

        return stmt;
    }

    public PGSelectStatement parseSelect() {
        PGSelectParser selectParser = createSQLSelectParser();
        SQLSelect select = selectParser.select();
        return new PGSelectStatement(select);
    }

    public SQLStatement parseWith() {
        SQLWithSubqueryClause with = this.parseWithQuery();
        // PGWithClause with = this.parseWithClause();
        if (lexer.token() == Token.INSERT) {
            PGInsertStatement stmt = this.parseInsert();
            stmt.setWith(with);
            return stmt;
        }

        if (lexer.token() == Token.SELECT) {
            PGSelectStatement stmt = this.parseSelect();
            stmt.getSelect().setWithSubQuery(with);
            return stmt;
        }

        if (lexer.token() == Token.DELETE) {
            PGDeleteStatement stmt = this.parseDeleteStatement();
            stmt.setWith(with);
            return stmt;
        }

        if (lexer.token() == Token.UPDATE) {
            PGUpdateStatement stmt = (PGUpdateStatement) this.parseUpdateStatement();
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
        column.setDbType(dbType);
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
        PGShowStatement stmt = new PGShowStatement(dbType);
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
        } else if (JdbcUtils.isPgsqlDbType(dbType) && ("schema".equalsIgnoreCase(parameter) || "names".equalsIgnoreCase(parameter))) {
            paramExpr = new SQLIdentifierExpr(parameter);
            lexer.nextToken();
            String value = lexer.stringVal();
            values.add(new SQLCharExpr(value));
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
        stmt.setUseSet(true);
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

        for (; ; ) {
            SQLSelectOrderByItem item;
            item = this.exprParser.parseSelectOrderByItem();

            if (lexer.identifierEquals("jsonb_path_ops") && item.getExpr() instanceof SQLIdentifierExpr) {
                String ident = ((SQLIdentifierExpr) item.getExpr()).getName() + " " + lexer.stringVal();
                lexer.nextToken();
                item.setExpr(new SQLIdentifierExpr(ident));
            }

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

            for (; ; ) {
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
        return new PGCreateTableParser(this.exprParser);
    }

    public PGAnalyzeStatement parseAnalyzeTable() {
        PGAnalyzeStatement stmt = new PGAnalyzeStatement(this.dbType);
        acceptIdentifier("ANALYZE");
        Lexer.SavePoint mark = lexer.mark();
        String strVal = lexer.stringVal();
        for (; ; ) {
            if (strVal.equalsIgnoreCase("VERBOSE")) {
                stmt.setVerbose(true);
                lexer.nextToken();
                mark = lexer.mark();
                strVal = lexer.stringVal();
                continue;
            } else if (strVal.equalsIgnoreCase("SKIP_LOCKED")) {
                stmt.setSkipLocked(true);
                lexer.nextToken();
                mark = lexer.mark();
                strVal = lexer.stringVal();
                continue;
            } else {
                lexer.reset(mark);
                break;
            }
        }
        List<SQLName> names = new ArrayList<SQLName>();
        this.exprParser.names(names, stmt);

        for (SQLName name : names) {
            SQLExprTableSource sqlExprTableSource = new SQLExprTableSource(name);
            sqlExprTableSource.setParent(stmt);
            stmt.getTableSources().add(sqlExprTableSource);
        }
        return stmt;
    }

    public PGVacuumStatement parseVacuumTable() {
        PGVacuumStatement stmt = new PGVacuumStatement(this.dbType);
        acceptIdentifier("VACUUM");
        Lexer.SavePoint mark = lexer.mark();
        String strVal = lexer.stringVal();
        for (; ; ) {
            if (Token.SEMI.equals(lexer.token())) {
                stmt.setAfterSemi(true);
                return stmt;
            }
            if (lexer.isEOF()) {
                lexer.nextToken();
                return stmt;
            }
            if (strVal.equalsIgnoreCase("FULL")) {
                stmt.setFull(true);
                lexer.nextToken();
                mark = lexer.mark();
                strVal = lexer.stringVal();
                continue;
            } else if (strVal.equalsIgnoreCase("FREEZE")) {
                stmt.setFreeze(true);
                lexer.nextToken();
                mark = lexer.mark();
                strVal = lexer.stringVal();
                continue;
            } else if (strVal.equalsIgnoreCase("VERBOSE")) {
                stmt.setVerbose(true);
                lexer.nextToken();
                mark = lexer.mark();
                strVal = lexer.stringVal();
                continue;
            } else if (strVal.equalsIgnoreCase("ANALYZE")) {
                stmt.setAnalyze(true);
                lexer.nextToken();
                mark = lexer.mark();
                strVal = lexer.stringVal();
                continue;
            } else if (strVal.equalsIgnoreCase("DISABLE_PAGE_SKIPPING")) {
                stmt.setDisablePageSkipping(true);
                lexer.nextToken();
                mark = lexer.mark();
                strVal = lexer.stringVal();
                continue;
            } else if (strVal.equalsIgnoreCase("SKIP_LOCKED")) {
                stmt.setSkipLocked(true);
                lexer.nextToken();
                mark = lexer.mark();
                strVal = lexer.stringVal();
                continue;
            } else if (strVal.equalsIgnoreCase("PROCESS_TOAST")) {
                stmt.setProcessToast(true);
                lexer.nextToken();
                mark = lexer.mark();
                strVal = lexer.stringVal();
                continue;
            } else if (strVal.equalsIgnoreCase("TRUNCATE")) {
                stmt.setTruncate(true);
                lexer.nextToken();
                mark = lexer.mark();
                strVal = lexer.stringVal();
                continue;
            } else {
                lexer.reset(mark);
                break;
            }
        }
        List<SQLName> names = new ArrayList<SQLName>();
        this.exprParser.names(names, stmt);
        for (SQLName name : names) {
            SQLExprTableSource sqlExprTableSource = new SQLExprTableSource(name);
            sqlExprTableSource.setParent(stmt);
            stmt.getTableSources().add(sqlExprTableSource);
        }
        return stmt;
    }

    public PGAlterDatabaseStatement parseAlterDatabase() {
        PGAlterDatabaseStatement stmt = new PGAlterDatabaseStatement(this.getDbType());
        stmt.setDatabaseName(this.exprParser.identifier());
        if ("RENAME".equalsIgnoreCase(lexer.stringVal())) {
            lexer.nextToken();
            accept(Token.TO);
            stmt.setRenameToName(this.exprParser.identifier());
        }
        if ("OWNER".equalsIgnoreCase(lexer.stringVal())) {
            lexer.nextToken();
            accept(Token.TO);
            stmt.setOwnerToName(this.exprParser.identifier());
        }
        if ("REFRESH".equalsIgnoreCase(lexer.stringVal())) {
            lexer.nextToken();
            acceptIdentifier("COLLATION");
            acceptIdentifier("VERSION");
            stmt.setRefreshCollationVersion(true);
        }
        if (Token.SET.equals(lexer.token())) {
            lexer.nextToken();
            if (Token.TABLESPACE.equals(lexer.token())) {
                lexer.nextToken();
                stmt.setSetTableSpaceName(this.exprParser.identifier());
            } else {
                stmt.setSetParameterName(this.exprParser.identifier());
                if (Token.TO.equals(lexer.token())) {
                    lexer.nextToken();
                    stmt.setSetParameterValue(this.exprParser.expr());
                } else {
                    accept(Token.EQ);
                    stmt.setUseEquals(true);
                    stmt.setSetParameterValue(this.exprParser.expr());
                }
            }
        }
        if ("RESET".equalsIgnoreCase(lexer.stringVal())) {
            lexer.nextToken();
            stmt.setResetParameterName(this.exprParser.identifier());
        }
        return stmt;
    }

    @Override
    public SQLStatement parseCreateUser() {
        accept(Token.CREATE);
        accept(Token.USER);
        SQLCreateUserStatement stmt = new SQLCreateUserStatement();
        stmt.setDbType(dbType);
        stmt.setUser(this.exprParser.name());
        if (lexer.token() == Token.WITH) {
            accept(Token.WITH);
            stmt.setPostgresqlWith(true);
        }
        if (lexer.identifierEquals("ENCRYPTED")) {
            stmt.setPostgresqlEncrypted(true);
            lexer.nextToken();
        }
        if (lexer.identifierEquals("PASSWORD")) {
            lexer.nextToken();
        }
        stmt.setPassword(this.exprParser.primary());
        return stmt;
    }
}

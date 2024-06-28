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
package com.alibaba.druid.sql.dialect.odps.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsert;
import com.alibaba.druid.sql.dialect.hive.ast.HiveMultiInsertStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateFunctionStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveLoadDataStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlKillStatement;
import com.alibaba.druid.sql.dialect.odps.ast.*;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

import java.util.List;

import static com.alibaba.druid.sql.parser.Token.IDENTIFIER;
import static com.alibaba.druid.sql.parser.Token.OVERWRITE;

public class OdpsStatementParser extends SQLStatementParser {
    public OdpsStatementParser(String sql) {
        super(new OdpsExprParser(sql));
    }

    public OdpsStatementParser(String sql, SQLParserFeature... features) {
        super(new OdpsExprParser(sql, features));
    }

    public OdpsStatementParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public SQLSelectStatement parseSelect() {
        SQLSelect select = new OdpsSelectParser(this.exprParser)
                .select();

//        if (select.getWithSubQuery() == null && select.getQuery() instanceof SQLSelectQueryBlock) {
//            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) select.getQuery();
//            if (queryBlock.getFrom() == null && queryBlock.getWhere() != null) {
//                throw new ParserException("none from query not support where clause.");
//            }
//        }

        return new SQLSelectStatement(select, DbType.odps);
    }

    public SQLCreateTableStatement parseCreateTable() {
        SQLCreateTableParser parser = new OdpsCreateTableParser(this.exprParser);
        return parser.parseCreateTable();
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new OdpsCreateTableParser(this.exprParser);
    }

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.token() == Token.FROM) {
            SQLStatement stmt = this.parseInsert();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("ANALYZE")) {
            SQLStatement stmt = parseAnalyze();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("ADD")) {
            lexer.nextToken();

            if (lexer.identifierEquals("STATISTIC")) {
                lexer.nextToken();
                OdpsAddStatisticStatement stmt = new OdpsAddStatisticStatement();
                stmt.setTable(this.exprParser.name());
                stmt.setStatisticClause(parseStaticClause());
                statementList.add(stmt);
                return true;
            }

            if (lexer.token() == Token.USER) {
                lexer.nextToken();
                OdpsAddUserStatement stmt = new OdpsAddUserStatement();
                stmt.setUser(this.exprParser.name());
                statementList.add(stmt);
                return true;
            }

            if (lexer.identifierEquals("ACCOUNTPROVIDER")) {
                lexer.nextToken();
                OdpsAddAccountProviderStatement stmt = new OdpsAddAccountProviderStatement();
                stmt.setProvider(this.exprParser.name());
                statementList.add(stmt);
                return true;
            }

            if (lexer.token() == Token.TABLE) {
                lexer.nextToken();
                OdpsAddTableStatement stmt = new OdpsAddTableStatement();
                stmt.setTable(this.exprParser.name());

                if (lexer.token() == Token.PARTITION) {
                    lexer.nextToken();
                    this.exprParser.parseAssignItem(stmt.getPartitions(), stmt);
                }

                if (lexer.token() == Token.AS) {
                    lexer.nextToken();
                    SQLName name = this.exprParser.name();
                    stmt.getTable().setAlias(name.toString());
                }

                if (lexer.token() == Token.COMMENT) {
                    lexer.nextToken();
                    stmt.setComment(this.exprParser.primary());
                }

                if (lexer.token() == Token.SUB) {
                    lexer.nextToken();
                    acceptIdentifier("f");
                    stmt.setForce(true);
                }

                if (lexer.token() == Token.TO) {
                    lexer.nextToken();
                    acceptIdentifier("PACKAGE");
                    SQLName packageName = this.exprParser.name();
                    stmt.setToPackage(packageName);

                    if (lexer.token() == Token.WITH) {
                        lexer.nextToken();
                        acceptIdentifier("PRIVILEGES");
                        parsePrivilege(stmt.getPrivileges(), stmt);
                    }
                }

                statementList.add(stmt);
                return true;
            }

            if (lexer.identifierEquals(FnvHash.Constants.FILE)
                    || lexer.identifierEquals(FnvHash.Constants.JAR)
                    || lexer.identifierEquals(FnvHash.Constants.PY)
                    || lexer.identifierEquals(FnvHash.Constants.ARCHIVE)) {
                OdpsAddFileStatement stmt = new OdpsAddFileStatement();

                long hash = lexer.hashLCase();
                if (hash == FnvHash.Constants.JAR) {
                    stmt.setType(OdpsAddFileStatement.FileType.JAR);
                } else if (hash == FnvHash.Constants.PY) {
                    stmt.setType(OdpsAddFileStatement.FileType.PY);
                } else if (hash == FnvHash.Constants.ARCHIVE) {
                    stmt.setType(OdpsAddFileStatement.FileType.ARCHIVE);
                }

                lexer.nextPath();
                String path = lexer.stringVal();

                lexer.nextToken();

                stmt.setFile(path);

                if (lexer.token() == Token.AS) {
                    lexer.nextToken();
                    SQLName name = this.exprParser.name();
                    stmt.setAlias(name.toString());
                }

                if (lexer.token() == Token.COMMENT) {
                    lexer.nextToken();
                    stmt.setComment(this.exprParser.primary());
                }

                if (lexer.token() == Token.SUB) {
                    lexer.nextToken();
                    acceptIdentifier("f");
                    stmt.setForce(true);
                }
                statementList.add(stmt);
                return true;
            }

            throw new ParserException("TODO " + lexer.info());
        }

        if (lexer.identifierEquals("REMOVE")) {
            lexer.nextToken();

            if (lexer.identifierEquals("STATISTIC")) {
                lexer.nextToken();
                OdpsRemoveStatisticStatement stmt = new OdpsRemoveStatisticStatement();
                stmt.setTable(this.exprParser.name());
                stmt.setStatisticClause(parseStaticClause());
                statementList.add(stmt);
                return true;
            }

            if (lexer.token() == Token.USER) {
                lexer.nextToken();
                OdpsRemoveUserStatement stmt = new OdpsRemoveUserStatement();
                stmt.setUser((SQLIdentifierExpr) this.exprParser.name());
                statementList.add(stmt);
                return true;
            }

            throw new ParserException("TODO " + lexer.info());
        }

        if (lexer.identifierEquals("READ")) {
            OdpsReadStatement stmt = new OdpsReadStatement();

            if (lexer.hasComment() && lexer.isKeepComments()) {
                stmt.addBeforeComment(lexer.readAndResetComments());
            }
            lexer.nextToken();

            stmt.setTable(this.exprParser.name());

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                this.exprParser.names(stmt.getColumns(), stmt);
                accept(Token.RPAREN);
            }

            if (lexer.token() == Token.PARTITION) {
                lexer.nextToken();

                accept(Token.LPAREN);
                parseAssignItems(stmt.getPartition(), stmt);
                accept(Token.RPAREN);
            }

            if (lexer.token() == Token.LITERAL_INT) {
                stmt.setRowCount(this.exprParser.primary());
            }

            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("LIST")) {
            OdpsListStmt stmt = new OdpsListStmt();

            lexer.nextToken();
            stmt.setObject(this.exprParser.expr());

            if (lexer.identifierEquals("ROLES")
                    && stmt.getObject() instanceof SQLIdentifierExpr && ((SQLIdentifierExpr) stmt.getObject()).nameEquals("TENANT")) {
                lexer.nextToken();
                stmt.setObject(new SQLIdentifierExpr("TENANT ROLES"));
            } else if (lexer.identifierEquals("OUTPUT")
                    && stmt.getObject() instanceof SQLIdentifierExpr && ((SQLIdentifierExpr) stmt.getObject()).nameEquals("TEMPORARY")) {
                lexer.nextToken();
                stmt.setObject(new SQLIdentifierExpr("TEMPORARY OUTPUT"));
            }

            statementList.add(stmt);

            return true;
        }

        if (lexer.token() == Token.DESC || lexer.identifierEquals("DESCRIBE")) {
            SQLStatement stmt = parseDescribe();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("WHOAMI")) {
            lexer.nextToken();
            SQLWhoamiStatement stmt = new SQLWhoamiStatement();
            stmt.setDbType(DbType.odps);
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("COUNT")) {
            lexer.nextToken();
            OdpsCountStatement stmt = new OdpsCountStatement();
            stmt.setTable(this.exprParser.name());

            if (lexer.token() == Token.PARTITION) {
                lexer.nextToken();
                this.exprParser.parseAssignItem(stmt.getPartitions(), stmt);
            }
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("MSCK")) {
            SQLStatement stmt = parseMsck();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("alias")) {
            SQLStatement stmt = parseSet();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("EXSTORE")) {
            lexer.nextToken();
            OdpsExstoreStatement stmt = new OdpsExstoreStatement();
            SQLExpr table = this.exprParser.expr();
            stmt.setTable(new SQLExprTableSource(table));
            accept(Token.PARTITION);
            this.exprParser.parseAssignItem(stmt.getPartitions(), stmt);
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("INSTALL")) {
            lexer.nextToken();
            acceptIdentifier("PACKAGE");
            OdpsInstallPackageStatement stmt = new OdpsInstallPackageStatement();
            stmt.setPackageName(
                    this.exprParser.name()
            );
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("PAI")) {
            lexer.nextToken();
            int semiPos = lexer.text.indexOf(';', lexer.pos());
            while (semiPos != -1 && semiPos + 2 < lexer.text.length()) {
                char next = lexer.text.charAt(semiPos + 1);
                if (next == '"' || next == '\'') {
                    semiPos = lexer.text.indexOf(';', semiPos + 1);
                    continue;
                }
                break;
            }
            String arguments;
            if (semiPos != -1) {
                int count = semiPos - lexer.pos();
                arguments = lexer.subString(lexer.pos(), count);
                lexer.reset(semiPos);
            } else {
                arguments = lexer.subString(lexer.pos());
                lexer.reset(lexer.text.length());
            }
            lexer.nextToken();

            OdpsPAIStmt stmt = new OdpsPAIStmt();
            stmt.setArguments(arguments);
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("COPY")) {
            SQLStatement stmt = parseCopy();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals(FnvHash.Constants.KILL)) {
            SQLStatement stmt = parseKill();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals(FnvHash.Constants.LOAD)) {
            HiveLoadDataStatement stmt = parseLoad();

            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals(FnvHash.Constants.MERGE)) {
            SQLStatement stmt = parseMerge();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals(FnvHash.Constants.CLONE)) {
            SQLStatement stmt = parseClone();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals(FnvHash.Constants.UNLOAD)) {
            SQLStatement stmt = parseUnload();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals(FnvHash.Constants.BEGIN)) {
            SQLStatement stmt = parseBlock();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals(FnvHash.Constants.RESTORE)) {
            lexer.nextToken();
            accept(Token.TABLE);
            OdpsRestoreStatement stmt = new OdpsRestoreStatement();
            stmt.setTable(this.exprParser.name());

            if (lexer.token() == Token.LPAREN) {
                this.exprParser.parseAssignItem(stmt.getProperties(), stmt);
            }

            if (lexer.token() == Token.PARTITION) {
                lexer.nextToken();
                this.exprParser.parseAssignItem(stmt.getPartitions(), stmt);
            }

            if (lexer.token() == Token.TO) {
                lexer.nextToken();
                acceptIdentifier("LSN");
                stmt.setTo(
                        this.exprParser.expr()
                );
            }

            if (lexer.token() == Token.AS) {
                lexer.nextToken();
                stmt.setAlias(
                        this.alias()
                );
            }

            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals(FnvHash.Constants.UNDO)) {
            lexer.nextToken();
            accept(Token.TABLE);
            OdpsUndoTableStatement stmt = new OdpsUndoTableStatement();
            stmt.setTable(
                    new SQLExprTableSource(
                            this.exprParser.name()
                    )
            );

            if (lexer.token() == Token.PARTITION) {
                lexer.nextToken();
                this.exprParser.parseAssignItem(stmt.getPartitions(), stmt);
            }
            accept(Token.TO);
            stmt.setTo(
                    this.exprParser.expr()
            );
            statementList.add(stmt);
            return true;
        }

        if (lexer.token() == Token.FUNCTION) {
            HiveCreateFunctionStatement stmt = (HiveCreateFunctionStatement) parseHiveCreateFunction();
            stmt.setDeclare(true);
            statementList.add(stmt);
            return true;
        }

        if (lexer.token() == Token.VARIANT && lexer.stringVal().startsWith("@")) {
            Lexer.SavePoint mark = lexer.mark();
            String variant = lexer.stringVal();
            lexer.nextToken();

            if (lexer.token() == Token.COLONEQ) {
                lexer.nextToken();

                boolean cache = false;
                if (lexer.identifierEquals(FnvHash.Constants.CACHE)) {
                    lexer.nextToken();
                    accept(Token.ON);
                    cache = true;
                }

                Lexer.SavePoint lpMark = null;
                if (lexer.token() == Token.LPAREN) {
                    lpMark = lexer.mark();
                    lexer.nextToken();
                }

                switch (lexer.token()) {
                    case LITERAL_INT:
                    case LITERAL_FLOAT:
                    case LITERAL_CHARS:
                    case LITERAL_ALIAS:
                    case IDENTIFIER:
                    case CASE:
                    case CAST:
                    case IF:
                    case VARIANT:
                    case REPLACE:
                    case NEW:
                    case SUB:
                    case TRUE:
                    case FALSE: {
                        if (lpMark != null) {
                            lexer.reset(lpMark);
                        }

                        SQLExpr expr = this.exprParser.expr();
                        SQLExprStatement stmt = new SQLExprStatement(
                                new SQLAssignItem(new SQLIdentifierExpr(variant), expr)
                        );
                        statementList.add(stmt);
                        return true;
                    }
                    default:
                        if (lpMark != null) {
                            lexer.reset(lpMark);
                        }

                        boolean paren = lexer.token() == Token.LPAREN;
                        Lexer.SavePoint parenMark = lexer.mark();
                        SQLSelect select;
                        try {
                            select = new OdpsSelectParser(this.exprParser)
                                    .select();
                        } catch (ParserException error) {
                            if (paren) {
                                lexer.reset(parenMark);
                                SQLExpr expr = this.exprParser.expr();
                                SQLExprStatement stmt = new SQLExprStatement(
                                        new SQLAssignItem(new SQLIdentifierExpr(variant), expr)
                                );
                                statementList.add(stmt);
                                return true;
                            }
                            throw error;
                        }
                        switch (lexer.token()) {
                            case GT:
                            case GTEQ:
                            case EQ:
                            case LT:
                            case LTEQ:
                                statementList.add(
                                        new SQLExprStatement(
                                                new SQLAssignItem(new SQLIdentifierExpr(variant),
                                                        this.exprParser.exprRest(new SQLQueryExpr(select))
                                                )
                                        )
                                );
                                return true;
                            default:
                                break;
                        }
                        SQLSelectStatement stmt = new SQLSelectStatement(select, dbType);

                        OdpsQueryAliasStatement aliasQueryStatement = new OdpsQueryAliasStatement(variant, stmt);
                        aliasQueryStatement.setCache(cache);
                        statementList.add(aliasQueryStatement);
                        return true;
                }
            }

            OdpsDeclareVariableStatement stmt = new OdpsDeclareVariableStatement();

            if (lexer.token() != Token.EQ && lexer.token() != Token.SEMI && lexer.token() != Token.EOF) {
                stmt.setDataType(
                        this.exprParser.parseDataType()
                );
            }

            if (lexer.token() == Token.EQ || lexer.token() == Token.COLONEQ) {
                lexer.nextToken();
                stmt.setInitValue(
                        this.exprParser.expr()
                );
            }

            if (lexer.token() == Token.SEMI) {
                lexer.nextToken();
            }
            statementList.add(stmt);
            return true;
        }

        if (lexer.token() == Token.IF) {
            SQLStatement stmt = parseIf();
            statementList.add(stmt);
            return true;
        }

        if (lexer.token() == Token.CODE) {
            Lexer.SavePoint mark = lexer.mark();
            lexer.nextToken();
            if (lexer.token() == Token.EOF || lexer.token() == Token.SEMI) {
                return true;
            }
            lexer.reset(mark);
        }

        if (identifierEquals("COST")) {
            SQLStatement stmt = parseCost();
            statementList.add(stmt);
            return true;
        }

        return false;
    }

    public SQLStatement parseIf() {
        accept(Token.IF);
        SQLIfStatement ifStmt = new SQLIfStatement();
        ifStmt.setCondition(
                this.exprParser.expr()
        );

        if (lexer.identifierEquals("BEGIN")) {
            lexer.nextToken();
            parseStatementList(ifStmt.getStatements(), -1, ifStmt);
            accept(Token.END);
        } else {
            SQLStatement stmt = parseStatement();
            ifStmt.getStatements().add(stmt);
            stmt.setParent(ifStmt);
        }

        if (lexer.token() == Token.SEMI) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.ELSE) {
            lexer.nextToken();

            SQLIfStatement.Else elseItem = new SQLIfStatement.Else();
            if (lexer.identifierEquals("BEGIN")) {
                lexer.nextToken();
                parseStatementList(elseItem.getStatements(), -1, ifStmt);
                accept(Token.END);
            } else {
                SQLStatement stmt = parseStatement();
                elseItem.getStatements().add(stmt);
                stmt.setParent(elseItem);
            }
            ifStmt.setElseItem(elseItem);
        }

        return ifStmt;
    }

    public SQLStatement parseKill() {
        acceptIdentifier("KILL");
        MySqlKillStatement stmt = new MySqlKillStatement();
        SQLExpr instanceId = this.exprParser.primary();
        stmt.setThreadId(instanceId);
        return stmt;
    }

    public SQLStatement parseUnload() {
        acceptIdentifier("UNLOAD");
        OdpsUnloadStatement stmt = new OdpsUnloadStatement();

        accept(Token.FROM);
        if (lexer.token() == Token.LPAREN || lexer.token() == Token.SELECT) {
            stmt.setFrom(
                    this.createSQLSelectParser().parseTableSource()
            );
        } else {
            stmt.setFrom(
                    this.exprParser.name()
            );
        }

        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();
            this.exprParser.parseAssignItem(stmt.getPartitions(), stmt);
        }

        accept(Token.INTO);

        if (lexer.identifierEquals("LOCATION")) {
            lexer.nextToken();
            stmt.setLocation(this.exprParser.primary());
        }

        if (lexer.identifierEquals("ROW")) {
            SQLExternalRecordFormat format = this.exprParser.parseRowFormat();
            stmt.setRowFormat(format);
        }

        for (; ; ) {
            if (lexer.identifierEquals(FnvHash.Constants.STORED)) {
                lexer.nextToken();
                if (lexer.token() == Token.BY) {
                    lexer.nextToken();
                } else {
                    accept(Token.AS);
                }
                stmt.setStoredAs(
                        this.exprParser.name());
                continue;
            }

            if (lexer.token() == Token.WITH) {
                lexer.nextToken();
                acceptIdentifier("SERDEPROPERTIES");
                this.exprParser.parseAssignItem(stmt.getSerdeProperties(), stmt);
                continue;
            }

            if (identifierEquals("PROPERTIES")) {
                lexer.nextToken();
                this.exprParser.parseAssignItem(stmt.getProperties(), stmt);
                continue;
            }

            break;
        }

        return stmt;
    }

    public SQLStatement parseClone() {
        acceptIdentifier("CLONE");
        accept(Token.TABLE);
        SQLCloneTableStatement stmt = new SQLCloneTableStatement();

        stmt.setFrom(
                this.exprParser.name());

        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();
            this.exprParser.parseAssignItem(stmt.getPartitions(), stmt);
        }

        accept(Token.TO);

        stmt.setTo(
                this.exprParser.name());

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);

            if (lexer.token() == OVERWRITE) {
                lexer.nextToken();
                stmt.setIfExistsOverwrite(true);
            } else {
                acceptIdentifier("IGNORE");
                stmt.setIfExistsIgnore(true);
            }
        }

        return stmt;
    }

    public SQLStatement parseBlock() {
        SQLBlockStatement block = new SQLBlockStatement();
        if (lexer.identifierEquals(FnvHash.Constants.BEGIN)) {
            lexer.nextToken();
        } else {
            accept(Token.BEGIN);
        }
        this.parseStatementList(block.getStatementList(), -1, block);
        accept(Token.END);
        return block;
    }

    protected OdpsStatisticClause parseStaticClause() {
        if (lexer.identifierEquals("TABLE_COUNT")) {
            lexer.nextToken();
            return new OdpsStatisticClause.TableCount();
        } else if (lexer.identifierEquals("NULL_VALUE")) {
            lexer.nextToken();
            OdpsStatisticClause.NullValue null_value = new OdpsStatisticClause.NullValue();
            null_value.setColumn(this.exprParser.name());
            return null_value;
        } else if (lexer.identifierEquals("DISTINCT_VALUE")) {
            lexer.nextToken();
            OdpsStatisticClause.DistinctValue distinctValue = new OdpsStatisticClause.DistinctValue();
            distinctValue.setColumn(this.exprParser.name());
            return distinctValue;
        } else if (lexer.identifierEquals("COLUMN_SUM")) {
            lexer.nextToken();
            OdpsStatisticClause.ColumnSum column_sum = new OdpsStatisticClause.ColumnSum();
            column_sum.setColumn(this.exprParser.name());
            return column_sum;
        } else if (lexer.identifierEquals("COLUMN_MAX")) {
            lexer.nextToken();
            OdpsStatisticClause.ColumnMax column_max = new OdpsStatisticClause.ColumnMax();
            column_max.setColumn(this.exprParser.name());
            return column_max;
        } else if (lexer.identifierEquals("COLUMN_MIN")) {
            lexer.nextToken();
            OdpsStatisticClause.ColumnMin column_min = new OdpsStatisticClause.ColumnMin();
            column_min.setColumn(this.exprParser.name());
            return column_min;
        } else if (lexer.identifierEquals("EXPRESSION_CONDITION")) {
            lexer.nextToken();
            OdpsStatisticClause.ExpressionCondition expr_condition = new OdpsStatisticClause.ExpressionCondition();
            expr_condition.setExpr(this.exprParser.expr());
            return expr_condition;
        } else {
            throw new ParserException("TODO " + lexer.info());
        }
    }

    public SQLStatement parseInsert() {
        if (lexer.token() == Token.FROM) {
            lexer.nextToken();

            HiveMultiInsertStatement stmt = new HiveMultiInsertStatement();

            if (lexer.token() == Token.IDENTIFIER || lexer.token() == Token.VARIANT) {
                Lexer.SavePoint mark = lexer.mark();
                SQLExpr tableName = this.exprParser.name();
                if (lexer.token() == Token.LPAREN) {
                    lexer.reset(mark);
                    tableName = this.exprParser.primary();
                }

                SQLTableSource from = new SQLExprTableSource(tableName);

                if (lexer.token() == Token.IDENTIFIER) {
                    String alias = alias();
                    from.setAlias(alias);
                }

                SQLSelectParser selectParser = createSQLSelectParser();
                from = selectParser.parseTableSourceRest(from);

                if (lexer.token() == Token.WHERE) {
                    lexer.nextToken();
                    SQLExpr where = this.exprParser.expr();
                    SQLSelectQueryBlock queryBlock = new SQLSelectQueryBlock();
                    queryBlock.addSelectItem(new SQLAllColumnExpr());
                    queryBlock.setFrom(from);
                    queryBlock.setWhere(where);

                    if (lexer.token() == Token.GROUP) {
                        selectParser.parseGroupBy(queryBlock);
                    }

                    stmt.setFrom(
                            new SQLSubqueryTableSource(queryBlock)
                    );
                } else {
                    stmt.setFrom(from);
                }
            } else {
                SQLCommentHint hint = null;
                if (lexer.token() == Token.HINT) {
                    hint = this.exprParser.parseHint();
                }
                accept(Token.LPAREN);

                boolean paren2 = lexer.token() == Token.LPAREN;

                SQLSelectParser selectParser = createSQLSelectParser();
                SQLSelect select = selectParser.select();

                SQLTableSource from = null;
                if (paren2 && lexer.token() != Token.RPAREN) {
                    String subQueryAs = null;
                    if (lexer.token() == Token.AS) {
                        lexer.nextToken();
                        subQueryAs = tableAlias(true);
                    } else {
                        subQueryAs = tableAlias(false);
                    }
                    SQLSubqueryTableSource subQuery = new SQLSubqueryTableSource(select, subQueryAs);
                    from = selectParser.parseTableSourceRest(subQuery);
                }

                accept(Token.RPAREN);

                String alias;

                if (lexer.token() == Token.INSERT) {
                    alias = null;
                } else if (lexer.token() == Token.SELECT) {
                    // skip
                    alias = null;
                } else {
                    if (lexer.token() == Token.AS) {
                        lexer.nextToken();
                    }
                    alias = lexer.stringVal();
                    accept(Token.IDENTIFIER);
                }

                if (from == null) {
                    from = new SQLSubqueryTableSource(select, alias);
                } else {
                    if (alias != null) {
                        from.setAlias(alias);
                    }
                }

                SQLTableSource tableSource = selectParser.parseTableSourceRest(from);

                if (hint != null) {
                    if (tableSource instanceof SQLJoinTableSource) {
                        ((SQLJoinTableSource) tableSource).setHint(hint);
                    }
                }

                stmt.setFrom(tableSource);
            }

            if (lexer.token() == Token.SELECT) {
                SQLSelectParser selectParser = createSQLSelectParser();
                SQLSelect query = selectParser.select();

                HiveInsert insert = new HiveInsert();
                insert.setQuery(query);
                stmt.addItem(insert);
                return stmt;
            }

            for (; ; ) {
                HiveInsert insert = parseHiveInsert();
                stmt.addItem(insert);

                if (lexer.token() != Token.INSERT) {
                    break;
                }
            }

            return stmt;
        }

        return parseHiveInsertStmt();
    }

    public SQLSelectParser createSQLSelectParser() {
        return new OdpsSelectParser(this.exprParser, selectListCache);
    }

    public SQLStatement parseShow() {
        accept(Token.SHOW);

        if (lexer.identifierEquals(FnvHash.Constants.PARTITIONS)) {
            lexer.nextToken();

            SQLShowPartitionsStmt stmt = new SQLShowPartitionsStmt();

            SQLExpr expr = this.exprParser.expr();
            stmt.setTableSource(new SQLExprTableSource(expr));

            if (lexer.token() == Token.PARTITION) {
                lexer.nextToken();
                accept(Token.LPAREN);
                parseAssignItems(stmt.getPartition(), stmt, false);
                accept(Token.RPAREN);
            }

            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                stmt.setWhere(
                        this.exprParser.expr()
                );
            }

            return stmt;
        }

        if (lexer.identifierEquals(FnvHash.Constants.STATISTIC)) {
            lexer.nextToken();

            SQLShowStatisticStmt stmt = new SQLShowStatisticStmt();

            SQLExpr expr = this.exprParser.expr();
            stmt.setTableSource(new SQLExprTableSource(expr));

            if (lexer.token() == Token.PARTITION) {
                lexer.nextToken();

                accept(Token.LPAREN);
                parseAssignItems(stmt.getPartitions(), stmt, false);
                accept(Token.RPAREN);
            }

            if (identifierEquals("COLUMNS")) {
                lexer.nextToken();

                if (lexer.token() != Token.SEMI) {
                    accept(Token.LPAREN);
                    this.exprParser.names(stmt.getColumns(), stmt);
                    accept(Token.RPAREN);
                }
            }

            return stmt;
        }

        if (lexer.identifierEquals(FnvHash.Constants.STATISTIC_LIST)) {
            lexer.nextToken();

            SQLShowStatisticListStmt stmt = new SQLShowStatisticListStmt();

            SQLExpr expr = this.exprParser.expr();
            stmt.setTableSource(new SQLExprTableSource(expr));

            return stmt;
        }

        if (lexer.identifierEquals(FnvHash.Constants.PACKAGES)) {
            lexer.nextToken();

            SQLShowPackagesStatement stmt = new SQLShowPackagesStatement();
            return stmt;
        }

        if (lexer.identifierEquals(FnvHash.Constants.TABLES)) {
            lexer.nextToken();

            SQLShowTablesStatement stmt = new SQLShowTablesStatement();

            if (lexer.token() == Token.FROM || lexer.token() == Token.IN) {
                lexer.nextToken();
                stmt.setDatabase(this.exprParser.name());
            } else if (lexer.token() == IDENTIFIER) {
                SQLName database = exprParser.name();
                stmt.setDatabase(database);
            }

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                stmt.setLike(this.exprParser.expr());
            } else if (lexer.token() == Token.LITERAL_CHARS || lexer.token() == Token.LITERAL_ALIAS) {
                stmt.setLike(this.exprParser.expr());
            }

            return stmt;
        }

        if (lexer.identifierEquals(FnvHash.Constants.LABEL)) {
            lexer.nextToken();
            acceptIdentifier("GRANTS");
            OdpsShowGrantsStmt stmt = new OdpsShowGrantsStmt();
            stmt.setLabel(true);

            if (lexer.token() == Token.ON) {
                lexer.nextToken();
                accept(Token.TABLE);
                stmt.setObjectType(this.exprParser.expr());
            }

            if (lexer.token() == Token.FOR) {
                lexer.nextToken();
                accept(Token.USER);
                stmt.setUser(this.exprParser.expr());
            }

            return stmt;
        }

        if (lexer.identifierEquals(FnvHash.Constants.GRANTS)) {
            lexer.nextToken();
            OdpsShowGrantsStmt stmt = new OdpsShowGrantsStmt();

            if (lexer.token() == Token.FOR) {
                lexer.nextToken();
                if (lexer.token() == Token.USER) {
                    lexer.nextToken();
                }
                stmt.setUser(this.exprParser.expr());
            }

            if (lexer.token() == Token.ON) {
                lexer.nextToken();
                acceptIdentifier("type");
                stmt.setObjectType(this.exprParser.expr());
            }

            return stmt;
        }

        if (lexer.identifierEquals(FnvHash.Constants.USERS)) {
            lexer.nextToken();
            SQLShowUsersStatement stmt = new SQLShowUsersStatement();
            return stmt;
        }

        if (lexer.identifierEquals("RECYCLEBIN")) {
            lexer.nextToken();
            SQLShowRecylebinStatement stmt = new SQLShowRecylebinStatement();
            return stmt;
        }

        if (lexer.identifierEquals("VARIABLES")) {
            lexer.nextToken();
            return parseShowVariants();
        }

        if (lexer.token() == Token.CREATE) {
            return parseShowCreateTable();
        }

        if (lexer.identifierEquals(FnvHash.Constants.FUNCTIONS)) {
            lexer.nextToken();

            SQLShowFunctionsStatement stmt = new SQLShowFunctionsStatement();
            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                stmt.setLike(
                        this.exprParser.expr()
                );
            } else if (lexer.token() == Token.LITERAL_CHARS || lexer.token() == IDENTIFIER) {
                stmt.setLike(
                        this.exprParser.expr()
                );
            }

            return stmt;
        }

        if (lexer.identifierEquals(FnvHash.Constants.ROLE)) {
            lexer.nextToken();

            SQLShowRoleStatement stmt = new SQLShowRoleStatement();

            if (lexer.token() == Token.GRANT) {
                lexer.nextToken();
                stmt.setGrant(
                        this.exprParser.name()
                );
            }
            return stmt;
        }

        if (lexer.identifierEquals("ACL")) {
            lexer.nextToken();

            SQLShowACLStatement stmt = new SQLShowACLStatement();

            if (lexer.token() == Token.FOR) {
                lexer.nextToken();
                stmt.setTable(
                        new SQLExprTableSource(
                                this.exprParser.name()
                        )
                );
            }
            return stmt;
        }

        if (lexer.identifierEquals(FnvHash.Constants.ROLES)) {
            lexer.nextToken();

            SQLShowRolesStatement stmt = new SQLShowRolesStatement();
            return stmt;
        }

        if (lexer.identifierEquals("HISTORY")) {
            lexer.nextToken();
            SQLShowHistoryStatement stmt = new SQLShowHistoryStatement();

            if (lexer.token() == Token.FOR) {
                lexer.nextToken();
                if (lexer.identifierEquals(FnvHash.Constants.TABLES)) {
                    lexer.nextToken();
                    stmt.setTables(true);
                } else if (lexer.token() == Token.TABLE) {
                    lexer.nextToken();
                    stmt.setTable(
                            new SQLExprTableSource(
                                    this.exprParser.name()
                            )
                    );
                }
            }

            if (lexer.token() == Token.LPAREN) {
                this.exprParser.parseAssignItem(stmt.getProperties(), stmt);
            }

            if (lexer.token() == Token.PARTITION) {
                lexer.nextToken();
                this.exprParser.parseAssignItem(stmt.getPartitions(), stmt);
            }

            return stmt;
        }

        if (lexer.identifierEquals("CHANGELOGS")) {
            lexer.nextToken();
            OdpsShowChangelogsStatement stmt = new OdpsShowChangelogsStatement();

            if (lexer.token() == Token.FOR) {
                lexer.nextToken();
                if (lexer.identifierEquals(FnvHash.Constants.TABLES)) {
                    lexer.nextToken();
                    stmt.setTables(true);
                } else if (lexer.token() == Token.TABLE) {
                    lexer.nextToken();
                    stmt.setTable(
                            new SQLExprTableSource(
                                    this.exprParser.name()
                            )
                    );
                } else if (lexer.token() == IDENTIFIER) {
                    stmt.setTable(
                            new SQLExprTableSource(
                                    this.exprParser.name()
                            )
                    );
                }
            }

            if (lexer.token() == Token.LPAREN) {
                this.exprParser.parseAssignItem(stmt.getProperties(), stmt);
            }

            if (lexer.token() == Token.PARTITION) {
                lexer.nextToken();
                this.exprParser.parseAssignItem(stmt.getPartitions(), stmt);
            }

            if (lexer.token() == Token.LITERAL_INT) {
                stmt.setId(
                        this.exprParser.primary()
                );
            }

            return stmt;
        }

        throw new ParserException("TODO " + lexer.info());
    }

    public SQLStatement parseCost() {
        acceptIdentifier("COST");
        acceptIdentifier("SQL");
        SQLStatement stmt = parseStatement();
        SQLCostStatement cost = new SQLCostStatement();
        cost.setStatement(stmt);
        return cost;
    }

    public SQLStatement parseSet() {
        List<String> comments = null;
        if (lexer.isKeepComments() && lexer.hasComment()) {
            comments = lexer.readAndResetComments();
        }

        boolean setProject = false;
        if (identifierEquals("SETPROJECT")) {
            lexer.nextToken();
            setProject = true;
        } else if (dbType == DbType.odps && identifierEquals("ALIAS")) {
            lexer.nextToken();
        } else {
            accept(Token.SET);
        }

        if (lexer.token() == Token.SET && dbType == DbType.odps) {
            lexer.nextToken();
        }

        if (lexer.identifierEquals("PROJECT")) {
            lexer.nextToken();
            setProject = true;
        }

        if (setProject) {
            SQLSetStatement stmt = new SQLSetStatement();
            stmt.setOption(SQLSetStatement.Option.PROJECT);
            SQLName target = this.exprParser.name();
            accept(Token.EQ);
            SQLExpr value = this.exprParser.expr();
            stmt.set(target, value);
            return stmt;
        } else if (lexer.identifierEquals("LABEL")) {
            OdpsSetLabelStatement stmt = new OdpsSetLabelStatement();

            if (comments != null) {
                stmt.addBeforeComment(comments);
            }

            lexer.nextToken();

            stmt.setLabel(lexer.stringVal());
            lexer.nextToken();
            accept(Token.TO);
            if (lexer.token() == Token.USER) {
                lexer.nextToken();

                SQLName name = this.exprParser.name();
                stmt.setUser(name);
                return stmt;
            }
            accept(Token.TABLE);
            SQLExpr expr = this.exprParser.name();
            stmt.setTable(new SQLExprTableSource(expr));

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                this.exprParser.names(stmt.getColumns(), stmt);
                accept(Token.RPAREN);
            }

            return stmt;
        } else {
            SQLSetStatement stmt = new SQLSetStatement(dbType);
            stmt.putAttribute("parser.set", Boolean.TRUE);

            if (comments != null) {
                stmt.addBeforeComment(comments);
            }

            parseAssignItems(stmt.getItems(), stmt);

            return stmt;
        }
    }

    public OdpsGrantStmt parseGrant() {
        accept(Token.GRANT);
        OdpsGrantStmt stmt = new OdpsGrantStmt();

        if (lexer.identifierEquals("LABEL")) {
            stmt.setLabel(true);
            lexer.nextToken();
            stmt.setLabel(this.exprParser.expr());
        } else {
            if (lexer.identifierEquals("SUPER")) {
                stmt.setSuper(true);
                lexer.nextToken();
            }

            parsePrivilege(stmt.getPrivileges(), stmt);
        }

        if (lexer.token() == Token.ON) {
            lexer.nextToken();

            if (lexer.identifierEquals("PROJECT")) {
                lexer.nextToken();
                stmt.setResourceType(SQLObjectType.PROJECT);
            } else if (lexer.identifierEquals("PACKAGE")) {
                lexer.nextToken();
                stmt.setResourceType(SQLObjectType.PACKAGE);
            } else if (lexer.token() == Token.FUNCTION) {
                lexer.nextToken();
                stmt.setResourceType(SQLObjectType.FUNCTION);
            } else if (lexer.token() == Token.TABLE) {
                lexer.nextToken();
                stmt.setResourceType(SQLObjectType.TABLE);
                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    this.exprParser.names(stmt.getColumns(), stmt);
                    accept(Token.RPAREN);
                }
            } else if (lexer.identifierEquals("RESOURCE")) {
                lexer.nextToken();
                stmt.setResourceType(SQLObjectType.RESOURCE);
            } else if (lexer.identifierEquals("INSTANCE")) {
                lexer.nextToken();
                stmt.setResourceType(SQLObjectType.INSTANCE);
            } else if (lexer.identifierEquals("JOB")) {
                lexer.nextToken();
                stmt.setResourceType(SQLObjectType.JOB);
            } else if (lexer.identifierEquals("VOLUME")) {
                lexer.nextToken();
                stmt.setResourceType(SQLObjectType.VOLUME);
            } else if (lexer.identifierEquals("OfflineModel")) {
                lexer.nextToken();
                stmt.setResourceType(SQLObjectType.OfflineModel);
            } else if (lexer.identifierEquals("XFLOW")) {
                lexer.nextToken();
                stmt.setResourceType(SQLObjectType.XFLOW);
            }

            stmt.setResource(this.exprParser.expr());
        }

        if (lexer.token() == Token.TO) {
            lexer.nextToken();
            if (lexer.token() == Token.USER) {
                lexer.nextToken();
                stmt.setSubjectType(SQLObjectType.USER);
            } else if (lexer.identifierEquals("ROLE")) {
                lexer.nextToken();
                stmt.setSubjectType(SQLObjectType.ROLE);
            }
            stmt.getUsers().add(this.exprParser.expr());
        }

        if (lexer.token() == Token.WITH) {
            lexer.nextToken();
            acceptIdentifier("EXP");
            stmt.setExpire(this.exprParser.expr());
        }

        return stmt;
    }

    protected void parsePrivilege(List<SQLPrivilegeItem> privileges, SQLObject parent) {
        for (; ; ) {
            String privilege = null;
            if (lexer.token() == Token.ALL) {
                lexer.nextToken();
                privilege = "ALL";
            } else if (lexer.token() == Token.SELECT) {
                privilege = "SELECT";
                lexer.nextToken();
            } else if (lexer.token() == Token.UPDATE) {
                privilege = "UPDATE";
                lexer.nextToken();
            } else if (lexer.token() == Token.DELETE) {
                privilege = "DELETE";
                lexer.nextToken();
            } else if (lexer.token() == Token.INSERT) {
                privilege = "INSERT";
                lexer.nextToken();
            } else if (lexer.token() == Token.DROP) {
                lexer.nextToken();
                privilege = "DROP";
            } else if (lexer.token() == Token.ALTER) {
                lexer.nextToken();
                privilege = "ALTER";
            } else if (lexer.identifierEquals("DESCRIBE")) {
                privilege = "DESCRIBE";
                lexer.nextToken();
            } else if (lexer.identifierEquals("READ")) {
                privilege = "READ";
                lexer.nextToken();
            } else if (lexer.identifierEquals("WRITE")) {
                privilege = "WRITE";
                lexer.nextToken();
            } else if (lexer.identifierEquals("EXECUTE")) {
                lexer.nextToken();
                privilege = "EXECUTE";
            } else if (lexer.identifierEquals("LIST")) {
                lexer.nextToken();
                privilege = "LIST";
            } else if (lexer.identifierEquals("CreateTable")) {
                lexer.nextToken();
                privilege = "CreateTable";
            } else if (lexer.identifierEquals("CreateInstance")) {
                lexer.nextToken();
                privilege = "CreateInstance";
            } else if (lexer.identifierEquals("CreateFunction")) {
                lexer.nextToken();
                privilege = "CreateFunction";
            } else if (lexer.identifierEquals("CreateResource")) {
                lexer.nextToken();
                privilege = "CreateResource";
            } else if (lexer.identifierEquals("CreateJob")) {
                lexer.nextToken();
                privilege = "CreateJob";
            } else if (lexer.identifierEquals("CreateVolume")) {
                lexer.nextToken();
                privilege = "CreateVolume";
            } else if (lexer.identifierEquals("CreateOfflineModel")) {
                lexer.nextToken();
                privilege = "CreateOfflineModel";
            } else if (lexer.identifierEquals("CreateXflow")) {
                lexer.nextToken();
                privilege = "CreateXflow";
            }

            SQLExpr expr = null;
            if (privilege != null) {
                expr = new SQLIdentifierExpr(privilege);
            } else {
                expr = this.exprParser.expr();
            }

            SQLPrivilegeItem privilegeItem = new SQLPrivilegeItem();
            privilegeItem.setAction(expr);

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                for (; ; ) {
                    privilegeItem.getColumns().add(this.exprParser.name());

                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);
            }

            expr.setParent(parent);
            privileges.add(privilegeItem);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
    }

    public SQLCreateFunctionStatement parseCreateFunction() {
        return parseHiveCreateFunction();
    }

    protected HiveLoadDataStatement parseLoad() {
        acceptIdentifier("LOAD");

        HiveLoadDataStatement stmt = new HiveLoadDataStatement();

        if (lexer.token() == OVERWRITE) {
            stmt.setOverwrite(true);
            lexer.nextToken();
        } else if (lexer.token() == Token.INTO) {
            lexer.nextToken();
        }

        accept(Token.TABLE);

        stmt.setInto(
                this.exprParser.expr());

        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();
            accept(Token.LPAREN);
            this.exprParser.exprList(stmt.getPartition(), stmt);
            accept(Token.RPAREN);
        }

        if (lexer.identifierEquals(FnvHash.Constants.LOCAL)) {
            lexer.nextToken();
            stmt.setLocal(true);
        }

        accept(Token.FROM);

        acceptIdentifier("LOCATION");

        SQLExpr inpath = this.exprParser.expr();
        stmt.setInpath(inpath);

        if (lexer.identifierEquals("STORED")) {
            lexer.nextToken();

            if (lexer.token() == Token.BY) {
                lexer.nextToken();
                stmt.setStoredBy(this.exprParser.expr());
            } else {
                accept(Token.AS);
                stmt.setStoredAs(this.exprParser.expr());
            }
        }

        if (lexer.identifierEquals("ROW")) {
            lexer.nextToken();

            acceptIdentifier("FORMAT");
            acceptIdentifier("SERDE");
            stmt.setRowFormat(this.exprParser.expr());
        }

        if (lexer.token() == Token.WITH) {
            lexer.nextToken();
            acceptIdentifier("SERDEPROPERTIES");

            accept(Token.LPAREN);

            for (; ; ) {
                String name = lexer.stringVal();
                lexer.nextToken();
                accept(Token.EQ);
                SQLExpr value = this.exprParser.primary();
                stmt.getSerdeProperties().put(name, value);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }

            accept(Token.RPAREN);
        }

        if (lexer.identifierEquals("STORED")) {
            lexer.nextToken();

            accept(Token.AS);
            stmt.setStoredAs(this.exprParser.expr());
        }

        if (lexer.identifierEquals(FnvHash.Constants.USING)) {
            lexer.nextToken();
            stmt.setUsing(
                    this.exprParser.expr()
            );
        }

        return stmt;
    }

    public SQLStatement parseCopy() {
        lexer.nextToken();
        int semiPos = lexer.text.indexOf(';', lexer.pos());
        String arguments;
        if (semiPos != -1) {
            int count = semiPos - lexer.pos();
            arguments = lexer.subString(lexer.pos(), count);
            lexer.reset(semiPos);
        } else {
            arguments = lexer.subString(lexer.pos());
            lexer.reset(lexer.text.length());
        }
        lexer.nextToken();

        OdpsCopyStmt stmt = new OdpsCopyStmt();
        stmt.setArguments(arguments);
        return stmt;
    }
}

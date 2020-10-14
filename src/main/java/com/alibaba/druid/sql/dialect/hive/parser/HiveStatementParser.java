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
package com.alibaba.druid.sql.dialect.hive.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsert;
import com.alibaba.druid.sql.dialect.hive.ast.HiveMultiInsertStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveLoadDataStatement;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

import java.util.List;

public class HiveStatementParser extends SQLStatementParser {
    {
        dbType = DbType.hive;
    }
    public HiveStatementParser(String sql) {
        super (new HiveExprParser(sql));
    }

    public HiveStatementParser(String sql, SQLParserFeature... features) {
        super (new HiveExprParser(sql, features));
    }

    public HiveStatementParser(Lexer lexer){
        super(new HiveExprParser(lexer));
    }

    public HiveSelectParser createSQLSelectParser() {
        return new HiveSelectParser(this.exprParser, selectListCache);
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new HiveCreateTableParser(this.exprParser);
    }

    public SQLStatement parseInsert() {
        if (lexer.token() == Token.FROM) {
            lexer.nextToken();

            HiveMultiInsertStatement stmt = new HiveMultiInsertStatement();

            if (lexer.token() == Token.IDENTIFIER) {
                SQLName tableName = this.exprParser.name();
                SQLExprTableSource from = new SQLExprTableSource(tableName);
                SQLTableSource tableSource = createSQLSelectParser().parseTableSourceRest(from);
                stmt.setFrom(tableSource);

                if (lexer.token() == Token.IDENTIFIER) {
                    from.setAlias(lexer.stringVal());
                    lexer.nextToken();
                }
            } else {
                accept(Token.LPAREN);

                SQLSelectParser selectParser = createSQLSelectParser();
                SQLSelect select = selectParser.select();

                accept(Token.RPAREN);

                String alias = lexer.stringVal();
                accept(Token.IDENTIFIER);

                SQLSubqueryTableSource from = new SQLSubqueryTableSource(select, alias);

                stmt.setFrom(from);
            }

            for (;;) {
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

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.token() == Token.HINT) {
            List<SQLCommentHint> hints = this.exprParser.parseHints();

            boolean tddlHints = false;
            boolean accept = false;

            boolean acceptHint = false;
            switch (lexer.token()) {
                case SELECT:
                case WITH:
                case DELETE:
                case UPDATE:
                case INSERT:
                case SHOW:
                case REPLACE:
                case TRUNCATE:
                case DROP:
                case ALTER:
                case CREATE:
                case CHECK:
                case SET:
                case DESC:
                case OPTIMIZE:
                case ANALYZE:
                case KILL:
                case EXPLAIN:
                case LPAREN:
                    acceptHint = true;
                default:
                    break;
            }

            if (lexer.identifierEquals("MSCK")) {
                acceptHint = true;
            }

            if (acceptHint) {
                SQLStatementImpl stmt = (SQLStatementImpl) this.parseStatement();
                stmt.setHeadHints(hints);
                statementList.add(stmt);
                return true;
            }
        }

        if (lexer.token() == Token.FROM) {
            SQLStatement stmt = this.parseInsert();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals(FnvHash.Constants.LOAD)) {
            HiveLoadDataStatement stmt = parseLoad();

            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals(FnvHash.Constants.ANALYZE)) {
            SQLStatement stmt = parseAnalyze();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals(FnvHash.Constants.EXPORT)) {
            SQLStatement stmt = parseExport();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals(FnvHash.Constants.IMPORT)) {
            SQLStatement stmt = parseImport();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("MSCK")) {
            SQLStatement stmt = parseMsck();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals(FnvHash.Constants.SHOW)) {
            Lexer.SavePoint savePoint = this.lexer.mark();
            lexer.nextToken();

            if (lexer.identifierEquals(FnvHash.Constants.VIEWS)) {
                lexer.nextToken();

                SQLShowViewsStatement stmt = new SQLShowViewsStatement();
                if (lexer.token() == Token.IN) {
                    lexer.nextToken();
                    SQLName db = this.exprParser.name();
                    stmt.setDatabase(db);
                }
                if (lexer.token() == Token.LIKE) {
                    lexer.nextToken();
                    SQLExpr pattern = this.exprParser.expr();
                    stmt.setLike(pattern);
                }
                statementList.add(stmt);
                return true;
            }

            if (lexer.identifierEquals(FnvHash.Constants.TABLES)) {
                lexer.reset(savePoint);
                SQLStatement stmt = this.parseShowTables();
                statementList.add(stmt);
                return true;
            }

            if (lexer.identifierEquals(FnvHash.Constants.DATABASES)) {
                lexer.nextToken();

                SQLShowDatabasesStatement stmt = parseShowDatabases(false);
                statementList.add(stmt);
                return true;
            }

            if (lexer.token() == Token.INDEX) {
                lexer.nextToken();
                SQLShowIndexesStatement stmt = new SQLShowIndexesStatement();
                stmt.setType("INDEX");

                if (lexer.token() == Token.ON) {
                    lexer.nextToken();
                    SQLName table = exprParser.name();
                    stmt.setTable(table);
                }

                if (lexer.token() == Token.HINT) {
                    stmt.setHints(this.exprParser.parseHints());
                }

                statementList.add(stmt);

                return true;
            }

            if (lexer.token() == Token.CREATE) {
                SQLShowCreateTableStatement stmt = parseShowCreateTable();

                statementList.add(stmt);
                return true;
            }

            if (lexer.identifierEquals(FnvHash.Constants.PARTITIONS)) {
                lexer.nextToken();

                SQLShowPartitionsStmt stmt = new SQLShowPartitionsStmt();

                if (lexer.token() == Token.FROM) {
                    lexer.nextToken();
                }
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

                statementList.add(stmt);
                return true;
            }

            if (lexer.identifierEquals(FnvHash.Constants.COLUMNS)) {
                lexer.nextToken();

                SQLShowColumnsStatement stmt = new SQLShowColumnsStatement();

                if (lexer.token() == Token.FROM || lexer.token() == Token.IN) {
                    lexer.nextToken();
                    SQLName table = exprParser.name();
                    if (lexer.token() == Token.SUB && table instanceof SQLIdentifierExpr) {
                        lexer.mark();
                        lexer.nextToken();
                        String strVal = lexer.stringVal();
                        lexer.nextToken();
                        if (table instanceof SQLIdentifierExpr) {
                            SQLIdentifierExpr ident = (SQLIdentifierExpr) table;
                            table = new SQLIdentifierExpr(ident.getName() + "-" + strVal);
                        }
                    }
                    stmt.setTable(table);
                }

                if (lexer.token() == Token.LIKE) {
                    lexer.nextToken();
                    SQLExpr like = exprParser.expr();
                    stmt.setLike(like);
                }

                if (lexer.token() == Token.WHERE) {
                    lexer.nextToken();
                    SQLExpr where = exprParser.expr();
                    stmt.setWhere(where);
                }

                statementList.add(stmt);
                return true;
            }

            throw new ParserException("TODO " + lexer.info());
        }

        return false;
    }

    protected HiveLoadDataStatement parseLoad() {
        acceptIdentifier("LOAD");
        acceptIdentifier("DATA");

        HiveLoadDataStatement stmt = new HiveLoadDataStatement();
        if (lexer.identifierEquals(FnvHash.Constants.LOCAL)) {
            lexer.nextToken();
            stmt.setLocal(true);
        }

        acceptIdentifier("INPATH");

        SQLExpr inpath = this.exprParser.expr();
        stmt.setInpath(inpath);

        if (lexer.token() == Token.OVERWRITE) {
            lexer.nextToken();
            stmt.setOverwrite(true);
        }

        accept(Token.INTO);
        accept(Token.TABLE);
        SQLExpr table = this.exprParser.expr();
        stmt.setInto(table);

        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();
            accept(Token.LPAREN);
            this.exprParser.exprList(stmt.getPartition(), stmt);
            accept(Token.RPAREN);
        }
        return stmt;
    }

    public SQLCreateTableStatement parseCreateTable() {
        SQLCreateTableParser parser = new HiveCreateTableParser(this.exprParser);
        return parser.parseCreateTable();
    }

    public SQLCreateFunctionStatement parseCreateFunction() {
       return parseHiveCreateFunction();
    }

    public SQLCreateIndexStatement parseCreateIndex(boolean acceptCreate) {
        if (acceptCreate) {
            accept(Token.CREATE);
        }

        accept(Token.INDEX);

        SQLCreateIndexStatement stmt = new SQLCreateIndexStatement(dbType);
        stmt.setName(this.exprParser.name());

        accept(Token.ON);

        if (lexer.token() == Token.TABLE) {
            lexer.nextToken();
        }

        stmt.setTable(this.exprParser.name());

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

        if (lexer.token() == Token.AS) {
            lexer.nextToken();
            String indexType = lexer.stringVal();
            accept(Token.LITERAL_CHARS);
            stmt.setType(indexType);
        }

        if (lexer.token() == Token.WITH) {
            lexer.nextToken();
            acceptIdentifier("DEFERRED");
            acceptIdentifier("REBUILD");
            stmt.setDeferedRebuild(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.IDXPROPERTIES)) {
            lexer.nextToken();
            this.exprParser.parseAssignItem(stmt.getProperties(), stmt);
        }

        if (lexer.token() == Token.IN) {
            lexer.nextToken();
            accept(Token.TABLE);
            SQLName inTable = this.exprParser.name();
            stmt.setIn(inTable);
        }

        if (lexer.token() == Token.ROW
                || lexer.identifierEquals(FnvHash.Constants.ROW)) {
            SQLExternalRecordFormat format = this.getExprParser().parseRowFormat();
            stmt.setRowFormat(format);
        }

        if (lexer.identifierEquals(FnvHash.Constants.STORED)) {
            lexer.nextToken();
            accept(Token.AS);
            SQLName name = this.exprParser.name();
            stmt.setStoredAs(name);
        }

        if (lexer.identifierEquals(FnvHash.Constants.TBLPROPERTIES)) {
            lexer.nextToken();
            this.exprParser.parseAssignItem(stmt.getTableProperties(), stmt);
        }

        return stmt;
    }

    protected SQLStatement parseExport() {
        lexer.nextToken();
        accept(Token.TABLE);
        SQLExportTableStatement stmt = new SQLExportTableStatement();
        stmt.setTable(
                new SQLExprTableSource(
                        this.exprParser.name()));

        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();

            accept(Token.LPAREN);
            parseAssignItems(stmt.getPartition(), stmt, false);
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.TO) {
            lexer.nextToken();
            SQLExpr to = this.exprParser.primary();
            stmt.setTo(to);
        }

        return stmt;
    }

    protected SQLStatement parseImport() {
        lexer.nextToken();
        SQLImportTableStatement stmt = new SQLImportTableStatement();
        if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
            lexer.nextToken();
            stmt.setExtenal(true);
        }

        if (lexer.token() == Token.TABLE) {
            lexer.nextToken();

            stmt.setTable(
                    new SQLExprTableSource(
                            this.exprParser.name()));
        }

        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();

            accept(Token.LPAREN);
            parseAssignItems(stmt.getPartition(), stmt, false);
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.FROM) {
            lexer.nextToken();
            SQLExpr to = this.exprParser.primary();
            stmt.setFrom(to);
        }

        return stmt;
    }

    protected SQLStatement parseAlterDatabase() {
        accept(Token.ALTER);
        if (lexer.token() == Token.SCHEMA) {
            lexer.nextToken();
        } else {
            accept(Token.DATABASE);
        }

        SQLAlterDatabaseStatement stmt = new SQLAlterDatabaseStatement();
        stmt.setName(this.exprParser.name());

        if (lexer.token() == Token.SET) {
            lexer.nextToken();

            if (lexer.identifierEquals(FnvHash.Constants.DBPROPERTIES)) {
                lexer.nextToken();
                this.exprParser.parseAssignItem(stmt.getProperties(), stmt);
            } else {
                throw new ParserException("TODO " + lexer.info());
            }
        }

        return stmt;
    }

    protected SQLStatement parseAlterSchema() {
        return parseAlterDatabase();
    }

    public SQLStatement parseCreateSchema() {
        return parseCreateDatabase();
    }

    @Override
    public HiveExprParser getExprParser() {
        return (HiveExprParser) exprParser;
    }
}

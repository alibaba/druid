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
package com.alibaba.druid.sql.dialect.odps.parser;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLObjectType;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.ast.statement.SQLShowTablesStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsAddStatisticStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsAnalyzeTableStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsGrantStmt;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsert;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsInsertStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsListStmt;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsReadStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsRemoveStatisticStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsSetLabelStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsShowGrantsStmt;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsShowPartitionsStmt;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsShowStatisticStmt;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsStatisticClause;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.JdbcConstants;

public class OdpsStatementParser extends SQLStatementParser {

    public OdpsStatementParser(String sql){
        super(new OdpsLexer(sql, true, true), JdbcConstants.ODPS);
        this.exprParser = new OdpsExprParser(this.lexer);
        this.lexer.nextToken();
    }

    public OdpsStatementParser(SQLExprParser exprParser){
        super(exprParser);
    }

    public SQLSelectStatement parseSelect() {
        return new SQLSelectStatement(
                new OdpsSelectParser(this.exprParser)
                        .select(), JdbcConstants.ODPS);
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
            lexer.nextToken();
            accept(Token.TABLE);

            OdpsAnalyzeTableStatement stmt = new OdpsAnalyzeTableStatement();

            SQLName table = this.exprParser.name();
            stmt.setTable(table);

            if (lexer.token() == Token.PARTITION) {
                lexer.nextToken();

                accept(Token.LPAREN);
                parseAssignItems(stmt.getPartition(), stmt);
                accept(Token.RPAREN);
            }

            accept(Token.COMPUTE);
            acceptIdentifier("STATISTICS");

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

            statementList.add(stmt);

            return true;
        }

        if (lexer.token() == Token.DESC || lexer.identifierEquals("DESCRIBE")) {
            SQLStatement stmt = parseDescribe();
            statementList.add(stmt);
            return true;
        }

        return false;
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
        OdpsInsertStatement stmt = new OdpsInsertStatement();

        if (lexer.token() == Token.FROM) {
            lexer.nextToken();

            if (lexer.token() == Token.IDENTIFIER) {
                SQLName tableName = this.exprParser.name();
                SQLExprTableSource from = new SQLExprTableSource(tableName);
                stmt.setFrom(from);
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

    public SQLSelectParser createSQLSelectParser() {
        return new OdpsSelectParser(this.exprParser, selectListCache);
    }

    public SQLStatement parseShow() {
        accept(Token.SHOW);

        if (lexer.identifierEquals("PARTITIONS")) {
            lexer.nextToken();

            OdpsShowPartitionsStmt stmt = new OdpsShowPartitionsStmt();

            SQLExpr expr = this.exprParser.expr();
            stmt.setTableSource(new SQLExprTableSource(expr));

            return stmt;
        }

        if (lexer.identifierEquals("STATISTIC")) {
            lexer.nextToken();

            OdpsShowStatisticStmt stmt = new OdpsShowStatisticStmt();

            SQLExpr expr = this.exprParser.expr();
            stmt.setTableSource(new SQLExprTableSource(expr));

            return stmt;
        }

        if (lexer.identifierEquals("TABLES")) {
            lexer.nextToken();

            SQLShowTablesStatement stmt = new SQLShowTablesStatement();

            if (lexer.token() == Token.FROM) {
                lexer.nextToken();
                stmt.setDatabase(this.exprParser.name());
            }

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                stmt.setLike(this.exprParser.expr());
            }

            return stmt;
        }

        if (lexer.identifierEquals("GRANTS")) {
            lexer.nextToken();
            OdpsShowGrantsStmt stmt = new OdpsShowGrantsStmt();

            if (lexer.token() == Token.FOR) {
                lexer.nextToken();
                stmt.setUser(this.exprParser.expr());
            }

            if (lexer.token() == Token.ON) {
                lexer.nextToken();
                acceptIdentifier("type");
                stmt.setObjectType(this.exprParser.expr());
            }

            return stmt;
        }

        throw new ParserException("TODO " + lexer.info());
    }

    public SQLStatement parseSet() {
        List<String> comments = null;
        if (lexer.isKeepComments() && lexer.hasComment()) {
            comments = lexer.readAndResetComments();
        }

        accept(Token.SET);

        if (lexer.identifierEquals("LABEL")) {
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
            SQLSetStatement stmt = new SQLSetStatement(getDbType());

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

            parsePrivileages(stmt.getPrivileges(), stmt);
        }

        if (lexer.token() == Token.ON) {
            lexer.nextToken();

            if (lexer.identifierEquals("PROJECT")) {
                lexer.nextToken();
                stmt.setObjectType(SQLObjectType.PROJECT);
            } else if (lexer.identifierEquals("PACKAGE")) {
                lexer.nextToken();
                stmt.setObjectType(SQLObjectType.PACKAGE);
            } else if (lexer.token() == Token.FUNCTION) {
                lexer.nextToken();
                stmt.setObjectType(SQLObjectType.FUNCTION);
            } else if (lexer.token() == Token.TABLE) {
                lexer.nextToken();
                stmt.setObjectType(SQLObjectType.TABLE);
                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    this.exprParser.names(stmt.getColumns(), stmt);
                    accept(Token.RPAREN);
                }
            } else if (lexer.identifierEquals("RESOURCE")) {
                lexer.nextToken();
                stmt.setObjectType(SQLObjectType.RESOURCE);
            } else if (lexer.identifierEquals("INSTANCE")) {
                lexer.nextToken();
                stmt.setObjectType(SQLObjectType.INSTANCE);
            } else if (lexer.identifierEquals("JOB")) {
                lexer.nextToken();
                stmt.setObjectType(SQLObjectType.JOB);
            } else if (lexer.identifierEquals("VOLUME")) {
                lexer.nextToken();
                stmt.setObjectType(SQLObjectType.VOLUME);
            } else if (lexer.identifierEquals("OfflineModel")) {
                lexer.nextToken();
                stmt.setObjectType(SQLObjectType.OfflineModel);
            } else if (lexer.identifierEquals("XFLOW")) {
                lexer.nextToken();
                stmt.setObjectType(SQLObjectType.XFLOW);
            }

            stmt.setOn(this.exprParser.expr());
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
            stmt.setTo(this.exprParser.expr());
        }

        if (lexer.token() == Token.WITH) {
            lexer.nextToken();
            acceptIdentifier("EXP");
            stmt.setExpire(this.exprParser.expr());
        }

        return stmt;
    }

    protected void parsePrivileages(List<SQLExpr> privileges, SQLObject parent) {
        for (;;) {
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
            expr.setParent(parent);
            privileges.add(expr);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
    }
}

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
package com.alibaba.druid.sql.dialect.presto.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsert;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveMultiInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MysqlDeallocatePrepareStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectStatement;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoAlterFunctionStatement;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoAlterSchemaStatement;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoExecuteStatement;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoPrepareStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

import java.util.List;

/**
 * Created by wenshao on 16/9/13.
 */
public class PrestoStatementParser extends SQLStatementParser {
    {
        dbType = DbType.presto;
    }
    public PrestoStatementParser(String sql) {
        super(new PrestoExprParser(sql));
    }
 public PrestoStatementParser(String sql, SQLParserFeature... features) {
        super(new PrestoExprParser(sql, features));
    }

    public PrestoStatementParser(Lexer lexer) {
        super(new PrestoExprParser(lexer));
    }

    @Override
    public PrestoSelectParser createSQLSelectParser() {
        return new PrestoSelectParser(this.exprParser, selectListCache);
    }

    @Override
    public PGSelectStatement parseSelect() {
        PrestoSelectParser selectParser = createSQLSelectParser();
        SQLSelect select = selectParser.select();
        return new PGSelectStatement(select);
    }

    @Override
    protected void parseInsertColumns(SQLInsertInto insert) {
        if (lexer.token() == Token.RPAREN) {
            return;
        }

        for (; ; ) {
            SQLName expr = this.exprParser.name();
            expr.setParent(insert);
            insert.getColumns().add(expr);

            if (lexer.token() == Token.IDENTIFIER) {
                String text = lexer.stringVal();
                if (text.equalsIgnoreCase("TINYINT")
                        || text.equalsIgnoreCase("BIGINT")
                        || text.equalsIgnoreCase("INTEGER")
                        || text.equalsIgnoreCase("DOUBLE")
                        || text.equalsIgnoreCase("DATE")
                        || text.equalsIgnoreCase("VARCHAR")) {
                    expr.getAttributes().put("dataType", text);
                    lexer.nextToken();
                } else if (text.equalsIgnoreCase("CHAR")) {
                    String dataType = text;
                    lexer.nextToken();
                    accept(Token.LPAREN);
                    SQLExpr char_len = this.exprParser.primary();
                    accept(Token.RPAREN);
                    dataType += ("(" + char_len.toString() + ")");
                    expr.getAttributes().put("dataType", dataType);
                }
            }

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }
    }

    @Override
    public SQLCreateTableParser getSQLCreateTableParser() {
        return new PrestoCreateTableParser(this.exprParser);
    }

    protected SQLStatement parseAlterFunction() {
        accept(Token.ALTER);
        accept(Token.FUNCTION);

        PrestoAlterFunctionStatement stmt = new PrestoAlterFunctionStatement();
        stmt.setDbType(dbType);

        SQLName name = this.exprParser.name();

        /*
         * 因支持写函数参数项，名称处理
         * ALTER FUNCTION qualified_function_name [ ( parameter_type[, ...] ) ]
         * RETURNS NULL ON NULL INPUT | CALLED ON NULL INPUT
         */
        if (lexer.token() == Token.LPAREN) {
            StringBuilder needAppendName = new StringBuilder();
            needAppendName.append("(");
            for (; ; ) {
                lexer.nextToken();
                needAppendName.append(lexer.stringVal());

                lexer.nextToken();
                if (lexer.token() == Token.RPAREN) {
                    break;
                }

                // 处理fn(a, )
                if (lexer.token() == Token.COMMA) {
                    needAppendName.append(",");
                    Lexer.SavePoint mark = lexer.mark();

                    lexer.nextToken();

                    if (lexer.token() == Token.RPAREN) {
                        setErrorEndPos(lexer.pos());
                        throw new ParserException("syntax error, actual " + lexer.token() + ", " + lexer.info());
                    }
                    lexer.reset(mark);
                }
            }
            accept(Token.RPAREN);
            needAppendName.append(")");

            if (needAppendName.length() > 0) {
                if (name instanceof SQLPropertyExpr) {
                    SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) name;
                    sqlPropertyExpr.setName(sqlPropertyExpr.getName() + needAppendName);
                } else if (name instanceof SQLIdentifierExpr) {
                    SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) name;
                    sqlIdentifierExpr.setName(sqlIdentifierExpr.getName() + needAppendName);
                }
            }
        }
        stmt.setName(name);

        if (lexer.identifierEquals("CALLED")) {
            lexer.nextToken();
            stmt.setCalledOnNullInput(true);
        } else if (lexer.identifierEquals("RETURNS")) {
            lexer.nextToken();
            acceptIdentifier("NULL");
            stmt.setCalledOnNullInput(true);
        } else {
            setErrorEndPos(lexer.pos());
            throw new ParserException("syntax error, actual " + lexer.token() + ", " + lexer.info());
        }
        accept(Token.ON);
        accept(Token.NULL);
        acceptIdentifier("INPUT");
        return stmt;
    }

    @Override
    protected SQLStatement alterSchema() {
        accept(Token.ALTER);
        accept(Token.SCHEMA);

        PrestoAlterSchemaStatement stmt = new PrestoAlterSchemaStatement();
        stmt.setDbType(dbType);

        SQLName name = this.exprParser.name();
        stmt.setSchemaName(name);

        acceptIdentifier("RENAME");
        accept(Token.TO);

        stmt.setNewName(this.exprParser.identifier());

        return stmt;
    }

    @Override
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

                SQLTableSource from = new SQLSubqueryTableSource(select, alias);

                switch (lexer.token()) {
                    case LEFT:
                    case RIGHT:
                    case FULL:
                    case JOIN:
                        from = selectParser.parseTableSourceRest(from);
                        break;
                    default:
                        break;
                }

                stmt.setFrom(from);
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

    @Override
    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.identifierEquals("PREPARE")) {
            PrestoPrepareStatement stmt = parsePrepare();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("EXECUTE")) {
            acceptIdentifier("EXECUTE");

            if (lexer.identifierEquals("IMMEDIATE")) {
                acceptIdentifier("IMMEDIATE");
            }

            PrestoExecuteStatement stmt = parseExecute();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("DEALLOCATE")) {
            MysqlDeallocatePrepareStatement stmt = parseDeallocatePrepare();
            statementList.add(stmt);
            return true;
        }
        return false;
    }

    public PrestoPrepareStatement parsePrepare() {
        acceptIdentifier("PREPARE");

        SQLName name = exprParser.name();
        accept(Token.FROM);
        PrestoPrepareStatement stmt = new PrestoPrepareStatement(name);

        if (lexer.token() == Token.SELECT) {
            SQLSelect select = createSQLSelectParser().select();
            stmt.setSelect(select);
        } else if (lexer.token() == Token.INSERT) {
            SQLStatement sqlStatement = parseInsert();
            stmt.setInsert((HiveInsertStatement) sqlStatement);
        }
        return stmt;
    }

    public PrestoExecuteStatement parseExecute() {
        PrestoExecuteStatement stmt = new PrestoExecuteStatement();

        SQLName statementName = exprParser.name();
        stmt.setStatementName(statementName);

        if (lexer.identifierEquals("USING")) {
            lexer.nextToken();
            exprParser.exprList(stmt.getParameters(), stmt);
        } else if (lexer.token() == Token.IDENTIFIER) {
            exprParser.exprList(stmt.getParameters(), stmt);
        }
        return stmt;
    }

    public MysqlDeallocatePrepareStatement parseDeallocatePrepare() {
        acceptIdentifier("DEALLOCATE");
        acceptIdentifier("PREPARE");

        MysqlDeallocatePrepareStatement stmt = new MysqlDeallocatePrepareStatement();
        SQLName statementName = exprParser.name();
        stmt.setStatementName(statementName);

        return stmt;
    }
}

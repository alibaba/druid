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
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLCurrentOfCursorExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGDeleteStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGShowStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGUpdateStatement;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.*;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

import java.util.ArrayList;
import java.util.List;

public class PGSQLStatementParser extends SQLStatementParser {
    public static final String TIME_ZONE = "TIME ZONE";
    public static final String TIME = "TIME";
    public static final String LOCAL = "LOCAL";

    public PGSQLStatementParser(PGExprParser parser) {
        super(parser);
    }
    
    public PGSQLStatementParser(String sql){
        super(new PGExprParser(sql));
    }

    public PGSQLStatementParser(String sql, SQLParserFeature... features){
        super(new PGExprParser(sql, features));
    }

    public PGSQLStatementParser(Lexer lexer){
        super(new PGExprParser(lexer));
    }

    public PGSelectParser createSQLSelectParser() {
        return new PGSelectParser(this.exprParser);
    }

    public SQLUpdateStatement parseUpdateStatement() {
        accept(Token.UPDATE);

        PGUpdateStatement udpateStatement = new PGUpdateStatement();

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

    public PGInsertStatement parseInsert() {
        PGInsertStatement stmt = new PGInsertStatement();

        if (lexer.token() == Token.INSERT) {
            lexer.nextToken();
            accept(Token.INTO);

            SQLName tableName = this.exprParser.name();
            stmt.setTableName(tableName);

            if (lexer.token() == Token.IDENTIFIER) {
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

        if (lexer.token() == Token.RETURNING) {
            lexer.nextToken();
            SQLExpr returning = this.exprParser.expr();
            stmt.setReturning(returning);
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
            for (;;) {
                SQLName name = this.exprParser.name();
                deleteStatement.getUsing().add(name);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
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
        case START: {
            lexer.nextToken();
            acceptIdentifier("TRANSACTION");
            PGStartTransactionStatement stmt = new PGStartTransactionStatement();
            statementList.add(stmt);
            lexer.nextToken();
            return true;
        }
        case WITH:
            statementList.add(parseWith());
            return true;
        default:
            return false;
        }
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

        SQLAlterTableAlterColumn alterColumn = new SQLAlterTableAlterColumn();
        alterColumn.setColumn(column);

        if (column.getDataType() == null && column.getConstraints().size() == 0) {
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
        PGShowStatement stmt = new PGShowStatement();
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
    
    private SQLStatement handleTimeZoneSet(String range) {
        // ZONE
        lexer.nextToken();
        // VALUE
        lexer.nextToken();
        String value = lexer.stringVal();
        List<SQLExpr> exprs = new ArrayList<SQLExpr>();
        if (lexer.token() == Token.IDENTIFIER) {
            exprs.add(new SQLIdentifierExpr(value.toUpperCase()));
        } else {
            exprs.add(new SQLCharExpr(value));
        }
        lexer.nextToken();
        return new PGSetStatement(range, TIME_ZONE, exprs);
    }

    @Override
    public SQLStatement parseSet() {
        accept(Token.SET);
        Token token = lexer.token();
        String range = "";
        if (token == Token.SESSION) {
            lexer.nextToken();
            range = Token.SESSION.name();
        } else if (token == Token.IDENTIFIER && LOCAL.equalsIgnoreCase(lexer.stringVal())) {
            range = LOCAL;
            lexer.nextToken();
        }
        String parameter = lexer.stringVal();
        if (TIME.equalsIgnoreCase(parameter)) {
            // SET [ SESSION | LOCAL ] TIME ZONE { timezone | LOCAL | DEFAULT }
            return handleTimeZoneSet(range);
        }
        // TO | =
        lexer.nextToken();

        // value | 'value' | DEFAULT
        List<SQLExpr> values = new ArrayList<SQLExpr>();
        while (!lexer.isEOF()) {
            lexer.nextToken();
            if (lexer.token() == Token.LITERAL_CHARS) {
                values.add(new SQLCharExpr(lexer.stringVal()));
            } else if (lexer.token() == Token.LITERAL_INT) {
                values.add(new SQLIdentifierExpr(lexer.numberString()));
            } else {
                values.add(new SQLIdentifierExpr(lexer.stringVal()));
            }
            // skip comma
            lexer.nextToken();
        }
        return new PGSetStatement(range, parameter, values);
    }


}

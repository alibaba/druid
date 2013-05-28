/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.sqlserver.parser;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerTop;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerUpdateStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class SQLServerStatementParser extends SQLStatementParser {

    public SQLServerStatementParser(String sql){
        super(new SQLServerExprParser(sql));
    }

    public SQLSelectParser createSQLSelectParser() {
        return new SQLServerSelectParser(this.exprParser);
    }

    public SQLServerStatementParser(Lexer lexer){
        super(new SQLServerExprParser(lexer));
    }

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.token() == Token.WITH) {
            SQLStatement stmt = parseSelect();
            statementList.add(stmt);
            return true;
        }

        return false;
    }

    public SQLStatement parseInsert() {
        SQLServerInsertStatement insertStatement = new SQLServerInsertStatement();

        if (lexer.token() == Token.INSERT) {
            accept(Token.INSERT);
        }

        parseInsert0(insertStatement);
        return insertStatement;
    }

    protected void parseInsert0(SQLInsertInto insert, boolean acceptSubQuery) {
        SQLServerInsertStatement insertStatement = (SQLServerInsertStatement) insert;

        if (lexer.token() == Token.INTO) {
            lexer.nextToken();

            SQLName tableName = this.exprParser.name();
            insertStatement.setTableName(tableName);

            if (lexer.token() == Token.LITERAL_ALIAS) {
                insertStatement.setAlias(as());
            }

            parseInsert0_hinits(insertStatement);

            if (lexer.token() == Token.IDENTIFIER) {
                insertStatement.setAlias(lexer.stringVal());
                lexer.nextToken();
            }
        }

        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();
            this.exprParser.exprList(insertStatement.getColumns());
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.VALUES) {
            lexer.nextToken();

            for (;;) {
                accept(Token.LPAREN);
                SQLInsertStatement.ValuesClause values = new SQLInsertStatement.ValuesClause();
                this.exprParser.exprList(values.getValues(), values);
                insertStatement.getValuesList().add(values);
                accept(Token.RPAREN);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                } else {
                    break;
                }
            }
        } else if (acceptSubQuery && (lexer.token() == Token.SELECT || lexer.token() == Token.LPAREN)) {
            SQLQueryExpr queryExpr = (SQLQueryExpr) this.exprParser.expr();
            insertStatement.setQuery(queryExpr.getSubQuery());
        }
    }

    protected SQLServerUpdateStatement createUpdateStatement() {
        return new SQLServerUpdateStatement();
    }

    public SQLUpdateStatement parseUpdateStatement() {
        SQLServerUpdateStatement udpateStatement = createUpdateStatement();

        accept(Token.UPDATE);
        
        SQLServerTop top = this.getExprParser().parseTop();
        if (top != null) {
            udpateStatement.setTop(top);
        }

        SQLTableSource tableSource = this.exprParser.createSelectParser().parseTableSource();
        udpateStatement.setTableSource(tableSource);

        parseUpdateSet(udpateStatement);

        if (lexer.token() == Token.FROM) {
            lexer.nextToken();
            SQLTableSource from = this.exprParser.createSelectParser().parseTableSource();
            udpateStatement.setFrom(from);
        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            udpateStatement.setWhere(this.exprParser.expr());
        }

        return udpateStatement;
    }
    
    public SQLServerExprParser getExprParser() {
        return (SQLServerExprParser) exprParser;
    }
}

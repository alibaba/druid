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
package com.alibaba.druid.sql.dialect.sqlserver.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerOutput;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerTop;
import com.alibaba.druid.sql.dialect.sqlserver.ast.expr.SQLServerObjectReferenceExpr;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;
import java.util.List;

public class SQLServerExprParser extends SQLExprParser {
    public final static String[] AGGREGATE_FUNCTIONS;

    public final static long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = {
                "AVG",
                "COUNT",
                "FIRST_VALUE",
                "MAX",
                "MIN",
                "ROW_NUMBER",
                "STDDEV",
                "SUM"
        };
        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = FnvHash.fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    public SQLServerExprParser(Lexer lexer){
        super(lexer);
        this.dbType = DbType.sqlserver;
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    public SQLServerExprParser(String sql){
        this(new SQLServerLexer(sql));
        this.lexer.nextToken();
        this.dbType = DbType.sqlserver;
    }

    public SQLServerExprParser(String sql, SQLParserFeature... features){
        this(new SQLServerLexer(sql, features));
        this.lexer.nextToken();
        this.dbType = DbType.sqlserver;
    }

    public SQLExpr primary() {

        if (lexer.token() == Token.LBRACKET) {
            lexer.nextToken();
            SQLExpr name = this.name();
            accept(Token.RBRACKET);
            return primaryRest(name);
        }

        return super.primary();
    }

    public SQLServerSelectParser createSelectParser() {
        return new SQLServerSelectParser(this);
    }

    public SQLExpr primaryRest(SQLExpr expr) {
        final Token token = lexer.token();
        if (token == Token.DOTDOT) {
            expr = nameRest((SQLName) expr);
        } else if (lexer.identifierEquals(FnvHash.Constants.VALUE)
                && expr instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
            if (identExpr.nameHashCode64() == FnvHash.Constants.NEXT) {
                lexer.nextToken();
                accept(Token.FOR);

                SQLName name = this.name();
                SQLSequenceExpr seq = new SQLSequenceExpr();
                seq.setSequence(name);
                seq.setFunction(SQLSequenceExpr.Function.NextVal);
                expr = seq;
            }
        }

        return super.primaryRest(expr);
    }

    protected SQLExpr dotRest(SQLExpr expr) {
        boolean backet = false;

        if (lexer.token() == Token.LBRACKET) {
            lexer.nextToken();
            backet = true;
        }

        expr = super.dotRest(expr);

        if (backet) {
            accept(Token.RBRACKET);
        }

        return expr;
    }

    public SQLName nameRest(SQLName expr) {
        if (lexer.token() == Token.DOTDOT) {
            lexer.nextToken();

            boolean backet = false;
            if (lexer.token() == Token.LBRACKET) {
                lexer.nextToken();
                backet = true;
            }
            String text = lexer.stringVal();
            lexer.nextToken();

            if (backet) {
                accept(Token.RBRACKET);
            }

            SQLServerObjectReferenceExpr owner = new SQLServerObjectReferenceExpr(expr);
            expr = new SQLPropertyExpr(owner, text);
        }

        return super.nameRest(expr);
    }

    public SQLServerTop parseTop() {
        if (lexer.token() == Token.TOP) {
            SQLServerTop top = new SQLServerTop();
            lexer.nextToken();

            boolean paren = false;
            if (lexer.token() == Token.LPAREN) {
                paren = true;
                lexer.nextToken();
            }

            if (lexer.token() == Token.LITERAL_INT) {
                top.setExpr(lexer.integerValue().intValue());
                lexer.nextToken();
            } else {
                top.setExpr(primary());
            }

            if (paren) {
                accept(Token.RPAREN);
            }

            if (lexer.token() == Token.PERCENT) {
                lexer.nextToken();
                top.setPercent(true);
            }

            return top;
        }

        return null;
    }
    
    protected SQLServerOutput parserOutput() {
        if (lexer.identifierEquals("OUTPUT")) {
            lexer.nextToken();
            SQLServerOutput output = new SQLServerOutput();

            final List<SQLSelectItem> selectList = output.getSelectList();
            for (;;) {
                final SQLSelectItem selectItem = parseSelectItem();
                selectList.add(selectItem);

                if (lexer.token() != Token.COMMA) {
                    break;
                }

                lexer.nextToken();
            }

            if (lexer.token() == Token.INTO) {
                lexer.nextToken();
                output.setInto(new SQLExprTableSource(this.name()));
                if (lexer.token() == (Token.LPAREN)) {
                    lexer.nextToken();
                    this.exprList(output.getColumns(), output);
                    accept(Token.RPAREN);
                }
            }
            return output;
        }
        return null;
    }

    public SQLSelectItem parseSelectItem() {
        SQLExpr expr;
        if (lexer.token() == Token.IDENTIFIER) {
            expr = new SQLIdentifierExpr(lexer.stringVal());
            lexer.nextTokenComma();

            if (lexer.token() != Token.COMMA) {
                expr = this.primaryRest(expr);
                expr = this.exprRest(expr);
            }
        } else {
            expr = this.expr();
        }
        final String alias = as();
        return new SQLSelectItem(expr, alias);
    }

    public SQLColumnDefinition createColumnDefinition() {
        SQLColumnDefinition column = new SQLColumnDefinition();
        column.setDbType(dbType);
        return column;
    }

    public SQLColumnDefinition parseColumnRest(SQLColumnDefinition column) {
        if (lexer.token() == Token.IDENTITY) {
            lexer.nextToken();

            SQLColumnDefinition.Identity identity = new SQLColumnDefinition.Identity();
            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
    
                SQLIntegerExpr seed = (SQLIntegerExpr) this.primary();
                accept(Token.COMMA);
                SQLIntegerExpr increment = (SQLIntegerExpr) this.primary();
                accept(Token.RPAREN);
                
                identity.setSeed((Integer) seed.getNumber());
                identity.setIncrement((Integer) increment.getNumber());
            }

            if (lexer.token() == Token.NOT) {
                lexer.nextToken();

                if (lexer.token() == Token.NULL) {
                    lexer.nextToken();
                    column.setDefaultExpr(new SQLNullExpr());
                } else {
                    accept(Token.FOR);
                    acceptIdentifier("REPLICATION ");
                    identity.setNotForReplication(true);
                }
            }

            column.setIdentity(identity);
        }

        return super.parseColumnRest(column);
    }
}

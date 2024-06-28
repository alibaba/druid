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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectStatement;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoAlterFunctionStatement;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoAlterSchemaStatement;
import com.alibaba.druid.sql.parser.*;

/**
 * Created by wenshao on 16/9/13.
 */
public class PrestoStatementParser extends SQLStatementParser {
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
}

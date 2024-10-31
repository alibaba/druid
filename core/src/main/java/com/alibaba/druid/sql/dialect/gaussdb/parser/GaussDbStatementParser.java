package com.alibaba.druid.sql.dialect.gaussdb.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLListExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.gaussdb.ast.stmt.GaussDbInsertStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.parser.EOFParserException;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.ArrayList;
import java.util.List;

public class GaussDbStatementParser extends PGSQLStatementParser {
    public GaussDbStatementParser(String sql) {
        super(new GaussDbExprParser(sql));
    }

    public GaussDbStatementParser(String sql, SQLParserFeature... features) {
        super(new GaussDbExprParser(sql, features));
    }

    public GaussDbCreateTableParser getSQLCreateTableParser() {
        return new GaussDbCreateTableParser(this.exprParser);
    }

    @Override
    public SQLCreateTableStatement parseCreateTable() {
        return getSQLCreateTableParser().parseCreateTable();
    }

    @Override
    public PGInsertStatement parseInsert() {
        GaussDbInsertStatement stmt = new GaussDbInsertStatement();

        if (lexer.token() == Token.INSERT) {
            lexer.nextToken();
            if (lexer.identifierEquals("IGNORE")) {
                lexer.nextToken();
                stmt.setIgnore(true);
            } else if (lexer.token() == Token.OVERWRITE) {
                lexer.nextToken();
                stmt.setOverwrite(true);
            }
            accept(Token.INTO);

            stmt.setTableSource(this.exprParser.name());
            if (lexer.token() == Token.AS) {
                lexer.nextToken();
                stmt.setAlias(lexer.stringVal());
                lexer.nextToken();
            } else if (lexer.token() == Token.IDENTIFIER) {
                stmt.setAlias(lexer.stringVal());
                lexer.nextToken();
            }

        }

        if (lexer.nextIf(Token.LPAREN)) {
            this.exprParser.exprList(stmt.getColumns(), stmt);
            accept(Token.RPAREN);
        }

        if (lexer.nextIf(Token.DEFAULT)) {
            accept(Token.VALUES);
            stmt.setDefaultValues(true);
        }

        if (lexer.nextIf(Token.VALUES)) {
            for (; ; ) {
                accept(Token.LPAREN);
                SQLInsertStatement.ValuesClause valuesCaluse = new SQLInsertStatement.ValuesClause();
                this.exprParser.exprList(valuesCaluse.getValues(), valuesCaluse);
                stmt.addValueCause(valuesCaluse);

                accept(Token.RPAREN);
                if (lexer.nextIf(Token.COMMA)) {
                    continue;
                }
                break;
            }
        } else if (lexer.token() == (Token.SELECT)) {
            SQLSelect select = this.createSQLSelectParser().select();
            stmt.setQuery(select);
        }

        if (lexer.nextIf(Token.ON)) {
            if (lexer.identifierEquals(FnvHash.Constants.CONFLICT)) {
                lexer.nextToken();

                if (lexer.nextIf(Token.LPAREN)) {
                    List<SQLExpr> onConflictTarget = new ArrayList<SQLExpr>();
                    this.exprParser.exprList(onConflictTarget, stmt);
                    stmt.setOnConflictTarget(onConflictTarget);
                    accept(Token.RPAREN);
                }

                if (lexer.nextIf(Token.ON)) {
                    accept(Token.CONSTRAINT);
                    SQLName constraintName = this.exprParser.name();
                    stmt.setOnConflictConstraint(constraintName);
                }

                if (lexer.nextIf(Token.WHERE)) {
                    SQLExpr where = this.exprParser.expr();
                    stmt.setOnConflictWhere(where);
                }

                if (lexer.nextIf(Token.DO)) {
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
                        if (lexer.nextIf(Token.WHERE)) {
                            SQLExpr where = this.exprParser.expr();
                            stmt.setOnConflictUpdateWhere(where);
                        }
                    }
                }
            } else if (lexer.identifierEquals("DUPLICATE")) {
                lexer.nextToken();
                accept(Token.KEY);
                accept(Token.UPDATE);

                List<SQLExpr> duplicateKeyUpdate = stmt.getDuplicateKeyUpdate();
                for (; ; ) {
                    SQLName name = this.exprParser.name();
                    accept(Token.EQ);
                    SQLExpr value;
                    try {
                        value = this.exprParser.expr();
                    } catch (EOFParserException e) {
                        throw new ParserException("EOF, " + name + "=", e);
                    }

                    SQLBinaryOpExpr assignment = new SQLBinaryOpExpr(name, SQLBinaryOperator.Equality, value);
                    assignment.setParent(stmt);
                    duplicateKeyUpdate.add(assignment);

                    if (lexer.token() == Token.COMMA) {
                        lexer.nextTokenIdent();
                        continue;
                    }
                    break;
                }
            }
        }

        if (lexer.nextIf(Token.RETURNING)) {
            SQLExpr returning = this.exprParser.expr();

            if (lexer.nextIf(Token.COMMA)) {
                SQLListExpr list = new SQLListExpr();
                list.addItem(returning);

                this.exprParser.exprList(list.getItems(), list);

                returning = list;
            }

            stmt.setReturning(returning);
        }
        return stmt;
    }

    protected void createOptionSkip() {
        lexer.nextIf(Token.LOCAL);
        lexer.nextIfIdentifier("UNLOGGED");
        super.createOptionSkip();
    }
}
